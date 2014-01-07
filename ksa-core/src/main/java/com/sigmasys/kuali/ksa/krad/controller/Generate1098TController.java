package com.sigmasys.kuali.ksa.krad.controller;

import java.io.PrintWriter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sigmasys.kuali.ksa.krad.model.Form1098TModel;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.sigmasys.kuali.ksa.exception.UserNotFoundException;
import com.sigmasys.kuali.ksa.krad.form.Generate1098TForm;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.*;
import com.sigmasys.kuali.ksa.util.CalendarUtils;
import com.sigmasys.kuali.ksa.util.ErrorUtils;


@Controller
@RequestMapping(value = "/generate1098T")
public class Generate1098TController extends DownloadController {

    /**
     * The logger.
     */
    private static final Log logger = LogFactory.getLog(Generate1098TController.class);

    /**
     * IRS Form 1098T Comparator. Compares by Tax Year in the reverse chronological order.
     */
    private static final Comparator<Irs1098T> form1098TComparator = new Irs1098TComparator();


    @Autowired
    private ReportService reportService;

    @Autowired
    private AccountService accountService;


    /**
     * Creates an initial form, which is the Admin page form.
     */
    @Override
    protected UifFormBase createInitialForm(HttpServletRequest request) {
        // Create a new Form:
        Generate1098TForm form = new Generate1098TForm();
        String userId = request.getParameter("userId");

        // Initialize the form:
        initializeForm(form, userId);

        return form;
    }

    /**
     * Invoked on display of the form.
     *
     * @param form   The form behind this screen.
     * @param userId ID of the Account displayed.
     * @return ModelAndView to be displayed.
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=display1098TForm")
    public ModelAndView displayGenerate1098TForm(@ModelAttribute("KualiForm") Generate1098TForm form, @RequestParam("userId") String userId) {
        // Initialize the form if it hasn't been initialized yet:
//    	initializeForm(form, userId);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked on a success callback to download the report form.
     *
     * @param response HTTP response.
     * @return Nothing to stay on the same page.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=download1098TForm")
    public ModelAndView download1098TForm(@ModelAttribute("KualiForm") Generate1098TForm form, HttpServletResponse response,
                                          @RequestParam("reportYear") int reportYear) throws Exception {
        // Read the report file:
        String form1098T = null;
        String accountId = form.getAccount().getId();

        // Get the form from the ReportService:
        try {
            form1098T = reportService.generate1098TReportByYear(accountId, reportYear, false);
        } catch (Exception e) {
            logger.error("Error generating form 1098T. See log file for details.", e);
        }

        // Reset the error messages:
        resetErrorMessages(form);

        // If the form content is available, start download:
        if (StringUtils.isNotEmpty(form1098T)) {
            // Start download:
            String reportFileName = generateReportFileName(form);

            doDownload(form1098T, reportFileName, "application/xml", response);
        } else {
            // Set the error on the form and return the ModelAndView:
            form.setDocumentGenerationError("Error generating form 1098T. See log file for details.");

            return getUIFModelAndView(form);
        }

        return null;
    }

    /**
     * Called to display previously generated and saved to the database forms 1098T for the current account.
     *
     * @param form The backing form.
     * @return ModelAndView.
     * @throws Exception
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=viewGenerated1098TForms")
    public ModelAndView viewGenerated1098TForms(@ModelAttribute("KualiForm") Generate1098TForm form) throws Exception {

        // Get all previously generated forms 1098T for the current account:
        List<Irs1098T> allForms = reportService.getIrs1098TReportsForAccount(form.getAccount().getId());

        // Sort by tax year in the reverse chronological order:
        Collections.sort(allForms, form1098TComparator);

        // Create collection lines for the display:
        List<Form1098TModel> collectionLines = new ArrayList<Form1098TModel>();
        String currentFormYear = null;

        for (Irs1098T form1098T : allForms) {
            // Create a new collection line:
            Form1098TModel line = new Form1098TModel();

            line.setForm1098T(form1098T);
            collectionLines.add(line);

            // If the year has changed, mark the form as "final revision":
            String formYear = form1098T.getFormYear();

            if (!StringUtils.equals(currentFormYear, formYear)) {
                line.setFinalRevision(true);
                currentFormYear = formYear;
            }
        }

        // Set the 1098T forms on the form object:
        form.setForm1098TModels(collectionLines);

        // Reset the error messages:
        resetErrorMessages(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked to display a single previously generated IRS Form 1098T.
     * Starts download of the saved form's content or displays an error on a next page.
     *
     * @param form     The backing form.
     * @param response HTTP response object.
     * @return <code>null</code> always.
     * @throws Exception If there are any errors.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=viewGenerated1098TForm")
    public ModelAndView viewGenerated1098TForm(@ModelAttribute("KualiForm") Generate1098TForm form,
                                               HttpServletResponse response,
                                               @RequestParam("form1098TId") Long form1098TId,
                                               @RequestParam("reportYear") String reportYear) throws Exception {
        String xml = null;

        // Get the report as XML string:
        try {
            xml = reportService.convertIrs1098TToXml(form1098TId);
        } catch (Exception e) {
            logger.error("Error converting an object Irs1098T to XML", e);
        }

        // If the XML content is valid, start downloading:
        if (StringUtils.isNotBlank(xml)) {
            // Get download started:
            String reportFileName = generateReportFileName(form);

            doDownload(xml, reportFileName, "application/xml", response);
        } else {
            // Load the previously displayed 1089T forms, set the error on the form and return a ModelAndView:
            viewGenerated1098TForms(form);
            form.setViewGeneratedFormError(String.format("Form 1098T for the tax year %s is unavailable.", reportYear));

            return getUIFModelAndView(form);
        }

        return null;
    }


    /* *****************************************************************************************************
      *
      * Helper methods.
      *
      * ****************************************************************************************************/

    /**
     * Resets the error messages on the form to null.
     *
     * @param form The form object.
     */
    private void resetErrorMessages(Generate1098TForm form) {
        form.setViewGeneratedFormError(null);
        form.setDocumentGenerationError(null);
    }

    private void initializeForm(Generate1098TForm form, String userId) {
        // Get the full Account if it has not been selected yet:
        if (form.getAccount() == null && userId != null) {
            // Find and set the Account:
            Account account = findAccount(userId);

            form.setAccount(account);
        }

        // Set the report year to the current year:
        form.setReportYears(Arrays.asList(String.valueOf(CalendarUtils.getYear(new Date()))));
    }

    /**
     * Finds an Account with the specified ID.
     *
     * @param userId ID of a user which Account to select.
     * @return Account with the specified ID.
     * @throws IllegalArgumentException If <code>userId</code> is null or blank.
     * @throws UserNotFoundException    If there is no such Account with the specified ID.
     */
    private Account findAccount(String userId) {

        if (StringUtils.isNotBlank(userId)) {
            // Get the full Account from the Account service:
            Account account = accountService.getFullAccount(userId);

            if (account == null) {
                throw new UserNotFoundException("Account not found for ID: " + userId);
            }

            return account;
        }

        throw new IllegalArgumentException("Invalid user ID selected: " + userId);
    }

    /**
     * Generates a name for a report file.
     *
     * @param form A form.
     * @return Generated report file name.
     */
    private static String generateReportFileName(Generate1098TForm form) {
        PersonName name = form.getAccount().getDefaultPersonName();
        return String.format("1098T_%s_%s.xml", name.getLastName(), name.getFirstName());
    }

    /**
     * Extracts the message from a Throwable. If the message is missing, checks the cause Throwable.
     *
     * @param t Throwable to get its or its cause's message.
     * @return Error message.
     */
    private static String getExceptionMessage(Throwable t, String defaultMessage) {
        String errorMessage = ErrorUtils.getMessage(t);
        if (StringUtils.isBlank(errorMessage)) {
            errorMessage = defaultMessage;
        }
        return errorMessage;
    }


    /**
     * IRS Form 1098T comparator. Compares forms by their date of creation in the reverse chronological order,
     * so the most recent versions come first.
     */
    private static class Irs1098TComparator implements Comparator<Irs1098T> {

        @Override
        public int compare(Irs1098T o1, Irs1098T o2) {
            int result = 0;

            if ((o1 != null) && (o2 != null)) {
                // Compare by Tax Year first:
                result =  StringUtils.isNotEmpty(o1.getFormYear())
                        && StringUtils.isNotEmpty(o2.getFormYear())
                        ? -o1.getFormYear().compareTo(o2.getFormYear()) : 0;

                // Then compare by the Creation Date:
                if (result == 0) {
                    result = -o1.getCreationDate().compareTo(o2.getCreationDate());
                }
            }

            return result;
        }
    }
}

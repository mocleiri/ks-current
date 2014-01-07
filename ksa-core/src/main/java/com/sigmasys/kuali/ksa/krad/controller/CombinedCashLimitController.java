package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.CashLimitForm;
import com.sigmasys.kuali.ksa.krad.form.TransactionFilterForm;
import com.sigmasys.kuali.ksa.krad.model.CashLimitEventModel;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.CashLimitService;
import com.sigmasys.kuali.ksa.service.ReportService;
import com.sigmasys.kuali.ksa.util.ZipFileEntry;
import com.sigmasys.kuali.ksa.util.ZipUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * This controller serves requests from the "Batch - Combined Cash Limit" page.
 *
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 11/5/13
 * Time: 2:48 AM
 * To change this template use File | Settings | File Templates.
 */
@Controller
@RequestMapping(value = "/batchCashLimitView")
public class CombinedCashLimitController extends TransactionFilterController {

    private static final Log logger = LogFactory.getLog(CombinedCashLimitController.class);

    /**
     * Date formatter.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

    @Autowired
    private CashLimitService cashLimitService;

    @Autowired
    private ReportService reportService;


    /**
     * Called by the framework to create the initial form object.
     *
     * @param request HTTP request.
     * @return The initial form object.
     */
    @Override
    protected CashLimitForm createInitialForm(HttpServletRequest request) {

        // Create a new form:
        CashLimitForm form = new CashLimitForm();

        initializeForm(form);

        // Set defaults:
        form.setCashLimitEvents(new ArrayList<CashLimitEventModel>());

        return form;
    }

    /**
     * Called when the form is initially opened.
     *
     * @param form The form object associated with the Account Refund page.
     * @return ModelAndView for the page.
     * @throws Exception If any errors occur.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=displayInitialPage")
    public ModelAndView displayInitialPage(@ModelAttribute("KualiForm") CashLimitForm form) throws Exception {
        // Get the model data:
        refreshModel(form);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked to download an archive of IRS Form 8300 for CashLimitEvents with IDs
     * found in the comma-separated list "cashLimitEventIds".
     * Returns null to stay on the same page after download starts.
     *
     * @param form              Form object.
     * @param response          HTTP Servlet response.
     * @param cashLimitEventIds A comma-separated list of CashLimitEvent IDs for which generate an archive.
     * @return null to stay on the same page after download starts.
     * @throws Exception If an error occurs.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=downloadGeneratedArchiveXml")
    public ModelAndView downloadGeneratedArchiveXml(@ModelAttribute("KualiForm") CashLimitForm form,
                                                    HttpServletResponse response,
                                                    @RequestParam("cashLimitEventIds") String cashLimitEventIds) throws Exception {

        // Find each CashLimitEvent from the comma-separated String of CashLimitEvent IDs:
        List<CashLimitEvent> cashLimitEvents = findCashLimitEvents(cashLimitEventIds);

        // Create a ZIP archive of the IRS Form 8300:
        File zipArchive = createForm8300Archive(cashLimitEvents);

        // If entries exist, start download:
        if ((zipArchive != null) && zipArchive.exists()) {

            String zipFileName = String.format("Combined_Cash_Limit_Archive_%s.zip", DATE_FORMAT.format(new Date()));

            // Start downloading:
            doDownload(new FileInputStream(zipArchive), zipFileName, "application/zip", response);

            // Remove the file when done:
            zipArchive.delete();
        }

        return null;
    }

    /**
     * Invoked to download a Complete CashLimitEvent XML.
     * Returns null to stay on the same page after download starts.
     *
     * @param form              Form object.
     * @param response          HTTP response.
     * @param cashLimitEventId  ID of a Cash Limit Event.
     * @param accountId         ID of an Account for which the report is to be generated.
     * @param eventDateString   String representation of the event date.
     * @return null to stay on the same page after download starts.
     * @throws Exception If an error occurs.
     */
    @RequestMapping(method = {RequestMethod.POST, RequestMethod.GET}, params = "methodToCall=downloadCashEventXml")
    public ModelAndView downloadCashEventXml(@ModelAttribute("KualiForm") CashLimitForm form,
                                             HttpServletResponse response,
                                             @RequestParam("cashLimitEventId") Long cashLimitEventId,
                                             @RequestParam("accountId") String accountId,
                                             @RequestParam("eventDateString") String eventDateString) throws Exception {

        // Get the CashLimitEvent:
        CashLimitEvent cashLimitEvent = cashLimitService.getCashLimitEvent(cashLimitEventId);
        String form8300 = null;

        // If it is "Queued", generate a Form 8300. Otherwise, extract existing:
        if (cashLimitEvent.getStatus() == CashLimitEventStatus.QUEUED) {

            // Generate a Form 8300:
            try {
                form8300 = reportService.generateIrs8300Report(cashLimitEvent.getId());
            } catch (Exception e) {
                logger.error("Error generating form 8300. See log file for details.", e);
            }
        } else if (cashLimitEvent.getStatus() == CashLimitEventStatus.COMPLETED) {

            // Get the existing Form 8300. If there no such form, nothing to do:
            form8300 = (cashLimitEvent.getXmlDocument() != null) ? cashLimitEvent.getXmlDocument().getXml() : null;
        }

        // Reset the error message:
        form.setFormSubmissionError(null);

        // If the form content is available, start download:
        if (StringUtils.isNotEmpty(form8300)) {
            // Start download:
            String reportFileName = generateReportFileName(cashLimitEvent);

            doDownload(form8300, reportFileName, "text/xml", response);

            // Get rid of the generated form until this CashLimitEvent is Completed
            cashLimitEvent.setXmlDocument(null);
        } else {
            // Set the error on the form and return the ModelAndView:
            form.setFormSubmissionError("Error obtaining IRS form 8300. See log file for details.");

            return getUIFModelAndView(form);
        }

        return null;
    }

    /**
     * Invoked when the "Complete Selected" button is pressed.
     *
     * @param form      The form object.
     * @return          ModelAndView
     * @throws Exception If an error occurs.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=completeSelectedAccounts")
    public ModelAndView completeSelectedAccounts(@ModelAttribute("KualiForm") CashLimitForm form) throws Exception {

        // Get the selected CashLimitAccounts from the form:
        List<CashLimitEventModel> selectedCashLimitEvents = getSelectedCashLimitEvents(form);
        boolean generateForm8300 = form.isGenerateDocumentsFromSelected();
        List<String> completedCashLimitEventIds = new ArrayList<String>();
        String formSubmissionError = null;

        // Completed each selected CashLimitEvent:
        try {
            for (CashLimitEventModel model : selectedCashLimitEvents) {

                Long id = model.getCashLimitEvent().getId();

                cashLimitService.completeCashLimitEvent(id, generateForm8300);
                completedCashLimitEventIds.add(id.toString());
            }
        } catch (Exception e) {
            formSubmissionError = e.getMessage();
        }

        // Refresh the model:
        refreshModel(form);

        // Set the IDs of the completed CashLimitEvents:
        form.setLastCompletedCashLimitEventIds(org.springframework.util.StringUtils.collectionToCommaDelimitedString(completedCashLimitEventIds));
        form.setFormSubmissionError(formSubmissionError);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked when the "Ignore Selected" button is pressed.
     *
     * @param form      The form object.
     * @return          ModelAndView
     * @throws Exception If an error occurs.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=ignoreSelectedAccounts")
    public ModelAndView ignoreSelectedAccounts(@ModelAttribute("KualiForm") CashLimitForm form) throws Exception {

        // Get the selected CashLimitAccounts from the form:
        List<CashLimitEventModel> selectedCashLimitEvents = getSelectedCashLimitEvents(form);
        String formSubmissionError = null;

        // Ignore each selected CashLimitEvent:
        try {
            for (CashLimitEventModel model : selectedCashLimitEvents) {

                Long id = model.getCashLimitEvent().getId();

                cashLimitService.ignoreCashLimitEvent(id);
            }
        } catch (Exception e) {
            formSubmissionError = e.getMessage();
        }

        // Refresh the model:
        refreshModel(form);
        form.setFormSubmissionError(formSubmissionError);

        return getUIFModelAndView(form);
    }

    /**
     * Invoked when the "Enqueue Selected" button is pressed.
     *
     * @param form      The form object.
     * @return          ModelAndView
     * @throws Exception If an error occurs.
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=enqueueSelectedAccounts")
    public ModelAndView enqueueSelectedAccounts(@ModelAttribute("KualiForm") CashLimitForm form) throws Exception {

        // Get the selected CashLimitAccounts from the form:
        List<CashLimitEventModel> selectedCashLimitEvents = getSelectedCashLimitEvents(form);
        String formSubmissionError = null;

        // Ignore each selected CashLimitEvent:
        try {
            for (CashLimitEventModel model : selectedCashLimitEvents) {

                Long id = model.getCashLimitEvent().getId();

                cashLimitService.queueCashLimitEvent(id);
            }
        } catch (Exception e) {
            formSubmissionError = e.getMessage();
        }

        // Refresh the model:
        refreshModel(form);
        form.setFormSubmissionError(formSubmissionError);

        return getUIFModelAndView(form);
    }

    /**
     * Refreshes the underlying data model. This action happens after a filter
     * is changes and a refresh is required to apply the changed filter.
     *
     * @param form  The form object.
     */
    @Override
    protected void refreshModel(TransactionFilterForm form) {

        // Create a List of CashModelEventModel objects:
        List<CashLimitEventModel> cashLimitEventModels = createCashLimitEventModelList(form);

        // Set the models on the form:
        ((CashLimitForm)form).setCashLimitEvents(cashLimitEventModels);

        // Remove the properties that must be reset on each refresh:
        ((CashLimitForm)form).setLastCompletedCashLimitEventIds(null);
        ((CashLimitForm)form).setFormSubmissionError(null);
    }


    /***********************************************************************************************
     *
     * Helper methods.
     *
     ***********************************************************************************************/

    /**
     * Creates a List of CashLimitEvent model objects.
     *
     * @param form  The form object.
     * @return A List of CashLimitEvent model objects.
     */
    private <T extends TransactionFilterForm> List<CashLimitEventModel> createCashLimitEventModelList(T form) {

        // Get the selected Account IDs:
        List<String> accountIds = getFilterAccountIds(form);
        List<CashLimitEventModel> cashLimitEventModels = new ArrayList<CashLimitEventModel>();

        // Get CashLimitEvent objects for the selected Accounts:
        if (CollectionUtils.isNotEmpty(accountIds)) {

            // Get the filtering values:
            CashLimitForm thisForm = (CashLimitForm)form;
            CashLimitEventStatus filteringStatus = thisForm.getFilterCashLimitEventStatus();
            boolean applyDateRange = "RANGE".equals(thisForm.getDateRangeType());
            Date dateFrom = applyDateRange ? thisForm.getFilterDateFrom() : null;
            Date dateTo = applyDateRange ? thisForm.getFilterDateTo() : null;

            // Get the CashLimitEvent objects based on the filtering criteria:
            List<CashLimitEvent> cashLimitEvents = (filteringStatus != null)
                    ? cashLimitService.getCashLimitEvents(accountIds, dateFrom, dateTo, filteringStatus)
                    : cashLimitService.getCashLimitEvents(accountIds, dateFrom, dateTo);

            if (CollectionUtils.isNotEmpty(cashLimitEvents)) {

                for (CashLimitEvent cashLimitEvent : cashLimitEvents) {

                    // Create a CashLimitEventModel for each object:
                    CashLimitEventModel model = createCashLimitEventModel(cashLimitEvent);

                    cashLimitEventModels.add(model);
                }
            }
        }

        return cashLimitEventModels;
    }

    /**
     * Creates a CashLimitEventModel object from the given CashLimitEvent object.
     *
     * @param cashLimitEvent A CashLimitEvent object.
     * @return A CashLimitEventModel object.
     */
    private CashLimitEventModel createCashLimitEventModel(CashLimitEvent cashLimitEvent) {

        // Create a new model object:
        CashLimitEventModel model = new CashLimitEventModel();
        Set<Payment> payments = cashLimitEvent.getPayments();
        List<Payment> paymentList = CollectionUtils.isNotEmpty(payments) ? new ArrayList<Payment>(payments) : new ArrayList<Payment>();
        Date eventDate = cashLimitEvent.getEventDate();
        String eventDateString = (eventDate != null) ? DATE_FORMAT.format(eventDate) : null;

        // Set up Account related information:
        String accountId = cashLimitEvent.getAccountId();
        Account account = accountService.getFullAccount(accountId);
        String dateOfBirthString = null;
        AccountProtectedInfo accountProtectedInfo = accountService.getAccountProtectedInfo(accountId);

        // Format the date of birth as String:
        if ((account instanceof DirectChargeAccount) && (((DirectChargeAccount)account).getDateOfBirth() != null)) {
            dateOfBirthString = DATE_FORMAT.format(((DirectChargeAccount)account).getDateOfBirth());
        }

        // Create a default AccountProtectedInfo if one is missing:
        if (accountProtectedInfo == null) {
            accountProtectedInfo = new AccountProtectedInfo();
        }

        // Create a default IdentityType if one is missing:
        if (accountProtectedInfo.getIdentityType() == null) {
            accountProtectedInfo.setIdentityType(new IdentityType());
        }

        // Calculate "Total Payments" and "Total Allocations"
        BigDecimal totalPayments = BigDecimal.ZERO;
        BigDecimal totalAllocations = BigDecimal.ZERO;

        if (CollectionUtils.isNotEmpty(cashLimitEvent.getPayments())) {
            for (Payment payment : cashLimitEvent.getPayments()) {

                // Add up the amounts:
                totalPayments = totalPayments.add(payment.getAmount());
                totalAllocations = totalAllocations.add(payment.getAllocatedAmount());
            }
        }

        // Set the values:
        model.setCashLimitEvent(cashLimitEvent);
        model.setEventDateString(eventDateString);
        model.setSelected(true);
        model.setStatusString(cashLimitEvent.getStatus().toString());
        model.setTotalPayments(totalPayments);
        model.setTotalAllocations(totalAllocations);
        model.setDateOfBirthString(dateOfBirthString);
        model.setAccount(account);
        model.setAccountProtectedInfo(accountProtectedInfo);
        model.setPayments(paymentList);

        return model;
    }

    /**
     * Creates a name for a 8300 XML report.
     *
     * @param cashLimitEvent    CashLimitEvent for which to generate a report file name.
     * @return Report file name.
     */
    private String generateReportFileName(CashLimitEvent cashLimitEvent) {

        // Compose report file name:
        String accountId = cashLimitEvent.getAccountId();
        String eventDateString = (cashLimitEvent.getEventDate() != null)
                ? DATE_FORMAT.format(cashLimitEvent.getEventDate()) : null;
        String reportFileName = String.format("%s--%d--Combined_Cash_Limit_%s.xml",
                accountId, cashLimitEvent.getId(), StringUtils.defaultString(eventDateString));

        return reportFileName;
    }

    /**
     * Returns a List of selected CashLimitEventModel objects from the form.
     *
     * @param form  The form object.
     * @return A List of selected CashLimitEventModels.
     */
    private List<CashLimitEventModel> getSelectedCashLimitEvents(CashLimitForm form) {

        // Find the selected objects:
        List<CashLimitEventModel> selected = new ArrayList<CashLimitEventModel>();
        List<CashLimitEventModel> allModels = form.getCashLimitEvents();

        if (allModels != null) {

            for (CashLimitEventModel model : allModels) {

                // Check if the model is selected and "Queued"
                if (model.isSelected()) {

                    selected.add(model);
                }
            }
        }

        return selected;
    }

    /**
     * Returns a List of CashLimitEvent object with IDs from the given comma-separated list of IDs.
     *
     * @param cashLimitEventIds A comma-separated list of CashLimitEvent IDs.
     * @return A List of CashLimitEvent object with the specified IDs.
     */
    private List<CashLimitEvent> findCashLimitEvents(String cashLimitEventIds) {

        // Get IDs safely:
        Set<String> stringIds = org.springframework.util.StringUtils.commaDelimitedListToSet(cashLimitEventIds);
        List<CashLimitEvent> result = new ArrayList<CashLimitEvent>();

        for (String idStr : stringIds) {

            // Find a CashLimitEvent:
            Long id = Long.valueOf(idStr);
            CashLimitEvent cashLimitEvent = cashLimitService.getCashLimitEvent(id);

            if (cashLimitEvent != null) {
                result.add(cashLimitEvent);
            }
        }

        return result;
    }

    /**
     * Creates a ZIP archive from the IRS Forms 8300 stored in the specified CashLimitEvents.
     *
     * @param cashLimitEvents   A List of CashLimitEvents to use their IRS 8300 Forms for a ZIP archive.
     * @return The new ZIP archive.
     */
    private File createForm8300Archive(List<CashLimitEvent> cashLimitEvents) {

        // Create a List of ZIP entries:
        List<ZipFileEntry> zipEntries = new ArrayList<ZipFileEntry>();

        for(CashLimitEvent cashLimitEvent : cashLimitEvents) {

            // Check if the Form 8300 exists, add it to ZIP entries:
            String form8300 = (cashLimitEvent.getXmlDocument() != null) ? cashLimitEvent.getXmlDocument().getXml() : null;

            if (StringUtils.isNotBlank(form8300)) {

                // Create a new ZipFileEntry:
                String form8300FileName = generateReportFileName(cashLimitEvent);
                ZipFileEntry zipFileEntry = new ZipFileEntry(form8300.getBytes(), form8300FileName);

                zipEntries.add(zipFileEntry);
            }
        }

        // If there are ZIP entries, generate a ZIP archive:
        File zipFile = null;

        if (!zipEntries.isEmpty()) {

            // Generate a name for the ZIP Archive:
            zipFile = ZipUtils.createZipFile(zipEntries, null);
        }

        return zipFile;
    }
}

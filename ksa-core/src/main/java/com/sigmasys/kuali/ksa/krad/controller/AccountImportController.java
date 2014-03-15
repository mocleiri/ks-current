package com.sigmasys.kuali.ksa.krad.controller;

import javax.servlet.http.HttpServletRequest;

import com.sigmasys.kuali.ksa.krad.form.FileUploadForm;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.sigmasys.kuali.ksa.service.AccountImportService;
import com.sigmasys.kuali.ksa.util.CommonUtils;

/**
 * The controller that handles student account import by uploading XML files.
 *
 * @author Sergey
 * @version 1.0
 */
@SuppressWarnings("deprecation")
@Controller
@RequestMapping(value = "/accountImportView")
public class AccountImportController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(AccountImportController.class);

    @Autowired
    private AccountImportService accountImportService;


    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected FileUploadForm createInitialForm(HttpServletRequest request) {
        FileUploadForm form = new FileUploadForm();
        form.setUploadProcessState("");
        return form;
    }

    /**
     * @param form FileUploadForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") FileUploadForm form) {
        return getUIFModelAndView(form);
    }

    /**
     * @param form FileUploadForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=submit")
    @Transactional(readOnly = false)
    public ModelAndView submit(@ModelAttribute("KualiForm") FileUploadForm form) {

        // org.springframework.web.multipart.MaxUploadSizeExceededException:
        // Maximum upload size of 500000 bytes exceeded; nested exception is
        // org.apache.commons.fileupload.FileUploadBase$SizeLimitExceededException:
        // the request was rejected because its size (992410) exceeds the configured maximum (500000)

        String processMsg = "";

        try {

            MultipartFile uploadXmlFile = form.getUploadFile();
            String contentType = uploadXmlFile.getContentType();
            if (contentType.endsWith("xml")) {

                try {

                    String xmlContent = CommonUtils.getStreamAsString(uploadXmlFile.getInputStream());

                    processMsg = "Processing Transaction(s)";
                    form.setUploadProcessState(processMsg);

                    accountImportService.importStudentProfile(xmlContent);

                    logger.info("Student account import successful");
                    processMsg = "Student account import successful";

                    form.setUploadProcessState(processMsg);
                } catch (Exception exp) {
                    String expMsg = exp.getMessage();
                    logger.error(expMsg);
                    processMsg = "Student account import Failed. Error: " + (StringUtils.isNotBlank(expMsg) ? expMsg : "Runtime error.") + " See log file for details.";
                    form.setUploadProcessState(processMsg);
                }
            } else {
                processMsg = "Only XML files are allowed. Please try again.";
                form.setUploadProcessState(processMsg);
                logger.error(processMsg);
            }
        } catch (Exception exp) {
            String expMsg = exp.getMessage();
            logger.error(expMsg);
            form.setUploadProcessState(expMsg);
        }

        return getUIFModelAndView(form);
    }

}

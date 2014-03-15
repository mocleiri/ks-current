package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.jaxb.KsaBatchTransactionResponse;
import com.sigmasys.kuali.ksa.krad.form.FileUploadForm;
import com.sigmasys.kuali.ksa.model.BatchReceiptStatus;
import com.sigmasys.kuali.ksa.service.TransactionImportService;
import com.sigmasys.kuali.ksa.util.ErrorUtils;
import com.sigmasys.kuali.ksa.util.JaxbUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by: dmulderink on 9/26/12 at 9:11 AM
 */
@Controller
@RequestMapping(value = "/batchTransactionsView")
public class BatchTransactionsController extends GenericSearchController {


    @Autowired
    private TransactionImportService transactionImportService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected FileUploadForm createInitialForm(HttpServletRequest request) {
        FileUploadForm form = new FileUploadForm();
        form.setMessage("");
        form.setUploadProcessState("");
        return form;
    }


    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=displayInitialPage")
    public ModelAndView displayInitialPage(@ModelAttribute("KualiForm") FileUploadForm form) {
        return getUIFModelAndView(form);
    }


    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST}, params = "methodToCall=uploadFile")
    public ModelAndView uploadFile(@ModelAttribute("KualiForm") FileUploadForm form) {

        // do submit stuff...
        // org.springframework.web.multipart.MaxUploadSizeExceededException:
        // Maximum upload size of 500000 bytes exceeded; nested exception is
        // org.apache.commons.fileupload.FileUploadBase$SizeLimitExceededException:
        // the request was rejected because its size (992410) exceeds the configured maximum (500000)

        try {

            MultipartFile xmlFile = form.getUploadFile();
            String contentType = xmlFile.getContentType();

            logger.info("File name: " + xmlFile.getOriginalFilename());
            logger.info("Content type: " + contentType);

            if (xmlFile.isEmpty()) {
                return handleError(form, "Please select an XML file to upload");
            }

            if (contentType.endsWith("xml")) {

                String xmlContent = new String(xmlFile.getBytes(), "UTF-8");

                int index = xmlContent.indexOf("<");
                if (index > 0) {
                    xmlContent = xmlContent.substring(index);
                } else if (index < 0) {
                    return handleError(form, "XML declaration is invalid");
                }

                String processResponse = transactionImportService.importTransactions(xmlContent);
                form.setResponseFile(processResponse);

                KsaBatchTransactionResponse responseObject =
                        JaxbUtils.fromXml(processResponse, KsaBatchTransactionResponse.class);

                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                form.setResponseFilename("xml_batch_" + df.format(new Date()) + "-" + responseObject.getResponseIdentifier() + ".xml");

                logger.info("Response: \n" + processResponse);

                String batchStatus = responseObject.getBatchStatus();

                KsaBatchTransactionResponse.BatchSummary batchSummary = responseObject.getBatchSummary();
                if (batchSummary != null) {
                    form.setTransactionCount(batchSummary.getTransactionsInBatch());
                    form.setSuccessfulCount(batchSummary.getTransactionsAccepted());
                    form.setFailedCount(batchSummary.getTransactionsFailed());
                }

                if (BatchReceiptStatus.FAILED_NAME.equals(batchStatus)) {
                    String errMsg = "";
                    KsaBatchTransactionResponse.Failed failed = responseObject.getFailed();
                    if (failed != null) {
                        List<Object> reasons = failed.getKsaTransactionAndReason();
                        if (CollectionUtils.isNotEmpty(reasons)) {
                            for (Object item : reasons) {
                                if ((item != null) && (item instanceof String)) {
                                    String reason = (String) item;
                                    if (StringUtils.isNotBlank(reason)) {
                                        errMsg += reason.trim() + ". ";
                                    }
                                }
                            }
                        }
                    }
                    return handleError(form, "Transaction(s) Processing Failed. " + errMsg);
                } else {
                    setMessage(form, "Transaction(s) Processing status: " + batchStatus);
                }

            } else {
                return handleError(form, "Only XML files are allowed. Please try again.");
            }

        } catch (Exception e) {
            String errMsg = "Transaction(s) Processing Failed. " + ErrorUtils.getMessage(e);
            logger.error(errMsg, e);
            return handleError(form, errMsg);
        }

        form.setPageId("BatchTransactionsProcessedPage");

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=download")
    public ModelAndView download(@ModelAttribute("KualiForm") FileUploadForm form, HttpServletResponse response) throws Exception {

        ServletOutputStream outputStream = response.getOutputStream();

        response.setContentType("application/xml");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + form.getResponseFilename() + "\"");

        InputStream is = new ByteArrayInputStream(form.getResponseFile().getBytes());


        try {

            int length;
            int bufSize = 1024;

            byte[] buffer = new byte[bufSize];

            while ((length = is.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } finally {
            is.close();
            outputStream.flush();
            outputStream.close();
        }

        return null;
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=ageDebts")
    public ModelAndView ageDebts(@ModelAttribute("KualiForm") FileUploadForm form) {

        try {

            accountService.ageDebt(true);

            String message = "Debts were successfully aged";

            GlobalVariables.getMessageMap().putInfo(getUIFModelAndView(form).getViewName(), RiceKeyConstants.ERROR_CUSTOM, message);

        } catch (Exception e) {
            return handleError(form, e);
        }

        return getUIFModelAndView(form);
    }

}
package com.sigmasys.kuali.ksa.krad.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.sigmasys.kuali.ksa.krad.form.EmptyForm;
import com.sigmasys.kuali.ksa.service.TransactionExportService;

/**
 * This controller initiates download of the XML file produced as the result
 * of the call to {@link TransactionExportService#exportTransactions()}
 *
 * @author Sergey Godunov
 */
@Controller
@RequestMapping(value = "/exportTransactions")
public class TransactionExportController extends GenericSearchController {

    @Autowired
    private TransactionExportService transactionExportService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected UifFormBase createInitialForm(HttpServletRequest request) {
        return new EmptyForm();
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView get(HttpServletResponse response) throws IOException {

        // Get the XML content for the export:
        String xml = transactionExportService.exportTransactions();

        // Do the download process:
        doDownload(xml, response);

        return null;
    }


    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView post(HttpServletResponse response) throws IOException {
        return get(response);
    }

    /**
     * Sends content as a String to the ServletResponse output stream.  Typically
     * you want the browser to receive a different name than the
     * name the file has been saved in your local database, since
     * your local names need to be unique.
     *
     * @param response The response
     */
    private void doDownload(String content, HttpServletResponse response) throws IOException {

        String fileName = "Transaction Export - " + new Date().toString() + ".xml";

        response.setContentType("application/xml");
        response.setContentLength(content.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
        ServletOutputStream outputStream = response.getOutputStream();

        try {

            int length;
            int bufSize = 1024;
            byte[] buffer = new byte[bufSize];
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }

        } finally {
            inputStream.close();
            outputStream.flush();
            outputStream.close();
        }
    }

}

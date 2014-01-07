package com.sigmasys.kuali.ksa.krad.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;


/**
 * This controller enables its subclasses to start downloading process
 * by writing the specified content into the provided <code>HttpServletResponse</code>
 * 
 * @author Sergey Godunov
 */
public abstract class DownloadController extends GenericSearchController {

    /**
     * Sends content as a String to the ServletResponse output stream.
     * Use this method for String content downloads, such as text or XML.
     *
     * @param content	Content to be sent to the client for downloading.
     * @param fileName	Name of the file to save the content as. 
     * @param mimeType	MIME type of the file to download.
     * @param response 	Response to send content to.
     */
    protected void doDownload(String content, String fileName, String mimeType, HttpServletResponse response) throws IOException {
        doDownload(content.getBytes(), fileName, mimeType, response);
    }

    /**
     * Sends content as a byte array to the ServletResponse output stream.
     * Use this method for binary content downloads, such as ZIP or Excel.
     *
     * @param bytes	    Content to be sent to the client for downloading.
     * @param fileName	Name of the file to save the content as.
     * @param mimeType	MIME type of the file to download.
     * @param response 	Response to send content to.
     */
    protected void doDownload(byte[] bytes, String fileName, String mimeType, HttpServletResponse response) throws IOException {
        response.setContentLength(bytes.length);
        doDownload(new ByteArrayInputStream(bytes), fileName, mimeType, response);
    }

    /**
     * Sends content from an InputStream to the ServletResponse output stream.
     * Use this method for binary content downloads, such as ZIP or Excel.
     *
     * @param inputStream	InputStream to be sent to the client for downloading.
     * @param fileName	    Name of the file to save the content as.
     * @param mimeType	    MIME type of the file to download.
     * @param response 	    Response to send content to.
     */
    protected void doDownload(InputStream inputStream, String fileName, String mimeType, HttpServletResponse response) throws IOException {
        response.setContentType(mimeType);
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

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

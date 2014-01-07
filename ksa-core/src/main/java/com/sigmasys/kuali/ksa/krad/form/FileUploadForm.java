package com.sigmasys.kuali.ksa.krad.form;

import org.springframework.web.multipart.MultipartFile;

/**
 * User: dmulderink
 * Date: 4/24/12
 * Time: 11:16 AM
 */
public class FileUploadForm extends AbstractViewModel {


   private MultipartFile uploadFile;

   private String uploadProcessState;


    private int transactionCount = 0;
    private int successfulCount = 0;
    private int failedCount = 0;

    private String responseFile;
    private String responseFilename;


   /**
    * Get the upload XML file
    * @return
    */
   public MultipartFile getUploadFile() {
      return uploadFile;
   }

   /**
    * Set the upload XML file
    * @param uploadFile
    */
   public void setUploadFile(MultipartFile uploadFile) {
      this.uploadFile = uploadFile;
   }

   /**
    * Get the file upload processing state
    * @return
    */
   public String getUploadProcessState() {
      return uploadProcessState;
   }

   /**
    * Set the file upload processing state
    * @param uploadProcessState
    */
   public void setUploadProcessState(String uploadProcessState) {
      this.uploadProcessState = uploadProcessState;
   }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public int getSuccessfulCount() {
        return successfulCount;
    }

    public void setSuccessfulCount(int successfulCount) {
        this.successfulCount = successfulCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public String getResponseFile() {
        return responseFile;
    }

    public void setResponseFile(String responseFile) {
        this.responseFile = responseFile;
    }

    public String getResponseFilename() {
        return responseFilename;
    }

    public void setResponseFilename(String responseFilename) {
        this.responseFilename = responseFilename;
    }
}

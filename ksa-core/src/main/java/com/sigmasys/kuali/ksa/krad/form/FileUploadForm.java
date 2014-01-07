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
}

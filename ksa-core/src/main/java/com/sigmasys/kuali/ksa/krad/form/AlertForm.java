package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.Alert;

import java.util.Date;

/**
 * Created by: dmulderink on 10/6/12 at 2:27 PM
 */
public class AlertForm extends AbstractViewModel {

   private Account account;

   private Date fromDate;

   private Date toDate;

   private Alert alert;

   private String statusMessage;

   // resuable add edit or followup instructional text
   private String instructionalText;

   /*
     Get/Set methods
   */

   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   public Date getFromDate() {
      return fromDate;
   }

   public void setFromDate(Date fromDate) {
      this.fromDate = fromDate;
   }

   public Date getToDate() {
      return toDate;
   }

   public void setToDate(Date toDate) {
      this.toDate = toDate;
   }

   public Alert getAlert() {
      return alert;
   }

   public void setAlert(Alert alert) {
      this.alert = alert;
   }

   public String getStatusMessage() {
      return statusMessage;
   }

   public void setStatusMessage(String statusMessage) {
      this.statusMessage = statusMessage;
   }

   public String getInstructionalText() {
      return instructionalText;
   }

   public void setInstructionalText(String instructionalText) {
      this.instructionalText = instructionalText;
   }
}

package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.Charge;
import com.sigmasys.kuali.ksa.model.DebitType;

import java.math.BigDecimal;

/**
 * Created by: dmulderink on 9/28/12 at 7:56 AM
 */
public class ChargeForm extends AbstractViewModel {


   private Account account;

   private Charge charge;

   private String chargeTransactionTypeId;

   private DebitType transactionType;

   private String transactionTypeMessage;

    private BigDecimal estimatedCurrentBalance;

   private BigDecimal estimatedPendingBalance;

   private String statusMessage;

   /*
     Get / Set methods
   */

   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   public Charge getCharge() {
      return charge;
   }

   public void setCharge(Charge charge) {
      this.charge = charge;
   }

   public String getChargeTransactionTypeId() {
      return chargeTransactionTypeId;
   }

   public void setChargeTransactionTypeId(String chargeTransactionTypeId) {
      this.chargeTransactionTypeId = chargeTransactionTypeId;
   }

    public String getChargeTransactionTypeId2() {
        if(this.transactionType != null){
            return this.transactionType.getId().getId();
        }
        return "";
    }

    public String getDescription() {
        if(this.transactionType != null){
            return transactionType.getDescription();
        }
        return "";
    }


    public BigDecimal getEstimatedCurrentBalance() {
      return estimatedCurrentBalance;
   }

   public void setEstimatedCurrentBalance(BigDecimal estimatedCurrentBalance) {
      this.estimatedCurrentBalance = estimatedCurrentBalance;
   }

   public BigDecimal getEstimatedPendingBalance() {
      return estimatedPendingBalance;
   }

   public void setEstimatedPendingBalance(BigDecimal estimatedPendingBalance) {
      this.estimatedPendingBalance = estimatedPendingBalance;
   }

   public String getStatusMessage() {
      return statusMessage;
   }

   public void setStatusMessage(String statusMessage) {
      this.statusMessage = statusMessage;
   }

    public DebitType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(DebitType transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionTypeMessage() {
        return transactionTypeMessage;
    }

    public void setTransactionTypeMessage(String transactionTypeMessage) {
        this.transactionTypeMessage = transactionTypeMessage;
    }
}

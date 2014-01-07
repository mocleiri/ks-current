package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.krad.model.TransactionModel;
import com.sigmasys.kuali.ksa.model.*;

import java.util.Date;
import java.util.List;

/**
 * Created by: dmulderink on 8/29/12 at 12:58 PM
 */
public class SearchForm extends AbstractViewModel {

   /*
     Account Overview
   */

   private List<TransactionModel> rollUpTransactionModelList;

   private List<TransactionModel> unGroupedTransactionModelList;

   private List<TransactionModel> byRollUpTransactionModelList;

   private List<Allocation> allocationList;

   private TransactionModel transactionModel;

   private Account account;


   // Allocation
   private String allocationId;

   private String allocationAccountId;


   private String payerTransactionId;

   private String paidTransactionId;

   private String allocationAmount;

   private String allocationLocked;


   // Memo
   private String previousMemoId;

   private String nextMemoId;

   private String memo;


   // Audit

   private Date creationDate;

   private String creatorId;

   // title or instructional information substitution

   private String selectedRollupType;

   private String selectedTransactionType;

   // The OverView Detail page readOnly property is both Read and Read/Write
   private String ovDetailReadWriteState;

   /*
      Get/Set methods
    */


   public List<TransactionModel> getRollUpTransactionModelList() {
      return rollUpTransactionModelList;
   }

   public void setRollUpTransactionModelList(List<TransactionModel> rollUpTransactionModelList) {
      this.rollUpTransactionModelList = rollUpTransactionModelList;
   }

   public List<TransactionModel> getUnGroupedTransactionModelList() {
      return unGroupedTransactionModelList;
   }

   public void setUnGroupedTransactionModelList(List<TransactionModel> unGroupedTransactionModelList) {
      this.unGroupedTransactionModelList = unGroupedTransactionModelList;
   }

   public List<TransactionModel> getByRollUpTransactionModelList() {
      return byRollUpTransactionModelList;
   }

   public void setByRollUpTransactionModelList(List<TransactionModel> byRollUpTransactionModelList) {
      this.byRollUpTransactionModelList = byRollUpTransactionModelList;
   }

   public List<Allocation> getAllocationList() {
      return allocationList;
   }

   public void setAllocationList(List<Allocation> allocationList) {
      this.allocationList = allocationList;
   }

   public TransactionModel getTransactionModel() {
      return transactionModel;
   }

   public void setTransactionModel(TransactionModel transactionModel) {
      this.transactionModel = transactionModel;
   }


   public Account getAccount() {
      return account;
   }

   public void setAccount(Account account) {
      this.account = account;
   }

   public String getAllocationId() {
      return allocationId;
   }

   public void setAllocationId(String allocationId) {
      this.allocationId = allocationId;
   }

   public String getAllocationAccountId() {
      return allocationAccountId;
   }

   public void setAllocationAccountId(String allocationAccountId) {
      this.allocationAccountId = allocationAccountId;
   }

   public String getPayerTransactionId() {
      return payerTransactionId;
   }

   public void setPayerTransactionId(String payerTransactionId) {
      this.payerTransactionId = payerTransactionId;
   }

   public String getPaidTransactionId() {
      return paidTransactionId;
   }

   public void setPaidTransactionId(String paidTransactionId) {
      this.paidTransactionId = paidTransactionId;
   }


   public String getAllocationAmount() {
      return allocationAmount;
   }

   public void setAllocationAmount(String allocationAmount) {
      this.allocationAmount = allocationAmount;
   }

   public String getAllocationLocked() {
      return allocationLocked;
   }

   public void setAllocationLocked(String allocationLocked) {
      this.allocationLocked = allocationLocked;
   }


   public String getPreviousMemoId() {
      return previousMemoId;
   }

   public void setPreviousMemoId(String previousMemoId) {
      this.previousMemoId = previousMemoId;
   }

   public String getNextMemoId() {
      return nextMemoId;
   }

   public void setNextMemoId(String nextMemoId) {
      this.nextMemoId = nextMemoId;
   }

   public String getMemo() {
      return memo;
   }

   public void setMemo(String memo) {
      this.memo = memo;
   }



   public Date getCreationDate() {
      return creationDate;
   }

   public void setCreationDate(Date creationDate) {
      this.creationDate = creationDate;
   }

   public String getCreatorId() {
      return creatorId;
   }

   public void setCreatorId(String creatorId) {
      this.creatorId = creatorId;
   }



   public String getSelectedRollupType() {
      return selectedRollupType;
   }

   public void setSelectedRollupType(String selectedRollupType) {
      this.selectedRollupType = selectedRollupType;
   }

   public String getSelectedTransactionType() {
      return selectedTransactionType;
   }

   public void setSelectedTransactionType(String selectedTransactionType) {
      this.selectedTransactionType = selectedTransactionType;
   }

   public String getOvDetailReadWriteState() {
      return ovDetailReadWriteState;
   }

   public void setOvDetailReadWriteState(String ovDetailReadWriteState) {
      this.ovDetailReadWriteState = ovDetailReadWriteState;
   }
}

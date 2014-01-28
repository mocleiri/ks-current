package com.sigmasys.kuali.ksa.krad.model;


import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.tree.Node;
import org.kuali.rice.core.api.util.tree.Tree;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by: dmulderink on 9/27/12 at 7:46 AM
 */
public class TransactionModel extends Transaction {

    private final Log logger = LogFactory.getLog(TransactionModel.class);

    private Transaction parentTransaction;

    private List<TransactionModel> subTransactions;

    private TransactionTypeValue transactionTypeValue;

    private List<AllocationModel> allocations;

    private String transactionTypeId;

    private String transactionTypeDesc;

    private String rollupDesc;

    // a comma delimited collection of rollup tag values for the transactionType
    private String rollupTag;

    // transaction type subcode
    private String transactionTypeSubCode;

    // Integer equivalent for a Debit, otherwise null
    private String debitPriority;

    private String deferredId;

    // Date for a Payment, otherwise null
    private Date paymentClearDate;

    private String refundRule;

    private BigDecimal refundAmount;

    private List<Memo> memos;

    private List<TransactionTagModel> tags;
    private String tagList;
    private String newTag;

    private List<Alert> alerts;
    private List<Flag> flags;

    // Document id as a string
    private String documentId;

    // General Ledger id;
    private String generalLedgerTypeId;

    // Boolean get method does not work well except as a string
    private String glOverRidden;

    // aggregated values
    private String rollUpBalance;

    private String rollUpDebit;

    private String rollUpCredit;

    private String unGroupedBalance;

    private String unGroupedDebit;

    private String unGroupedCredit;

    private String unGroupedTotalCredit;


    private BigDecimal chargeAmount;
    private BigDecimal paymentAmount;
    private BigDecimal defermentAmount;

    private BigDecimal runningBalance;

    private BigDecimal newAllocation;

    // checkboxes
    private String paymentBilling;

    private String entryGenerated;

    private String allocationLocked;

    private String message;

    private String reverseTransactionReason;
    private boolean reverseTransactionInternalOnly;

    // Constructor
    public TransactionModel() {
    }

    public TransactionModel(Transaction transaction) {

        parentTransaction = transaction;

        transactionTypeValue = transaction.getTransactionTypeValue();

        Set<Tag> transactionTags = transaction.getTags();

        setTags(transactionTags != null ? transactionTags : new HashSet<Tag>());
        setTagsList(transactionTags != null ? new ArrayList<Tag>(transactionTags) : new ArrayList<Tag>());

        // populate TransactionModel's properties from Transaction instance
        setId(transaction.getId());
        setAccountId(transaction.getAccountId());
        setTransactionType(transaction.getTransactionType());
        setRollup(transaction.getRollup());
        setExternalId(transaction.getExternalId());
        setCreationDate(transaction.getCreationDate());
        setEffectiveDate(transaction.getEffectiveDate());
        setOriginationDate(transaction.getOriginationDate());
        setRecognitionDate(transaction.getRecognitionDate());
        setAmount(transaction.getAmount());
        setNativeAmount(transaction.getNativeAmount());
        setCurrency(transaction.getCurrency());
        setInternal(transaction.isInternal());
        setAllocatedAmount(transaction.getAllocatedAmount());
        setLockedAllocatedAmount(transaction.getLockedAllocatedAmount());
        setStatementText(transaction.getStatementText());
        setDocument(transaction.getDocument());
        setAccount(transaction.getAccount());
        setGlEntryGenerated(transaction.isGlEntryGenerated());
        setGeneralLedgerType(transaction.getGeneralLedgerType());
        setGlOverridden(transaction.isGlOverridden());

        if (parentTransaction != null) {
            if (parentTransaction.getCurrency() != null) {
                parentTransaction.getCurrency().getCode();
            }
            if (parentTransaction.getGeneralLedgerType() != null) {
                parentTransaction.getGeneralLedgerType().getDescription();
            }
            if (parentTransaction.getTransactionType() != null) {
                parentTransaction.getTransactionType().getId().getId();
            }
            if (parentTransaction.getRollup() != null) {
                parentTransaction.getRollup().getDescription();
            }
        }

        // charge, payment or deferment specific data members
        BigDecimal amount = transaction.getAmount();

        switch (transaction.getTransactionTypeValue()) {
            case CHARGE:
                setChargeAmount(amount);
                break;
            case PAYMENT:
                setPaymentAmount(amount);
                break;
            case DEFERMENT:
                setDefermentAmount(amount);
        }

    }

    public String getIdString() {
        // Krad tries to put commas in things.
        return parentTransaction.getId().toString();
    }

    @Override
    public TransactionTypeValue getTransactionTypeValue() {
        return transactionTypeValue;
    }

    @Transient
    public String getFormattedAmount(BigDecimal value) {
        String formattedNumber = "";
        if (value != null) {
            NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            formattedNumber = numberFormat.format(value);
        }
        return formattedNumber;
    }

    @Transient
    public String getFormattedAmount(String value) {
        return getFormattedAmount(new BigDecimal(value));
    }

    public String getTransactionTypeId() {
        return transactionTypeId;
    }

    public void setTransactionTypeId(String transactionTypeId) {
        this.transactionTypeId = transactionTypeId;
    }

    public String getTransactionTypeDesc() {
        return transactionTypeDesc;
    }

    public void setTransactionTypeDesc(String transactionTypeDesc) {
        this.transactionTypeDesc = transactionTypeDesc;
    }

    public String getCurrencyCode() {
        return (this.getCurrency() != null) ? this.getCurrency().getCode() : "";
    }

    public void setCurrencyCode(String currencyCode) {
        if (this.getCurrency() != null) {
            this.getCurrency().setCode(currencyCode);
        }
    }

    public String getTransactionAmount() {
        return getFormattedAmount(this.getAmount());
    }

    public void setTransactionAmount(String transactionAmount) {
        double value = Double.parseDouble(transactionAmount);
        this.setAmount(BigDecimal.valueOf(value));
    }


    public void setTransactionAmountAllocated(String transactionAmountAllocated) {
        double value = Double.parseDouble(transactionAmountAllocated);
        this.setAllocatedAmount(BigDecimal.valueOf(value));
    }

    public String getTransactionAmountAllocatedLocked() {
        return getFormattedAmount(this.getLockedAllocatedAmount());
    }

    public void setTransactionAmountAllocatedLocked(String transactionAmountAllocatedLocked) {
        double value = Double.parseDouble(transactionAmountAllocatedLocked);
        this.setLockedAllocatedAmount(BigDecimal.valueOf(value));
    }

    public String getRollupDesc() {
        return rollupDesc;
    }

    public void setRollupDesc(String rollupDesc) {
        this.rollupDesc = rollupDesc;
    }

    public String getTransactionTypeSubCode() {
        return transactionTypeSubCode;
    }

    public String getRollupTag() {
        return rollupTag;
    }

    public void setRollupTag(String rollupTag) {
        this.rollupTag = rollupTag;
    }

    public void setTransactionTypeSubCode(String transactionTypeSubCode) {
        this.transactionTypeSubCode = transactionTypeSubCode;
    }

    public String getDebitPriority() {
        return debitPriority;
    }

    public void setDebitPriority(String debitPriority) {
        this.debitPriority = debitPriority;
    }

    public String getDeferredId() {
        return deferredId;
    }

    public void setDeferredId(String deferredId) {
        this.deferredId = deferredId;
    }

    public Date getPaymentClearDate() {
        return paymentClearDate;
    }

    public void setPaymentClearDate(Date paymentClearDate) {
        this.paymentClearDate = paymentClearDate;
    }

    public String getRefundRule() {
        return refundRule;
    }

    public void setRefundRule(String refundRule) {
        this.refundRule = refundRule;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getGeneralLedgerTypeId() {
        return generalLedgerTypeId;
    }

    public void setGeneralLedgerTypeId(String generalLedgerTypeId) {
        this.generalLedgerTypeId = generalLedgerTypeId;
    }

    public String getGlOverRidden() {
        return glOverRidden;
    }

    public void setGlOverRidden(String glOverRidden) {
        this.glOverRidden = glOverRidden;
    }

    public String getPaymentBilling() {
        return paymentBilling;
    }

    public void setPaymentBilling(String paymentBilling) {
        this.paymentBilling = paymentBilling;
    }

    public String getEntryGenerated() {
        return entryGenerated;
    }

    public void setEntryGenerated(String entryGenerated) {
        this.entryGenerated = entryGenerated;
    }

    public String getAllocationLocked() {
        return allocationLocked;
    }

    public void setAllocationLocked(String allocationLocked) {
        this.allocationLocked = allocationLocked;
    }

    // aggregates
    public String getRollUpBalance() {
        return getFormattedAmount(rollUpBalance);
    }

    public void setRollUpBalance(String rollUpBalance) {
        this.rollUpBalance = rollUpBalance;
    }

    public String getRollUpDebit() {
        return getFormattedAmount(rollUpDebit);
    }

    public void setRollUpDebit(String rollUpDebit) {
        this.rollUpDebit = rollUpDebit;
    }

    public String getRollUpCredit() {
        return getFormattedAmount(rollUpCredit);
    }

    public void setRollUpCredit(String rollUpCredit) {
        this.rollUpCredit = rollUpCredit;
    }

    public String getUnGroupedBalance() {
        return getFormattedAmount(unGroupedBalance);
    }

    public void setUnGroupedBalance(String unGroupedBalance) {
        this.unGroupedBalance = unGroupedBalance;
    }

    public String getUnGroupedDebit() {
        return getFormattedAmount(unGroupedDebit);
    }

    public void setUnGroupedDebit(String unGroupedDebit) {
        this.unGroupedDebit = unGroupedDebit;
    }

    public String getUnGroupedCredit() {
        return getFormattedAmount(unGroupedCredit);
    }

    public void setUnGroupedCredit(String unGroupedCredit) {
        this.unGroupedCredit = unGroupedCredit;
    }

    public String getUnGroupedTotalCredit() {
        return getFormattedAmount(unGroupedTotalCredit);
    }

    public void setUnGroupedTotalCredit(String unGroupedTotalCredit) {
        this.unGroupedTotalCredit = unGroupedTotalCredit;
    }

    public List<TransactionModel> getSubTransactions() {
        if (this.subTransactions == null) {
            this.subTransactions = new ArrayList<TransactionModel>();
        }
        return subTransactions;
    }

    public void setSubTransactions(List<TransactionModel> subTransactions) {
        this.subTransactions = subTransactions;
    }

    public void addSubTransaction(TransactionModel transaction) {

        if (subTransactions == null) {
            subTransactions = new ArrayList<TransactionModel>();
        }
        subTransactions.add(transaction);

        setAmount(add(getAmount(), transaction.getAmount()));
        setChargeAmount(add(getChargeAmount(), transaction.getChargeAmount()));
        setPaymentAmount(add(getPaymentAmount(), transaction.getPaymentAmount()));
        setDefermentAmount(add(getDefermentAmount(), transaction.getDefermentAmount()));
        setAllocatedAmount(add(getAllocatedAmount(), transaction.getAllocatedAmount()));
        setLockedAllocatedAmount(add(getLockedAllocatedAmount(), transaction.getLockedAllocatedAmount()));
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getDefermentAmount() {
        return defermentAmount;
    }

    public void setDefermentAmount(BigDecimal defermentAmount) {
        this.defermentAmount = defermentAmount;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public BigDecimal getAllocatedLockedAllocated() {
        BigDecimal alloc = this.getAllocatedAmount();
        if (alloc == null) {
            alloc = BigDecimal.ZERO;
        }
        BigDecimal locked = this.getLockedAllocatedAmount();
        if (locked == null) {
            locked = BigDecimal.ZERO;
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            alloc = alloc.multiply(new BigDecimal(-1));
            locked = locked.multiply(new BigDecimal(-1));
        }

        return alloc.add(locked);
    }

    // BigDecimal is not friendly to nulls
    private BigDecimal add(BigDecimal d1, BigDecimal d2) {
        if (d1 == null) {
            return d2;
        }
        if (d2 == null) {
            return d1;
        }
        return d1.add(d2);
    }


    public List<Memo> getMemos() {
        if (memos == null) {
            this.memos = new ArrayList<Memo>();
        }
        return memos;
    }

    public void setMemos(List<Memo> memos) {
        this.memos = memos;
    }

    public Transaction getParentTransaction() {
        return parentTransaction;
    }

    public void setParentTransaction(Transaction parentTransaction) {
        this.parentTransaction = parentTransaction;
    }

    public String getTransactionDisplayType() {

        if (parentTransaction == null) {
            return null;
        }

        switch (parentTransaction.getTransactionTypeValue()) {
            case PAYMENT:
                return "Credit (Payment)";
            case DEFERMENT:
                return "Credit (Deferment)";
            case CHARGE:
                return "Debit (Charge)";
            default:
                return "Generic";
        }
    }

    public boolean isPayment() {
        return parentTransaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT;
    }

    public boolean isDeferment() {
        return parentTransaction.getTransactionTypeValue() == TransactionTypeValue.DEFERMENT;
    }

    public boolean isCharge() {
        return parentTransaction.getTransactionTypeValue() == TransactionTypeValue.CHARGE;
    }

    public String getGlEntryGenerated() {
        return (parentTransaction.isGlEntryGenerated() ? "" : "Not ") + "Generated";
    }

    public String getInternal() {
        return parentTransaction.isInternal().toString();
    }

    public String getGlOverridden() {
        return parentTransaction.isGlOverridden().toString();
    }

    public String getTagList() {
        return tagList;
    }

    public List<Tag> getTagsList() {
        List<Tag> tagsList = new ArrayList<Tag>();
        if (tags != null) {
            for (TransactionTagModel model : tags) {
                tagsList.add(model.getTag());
            }
        }

        return tagsList;
    }

    public void setTagsList(List<Tag> tags) {

        this.tags = new ArrayList<TransactionTagModel>();

        if (tags != null) {
            for (Tag tag : tags) {
                TransactionTagModel model = new TransactionTagModel();
                model.setTransactionId(parentTransaction.getId());
                model.setTag(tag);
                this.tags.add(model);
            }
        }
        // more happens in the setter.
        this.setTagModels(this.tags);
    }


    public List<TransactionTagModel> getTagModels() {
        if (tags == null) {
            tags = new ArrayList<TransactionTagModel>();
        }
        return tags;
    }

    public void setTagModels(List<TransactionTagModel> tags) {

        this.tags = tags;

        if (tags == null || tags.size() == 0) {
            this.tags = new ArrayList<TransactionTagModel>();
            this.tagList = "None";
            return;
        }

        StringBuilder tagList = new StringBuilder();
        boolean first = true;
        for (TransactionTagModel tag : tags) {
            if (!first) {
                tagList.append(", ");
            } else {
                first = false;
            }
            tagList.append(tag.getTag().getCode());
        }

        this.tagList = tagList.toString();
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
    }

    public void addAlert(Alert a) {
        if (this.alerts == null) {
            this.alerts = new ArrayList<Alert>();
        }
        this.alerts.add(a);
    }

    public List<Flag> getFlags() {
        return flags;
    }

    public void setFlags(List<Flag> flags) {
        this.flags = flags;
    }

    public void addFlag(Flag f) {
        if (flags == null) {
            flags = new ArrayList<Flag>();
        }
        flags.add(f);
    }

    public BigDecimal getRunningBalance() {
        return runningBalance;
    }

    public void setRunningBalance(BigDecimal runningBalance) {
        this.runningBalance = runningBalance;
    }

    public Tree<Memo, String> getMemoTree() {
        logger.info("TJB: Returning MemoTree.  Memos size: " + memos.size());
        Tree<Memo, String> memoTree = new Tree<Memo, String>();

        Node<Memo, String> rootNode = new Node<Memo, String>(new Memo(), "Root");
        memoTree.setRootElement(rootNode);

        if (this.memos == null) {
            return memoTree;
        }

        // Need to put the memos in order
        for (Memo memo : memos) {
            rootNode.addChild(new Node<Memo, String>(memo, memo.getDisplayValue()));
        }

        return memoTree;
    }

    public List<AllocationModel> getAllocations() {
        return allocations;
    }

    public void setAllocations(List<Allocation> alloc) {
        this.allocations = new ArrayList<AllocationModel>(alloc.size());
        for (Allocation a : alloc) {
            allocations.add(new AllocationModel(this.parentTransaction, a));
        }
    }

    public Date getClearDate() {
        if (parentTransaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT) {
            return ((Payment) parentTransaction).getClearDate();
        }
        return null;
    }

    public boolean isPaymentRefundable() {
        if (parentTransaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT) {
            Payment payment = (Payment) parentTransaction;

            // status must be active.
            if (!TransactionStatus.ACTIVE.equals(payment.getStatus())) {
                return false;
            }

            // No negative or zero amounts are refundable
            if (payment.getAmount().compareTo(BigDecimal.ZERO) < 1) {
                return false;
            }

            // has an unallocated balance
            if (payment.getUnallocatedAmount().compareTo(BigDecimal.ZERO) < 1) {
                return false;
            }

            // clear date in the past
            if (payment.getClearDate().after(new Date())) {
                return false;
            }

            return payment.isRefundable();
        }
        return false;

    }

    public String getPaymentRefundable() {
        if (parentTransaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT) {
            return ((Payment) parentTransaction).isRefundable().toString();
        }
        return null;
    }

    public String getPaymentRefundRule() {
        if (parentTransaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT) {
            return ((Payment) parentTransaction).getRefundRule();
        }
        return null;
    }

    public BigDecimal getOriginalAmount() {
        if (parentTransaction.getTransactionTypeValue() == TransactionTypeValue.DEFERMENT) {
            return ((Deferment) parentTransaction).getOriginalAmount();
        }
        return null;
    }

    public Date getDefermentExpirationDate() {
        if (parentTransaction.getTransactionTypeValue() == TransactionTypeValue.DEFERMENT) {
            return ((Deferment) parentTransaction).getExpirationDate();
        }
        return null;
    }

    public String getChargeCancellationRule() {
        if (parentTransaction.getTransactionTypeValue() == TransactionTypeValue.CHARGE) {
            return ((Charge) parentTransaction).getCancellationRule();
        }
        return null;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNewTag() {
        return newTag;
    }

    public void setNewTag(String newTag) {
        this.newTag = newTag;
    }

    public BigDecimal getNewAllocation() {
        return newAllocation;
    }

    public void setNewAllocation(BigDecimal newAllocation) {
        this.newAllocation = newAllocation;
    }

    public String getStatusString() {
        if (parentTransaction != null) {
            return parentTransaction.getStatus().toString();
        }
        return "";
    }

    public void setStatusString(String status) {
        if (parentTransaction != null) {
            TransactionStatus transactionStatus = EnumUtils.findById(TransactionStatus.class, status);
            parentTransaction.setStatus(transactionStatus);
        }
    }

    public String getReverseTransactionReason() {
        return reverseTransactionReason;
    }

    public void setReverseTransactionReason(String reverseTransactionReason) {
        this.reverseTransactionReason = reverseTransactionReason;
    }

    public boolean getReverseTransactionInternalOnly() {
        return reverseTransactionInternalOnly;
    }

    public void setReverseTransactionInternalOnly(boolean reverseTransactionInternalOnly) {
        this.reverseTransactionInternalOnly = reverseTransactionInternalOnly;
    }

    public boolean isReversible() {
        return !(getUnallocatedAmount().compareTo(BigDecimal.ZERO) <= 0) &&
                !TransactionStatus.REFUND_REQUESTED.equals(parentTransaction.getStatus());
    }

    public boolean isRefundRequested() {
        return (TransactionStatus.REFUND_REQUESTED.equals(parentTransaction.getStatus()));
    }
}

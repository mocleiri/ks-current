package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.Memo;
import com.sigmasys.kuali.ksa.model.TransactionTransfer;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingPlan;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingQueue;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingSchedule;
import com.sigmasys.kuali.ksa.model.pb.PaymentBillingTransferDetail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: tbornholtz
 * Date: 12/10/13
 */
public class PaymentBillingTransferDetailModel {

    private PaymentBillingTransferDetail paymentBillingTransferDetail;

    private List<PaymentBillingQueue> paymentBillingQueues;

    private List<TransactionTransfer> transactionTransfers;

    private List<PaymentBillingSchedule> paymentBillingSchedules;

    private Memo changeMemo;

    public PaymentBillingTransferDetailModel() {

    }

    public PaymentBillingTransferDetailModel(PaymentBillingTransferDetail paymentBillingTransferDetail) {
        setPaymentBillingTransferDetail(paymentBillingTransferDetail);
    }

    public PaymentBillingTransferDetail getPaymentBillingTransferDetail() {
        return paymentBillingTransferDetail;
    }

    public void setPaymentBillingTransferDetail(PaymentBillingTransferDetail paymentBillingTransferDetail) {
        this.paymentBillingTransferDetail = paymentBillingTransferDetail;
    }

    public Long getId() {
        if(paymentBillingTransferDetail != null) {
            return paymentBillingTransferDetail.getId();
        }
        return -1L;
    }

    public PaymentBillingPlan getPlan() {
        if(paymentBillingTransferDetail != null) {
            return paymentBillingTransferDetail.getPlan();
        }
        if(paymentBillingQueues.size() > 0) {
            return paymentBillingQueues.get(0).getPlan();
        }
        return null;
    }

    public void setPlan(PaymentBillingPlan plan) {
        if(paymentBillingTransferDetail != null) {
            paymentBillingTransferDetail.setPlan(plan);
        }
    }

    public List<TransactionTransfer> getTransactionTransfers() {
        if(transactionTransfers == null) {
            transactionTransfers = new ArrayList<TransactionTransfer>();
        }
        return transactionTransfers;
    }

    public void setTransactionTransfers(List<TransactionTransfer> transactionTransfers) {
        this.transactionTransfers = transactionTransfers;
    }

    public Memo getChangeMemo() {
        if(changeMemo == null) {
            changeMemo = new Memo();
        }
        return changeMemo;
    }

    public void setChangeMemo(Memo changeMemo) {
        this.changeMemo = changeMemo;
    }

    public List<PaymentBillingQueue> getPaymentBillingQueues() {
        if(paymentBillingQueues == null) {
            paymentBillingQueues = new ArrayList<PaymentBillingQueue>();
        }
        return paymentBillingQueues;
    }

    public void setPaymentBillingQueues(List<PaymentBillingQueue> paymentBillingQueues) {
        this.paymentBillingQueues = paymentBillingQueues;
    }

    public void addPaymentBillingQueue(PaymentBillingQueue paymentBillingQueue) {
        getPaymentBillingQueues().add(paymentBillingQueue);
    }


    public Date getInitiationDate() {
        if(paymentBillingTransferDetail != null) {
            return paymentBillingTransferDetail.getInitiationDate();
        }
        if(paymentBillingQueues.size() > 0) {
            return paymentBillingQueues.get(0).getInitiationDate();
        }
        return null;
    }

    public BigDecimal getMaxAmount() {
        if(paymentBillingTransferDetail != null) {
            return paymentBillingTransferDetail.getMaxAmount();
        }
        return null;
    }

    public BigDecimal getPlanAmount() {
        if(paymentBillingTransferDetail != null) {
            return paymentBillingTransferDetail.getPlanAmount();
        }
        return null;
    }

    public BigDecimal getFlatFeeChargeAmount() {
        if(paymentBillingTransferDetail != null) {
            return paymentBillingTransferDetail.getFlatFeeCharge().getAmount();
        }
        return null;
    }

    public BigDecimal getVariableFeeChargeAmount() {
        if(paymentBillingTransferDetail != null) {
            return paymentBillingTransferDetail.getVariableFeeCharge().getAmount();
        }
        return null;
    }

    public String getChargeStatus() {
        if(paymentBillingTransferDetail != null) {
            return paymentBillingTransferDetail.getChargeStatus().toString();
        }
        if(paymentBillingQueues.size() > 0) {
            return "Queued";
        }
        return "";
    }

    public List<PaymentBillingSchedule> getPaymentBillingSchedules() {
        if(paymentBillingSchedules == null) {
            paymentBillingSchedules = new ArrayList<PaymentBillingSchedule>();
        }
        return paymentBillingSchedules;
    }

    public void setPaymentBillingSchedules(List<PaymentBillingSchedule> paymentBillingSchedules) {
        this.paymentBillingSchedules = paymentBillingSchedules;
    }
}

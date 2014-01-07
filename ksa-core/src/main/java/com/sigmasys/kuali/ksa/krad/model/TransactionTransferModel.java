package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.TransactionTransfer;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyTransferDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * User: tbornholtz
 * Date: 9/18/13
 */
public class TransactionTransferModel {

    private ThirdPartyTransferDetail thirdPartyTransferDetail;

    private List<TransactionTransfer> transactionTransfers;


    public ThirdPartyTransferDetail getThirdPartyTransferDetail() {
        return thirdPartyTransferDetail;
    }

    public void setThirdPartyTransferDetail(ThirdPartyTransferDetail thirdPartyTransferDetail) {
        this.thirdPartyTransferDetail = thirdPartyTransferDetail;
    }

    public String getDirectChargeAccountName() {
        if(thirdPartyTransferDetail != null) {
            return thirdPartyTransferDetail.getDirectChargeAccount().getCompositeDefaultPersonName();
        }
        return "Unknown";
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
}

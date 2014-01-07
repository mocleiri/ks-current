package com.sigmasys.kuali.ksa.krad.model;


import com.sigmasys.kuali.ksa.model.Allocation;
import com.sigmasys.kuali.ksa.model.Memo;
import com.sigmasys.kuali.ksa.model.Transaction;

import java.io.Serializable;
import java.util.Date;


public class AllocationModel implements Serializable {

    private Allocation allocation;

    private Transaction parentTransaction;
    private Transaction allocatedTransaction;

    private Memo memoModel;

    public AllocationModel() {
    }

    public AllocationModel(Transaction t, Allocation a) {
        this.parentTransaction = t;
        this.allocation = a;
        this.setAllocatedTransaction();
    }

    public Allocation getAllocation() {
        return allocation;
    }

    public void setAllocation(Allocation allocation) {
        this.allocation = allocation;
        this.setAllocatedTransaction();
    }

    public Transaction getParentTransaction() {
        return parentTransaction;
    }

    public void setParentTransaction(Transaction parentTransaction) {
        this.parentTransaction = parentTransaction;
        this.setAllocatedTransaction();
    }

    public Transaction getAllocatedTransaction() {
        return allocatedTransaction;
    }

    public void setAllocatedTransaction(Transaction allocatedTransaction) {
        this.allocatedTransaction = allocatedTransaction;
    }

    private void setAllocatedTransaction() {
        if (this.allocation != null && this.parentTransaction != null) {
            if (this.parentTransaction.equals(this.allocation.getFirstTransaction())) {
                this.allocatedTransaction = this.allocation.getSecondTransaction();
            } else {
                this.allocatedTransaction = this.allocation.getFirstTransaction();
            }
        }
    }

    public Memo getMemoModel() {
        if(memoModel == null){
            memoModel = new Memo();
            memoModel.setEffectiveDate(new Date());
        }
        return memoModel;
    }

    public void setMemoModel(Memo memoModel) {
        this.memoModel = memoModel;
    }
}

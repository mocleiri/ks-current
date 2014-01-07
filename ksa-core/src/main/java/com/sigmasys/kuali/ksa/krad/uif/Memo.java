package com.sigmasys.kuali.ksa.model;

import javax.persistence.*;

/**
 * Memo entity.
 *
 * @author Michael Ivanov
 */
@Entity
@DiscriminatorValue(InformationTypeValue.MEMO_CODE)
public class Memo extends Information {

    /**
     * Follow up memo
     */
    private Memo nextMemo;

    /**
     * Prior memo
     */
    private Memo previousMemo;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NEXT_ID")
    public Memo getNextMemo() {
        return nextMemo;
    }

    public void setNextMemo(Memo nextMemo) {
        this.nextMemo = nextMemo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PREV_ID")
    public Memo getPreviousMemo() {
        return previousMemo;
    }

    public void setPreviousMemo(Memo previousMemo) {
        this.previousMemo = previousMemo;
    }
}


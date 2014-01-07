package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.Information;
import com.sigmasys.kuali.ksa.model.Memo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: tbornholtz
 * Date: 9/10/13
 */
public class MemoModel extends InformationModel {

    private List<MemoModel> memoModels;

    private MemoModel followupMemoModel;

    public MemoModel() {

    }

    public MemoModel(Memo memo) {
        this.setParentEntity(memo);
    }

    public List<MemoModel> getMemoModels() {
        if(memoModels == null) {
            memoModels = new ArrayList<MemoModel>();
        }
        return memoModels;
    }

    public void setMemoModels(List<MemoModel> memoModels) {
        this.memoModels = memoModels;
    }

    public MemoModel getFollowupMemoModel() {
        if(followupMemoModel == null) {
            followupMemoModel = new MemoModel();
            Memo memo = new Memo();
            Information parent = this.getParentEntity();
            if(parent != null) {
                memo.setEffectiveDate(new Date());
                memo.setExpirationDate(parent.getExpirationDate());
            }
            followupMemoModel.setParentEntity(memo);
        }
        return followupMemoModel;
    }

    public void setFollowupMemoModel(MemoModel followupMemoModel) {
        this.followupMemoModel = followupMemoModel;
    }
}

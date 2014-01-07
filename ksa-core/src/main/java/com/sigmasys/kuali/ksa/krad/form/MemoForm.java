package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.krad.model.MemoModel;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.Memo;
import com.sigmasys.kuali.ksa.model.Transaction;

import java.util.*;

public class MemoForm extends AbstractViewModel {


    private Account account;

    private Date fromDate;

    private Date toDate;

    private List<MemoModel> memoModels;

    private Memo memoModel;

    private MemoModel newMemoModel;

    // resuable add edit or followup instructional text
    private String aefInstructionalText;
    /*
      Get / Set methods
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

    public void setMemos(List<Memo> memos) {
        this.memoModels = new ArrayList<MemoModel>();
        Map<Long, MemoModel> idList = new HashMap<Long, MemoModel>();

        for(Memo m : memos) {
            logger.error("Memo: " + m.getId() + " Previous: " + (m.getPreviousMemo() == null ? "null" : m.getPreviousMemo().getId()));
        }
        // Sort the memos by previous memo's ID where null will be sorted first.
        // This gets all of the parent memos that have no other parent memos first

        Collections.sort(memos, new Comparator<Memo>(){
            public int compare(Memo m1, Memo m2) {
                if(m1.getPreviousMemo() == null && m2.getPreviousMemo() == null) {
                    return m1.getId().compareTo(m2.getId());
                } else if(m1.getPreviousMemo() == null && m2.getPreviousMemo() != null) {
                    return -1;
                } else if(m1.getPreviousMemo() != null && m2.getPreviousMemo() == null) {
                    return 1;
                } else {
                    return m1.getPreviousMemo().getId().compareTo(m2.getPreviousMemo().getId());
                }
            }
        });

        for(Memo m : memos) {
            logger.error("Memo: " + m.getId() + " Previous: " + (m.getPreviousMemo() == null ? "null" : m.getPreviousMemo().getId()));
        }

        // List for holding the child memos that came in before the parent
        List<Memo> childMemos = new ArrayList<Memo>();

        for(Memo memo : memos) {
            Memo previousMemo = memo.getPreviousMemo();

            // Make sure the rest of the data is retrieved
            memo.getAccount().getCompositeDefaultPersonName();
            Transaction t = memo.getTransaction();
            if(t != null) {
                t.getTransactionType().getDescription();
            }

            MemoModel model = new MemoModel(memo);

            if(previousMemo == null) {
                memoModels.add(model);
                idList.put(memo.getId(), model);
            } else if(!idList.keySet().contains(previousMemo.getId())){
                childMemos.add(memo);
            } else {
                MemoModel parent = idList.get(previousMemo.getId());
                List<MemoModel> parentModels = parent.getMemoModels();
                parentModels.add(model);
            }

        }


        Collections.sort(memoModels, new Comparator<MemoModel>(){
            public int compare(MemoModel m1, MemoModel m2) {
                return m2.getEffectiveDate().compareTo(m1.getEffectiveDate());
            }
        });

        /*
        for(Memo child : childMemos) {
            Long previousId = child.getPreviousMemo().getId();
            MemoModel parent = idList.get(previousId);
            if(parent != null) {
                List<MemoModel> parentModels = parent.getMemoModels();
                MemoModel model = new MemoModel(child);
                parentModels.add(model);
            }
        }
        */
    }

    public List<MemoModel> getMemoModels() {
        return memoModels;
    }

    public void setMemoModels(List<MemoModel> memoModels) {
        this.memoModels = memoModels;
    }

    public Memo getMemoModel() {
        return memoModel;
    }

    public void setMemoModel(Memo memoModel) {
        this.memoModel = memoModel;
    }

    public String getAefInstructionalText() {
        return aefInstructionalText;
    }

    public void setAefInstructionalText(String aefInstructionalText) {
        this.aefInstructionalText = aefInstructionalText;
    }

    public MemoModel getNewMemoModel() {
        if(newMemoModel == null) {
            newMemoModel = new MemoModel();
            Memo memo = new Memo();
            memo.setEffectiveDate(new Date());
            newMemoModel.setParentEntity(memo);
        }
        return newMemoModel;
    }

    public void setNewMemoModel(MemoModel newMemoModel) {
        this.newMemoModel = newMemoModel;
    }
}

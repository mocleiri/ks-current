package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.MemoForm;
import com.sigmasys.kuali.ksa.krad.model.MemoModel;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.InformationAccessLevel;
import com.sigmasys.kuali.ksa.model.Memo;
import com.sigmasys.kuali.ksa.service.InformationService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * TODO -> handle InformationAccessLevel logic
 * <p/>
 * Created by: dmulderink on 10/4/12 at 7:53 AM
 */
@Controller
@RequestMapping(value = "/memoView")
public class MemoController extends GenericSearchController {


    @Autowired
    private InformationService informationService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected MemoForm createInitialForm(HttpServletRequest request) {

        MemoForm form = new MemoForm();

        String userId = request.getParameter("userId");

        if (userId != null) {

            Account account = accountService.getFullAccount(userId);

            if (account == null) {
                String errMsg = "Cannot find Account by ID = " + userId;
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            form.setAccount(account);
        }

        return form;
    }

    /**
     * @param form    MemoForm
     * @param request HttpServletRequest
     * @return MemoForm
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") MemoForm form, HttpServletRequest request) {

        String viewId = request.getParameter("viewId");
        String pageId = request.getParameter("pageId");
        String userId = request.getParameter("userId");
        String memoId = request.getParameter("memoId");

        logger.info("View: " + viewId + " User: " + userId + " Memo ID: " + memoId);

        if (pageId != null && pageId.equals("MemosPage")) {
            if (userId == null || userId.isEmpty()) {
                throw new IllegalArgumentException("'userId' request parameter must be specified");
            }

            List<Memo> memos = informationService.getMemos(userId);

            form.setMemos(memos);

        } else if (pageId != null && pageId.equals("AddMemoPage")) {
            if (userId == null || userId.isEmpty()) {
                throw new IllegalArgumentException("'userId' request parameter must be specified");
            }

            // create a Memo, set defaults for the view
            Account account = accountService.getFullAccount(userId);
            String accountId = account.getId();

            // don't create a persistent memo when adding a memo, jus0t initialize one for use
            // rather persist when the user submits (button control) an insert on the memo
            Memo memo = new Memo();
            memo.setAccount(account);
            memo.setAccountId(accountId);
            memo.setEffectiveDate(new Date());
            memo.setText("");
            memo.setAccessLevel(new InformationAccessLevel());

            form.setMemoModel(memo);

            form.setAefInstructionalText("Add a memo");

        } else if (pageId != null && pageId.equals("ViewMemoPage")) {
            if (userId == null || userId.isEmpty()) {
                throw new IllegalArgumentException("'userId' request parameter must be specified");
            }

            try {
                Memo memo = informationService.getMemo(new Long(memoId));
                if (memo != null) {
                    form.setMemoModel(memo);
                }
            } catch (Exception exp) {
                String errMsg = "'memoId' request parameter cannot be null";
                logger.error(errMsg);
            }

            form.setAefInstructionalText("View a memo");

        } else if (pageId != null && pageId.equals("EditMemoPage")) {

            if (userId == null || userId.isEmpty()) {
                throw new IllegalArgumentException("'userId' request parameter must be specified");
            }

            try {
                Memo memo = informationService.getMemo(new Long(memoId));
                if (memo != null) {
                    form.setMemoModel(memo);
                }
            } catch (Exception exp) {
                String errMsg = "'memoId' request parameter cannot be null";
                logger.error(errMsg);
            }

            form.setAefInstructionalText("Edit a memo");

        } else if (pageId != null && pageId.equals("FollowUpMemoPage")) {
            if (userId == null || userId.isEmpty()) {
                throw new IllegalArgumentException("'userId' request parameter must be specified");
            }

            try {
                Memo memo = informationService.getMemo(new Long(memoId));
                if (memo != null) {
                    form.setMemoModel(memo);
                }
            } catch (Exception exp) {
                String errMsg = "'memoId' request parameter cannot be null";
                logger.error(errMsg);
            }

            form.setAefInstructionalText("Follow-up a memo");
        }

        return getUIFModelAndView(form);
    }

    /**
     * @param form    MemoForm
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=insertMemo")
    public ModelAndView insertMemo(@ModelAttribute("KualiForm") MemoForm form, HttpServletRequest request) {

        String viewId = request.getParameter("viewId");
        String parentMemoId = request.getParameter("actionParameters[parentMemoID]");

        Memo parentMemo = null;
        Long parentId = null;

        if (parentMemoId != null) {

            // This is a follow up memo.
            try {
                parentId = Long.parseLong(parentMemoId);
            } catch (NumberFormatException e) {
                GlobalVariables.getMessageMap().putError("MemoView", RiceKeyConstants.ERROR_CUSTOM, "Invalid parent memo ID: '" + parentMemoId + "'");
                return getUIFModelAndView(form);
            }
            parentMemo = informationService.getMemo(parentId);
        }

        String accountId = form.getAccount().getId();

        if (accountId == null) {
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Error determining userid for memo");
            return getUIFModelAndView(form);
        }

        logger.info("View: " + viewId + " User: " + accountId);
        MemoModel memoModel = null;
        if (parentMemo == null) {
            memoModel = form.getNewMemoModel();
        } else {
            // loop through the form's memos and find the right one
            for (MemoModel model : form.getMemoModels()) {
                if (parentId.equals(model.getId())) {
                    memoModel = model.getFollowupMemoModel();
                    break;
                }
            }
        }

        if (memoModel == null) {
            // something went wrong here
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Unable to find parent memo.");
            return getUIFModelAndView(form);
        }

        String memoText = memoModel.getText();

        String accessLevelCode = "DEF_MEMO_LEVEL_CD";

        Date effectiveDate = memoModel.getEffectiveDate();
        Date expirationDate = memoModel.getExpirationDate();

        try {

            Memo memo = informationService.createMemo(accountId, memoText, accessLevelCode, effectiveDate, expirationDate, parentId);

            Long persistResult = informationService.persistInformation(memo);

            if (persistResult >= 0) {

                String statusMsg = "Memo saved";
                GlobalVariables.getMessageMap().putInfo("MemoView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);

                List<Memo> memos = informationService.getMemos(accountId);

                form.setMemos(memos);

                form.setNewMemoModel(null);

            } else {
                String failedMsg = "Failed to add memo. result code: " + persistResult.toString();
                GlobalVariables.getMessageMap().putError("MemoView", RiceKeyConstants.ERROR_CUSTOM, failedMsg);
            }

        } catch (Exception exp) {
            String errMsg = "'Failed to add memo. " + exp.getLocalizedMessage();
            GlobalVariables.getMessageMap().putError("MemoView", RiceKeyConstants.ERROR_CUSTOM, errMsg);
            logger.error(errMsg);
        }

        return getUIFModelAndView(form);
    }

    /**
     * @param form    MemoForm
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=updateMemo")
    public ModelAndView updateMemo(@ModelAttribute("KualiForm") MemoForm form, HttpServletRequest request) {

        String viewId = request.getParameter("viewId");
        String userId = request.getParameter("actionParameters[userId]");
        String memoIdString = request.getParameter("actionParameters[memoId]");

        Long memoId;

        try {
            memoId = Long.parseLong(memoIdString);
        } catch (NumberFormatException e) {
            GlobalVariables.getMessageMap().putError("MemoView", RiceKeyConstants.ERROR_CUSTOM, "Memo with id " + memoIdString + " is not valid");
            return getUIFModelAndView(form);
        }

        logger.info("View: " + viewId + " User: " + userId + " Memo ID: " + memoId);

        Memo updatedMemo = null;

        for (MemoModel model : form.getMemoModels()) {

            if (model.getId().equals(memoId)) {
                updatedMemo = (Memo) model.getParentEntity();
            } else {
                for (MemoModel childModel : model.getMemoModels()) {
                    if (childModel.getId().equals(memoId)) {
                        updatedMemo = (Memo) childModel.getParentEntity();
                        break;
                    }
                }
            }

            if (updatedMemo != null) {
                break;
            }
        }

        if (updatedMemo != null) {
            informationService.persistInformation(updatedMemo);
            GlobalVariables.getMessageMap().putInfo("MemoView", RiceKeyConstants.ERROR_CUSTOM, "Memo updated");
        } else {
            GlobalVariables.getMessageMap().putError("MemoView", RiceKeyConstants.ERROR_CUSTOM, "Memo with id " + memoIdString + " not found");
        }

        return getUIFModelAndView(form);
    }

    /**
     * @param form    MemoForm
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=followUpMemo")
    public ModelAndView followUpMemo(@ModelAttribute("KualiForm") MemoForm form, HttpServletRequest request) {

        String viewId = request.getParameter("viewId");
        String userId = request.getParameter("actionParameters[userId]");
        String memoId = request.getParameter("actionParameters[memoId]");

        logger.info("View: " + viewId + " User: " + userId + " Memo ID: " + memoId);

        this.updateMemo(form, memoId);

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=expireMemo")
    public ModelAndView expireMemo(@ModelAttribute("KualiForm") MemoForm form, HttpServletRequest request) {

        String memoIdString = request.getParameter("actionParameters[memoId]");
        Long memoId;

        try {
            memoId = Long.parseLong(memoIdString);
        } catch (NumberFormatException e) {
            String failedMsg = "Invalid Memo: " + memoIdString;
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, failedMsg);
            logger.error(failedMsg);
            return getUIFModelAndView(form);
        }

        String userId = form.getAccount().getId();

        Memo memo = informationService.getMemo(memoId);

        if (userId == null || !userId.equals(memo.getAccount().getId())) {
            String failedMsg = "Memo is not for the selected user";
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, failedMsg);
            logger.error(failedMsg);
            return getUIFModelAndView(form);
        }

        memo.setExpirationDate(new Date());

        informationService.persistInformation(memo);

        List<Memo> memos = informationService.getMemos(userId);
        form.setMemos(memos);

        GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, "Memo expired");

        return getUIFModelAndView(form);
    }


    private void updateMemo(MemoForm form, String memoId) {

        try {

            Memo memo = informationService.getMemo(new Long(memoId));

            if (memo != null) {

                Memo memoModel = form.getMemoModel();
                InformationAccessLevel accessLevel = memoModel.getAccessLevel();
                memo.setEffectiveDate(memoModel.getEffectiveDate());
                memo.setExpirationDate(memoModel.getExpirationDate());
                memo.setText(memoModel.getText());
                memo.setAccessLevel(accessLevel);

                Long persistResult = informationService.persistInformation(memo);

                if (persistResult >= 0) {
                    GlobalVariables.getMessageMap().putInfo("MemoView", RiceKeyConstants.ERROR_CUSTOM, "Memo updated");
                    logger.info("Successful update of memo number " + memoId);
                } else {
                    String failedMsg = "Failed to update memo number " + memoId + " result code: " + persistResult.toString();
                    GlobalVariables.getMessageMap().putError("MemoView", RiceKeyConstants.ERROR_CUSTOM, failedMsg);
                    logger.error(failedMsg);
                }
            }

            // refresh the view page section
            memo = informationService.getMemo(new Long(memoId));

            if (memo != null) {
                form.setMemoModel(memo);
            }

        } catch (Exception exp) {
            String errMsg = "'memoId' request parameter cannot be null";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

    }
}

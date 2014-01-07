package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.krad.form.TransactionTypeForm;
import com.sigmasys.kuali.ksa.krad.model.GlBreakdownModel;
import com.sigmasys.kuali.ksa.krad.model.TransactionTypeGroupModel;
import com.sigmasys.kuali.ksa.krad.model.TransactionTypeModel;
import com.sigmasys.kuali.ksa.krad.util.AuditableEntityKeyValuesFinder;
import com.sigmasys.kuali.ksa.krad.util.CreditDebitKeyValuesFinder;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.ActivityService;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.GeneralLedgerService;
import com.sigmasys.kuali.ksa.service.TransactionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.*;


/**
 * Created by: dmulderink on 10/10/12 at 8:06 PM
 */
@Controller
@RequestMapping(value = "/transactionTypeView")
public class TransactionTypeController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(TransactionTypeController.class);

    private static final Integer MIN_PRIORITY = 1;
    private static final Integer MAX_PRIORITY = 1000;

    private static final String TAG_GROUP_FIELD = "tagGroup";

    private KeyValuesFinder creditDebitTypeOptionsFinder;

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private GeneralLedgerService glService;

    @Autowired
    private ActivityService activityService;

    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected TransactionTypeForm createInitialForm(HttpServletRequest request) {
        TransactionTypeForm form = new TransactionTypeForm();
        form.setStatusMessage("");
        return form;
    }

    /**
     * @param form    TransactionTypeForm
     * @param request HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") TransactionTypeForm form, HttpServletRequest request) {

        // do get stuff...
        String viewId = request.getParameter("viewId");
        String pageId = request.getParameter("pageId");

        logger.info("View: " + viewId + " Page: " + pageId);

        if ("TransactionTypePage".equals(pageId)) {
            List<TransactionType> entities = auditableEntityService.getAuditableEntities(TransactionType.class);
            logger.info("Transaction Type Count: " + entities.size());

            Map<String, TransactionTypeGroupModel> map = new HashMap<String, TransactionTypeGroupModel>();

            for (TransactionType transactionType : entities) {
                TransactionTypeModel ttModel = new TransactionTypeModel(transactionType);
                String id = transactionType.getId().getId();
                TransactionTypeGroupModel group = map.get(id);
                if (group == null) {
                    group = new TransactionTypeGroupModel();
                    map.put(id, group);
                }


                group.addTransactionType(ttModel);

                ttModel.setGlBreakdowns(glService.getGlBreakdowns(transactionType.getId()));
            }

            form.setTransactionTypeGroups(map);

            //form.setTransactionTypes(entities);
        } else if ("TransactionTypeDetailsPage".equals(pageId)) {
            String entityId = request.getParameter("entityId");
            String subCode = request.getParameter("subCode");

            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }

            if (subCode == null) {
                subCode = "1";
            }

            TransactionTypeId transactionTypeId = new TransactionTypeId(entityId, Integer.parseInt(subCode));

            TransactionType transactionType = transactionService.getTransactionType(transactionTypeId);
            logger.info("Retrieved Transaction Type: " + transactionType.getDescription());

            form.setTransactionType(transactionType);
        }

        return getUIFModelAndView(form);
    }


    /**
     * @param form TransactionTypeForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=create")
    public ModelAndView create(@ModelAttribute("KualiForm") TransactionTypeForm form, HttpServletRequest request) {

        form.reset();
        form.setType(TransactionType.CREDIT_TYPE);

        String modelToCopy = request.getParameter("model");

        if (modelToCopy != null) {
            TransactionType ttSource = transactionService.getTransactionType(modelToCopy, new Date());

            if (ttSource != null) {
                this.loadFormFromTransactionType(form, ttSource, true);
            }
            form.setNewTransactionType(false);
        } else {
            form.setNewTransactionType(true);
        }

        form.setCreditDebitKeyValuesFinder(this.getCreditDebitTypeOptionsFinder());
        form.setRollupOptionsFinder(this.getRollupOptionsFinder());

        return getUIFModelAndView(form);
    }

    /**
     * @param form TransactionTypeForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=insert")
    public ModelAndView insert(@ModelAttribute("KualiForm") TransactionTypeForm form) {

        String type = form.getType();
        String code = form.getCode();

        // If the sub-code is anything other than -1 then this is an edit.
        Integer subCode = form.getSubCode();
        Date startDate = form.getStartDate();
        Integer priority = form.getPriority();

        if (priority == null) {
            priority = 1;
        }

        String description = form.getDescription();

        List<GlBreakdown> breakdowns = new ArrayList<GlBreakdown>();

        // For now, all breakdowns use the default GL Account
        GeneralLedgerType defaultGlType = glService.getDefaultGeneralLedgerType();

        /*
            validate as much as we can before we save anything.
         */
        boolean errors = validateBreakdowns(breakdowns, form, defaultGlType);

        boolean typeExists = transactionService.transactionTypeExists(code);

        if(typeExists && (subCode == null || subCode == -1)) {
            TransactionType previousTransactionType = transactionService.getTransactionType(code, new Date());

            // make sure that this transaction type doesn't start on the same day (or before) the currently existing tt.
            // When this happens all kinds of bad things happen in the system.
            if(previousTransactionType != null && (previousTransactionType.getStartDate().compareTo(startDate) >= 0)) {


                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                String errMsg = "New transaction type cannot start before previous current transaction type start date of '" + df.format(previousTransactionType.getStartDate()) + "'";

                GlobalVariables.getMessageMap().putError("TransactionTypeView", RiceKeyConstants.ERROR_CUSTOM, errMsg);
                errors = true;
            }

        }

        if (errors) {
            return getUIFModelAndView(form);
        }


        TransactionType transactionType;

        if (subCode == null || subCode == -1) {
            if (TransactionType.CREDIT_TYPE.equalsIgnoreCase(type)) {
                if (!typeExists) {
                    transactionType = transactionService.createCreditType(code, "", startDate, priority, description);
                } else {
                    transactionType = transactionService.createCreditSubType(code, startDate);
                    if (!priority.equals(transactionType.getPriority())) {
                        transactionType.setPriority(priority);
                    }
                    if (!description.equals(transactionType.getDescription())) {
                        transactionType.setDescription(description);
                    }
                }

            } else if (TransactionType.DEBIT_TYPE.equalsIgnoreCase(type)) {
                if (!typeExists) {
                    transactionType = transactionService.createDebitType(code, "", startDate, priority, description);
                } else {
                    transactionType = transactionService.createDebitSubType(code, startDate);
                    if (!priority.equals(transactionType.getPriority())) {
                        transactionType.setPriority(priority);
                    }
                    if (!description.equals(transactionType.getDescription())) {
                        transactionType.setDescription(description);
                    }
                }

            } else {
                String errMsg = "Invalid transaction type '" + type + "'";
                logger.error(errMsg);
                GlobalVariables.getMessageMap().putError("TransactionTypeView", RiceKeyConstants.ERROR_CUSTOM, errMsg);
                return getUIFModelAndView(form);
            }
        } else {

            TransactionTypeId ttId = new TransactionTypeId(code, subCode);
            transactionType = transactionService.getTransactionType(ttId);

            // If start date changed, make sure there's an audit message
            if (transactionType.getStartDate() != null && (!form.getStartDate().equals(transactionType.getStartDate()))) {
                String reason = form.getStartDateAuditReason();
                if (reason == null || "".equals(reason)) {
                    String errMsg = "When changing the start date of an existing transaction type, a reason must be provided";
                    logger.error(errMsg);
                    GlobalVariables.getMessageMap().putError("TransactionTypeView", RiceKeyConstants.ERROR_CUSTOM, errMsg);
                    return getUIFModelAndView(form);
                }

                this.persistAudit(form, transactionType);
            }


            transactionType.setStartDate(startDate);
            transactionType.setPriority(priority);
            transactionType.setDescription(description);
        }

        // If there are errors further on and they want to resubmit things, make sure they're modifying the same transactionType, not creating a new subcode.
        form.setSubCode(transactionType.getId().getSubCode());


        List<Tag> tags = form.getTags();

        if (!persistTags(tags)) {
            return getUIFModelAndView(form);
        }

        transactionType.setTags(new HashSet<Tag>(tags));

        Long rollupId;
        try {
            rollupId = new Long(form.getRollupId());
        } catch (NumberFormatException e) {
            rollupId = null;
        }

        if (rollupId != null) {
            Rollup r = auditableEntityService.getAuditableEntity(rollupId, Rollup.class);
            transactionType.setRollup(r);
        }

        TransactionTypeId transactionTypeId = transactionService.persistTransactionType(transactionType);

        if (transactionType instanceof DebitType) {

            ((DebitType) transactionType).setCancellationRule(form.getCancellationRule());
            transactionService.persistTransactionType(transactionType);
        } else {

            CreditType creditType = (CreditType) transactionType;

            String unAllocatedGlOperation = form.getUnallocatedGLOperation();

            GlOperationType unallocatedGlOperationType =
                    (GlOperationType.CREDIT.getId().equals(unAllocatedGlOperation)) ?
                            GlOperationType.CREDIT : GlOperationType.DEBIT;

            creditType.setUnallocatedGlOperation(unallocatedGlOperationType);
            creditType.setUnallocatedGlAccount(form.getUnallocatedGLAccount());
            creditType.setClearPeriod(form.getClearPeriod());
            creditType.setRefundable(form.getRefundable());
            creditType.setRefundRule(form.getRefundRule());
            creditType.setAuthorizationText(form.getAuthorizationText());

            transactionService.persistTransactionType(transactionType);

            List<CreditPermission> creditPermissions = form.getCreditPermissions();
            if (creditPermissions != null && creditPermissions.size() > 0) {
                for (CreditPermission creditPermission : creditPermissions) {
                    Long id = creditPermission.getId();
                    if(id == null) {
                        transactionService.createCreditPermission(transactionType.getId(), creditPermission.getAllowableDebitType(), creditPermission.getPriority());
                    } else {
                        transactionService.persistCreditPermission(creditPermission);
                    }
                }
            }
        }

        for (GlBreakdown breakdown : breakdowns) {

            breakdown.setTransactionType(transactionType);

            // Breakdowns are actually stored at integers (well, greater than 1) so multiply by 100 to get the right number in the database
            breakdown.setBreakdown(breakdown.getBreakdown().multiply(BigDecimal.valueOf(100)));
        }

        glService.createGlBreakdowns(defaultGlType.getId(), transactionTypeId, breakdowns);

        GlobalVariables.getMessageMap().putInfo("TransactionTypeView", RiceKeyConstants.ERROR_CUSTOM, "Transaction Type saved");

        return getUIFModelAndView(form);
    }

    /**
     * @param form TransactionTypeForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=edit")
    public ModelAndView edit(@ModelAttribute("KualiForm") TransactionTypeForm form, HttpServletRequest request) {

        form.reset();
        form.setType(TransactionType.CREDIT_TYPE);

        String modelToCopy = request.getParameter("model");
        Integer subCode = new Integer(request.getParameter("subCode"));

        TransactionTypeId ttId = new TransactionTypeId(modelToCopy, subCode);


        if (modelToCopy != null) {
            TransactionType ttSource = transactionService.getTransactionType(ttId);

            if (ttSource != null) {
                form.setSubCode(ttSource.getId().getSubCode());
                this.loadFormFromTransactionType(form, ttSource, false);
            }

            long number = transactionService.getNumberOfTransactions(ttId);
            form.setTransactionsAffectedCount(number);
        }


        form.setCreditDebitKeyValuesFinder(this.getCreditDebitTypeOptionsFinder());
        form.setRollupOptionsFinder(this.getRollupOptionsFinder());

        return getUIFModelAndView(form);
    }


    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=update")
    public <T extends AuditableEntity> ModelAndView update(@ModelAttribute("KualiForm") TransactionTypeForm form) {

        TransactionTypeModel entity = form.getTransactionType();

        if (entity == null) {
            String errMsg = "TransactionType cannot be null";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        logger.debug("Entity code: " + entity.getCode());

        try {
            // occurs in the detail page.
            auditableEntityService.persistAuditableEntity(entity.getTransactionType());
            // success in updating the currency.
            String statusMsg = "Success: Transaction Type entity updated. Entity ID =  " + entity.getId();
            form.setStatusMessage(statusMsg);
            logger.info(statusMsg);
        } catch (Exception e) {
            // failed to update the currency. Leave the currency information in the view
            String statusMsg = "Failure: Transaction Type entity did not update, Entity ID =  " + entity.getId() + ". " + e.getMessage();
            form.setStatusMessage(statusMsg);
            logger.error(statusMsg);
        }

        return getUIFModelAndView(form);
    }

    /**
     * @param form TransactionTypeForm
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=priority")
    public ModelAndView priority(@ModelAttribute("KualiForm") TransactionTypeForm form, HttpServletRequest request) {

        String modelToCopy = request.getParameter("actionParameters[model]");
        //Integer subCode = new Integer(request.getParameter("actionParameters[subCode]"));

        String priorityChange = request.getParameter("actionParameters[priority]");

        TransactionType tt = transactionService.getTransactionType(modelToCopy, new Date());

        boolean error = false;

        if ("min".equalsIgnoreCase(priorityChange)) {
            tt.setPriority(MIN_PRIORITY);
        } else if ("max".equalsIgnoreCase(priorityChange)) {
            tt.setPriority(MAX_PRIORITY);
        } else if ("plus1".equalsIgnoreCase(priorityChange)) {
            if (tt.getPriority().equals(MAX_PRIORITY)) {
                String message = "Transaction type '" + tt.getId().getId() + "' - " + tt.getDescription() + " is already at the highest priority";
                GlobalVariables.getMessageMap().putError("TransactionTypeView", RiceKeyConstants.ERROR_CUSTOM, message);
                error = true;
            } else {
                tt.setPriority(tt.getPriority() + 1);
            }
        } else if ("minus1".equalsIgnoreCase(priorityChange)) {
            if (tt.getPriority().equals(MIN_PRIORITY)) {
                String message = "Transaction type '" + tt.getId().getId() + "' - " + tt.getDescription() + " is already at the lowest priority";
                GlobalVariables.getMessageMap().putError("TransactionTypeView", RiceKeyConstants.ERROR_CUSTOM, message);
                error = true;
            } else {
                tt.setPriority(tt.getPriority() - 1);
            }
        } else {
            // Try to parse the priorityChange and use that value
            try {
                Integer value = Integer.parseInt(priorityChange);
                tt.setPriority(value);
            } catch (NumberFormatException e) {
                String message = "Invalid priority value";
                GlobalVariables.getMessageMap().putError("TransactionTypeView", RiceKeyConstants.ERROR_CUSTOM, message);
                error = true;
            }
        }

        if (!error) {
            transactionService.persistTransactionType(tt);
            Map<String, TransactionTypeGroupModel> groups = form.getTransactionTypeGroups();
            TransactionTypeGroupModel model = groups.get(modelToCopy);
            model.setCurrentTransactionType(new TransactionTypeModel(tt));
        }

        return getUIFModelAndView(form);

    }

    public synchronized KeyValuesFinder getCreditDebitTypeOptionsFinder() {
        if (creditDebitTypeOptionsFinder == null) {
            creditDebitTypeOptionsFinder = new CreditDebitKeyValuesFinder(true);
        }
        return creditDebitTypeOptionsFinder;
    }

    /*
      * Returns Rollup type option finder.
      */
    public KeyValuesFinder getRollupOptionsFinder() {
        // Don't cache the values finder or else new entries will not show when added

        return new AuditableEntityKeyValuesFinder<Rollup>(Rollup.class);
    }

    /**
     * Loop through all tags in the collection and make sure that they're real tags
     *
     * @param tags list of tags
     * @return true or false
     */
    private boolean persistTags(List<Tag> tags) {
        //If the tag already has an id then it has been previously persisted.
        // If the tag is null then somehow one got added that wasn't really a tag. Check again to be sure.
        for (Tag tag : tags) {
            Long id = tag.getId();
            String name = tag.getName();
            if (id == null) {
                List<Tag> searchTags = auditableEntityService.getAuditableEntitiesByNamePattern(name, Tag.class);
                if (searchTags.size() == 0) {
                    String errorMessage = "'" + name + "' is not a valid tag";
                    GlobalVariables.getMessageMap().putError(TAG_GROUP_FIELD, RiceKeyConstants.ERROR_CUSTOM, errorMessage);
                    return false;
                } else if (searchTags.size() > 1) {
                    String errorMessage = "'" + name + "' matches multiple tags, please select one tag at a time to add";
                    GlobalVariables.getMessageMap().putError(TAG_GROUP_FIELD, RiceKeyConstants.ERROR_CUSTOM, errorMessage);
                    return false;
                }
                tag.setId(searchTags.get(0).getId());
            }

        }
        return true;
    }

    private void loadFormFromTransactionType(TransactionTypeForm form, TransactionType transactionType, boolean createNew) {

        if (transactionType instanceof CreditType) {
            form.setType(TransactionType.CREDIT_TYPE);
            List<CreditPermission> creditPermissions = transactionService.getCreditPermissions(transactionType.getId());
            form.setCreditPermissions(creditPermissions);
        } else {
            form.setType(TransactionType.DEBIT_TYPE);
        }

        form.setCode(transactionType.getId().getId());
        if (createNew) {
            form.setStartDate(new Date());
        } else {
            form.setStartDate(transactionType.getStartDate());
        }
        form.setDescription(transactionType.getDescription());

        if (transactionType.getRollup() != null) {
            form.setRollupId(transactionType.getRollup().getId().toString());
        }

        form.setTags(new ArrayList<Tag>(transactionType.getTags()));

        List<GlBreakdown> sourceBreakdowns = glService.getGlBreakdowns(transactionType.getId());

        List<GlBreakdownModel> breakdowns = new ArrayList<GlBreakdownModel>(sourceBreakdowns.size());

        for (GlBreakdown sourceBreakdown : sourceBreakdowns) {
            GlBreakdownModel breakdownModel = new GlBreakdownModel();
            breakdownModel.setGlAccount(sourceBreakdown.getGlAccount());
            breakdownModel.setOperation(sourceBreakdown.getGlOperation().getId());
            breakdownModel.setBreakdown(sourceBreakdown.getBreakdown().divide(BigDecimal.valueOf(100)));
            breakdowns.add(breakdownModel);
        }

        form.setGlBreakdowns(breakdowns);

        if (transactionType instanceof DebitType) {

            form.setCancellationRule(((DebitType) transactionType).getCancellationRule());


        } else if (transactionType instanceof CreditType) {

            CreditType creditType = (CreditType) transactionType;

            form.setClearPeriod(creditType.getClearPeriod());
            form.setRefundable(creditType.isRefundable());
            form.setRefundRule(creditType.getRefundRule());
            form.setAuthorizationText(creditType.getAuthorizationText());
            form.setUnallocatedGLAccount(creditType.getUnallocatedGlAccount());
            GlOperationType operation = creditType.getUnallocatedGlOperation();
            if (operation != null) {
                form.setUnallocatedGLOperation(operation.getId());
            }
        }
    }

    public boolean validateBreakdowns(List<GlBreakdown> breakdowns, TransactionTypeForm form, GeneralLedgerType defaultGlType) {

        boolean errors = false;

        // Handle the GL Breakdown sections.
        BigDecimal total = new BigDecimal(0);

        boolean zeroRow = false;

        for (GlBreakdownModel breakdownModel : form.getGlBreakdowns()) {

            GlBreakdown breakdown = new GlBreakdown();
            breakdowns.add(breakdown);

            breakdown.setGeneralLedgerType(defaultGlType);

            breakdown.setGlAccount(breakdownModel.getGlAccount());
            if (!glService.isGlAccountValid(breakdown.getGlAccount())) {
                GlobalVariables.getMessageMap().putError("glBreakdownList", RiceKeyConstants.ERROR_CUSTOM, "GL Account '" + breakdown.getGlAccount() + "' is not valid");
                errors = true;
            }

            String operation = breakdownModel.getOperation();
            if (operation == null || "".equals(operation)) {
                GlobalVariables.getMessageMap().putError("glBreakdownList", RiceKeyConstants.ERROR_CUSTOM, "Operation is required");
                errors = true;
            }
            GlOperationType operationType = (GlOperationType.CREDIT.getId().equals(operation)) ? GlOperationType.CREDIT : GlOperationType.DEBIT;

            breakdown.setGlOperation(operationType);

            BigDecimal b = breakdownModel.getBreakdown();
            if (b == null) {
                b = BigDecimal.ZERO;
            }
            breakdown.setBreakdown(b);
            total = total.add(b);

            if (BigDecimal.ZERO.equals(b)) {
                if (!zeroRow) {
                    zeroRow = true;
                } else {
                    // There can be only one
                    GlobalVariables.getMessageMap().putError("glBreakdownList", RiceKeyConstants.ERROR_CUSTOM, "Only one row may have a breakdown amount of 0%");
                    errors = true;
                }
            }
            if (total.compareTo(BigDecimal.ONE) >= 0) {
                // Error, the numbers exceed the allowable amount.  It must be less than 100%
                GlobalVariables.getMessageMap().putError("glBreakdownList", RiceKeyConstants.ERROR_CUSTOM, "Breakdowns must be less than 100%");
                errors = true;
            }


        }

        if (!zeroRow) {
            // Tell the user here that there needs to be a row with a 0 amount
            GlobalVariables.getMessageMap().putError("glBreakdownList", RiceKeyConstants.ERROR_CUSTOM, "One row must have a breakdown of 0%");
            errors = true;
        }

        if (glService.getDefaultGeneralLedgerType() == null) {
            GlobalVariables.getMessageMap().putError("glBreakdownList", RiceKeyConstants.ERROR_CUSTOM, "No default GL Type configured");
            errors = true;
        }

        return errors;
    }

    private void persistAudit(TransactionTypeForm form, TransactionType transactionType) {
        ActivityType activityType = activityService.getActivityType("LOGMEMO");

        Activity activity = new Activity();
        activity.setTypeId(activityType.getId());
        activity.setLogDetail(form.getStartDateAuditReason());
        activity.setEntityType("TransactionType");
        activity.setEntityId(transactionType.getId().toString());


        activityService.persistActivity(activity);

    }
}

package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.krad.form.SettingsForm;
import com.sigmasys.kuali.ksa.krad.model.AuditableEntityModel;
import com.sigmasys.kuali.ksa.krad.model.CashLimitParameterModel;
import com.sigmasys.kuali.ksa.krad.model.RefundTypeModel;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.Currency;
import com.sigmasys.kuali.ksa.service.AuditableEntityService;
import com.sigmasys.kuali.ksa.service.CashLimitService;
import com.sigmasys.kuali.ksa.service.InformationService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * TODO: Tim, this controller needs refactoring -> especially checks on null of entity and parentEntity objects (Mike)
 * <p/>
 * Created by: dmulderink on 10/10/12 at 8:06 PM
 */
@Controller
@RequestMapping(value = "/settingsView")
public class SettingsController extends GenericSearchController {

    private static final Log logger = LogFactory.getLog(SettingsController.class);

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private CashLimitService cashLimitService;

    @Autowired
    protected ConfigService configService;

    @Autowired
    protected InformationService informationService;


    /**
     * @see org.kuali.rice.krad.web.controller.UifControllerBase#createInitialForm(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected SettingsForm createInitialForm(HttpServletRequest request) {
        SettingsForm form = new SettingsForm();
        return form;
    }

    /**
     * @param form    SettingsForm instance
     * @param request HttpServletRequest instance
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=get")
    public ModelAndView get(@ModelAttribute("KualiForm") SettingsForm form, HttpServletRequest request) {

        // do get stuff...
        String viewId = request.getParameter("viewId");
        String pageId = request.getParameter("pageId");

        String entityId = request.getParameter("entityId");

        logger.info("View: " + viewId + " Page: " + pageId + " Entity ID: " + entityId);

        // Currency type
        if ("CurrencyPage".equals(pageId)) {
            Currency c = new Currency();
            form.setAuditableEntity(c);

            form.setAuditableEntities(auditableEntityService.getAuditableEntities(Currency.class));
        } else if ("CurrencyDetailsPage".equals(pageId)) {
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }
            form.setAuditableEntity(auditableEntityService.getAuditableEntity(Long.valueOf(entityId), Currency.class));
        } else if ("RollupPage".equals(pageId)) {
            form.setAuditableEntity(new Rollup());
            form.setAuditableEntities(auditableEntityService.getAuditableEntities(Rollup.class));
        } else if ("RollupDetailsPage".equals(pageId)) {
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }
            form.setAuditableEntity(auditableEntityService.getAuditableEntity(Long.valueOf(entityId), Rollup.class));
        } else if ("TagPage".equals(pageId)) {
            form.setAuditableEntity(new Tag());
            form.setAuditableEntities(auditableEntityService.getAuditableEntities(Tag.class));
        } else if ("TagDetailsPage".equals(pageId)) {
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }
            form.setAuditableEntity(auditableEntityService.getAuditableEntity(Long.valueOf(entityId), Tag.class));
        } else if ("BankTypePage".equals(pageId)) {
            form.setAuditableEntity(new BankType());
            form.setAuditableEntities(auditableEntityService.getAuditableEntities(BankType.class));
        } else if ("BankTypeDetailsPage".equals(pageId)) {
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }
            form.setAuditableEntity(auditableEntityService.getAuditableEntity(Long.valueOf(entityId), BankType.class));
        } else if ("TaxTypePage".equals(pageId)) {
            form.setAuditableEntity(new TaxType());
            form.setAuditableEntities(auditableEntityService.getAuditableEntities(TaxType.class));
        } else if ("TaxTypeDetailsPage".equals(pageId)) {
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }
            form.setAuditableEntity(auditableEntityService.getAuditableEntity(Long.valueOf(entityId), TaxType.class));
        } else if ("LatePeriodPage".equals(pageId)) {
            form.setAuditableEntity(new LatePeriod());
            form.setAuditableEntities(auditableEntityService.getAuditableEntities(LatePeriod.class));
        } else if ("LatePeriodDetailsPage".equals(pageId)) {
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }
            form.setAuditableEntity(auditableEntityService.getAuditableEntity(Long.valueOf(entityId), LatePeriod.class));
        } else if ("AccountStatusTypePage".equals(pageId)) {
            form.setAuditableEntity(new AccountStatusType());
            form.setAuditableEntities(auditableEntityService.getAuditableEntities(AccountStatusType.class));
        } else if ("AccountStatusTypeDetailsPage".equals(pageId)) {
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }
            form.setAuditableEntity(auditableEntityService.getAuditableEntity(Long.valueOf(entityId), AccountStatusType.class));
        } else if ("FlagTypePage".equals(pageId)) {
            form.setAuditableEntity(new FlagType());
            form.setAuditableEntities(informationService.getFlagTypes());
        } else if ("FlagTypeDetailsPage".equals(pageId)) {
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }
            form.setAuditableEntity(informationService.getFlagType(Long.valueOf(entityId)));
        } else if ("ActivityTypePage".equals(pageId)) {
            form.setAuditableEntity(new ActivityType());
            form.setAuditableEntities(auditableEntityService.getAuditableEntities(ActivityType.class));
        } else if ("ActivityTypeDetailsPage".equals(pageId)) {
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }
            form.setAuditableEntity(auditableEntityService.getAuditableEntity(Long.valueOf(entityId), ActivityType.class));
        } else if ("GeneralLedgerTypePage".equals(pageId)) {
            form.setAuditableEntity(new GeneralLedgerType());
            form.getAuditableEntity().setGlOperationCode(GlOperationType.DEBIT_CODE);
            form.setAuditableEntities(auditableEntityService.getAuditableEntities(GeneralLedgerType.class));
        } else if ("GeneralLedgerTypeDetailsPage".equals(pageId)) {
            if (entityId == null || entityId.trim().isEmpty()) {
                throw new IllegalArgumentException("'entityId' request parameter must be specified");
            }
            form.setAuditableEntity(auditableEntityService.getAuditableEntity(Long.valueOf(entityId), GeneralLedgerType.class));
        } else if ("SystemConfigurationPage".equals(pageId)) {
            // Only non read-only parameters can be edited
            List<ConfigParameter> parameters = configService.getParameters();
            List<ConfigParameter> modifiableParameters = new LinkedList<ConfigParameter>();
            for (ConfigParameter parameter : parameters) {
                if (!parameter.isReadOnly()) {
                    modifiableParameters.add(parameter);
                }
            }
            form.setConfigParameters(modifiableParameters);
        } else if ("CashLimitParameterPage".equals(pageId)) {
            form.setAuditableEntity(new CashLimitParameter());
            List<CashLimitParameter> entities = auditableEntityService.getAuditableEntities(CashLimitParameter.class);
            List<CashLimitParameterModel> models = new ArrayList<CashLimitParameterModel>(entities.size());
            for (CashLimitParameter entity : entities) {
                models.add(new CashLimitParameterModel(entity));
            }
            form.setCashLimitParameters(models);
        } else if ("RefundTypePage".equals(pageId)) {
            form.setAuditableEntity(new RefundType());
            List<RefundType> entities = auditableEntityService.getAuditableEntities(RefundType.class);
            List<RefundTypeModel> models = new ArrayList<RefundTypeModel>(entities.size());
            for (RefundType entity : entities) {
                models.add(new RefundTypeModel(entity));
            }
            form.setRefundTypes(models);
        }

        return getUIFModelAndView(form);
    }

    /**
     * @param form SettingsForm instance
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=insertAuditableEntity")
    public ModelAndView insertAuditableEntity(@ModelAttribute("KualiForm") SettingsForm form) {

        AuditableEntityModel entity = form.getAuditableEntity();
        AuditableEntity parentEntity = entity.getParentEntity();

        logger.info("Entity is of type : " + entity.getClass().getName());
        logger.info("Entity code: " + entity.getCode());

        if (parentEntity != null) {

            logger.info("Parent entity is of type : " + parentEntity.getClass().getName());

            try {

                String code = parentEntity.getCode();
                String name = parentEntity.getName();
                String description = parentEntity.getDescription();

                if(parentEntity instanceof GeneralLedgerType) {
                    auditableEntityService.persistAuditableEntity(parentEntity);
                } else {

                    AuditableEntity value = auditableEntityService.createAuditableEntity(code, name, description, parentEntity.getClass());
                    if (value instanceof Tag) {
                        ((Tag) value).setAdministrative(entity.getAdministrative());
                        auditableEntityService.persistAuditableEntity(value);
                    }
                }

                form.setAuditableEntities(auditableEntityService.getAuditableEntities(parentEntity.getClass()));

                form.setAuditableEntity(parentEntity.getClass().newInstance());

                // success in creating the currency.
                String statusMsg = "Success: " + parentEntity.getClass().getName() + " saved, ID = " + parentEntity.getId();
                GlobalVariables.getMessageMap().putInfo("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
                logger.info(statusMsg);

            } catch (Exception e) {
                String statusMsg = e.getMessage();
                GlobalVariables.getMessageMap().putError("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
                logger.error(statusMsg, e);
            }
        }

        return getUIFModelAndView(form);
    }

    /**
     * @param form SettingsForm instance
     * @return ModelAndView instance
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=insertLatePeriod")
    public ModelAndView insertLatePeriod(@ModelAttribute("KualiForm") SettingsForm form) {

        AuditableEntityModel entity = form.getAuditableEntity();
        LatePeriod parentEntity = entity.getLatePeriod();

        logger.info("Entity is of type : " + entity.getClass().getName());
        logger.info("Entity code: " + entity.getCode());

        if (parentEntity != null) {

            logger.info("Parent entity is of type : " + parentEntity.getClass().getName());

            try {

                auditableEntityService.persistAuditableEntity(parentEntity);

                form.setAuditableEntities(auditableEntityService.getAuditableEntities(parentEntity.getClass()));

                form.setAuditableEntity(parentEntity.getClass().newInstance());

                // success in creating the currency.
                String statusMsg = "Success: " + parentEntity.getClass().getName() + " saved, ID = " + parentEntity.getId();
                GlobalVariables.getMessageMap().putInfo("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
                logger.info(statusMsg);

            } catch (Exception e) {
                // failed to create the currency. Leave the currency information in the view
                String statusMsg = e.getMessage();
                GlobalVariables.getMessageMap().putError("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
                logger.error(statusMsg, e);
            }

        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=updateAuditableEntity")
    public ModelAndView updateAuditableEntity(@ModelAttribute("KualiForm") SettingsForm form) {

        AuditableEntity entity = form.getAuditableEntity();
        if (entity instanceof AuditableEntityModel) {
            entity = ((AuditableEntityModel) entity).getParentEntity();
        }

        try {
            // occurs in the detail page.
            auditableEntityService.persistAuditableEntity(entity);
            // success in updating the currency.
            String statusMsg = "Success: " + entity.getClass() + " entity updated. Entity ID =  " + entity.getId();
            GlobalVariables.getMessageMap().putInfo("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.info(statusMsg);
        } catch (Exception e) {
            String statusMsg = "Failure: " + entity.getClass() + " entity did not update, Entity ID =  " + entity.getId() + ". " + e.getMessage();
            GlobalVariables.getMessageMap().putError("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg);
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=updateSystemConfig")
    public ModelAndView updateSystemConfig(@ModelAttribute("KualiForm") SettingsForm form) {

        List<ConfigParameter> parameters = form.getConfigParameters();

        try {

            if (CollectionUtils.isNotEmpty(parameters)) {

                List<ConfigParameter> readOnlyParameters = configService.getReadOnlyParameters();

                if (CollectionUtils.isNotEmpty(readOnlyParameters)) {

                    Set<String> readOnlyParamNames = new HashSet<String>(readOnlyParameters.size());

                    for (ConfigParameter readOnlyParameter : readOnlyParameters) {
                        readOnlyParamNames.add(readOnlyParameter.getName().toLowerCase());
                    }

                    for (ConfigParameter parameter : parameters) {
                        if (readOnlyParamNames.contains(parameter.getName().toLowerCase())) {
                            String msg = "Read-only System Parameter '" + parameter.getName() + "' cannot be overridden";
                            GlobalVariables.getMessageMap().putError("SettingsView", RiceKeyConstants.ERROR_CUSTOM, msg);
                            return getUIFModelAndView(form);
                        }
                    }
                }

                configService.updateParameters(parameters);
            }

            String statusMsg = "System Parameters updated.";
            GlobalVariables.getMessageMap().putInfo("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.info(statusMsg);

        } catch (Exception e) {
            String statusMsg = "System Parameters did not update. ";
            if (e instanceof DuplicateKeyException) {
                statusMsg += "Duplicate Names not allowed.";
            }
            GlobalVariables.getMessageMap().putError("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg + " " + e.getMessage());
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=saveCashLimitParameters")
    public ModelAndView saveCashLimitParameters(@ModelAttribute("KualiForm") SettingsForm form) {
        List<CashLimitParameterModel> parameters = form.getCashLimitParameters();


        boolean errors = false;
        boolean success = false;

        try {
            for (CashLimitParameterModel p : parameters) {
                CashLimitParameter parent = (CashLimitParameter) p.getParentEntity();
                if (parent.getTag() == null) {
                    Tag tag = auditableEntityService.getAuditableEntity(p.getTagString(), Tag.class);
                    if(tag == null) {
                        String statusMsg = p.getTagString() + " is not a valid tag. Row not saved.";
                        GlobalVariables.getMessageMap().putError("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);

                        errors = true;
                        continue;
                    }

                    parent.setTag(tag);
                }
                cashLimitService.persistCashLimitParameter(parent);
                success = true;
            }

        } catch (Throwable t) {
            String statusMsg = "System Parameters did not update: " + t.getLocalizedMessage();
            GlobalVariables.getMessageMap().putError("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg + " " + t.getMessage());
            errors = true;
        }

        if(success && errors) {
            String statusMsg = "Some Cash Limit Parameters saved, but others contained errors.";
            GlobalVariables.getMessageMap().putWarning("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.info(statusMsg);

        } else if(success) {
            if(success) {
                String statusMsg = "Cash Limit Parameters saved";
                GlobalVariables.getMessageMap().putInfo("SettingsView", RiceKeyConstants.ERROR_CUSTOM, statusMsg);
                logger.info(statusMsg);
            }

        }


        return getUIFModelAndView(form);

    }

    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=saveRefundType")
    public ModelAndView saveRefundType(@ModelAttribute("KualiForm") SettingsForm form) {
        List<RefundTypeModel> parameters = form.getRefundTypes();


        boolean errors = false;
        boolean success = false;

        try {
            for (RefundTypeModel p : parameters) {
                RefundType parent = (RefundType) p.getParentEntity();

                auditableEntityService.persistAuditableEntity(parent);
                success = true;
            }

        } catch (Throwable t) {
            String statusMsg = "Refund Types did not update: " + t.getLocalizedMessage();
            GlobalVariables.getMessageMap().putError(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.error(statusMsg + " " + t.getMessage());
            errors = true;
        }

        if(success && errors) {
            String statusMsg = "Some Refund Types saved, but others contained errors.";
            GlobalVariables.getMessageMap().putWarning(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, statusMsg);
            logger.info(statusMsg);

        } else if(success) {
            if(success) {
                String statusMsg = "Refunt Types saved";
                GlobalVariables.getMessageMap().putInfo(form.getViewId(), RiceKeyConstants.ERROR_CUSTOM, statusMsg);
                logger.info(statusMsg);
            }

        }


        return getUIFModelAndView(form);

    }


}

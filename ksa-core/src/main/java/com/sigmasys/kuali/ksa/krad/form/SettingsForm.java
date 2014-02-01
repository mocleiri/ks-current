package com.sigmasys.kuali.ksa.krad.form;

import com.sigmasys.kuali.ksa.krad.model.AuditableEntityModel;
import com.sigmasys.kuali.ksa.krad.model.CashLimitParameterModel;
import com.sigmasys.kuali.ksa.krad.model.GlRecognitionPeriodModel;
import com.sigmasys.kuali.ksa.krad.model.RefundTypeModel;
import com.sigmasys.kuali.ksa.krad.util.AuditTooltipUtil;
import com.sigmasys.kuali.ksa.model.*;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class SettingsForm extends AbstractViewModel {

    private Account account;

    private List<AuditableEntityModel> entities;

    private AuditableEntityModel auditableEntity;

    private List<ConfigParameter> configParameters;

    private List<CashLimitParameterModel> cashLimitParameters;

    private List<RefundTypeModel> refundTypeModels;

    private List<GlRecognitionPeriodModel> glRecognitionPeriodModels;


    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public List<AuditableEntityModel> getAuditableEntities() {
        return entities;
    }

    public <T extends AuditableEntity> void setAuditableEntities(List<T> entities) {

        List<AuditableEntityModel> list = new ArrayList<AuditableEntityModel>(entities.size());

        for (AuditableEntity entity : entities) {
            AuditableEntityModel m;
            if (entity instanceof AuditableEntityModel) {
                m = (AuditableEntityModel) entity;
            } else {
                m = new AuditableEntityModel(entity);
            }
            list.add(m);
        }

        this.entities = list;
    }

    public AuditableEntityModel getAuditableEntity() {
        return auditableEntity;
    }

    public void setAuditableEntity(AuditableEntityModel auditableEntity) {
        if (this.auditableEntity == null) {
            this.auditableEntity = auditableEntity;
        }
    }


    public <T extends AuditableEntity> void setAuditableEntity(T auditableEntity) {
        if (auditableEntity instanceof AuditableEntityModel) {
            this.auditableEntity = (AuditableEntityModel) auditableEntity;
        } else {
            this.auditableEntity = new AuditableEntityModel(auditableEntity);
        }

    }

    public String getType() {
        if (auditableEntity == null) {
            return "null";
        }
        return auditableEntity.getParentEntity().getClass().getName();
    }

    public String getSimpleType() {
        if (auditableEntity == null) {
            return "null";
        }
        return auditableEntity.getParentEntity().getClass().getSimpleName();
    }

    public String getHumanType() {

        if (auditableEntity == null) {
            return "Unknown Entity Type";
        }

        AuditableEntity parent = auditableEntity.getParentEntity();
        if (parent == null) {
            return "Unknown Type";
        }

        if (parent instanceof AccountStatusType) {
            return "Account Status Type";
        } else if (parent instanceof ActivityType) {
            return "Activity Type";
        } else if (parent instanceof BankType) {
            return "Bank Type";
        } else if (parent instanceof Currency) {
            return "Currency Type";
        } else if (parent instanceof FlagType) {
            return "Flag Type";
        } else if (parent instanceof LatePeriod) {
            return "Late Period Type";
        } else if (parent instanceof Rollup) {
            return "Rollup Type";
        } else if (parent instanceof TaxType) {
            return "Tax Type";
        } else if (parent instanceof GeneralLedgerType) {
            return "General Ledger Type";
        } else if (parent instanceof RefundType) {
            return "Refund Type";
        }

        return this.getSimpleType();
    }

    public List<ConfigParameter> getConfigParameters() {
        return this.configParameters;
    }

    public void setConfigParameters(List<ConfigParameter> parameters) {
        this.configParameters = parameters;
    }

    public List<CashLimitParameterModel> getCashLimitParameters() {
        return cashLimitParameters;
    }

    public void setCashLimitParameters(List<CashLimitParameterModel> cashLimitParameters) {
        this.cashLimitParameters = cashLimitParameters;
    }

    public <T extends AuditableEntity> String getAuditTooltip(AuditableEntityModel entity) {
        return (entity.getParentEntity() != null) ? AuditTooltipUtil.getAuditTooltip(entity.getParentEntity()) : "";
    }

    public List<RefundTypeModel> getRefundTypes() {
        return refundTypeModels;
    }

    public void setRefundTypes(List<RefundTypeModel> refundTypeModels) {
        this.refundTypeModels = refundTypeModels;
    }

    public List<GlRecognitionPeriodModel> getGlRecognitionPeriodModels() {
        return glRecognitionPeriodModels;
    }

    public void setGlRecognitionPeriodModels(List<GlRecognitionPeriodModel> glRecognitionPeriodModels) {
        this.glRecognitionPeriodModels = glRecognitionPeriodModels;
    }
}

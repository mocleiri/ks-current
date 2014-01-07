package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.util.EnumUtils;

import java.util.Date;

/**
 * User: timb
 * Date: 11/29/12
 * Time: 11:43 AM
 */
public class AuditableEntityModel extends AuditableEntity<Long> {

    protected AuditableEntity<Long> parentEntity;

    public AuditableEntityModel() {
    }

    public AuditableEntityModel(AuditableEntity<Long> entity) {
        setAuditableEntity(entity);
    }

    public void setAuditableEntity(AuditableEntity<Long> entity) {
        parentEntity = entity;
    }

    public AuditableEntity getParentEntity() {
        return parentEntity;
    }

    public Currency getCurrency() {
        return (parentEntity instanceof Currency) ? (Currency) parentEntity : null;
    }

    public GeneralLedgerType getGeneralLedgerType() {
        return (parentEntity instanceof GeneralLedgerType) ? (GeneralLedgerType) parentEntity : null;
    }

    public Rollup getRollup() {
        return (parentEntity instanceof Rollup) ? (Rollup) parentEntity : null;
    }

    public LatePeriod getLatePeriod() {
        return (parentEntity instanceof LatePeriod) ? (LatePeriod) parentEntity : null;
    }

    public void setLatePeriod(LatePeriod entity){
        this.parentEntity = entity;
    }

    @Override
    public Long getId() {
        return parentEntity.getId();
    }

    @Override
    public void setId(Long id) {
        parentEntity.setId(id);
    }

    @Override
    public String getCode() {
        if (parentEntity == null || parentEntity.getCode() == null) {
            return "";
        }
        return parentEntity.getCode();
    }

    @Override
    public void setCode(String code) {
        parentEntity.setCode(code);
    }

    @Override
    public String getName() {
        return parentEntity.getName();
    }

    @Override
    public void setName(String name) {
        parentEntity.setName(name);
    }

    @Override
    public String getCreatorId() {
        return parentEntity.getCreatorId();
    }

    @Override
    public void setCreatorId(String creatorId) {
        parentEntity.setCreatorId(creatorId);
    }

    @Override
    public String getEditorId() {
        return parentEntity.getEditorId();
    }

    @Override
    public void setEditorId(String editorId) {
        parentEntity.setEditorId(editorId);
    }

    @Override
    public Date getLastUpdate() {
        return parentEntity.getLastUpdate();
    }

    @Override
    public void setLastUpdate(Date lastUpdate) {
        parentEntity.setLastUpdate(lastUpdate);
    }

    @Override
    public String getDescription() {
        return parentEntity.getDescription();
    }

    @Override
    public void setDescription(String description) {
        parentEntity.setDescription(description);
    }

    @Override
    public Date getCreationDate() {
        return parentEntity.getCreationDate();
    }

    @Override
    public void setCreationDate(Date creationDate) {
        parentEntity.setCreationDate(creationDate);
    }

    @Override
    public String toString() {
        return "Auditable Entity Model parent: " + parentEntity;
    }

    public Boolean getAdministrative(){
        if(parentEntity instanceof Tag){
            return ((Tag)parentEntity).isAdministrative();
        }
        return false;
    }

    public void setAdministrative(Boolean administrative){
        if(parentEntity instanceof Tag){
            ((Tag)parentEntity).setAdministrative(administrative);
        }
    }

    public String getGlAccountId() {
        if(parentEntity instanceof GeneralLedgerType) {
            return ((GeneralLedgerType)parentEntity).getGlAccountId();
        }
        return null;
    }

    public void setGlAccountId(String glAccountId) {
        if(parentEntity instanceof GeneralLedgerType) {
            ((GeneralLedgerType)parentEntity).setGlAccountId(glAccountId);
        }
    }

    public String getGlOperationCode() {
        if(parentEntity instanceof GeneralLedgerType) {
            GlOperationType optionType = ((GeneralLedgerType)parentEntity).getGlOperationOnCharge();
            if(optionType != null) {
                return optionType.getId();
            }
        }
        return null;
    }

    public void setGlOperationCode(String glOperationCode) {
        if(parentEntity instanceof GeneralLedgerType) {
            GlOperationType glOperationOnCharge = EnumUtils.findById(GlOperationType.class, glOperationCode);

            ((GeneralLedgerType)parentEntity).setGlOperationOnCharge(glOperationOnCharge);
        }
    }

    public String getGlOperation() {
        if(parentEntity instanceof GeneralLedgerType) {
            GlOperationType optionType = ((GeneralLedgerType)parentEntity).getGlOperationOnCharge();
            if(optionType != null) {
                return optionType.toString();
            }
        }
        return null;
    }


}

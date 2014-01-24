package org.kuali.student.r2.core.organization.model;

import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.entity.AttributeOwner;
import org.kuali.student.r2.common.entity.MetaEntity;
import org.kuali.student.r2.common.infc.Attribute;
import org.kuali.student.r2.core.organization.dto.OrgOrgRelationInfo;
import org.kuali.student.r2.core.organization.infc.OrgOrgRelation;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="KSEN_ORG_ORG_RELTN")
@NamedQueries({
        @NamedQuery(name = "OrgOrgRelationEntity.getIdsByType",
                query = "select id from OrgOrgRelationEntity where orgOrgRelationType = :type"),
        @NamedQuery(name = "OrgOrgRelationEntity.hasOrgOrgRelation",
                query = "select oor from OrgOrgRelationEntity oor where oor.orgId = :orgId and oor.relatedOrgId = :relatedOrgId and oor.orgOrgRelationType = :type"),
        @NamedQuery(name = "OrgOrgRelationEntity.getOrgOrgRelationsByOrgId",
                query = "select oor from OrgOrgRelationEntity oor where oor.orgId = :orgId or oor.relatedOrgId = :orgId"),
        @NamedQuery(name = "OrgOrgRelationEntity.getOrgOrgRelationsByOrgIdAndRelatedOrgId",
                query = "select oor from OrgOrgRelationEntity oor where (oor.orgId = :orgId and oor.relatedOrgId = :relatedOrgId) or " +
                        "(oor.orgId = :relatedOrgId and oor.relatedOrgId = :orgId)"),
        @NamedQuery(name = "OrgOrgRelationEntity.getOrgOrgRelationsByTypeAndOrgId",
                query = "select oor from OrgOrgRelationEntity oor where oor.orgId = :orgId and oor.orgOrgRelationType = :type"),
        @NamedQuery(name = "OrgOrgRelationEntity.getOrgOrgRelationsByTypeAndRelatedOrgId",
        query = "select oor from OrgOrgRelationEntity oor where oor.relatedOrgId = :relatedOrgId and oor.orgOrgRelationType = :type")
})
public class OrgOrgRelationEntity extends MetaEntity implements AttributeOwner<OrgOrgRelationAttributeEntity> {
    @Column(name = "ORG_RELTN_TYPE", nullable = false)
    private String orgOrgRelationType;
    @Column(name = "ORG_RELTN_STATE", nullable = false)
    private String orgOrgRelationState;
    @Column(name = "ORG_ID", nullable = false)
    private String orgId;
    @Column(name = "RELATED_ORG_ID", nullable = false)
    private String relatedOrgId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EXPIR_DT", nullable = false)
    private Date expirationDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFF_DT")
    private Date effectiveDate;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<OrgOrgRelationAttributeEntity> attributes = new HashSet<OrgOrgRelationAttributeEntity>();

    public  OrgOrgRelationEntity() {
    }

    public OrgOrgRelationEntity(OrgOrgRelation orgOrgRelation) {
        super(orgOrgRelation);
        setId(orgOrgRelation.getId());
        setOrgOrgRelationType(orgOrgRelation.getTypeKey());
        setOrgId(orgOrgRelation.getOrgId());
        setRelatedOrgId(orgOrgRelation.getRelatedOrgId());
        fromDto(orgOrgRelation);
    }

    public String getOrgOrgRelationType() {
        return orgOrgRelationType;
    }

    public void setOrgOrgRelationType(String orgOrgRelationType) {
        this.orgOrgRelationType = orgOrgRelationType;
    }

    public String getOrgOrgRelationState() {
        return orgOrgRelationState;
    }

    public void setOrgOrgRelationState(String orgOrgRelationState) {
        this.orgOrgRelationState = orgOrgRelationState;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getRelatedOrgId() {
        return relatedOrgId;
    }

    public void setRelatedOrgId(String relatedOrgId) {
        this.relatedOrgId = relatedOrgId;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    @Override
    public Set<OrgOrgRelationAttributeEntity> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(Set<OrgOrgRelationAttributeEntity> attributes) {
        this.attributes.clear();
        if (attributes != null) {
            this.attributes.addAll(attributes);
        }
    }

    public void fromDto(OrgOrgRelation orgOrgRelation) {
        setOrgOrgRelationState(orgOrgRelation.getStateKey());
        setExpirationDate(orgOrgRelation.getExpirationDate());
        setEffectiveDate(orgOrgRelation.getEffectiveDate());

        attributes.clear();
        for (Attribute att : orgOrgRelation.getAttributes()) {
            OrgOrgRelationAttributeEntity attEntity = new OrgOrgRelationAttributeEntity(att, this);
            getAttributes().add(attEntity);
        }
    }

    public OrgOrgRelationInfo toDto() {
        OrgOrgRelationInfo info = new OrgOrgRelationInfo();
        info.setId(getId());
        info.setTypeKey(getOrgOrgRelationType());
        info.setStateKey(getOrgOrgRelationState());
        info.setOrgId(getOrgId());
        info.setRelatedOrgId(getRelatedOrgId());
        info.setExpirationDate(getExpirationDate());
        info.setEffectiveDate(getEffectiveDate());
        info.setMeta(super.toDTO());

        for (OrgOrgRelationAttributeEntity att : getAttributes()) {
            AttributeInfo attInfo = att.toDto();
            info.getAttributes().add(attInfo);
        }

        return info;
    }
}

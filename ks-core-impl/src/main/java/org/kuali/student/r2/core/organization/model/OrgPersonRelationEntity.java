package org.kuali.student.r2.core.organization.model;

import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.entity.AttributeOwner;
import org.kuali.student.r2.common.entity.MetaEntity;
import org.kuali.student.r2.common.infc.Attribute;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.infc.OrgPersonRelation;

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
@Table(name="KSEN_ORG_PERS_RELTN")
@NamedQueries({
        @NamedQuery(name = "OrgPersonRelationEntity.getIdsByType",
                query = "select id from OrgPersonRelationEntity where orgPersonRelationType = :type"),
        @NamedQuery(name = "OrgPersonRelationEntity.hasOrgPersonRelation",
                query = "select opr from OrgPersonRelationEntity opr where opr.orgId = :orgId and opr.personId = :personId" +
                        " and orgPersonRelationType = :type"),
        @NamedQuery(name = "OrgPersonRelationEntity.getOrgPersonRelationsByOrg",
                query = "select opr from OrgPersonRelationEntity opr where opr.orgId = :orgId"),
        @NamedQuery(name = "OrgPersonRelationEntity.getOrgPersonRelationsByTypeAndOrg",
                query = "select opr from OrgPersonRelationEntity opr where opr.orgId = :orgId and opr.orgPersonRelationType = :type"),
        @NamedQuery(name = "OrgPersonRelationEntity.getOrgPersonRelationsByPerson",
                query = "select opr from OrgPersonRelationEntity opr where opr.personId = :personId"),
        @NamedQuery(name = "OrgPersonRelationEntity.getOrgPersonRelationsByTypeAndPerson",
                query = "select opr from OrgPersonRelationEntity opr where opr.personId = :personId and opr.orgPersonRelationType = :type"),
        @NamedQuery(name = "OrgPersonRelationEntity.getOrgPersonRelationsByOrgAndPerson",
                query = "select opr from OrgPersonRelationEntity opr where opr.orgId = :orgId and opr.personId = :personId"),
        @NamedQuery(name = "OrgPersonRelationEntity.getOrgPersonRelationsByOrgAndTypeAndPerson",
                query = "select opr from OrgPersonRelationEntity opr where opr.orgId = :orgId and opr.orgPersonRelationType = :type and opr.personId = :personId")

})
public class OrgPersonRelationEntity extends MetaEntity implements AttributeOwner<OrgPersonRelationAttributeEntity> {
    @Column(name = "ORG_PERS_RELTN_TYPE", nullable = false)
    private String orgPersonRelationType;
    @Column(name = "ORG_PERS_RELTN_STATE", nullable = false)
    private String orgPersonRelationState;
    @Column(name = "ORG_ID", nullable = false)
    private String orgId;
    @Column(name = "PERS_ID", nullable = false)
    private String personId;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EXPIR_DT")
    private Date expirationDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFF_DT", nullable = false)
    private Date effectiveDate;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<OrgPersonRelationAttributeEntity> attributes = new HashSet<OrgPersonRelationAttributeEntity>();

    public  OrgPersonRelationEntity() { }

    public OrgPersonRelationEntity(OrgPersonRelation orgPersonRelation) {
        super(orgPersonRelation);
        setId(orgPersonRelation.getId());
        setOrgPersonRelationType(orgPersonRelation.getTypeKey());
        setOrgId(orgPersonRelation.getOrgId());
        setPersonId(orgPersonRelation.getPersonId());
        fromDto(orgPersonRelation);
    }

    public String getOrgPersonRelationType() {
        return orgPersonRelationType;
    }

    public void setOrgPersonRelationType(String orgPersonRelationType) {
        this.orgPersonRelationType = orgPersonRelationType;
    }

    public String getOrgPersonRelationState() {
        return orgPersonRelationState;
    }

    public void setOrgPersonRelationState(String orgPersonRelationState) {
        this.orgPersonRelationState = orgPersonRelationState;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
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
    public Set<OrgPersonRelationAttributeEntity> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(Set<OrgPersonRelationAttributeEntity> attributes) {
        this.attributes.clear();
        if (attributes != null) {
            this.attributes.addAll(attributes);
        }
    }

    public void fromDto(OrgPersonRelation orgPersonRelation) {
        setOrgPersonRelationState(orgPersonRelation.getStateKey());
        setExpirationDate(orgPersonRelation.getExpirationDate());
        setEffectiveDate(orgPersonRelation.getEffectiveDate());

        attributes.clear();
        for (Attribute att : orgPersonRelation.getAttributes()) {
            OrgPersonRelationAttributeEntity attEntity = new OrgPersonRelationAttributeEntity(att, this);
            getAttributes().add(attEntity);
        }
    }

    public OrgPersonRelationInfo toDto() {
        OrgPersonRelationInfo info = new OrgPersonRelationInfo();
        info.setId(getId());
        info.setTypeKey(getOrgPersonRelationType());
        info.setStateKey(getOrgPersonRelationState());
        info.setOrgId(getOrgId());
        info.setPersonId(getPersonId());
        info.setExpirationDate(getExpirationDate());
        info.setEffectiveDate(getEffectiveDate());
        info.setMeta(super.toDTO());

        for (OrgPersonRelationAttributeEntity att : getAttributes()) {
            AttributeInfo attInfo = att.toDto();
            info.getAttributes().add(attInfo);
        }
        return info;
    }
}

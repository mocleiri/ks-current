package org.kuali.student.r2.core.organization.model;


import org.kuali.student.r1.common.entity.KSEntityConstants;
import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.dto.TimeAmountInfo;
import org.kuali.student.r2.common.entity.AttributeOwner;
import org.kuali.student.r2.common.entity.MetaEntity;
import org.kuali.student.r2.common.infc.Attribute;
import org.kuali.student.r2.common.util.RichTextHelper;
import org.kuali.student.r2.core.organization.dto.OrgPositionRestrictionInfo;
import org.kuali.student.r2.core.organization.infc.OrgPositionRestriction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="KSEN_ORG_POS_RESTR")
@NamedQueries({
        @NamedQuery(name = "OrgPositionRestrictionEntity.getIdsByOrgPersonRelationType",
                query = "select id from OrgPositionRestrictionEntity where orgPersonRelationType = :type"),
        @NamedQuery(name = "OrgPositionRestrictionEntity.getIdsByOrgId",
                query = "select id from OrgPositionRestrictionEntity where orgId = :orgId")
})
public class OrgPositionRestrictionEntity extends MetaEntity implements AttributeOwner<OrgPositionRestrictionAttributeEntity> {
    @Column(name = "TITLE")
    private String title;
    @Column(name = "ORG_ID", nullable = false)
    private String orgId;
    @Column(name = "DESCR_PLAIN", length = KSEntityConstants.EXTRA_LONG_TEXT_LENGTH)
    private String descrPlain;
    @Column(name = "DESCR_FORMATTED", length = KSEntityConstants.EXTRA_LONG_TEXT_LENGTH)
    private String descrFormatted;
    @Column(name = "STD_DUR_TYPE")
    private String standardDurationType;
    @Column(name = "STD_DUR_TIME_QTY")
    private Integer standardDurationTime;
    @Column(name = "MIN_NUM_REL")
    private Integer minRelations;
    @Column(name = "MAX_NUM_REL")
    private Integer maxRelations;
    @Column(name = "ORG_PERS_RELTN_TYPE", nullable = false)
    private String orgPersonRelationType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<OrgPositionRestrictionAttributeEntity> attributes = new HashSet<OrgPositionRestrictionAttributeEntity>();

    public  OrgPositionRestrictionEntity() { }

    public OrgPositionRestrictionEntity(OrgPositionRestriction orgPositionRestriction) {
        super(orgPositionRestriction);
        setId(orgPositionRestriction.getId());
        setOrgPersonRelationType(orgPositionRestriction.getOrgPersonRelationTypeKey());
        fromDto(orgPositionRestriction);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getDescrPlain() {
        return descrPlain;
    }

    public void setDescrPlain(String descrPlain) {
        this.descrPlain = descrPlain;
    }

    public String getDescrFormatted() {
        return descrFormatted;
    }

    public void setDescrFormatted(String descrFormatted) {
        this.descrFormatted = descrFormatted;
    }

    public String getStandardDurationType() {
        return standardDurationType;
    }

    public void setStandardDurationType(String standardDurationType) {
        this.standardDurationType = standardDurationType;
    }

    public Integer getStandardDurationTime() {
        return standardDurationTime;
    }

    public void setStandardDurationTime(Integer standardDurationTime) {
        this.standardDurationTime = standardDurationTime;
    }

    public Integer getMinRelations() {
        return minRelations;
    }

    public void setMinRelations(Integer minRelations) {
        this.minRelations = minRelations;
    }

    public Integer getMaxRelations() {
        return maxRelations;
    }

    public void setMaxRelations(Integer maxRelations) {
        this.maxRelations = maxRelations;
    }

    public String getOrgPersonRelationType() {
        return orgPersonRelationType;
    }

    public void setOrgPersonRelationType(String orgPersonRelationType) {
        this.orgPersonRelationType = orgPersonRelationType;
    }

    @Override
    public Set<OrgPositionRestrictionAttributeEntity> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(Set<OrgPositionRestrictionAttributeEntity> attributes) {
        this.attributes.clear();
        if (attributes != null) {
            this.attributes.addAll(attributes);
        }
    }

    public void fromDto(OrgPositionRestriction orgPositionRestriction) {
        setTitle(orgPositionRestriction.getTitle());
        setOrgId(orgPositionRestriction.getOrgId());
        if(orgPositionRestriction.getDescr() != null) {
            setDescrPlain(orgPositionRestriction.getDescr().getPlain());
            setDescrFormatted(orgPositionRestriction.getDescr().getFormatted());
        }
        if(orgPositionRestriction.getStdDuration() != null) {
            setStandardDurationType(orgPositionRestriction.getStdDuration().getAtpDurationTypeKey());
            setStandardDurationTime(orgPositionRestriction.getStdDuration().getTimeQuantity());
        }

        setMinRelations(orgPositionRestriction.getMinNumRelations());
        setMaxRelations(orgPositionRestriction.getMaxNumRelations());

        attributes.clear();
        for (Attribute att : orgPositionRestriction.getAttributes()) {
            OrgPositionRestrictionAttributeEntity attEntity = new OrgPositionRestrictionAttributeEntity(att, this);
            getAttributes().add(attEntity);
        }
    }

    public OrgPositionRestrictionInfo toDto() {
        OrgPositionRestrictionInfo info = new OrgPositionRestrictionInfo();
        info.setId(getId());
        info.setOrgPersonRelationTypeKey(getOrgPersonRelationType());
        info.setTitle(getTitle());
        info.setOrgId(getOrgId());
        info.setDescr(RichTextHelper.buildRichTextInfo(getDescrPlain(), getDescrFormatted()));
        if(getStandardDurationType() != null) {
            TimeAmountInfo time = new TimeAmountInfo();
            time.setAtpDurationTypeKey(getStandardDurationType());
            time.setTimeQuantity(getStandardDurationTime());
            info.setStdDuration(time);
        }
        info.setMinNumRelations(getMinRelations());
        info.setMaxNumRelations(getMaxRelations());

        info.setMeta(super.toDTO());

        for (OrgPositionRestrictionAttributeEntity att : getAttributes()) {
            AttributeInfo attInfo = att.toDto();
            info.getAttributes().add(attInfo);
        }

        return info;
    }
}

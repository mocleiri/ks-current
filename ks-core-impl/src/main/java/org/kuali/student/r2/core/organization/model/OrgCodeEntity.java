package org.kuali.student.r2.core.organization.model;


import org.kuali.student.r1.common.entity.KSEntityConstants;
import org.kuali.student.r2.common.dto.AttributeInfo;
import org.kuali.student.r2.common.entity.AttributeOwner;
import org.kuali.student.r2.common.entity.MetaEntity;
import org.kuali.student.r2.common.infc.Attribute;
import org.kuali.student.r2.common.util.RichTextHelper;
import org.kuali.student.r2.core.organization.dto.OrgCodeInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.infc.Org;
import org.kuali.student.r2.core.organization.infc.OrgCode;

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
@Table(name="KSEN_ORG_CD")
public class OrgCodeEntity extends MetaEntity implements AttributeOwner<OrgCodeAttributeEntity> {

    @Column(name = "ORG_CD_TYPE", nullable = false)
    private String orgCodeType;
    @Column(name = "ORG_CD_STATE", nullable = false)
    private String orgCodeState;
    @Column(name = "ORG_ID", nullable = false)
    private String orgId;
    @Column(name = "VALUE", nullable = false)
    private String value;
    @Column(name = "DESCR_PLAIN", length = KSEntityConstants.EXTRA_LONG_TEXT_LENGTH)
    private String descrPlain;
    @Column(name = "DESCR_FORMATTED", length = KSEntityConstants.EXTRA_LONG_TEXT_LENGTH)
    private String descrFormatted;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "owner", fetch = FetchType.EAGER, orphanRemoval = true)
    private final Set<OrgCodeAttributeEntity> attributes = new HashSet<OrgCodeAttributeEntity>();

    public  OrgCodeEntity() {
    }

    public OrgCodeEntity(OrgCode orgCode, OrgEntity owningOrg) {
        super(orgCode);
        setId(orgCode.getId());
        setOrgCodeType(orgCode.getTypeKey());
        setOrgId(owningOrg.getId());
        fromDto(orgCode);
    }

    public String getOrgCodeType() {
        return orgCodeType;
    }

    public void setOrgCodeType(String orgCodeType) {
        this.orgCodeType = orgCodeType;
    }

    public String getOrgCodeState() {
        return orgCodeState;
    }

    public void setOrgCodeState(String orgCodeState) {
        this.orgCodeState = orgCodeState;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    @Override
    public Set<OrgCodeAttributeEntity> getAttributes() {
        return attributes;
    }

    @Override
    public void setAttributes(Set<OrgCodeAttributeEntity> attributes) {
        this.attributes.clear();
        if (attributes != null) {
            this.attributes.addAll(attributes);
        }
    }

    public void fromDto(OrgCode orgCode) {
        setOrgCodeState(orgCode.getStateKey());
        setValue(orgCode.getValue());
        if(orgCode.getDescr() != null) {
            setDescrPlain(orgCode.getDescr().getPlain());
            setDescrFormatted(orgCode.getDescr().getFormatted());
        }

        attributes.clear();
        for (Attribute att : orgCode.getAttributes()) {
            OrgCodeAttributeEntity attEntity = new OrgCodeAttributeEntity(att, this);
            getAttributes().add(attEntity);
        }
    }

    public OrgCodeInfo toDto() {
        OrgCodeInfo info = new OrgCodeInfo();
        info.setId(getId());
        info.setTypeKey(getOrgCodeType());
        info.setStateKey(getOrgCodeState());
        info.setValue(getValue());
        info.setDescr(RichTextHelper.buildRichTextInfo(descrPlain, descrFormatted));
        info.setMeta(super.toDTO());

        for (OrgCodeAttributeEntity att : getAttributes()) {
            AttributeInfo attInfo = att.toDto();
            info.getAttributes().add(attInfo);
        }

        return info;
    }
}

package org.kuali.student.r2.core.organization.model;

import org.kuali.student.r2.common.entity.BaseAttributeEntity;
import org.kuali.student.r2.common.infc.Attribute;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "KSEN_ORG_POS_RESTR_ATTR")
public class OrgPositionRestrictionAttributeEntity extends BaseAttributeEntity<OrgPositionRestrictionEntity> {
    public OrgPositionRestrictionAttributeEntity() {
        super();
    }

    public OrgPositionRestrictionAttributeEntity(Attribute att, OrgPositionRestrictionEntity owner) {
        super(att, owner);
    }
}

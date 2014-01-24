package org.kuali.student.r2.core.organization.model;

import org.kuali.student.r2.common.entity.BaseAttributeEntity;
import org.kuali.student.r2.common.infc.Attribute;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "KSEN_ORG_PERS_REL_ATTR")
public class OrgPersonRelationAttributeEntity extends BaseAttributeEntity<OrgPersonRelationEntity> {
    public OrgPersonRelationAttributeEntity() {
        super();
    }

    public OrgPersonRelationAttributeEntity(Attribute att, OrgPersonRelationEntity owner) {
        super(att, owner);
    }
}

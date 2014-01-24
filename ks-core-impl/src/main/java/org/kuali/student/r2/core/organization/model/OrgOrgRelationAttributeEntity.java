package org.kuali.student.r2.core.organization.model;


import org.kuali.student.r2.common.entity.BaseAttributeEntity;
import org.kuali.student.r2.common.infc.Attribute;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "KSEN_ORG_ORG_REL_ATTR")
public class OrgOrgRelationAttributeEntity extends BaseAttributeEntity<OrgOrgRelationEntity> {
    public OrgOrgRelationAttributeEntity() {
        super();
    }

    public OrgOrgRelationAttributeEntity(Attribute att, OrgOrgRelationEntity owner) {
        super(att, owner);
    }
}

package org.kuali.student.r2.core.organization.model;


import org.kuali.student.r2.common.entity.BaseAttributeEntity;
import org.kuali.student.r2.common.infc.Attribute;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "KSEN_ORG_ATTR")
public class OrgAttributeEntity extends BaseAttributeEntity<OrgEntity> {

    public OrgAttributeEntity() {
        super();
    }

    public OrgAttributeEntity(Attribute att, OrgEntity owner) {
        super(att, owner);
    }
}

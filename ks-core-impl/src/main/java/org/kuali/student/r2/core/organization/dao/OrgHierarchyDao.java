package org.kuali.student.r2.core.organization.dao;


import org.kuali.student.r2.common.dao.GenericEntityDao;
import org.kuali.student.r2.core.organization.model.OrgHierarchyEntity;

import javax.persistence.Query;
import java.util.List;

public class OrgHierarchyDao extends GenericEntityDao<OrgHierarchyEntity> {

    public List<String> getIdsByType(String type) {
        Query query = em.createNamedQuery("OrgHierarchyEntity.getIdsByType");
        query.setParameter("type", type);
        return query.getResultList();
    }
}

package org.kuali.student.r2.core.organization.dao;


import org.kuali.student.r2.common.dao.GenericEntityDao;
import org.kuali.student.r2.core.organization.model.OrgPositionRestrictionEntity;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class OrgPositionRestrictionDao extends GenericEntityDao<OrgPositionRestrictionEntity> {

    public List<String> getOrgPositionRestrictionIdsByType(String type) {
        Query query = em.createNamedQuery("OrgPositionRestrictionEntity.getIdsByOrgPersonRelationType");
        query.setParameter("type", type);
        List<String> results = query.getResultList();
        if(results == null) {
            results = new ArrayList<String>();
        }
        return results;
    }

    public List<String> getOrgPositionRestrictionIdsByOrg(String orgId) {
        Query query = em.createNamedQuery("OrgPositionRestrictionEntity.getIdsByOrgId");
        query.setParameter("orgId", orgId);
        List<String> results = query.getResultList();
        if(results == null) {
            results = new ArrayList<String>();
        }
        return results;
    }

}

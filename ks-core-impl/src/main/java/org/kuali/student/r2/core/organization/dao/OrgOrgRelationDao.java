package org.kuali.student.r2.core.organization.dao;



import org.kuali.student.r2.common.dao.GenericEntityDao;
import org.kuali.student.r2.core.organization.dto.OrgOrgRelationInfo;
import org.kuali.student.r2.core.organization.model.OrgOrgRelationEntity;

import javax.persistence.Query;
import java.util.List;

public class OrgOrgRelationDao extends GenericEntityDao<OrgOrgRelationEntity> {

    public List<String> getIdsByType(String type) {
        Query query = em.createNamedQuery("OrgOrgRelationEntity.getIdsByType");
        query.setParameter("type", type);
        return query.getResultList();
    }

    public Boolean hasOrgOrgRelation(String orgId, String comparisonOrgId, String orgOrgRelationTypeKey) {
        Query query = em.createNamedQuery("OrgOrgRelationEntity.hasOrgOrgRelation");
        query.setParameter("orgId", orgId);
        query.setParameter("relatedOrgId", comparisonOrgId);
        query.setParameter("type", orgOrgRelationTypeKey);
        List<OrgOrgRelationEntity> list = (List<OrgOrgRelationEntity>)query.getResultList();
        return list != null && list.size() > 0;
    }

    public List<OrgOrgRelationEntity> getOrgOrgRelationsByOrg(String orgId) {
        Query query = em.createNamedQuery("OrgOrgRelationEntity.getOrgOrgRelationsByOrgId");
        query.setParameter("orgId", orgId);
        return (List<OrgOrgRelationEntity>)query.getResultList();
    }

    public List<OrgOrgRelationEntity> getOrgOrgRelationsByOrgs(String orgId, String peerOrgId) {
        Query query = em.createNamedQuery("OrgOrgRelationEntity.getOrgOrgRelationsByOrgIdAndRelatedOrgId");
        query.setParameter("orgId", orgId);
        query.setParameter("relatedOrgId", peerOrgId);
        return (List<OrgOrgRelationEntity>)query.getResultList();
    }

    public List<OrgOrgRelationEntity> getOrgOrgRelationsByTypeAndOrg(String orgId, String type) {
        Query query = em.createNamedQuery("OrgOrgRelationEntity.getOrgOrgRelationsByTypeAndOrgId");
        query.setParameter("type", type);
        query.setParameter("orgId", orgId);
        return (List<OrgOrgRelationEntity>)query.getResultList();
    }

    public List<OrgOrgRelationEntity> getOrgOrgRelationsByTypeAndRelatedOrg(String relatedOrgId, String type) {
        Query query = em.createNamedQuery("OrgOrgRelationEntity.getOrgOrgRelationsByTypeAndRelatedOrgId");
        query.setParameter("type", type);
        query.setParameter("relatedOrgId", relatedOrgId);
        return (List<OrgOrgRelationEntity>)query.getResultList();
    }
}

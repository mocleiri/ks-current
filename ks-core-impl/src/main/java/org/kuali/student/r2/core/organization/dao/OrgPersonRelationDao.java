package org.kuali.student.r2.core.organization.dao;



import org.kuali.student.r2.common.dao.GenericEntityDao;
import org.kuali.student.r2.core.organization.model.OrgOrgRelationEntity;
import org.kuali.student.r2.core.organization.model.OrgPersonRelationEntity;

import javax.persistence.Query;
import java.util.List;

public class OrgPersonRelationDao extends GenericEntityDao<OrgPersonRelationEntity> {

    public List<String> getIdsByType(String type) {
        Query query = em.createNamedQuery("OrgPersonRelationEntity.getIdsByType");
        query.setParameter("type", type);
        return query.getResultList();
    }

    public Boolean hasOrgPersonRelation(String orgId, String personId, String orgPersonRelationType) {
        Query query = em.createNamedQuery("OrgPersonRelationEntity.hasOrgPersonRelation");
        query.setParameter("orgId", orgId);
        query.setParameter("personId", personId);
        query.setParameter("type", orgPersonRelationType);
        List<OrgPersonRelationEntity> list = (List<OrgPersonRelationEntity>)query.getResultList();
        return list != null && list.size() > 0;
    }

    public List<OrgPersonRelationEntity> getOrgPersonRelationsByOrg(String orgId) {
        Query query = em.createNamedQuery("OrgPersonRelationEntity.getOrgPersonRelationsByOrg");
        query.setParameter("orgId", orgId);
        return (List<OrgPersonRelationEntity>)query.getResultList();
    }

    public List<OrgPersonRelationEntity> getOrgPersonRelationsByTypeAndOrg(String type, String orgId) {
        Query query = em.createNamedQuery("OrgPersonRelationEntity.getOrgPersonRelationsByTypeAndOrg");
        query.setParameter("type", type);
        query.setParameter("orgId", orgId);
        return (List<OrgPersonRelationEntity>)query.getResultList();
    }

    public List<OrgPersonRelationEntity> getOrgPersonRelationsByPerson(String personId) {
        Query query = em.createNamedQuery("OrgPersonRelationEntity.getOrgPersonRelationsByPerson");
        query.setParameter("personId", personId);
        return (List<OrgPersonRelationEntity>)query.getResultList();
    }

    public List<OrgPersonRelationEntity> getOrgPersonRelationsByTypeAndPerson(String type, String personId) {
        Query query = em.createNamedQuery("OrgPersonRelationEntity.getOrgPersonRelationsByTypeAndPerson");
        query.setParameter("type", type);
        query.setParameter("personId", personId);
        return (List<OrgPersonRelationEntity>)query.getResultList();
    }

    public List<OrgPersonRelationEntity> getOrgPersonRelationsByOrgAndPerson(String orgId, String personId) {
        Query query = em.createNamedQuery("OrgPersonRelationEntity.getOrgPersonRelationsByOrgAndPerson");
        query.setParameter("orgId", orgId);
        query.setParameter("personId", personId);
        return (List<OrgPersonRelationEntity>)query.getResultList();
    }

    public List<OrgPersonRelationEntity> getOrgPersonRelationsByTypeAndOrgAndPerson(String type, String orgId, String personId) {
        Query query = em.createNamedQuery("OrgPersonRelationEntity.getOrgPersonRelationsByOrgAndTypeAndPerson");
        query.setParameter("orgId", orgId);
        query.setParameter("type", type);
        query.setParameter("personId", personId);
        return (List<OrgPersonRelationEntity>)query.getResultList();
    }
}

package org.kuali.student.ap.academicplan.dao;

import java.util.List;

import org.kuali.student.ap.academicplan.model.ReferenceObjectListEntity;
import org.kuali.student.r2.common.dao.GenericEntityDao;

public class ReferenceObjectListDao extends GenericEntityDao<ReferenceObjectListEntity> {

	   @SuppressWarnings("unchecked")
		public List<ReferenceObjectListEntity> getReferenceOjbectListItems(String listId) {
	        return em.createQuery("select rol from ReferenceObjectListEntity rol where rol.listId =:listId")
	                .setParameter("listId", listId).getResultList();
	    }
}

package org.kuali.student.ap.academicplan.dao;

import java.util.List;

import org.kuali.student.ap.academicplan.model.ReferenceObjectListItemEntity;
import org.kuali.student.r2.common.dao.GenericEntityDao;

public class ReferenceObjectListDao extends GenericEntityDao<ReferenceObjectListItemEntity> {

	   @SuppressWarnings("unchecked")
		public List<ReferenceObjectListItemEntity> getReferenceOjbectListItems(String listId) {
	        return em.createQuery("select rol from ReferenceObjectListItemEntity rol where rol.listId =:listId")
	                .setParameter("listId", listId).getResultList();
	    }
}

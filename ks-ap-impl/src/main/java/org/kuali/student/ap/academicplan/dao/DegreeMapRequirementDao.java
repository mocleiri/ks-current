package org.kuali.student.ap.academicplan.dao;

import java.util.Date;
import java.util.List;

import org.kuali.student.ap.academicplan.model.DegreeMapRequirementEntity;
import org.kuali.student.r2.common.dao.GenericEntityDao;

public class DegreeMapRequirementDao extends GenericEntityDao<DegreeMapRequirementEntity> {

    @SuppressWarnings("unchecked")
	public List<DegreeMapRequirementEntity> getDegreeMapRequirementsByDegreeMapIdEffdt(String degreeMapId, Date degreeMapEffectiveDate) {
        return em.createQuery("select dmr from DegreeMapRequirementEntity dmr where dmr.degreeMapId =:degreeMapId and dmr.degreeMapEffectiveDate =:degreeMapEffectiveDate")
                .setParameter("degreeMapId", degreeMapId).setParameter("degreeMapEffectiveDate", degreeMapEffectiveDate).getResultList();
    }
    
}

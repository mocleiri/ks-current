package org.kuali.student.ap.academicplan.infc;

import java.util.Date;
import java.util.List;

import org.kuali.student.r2.common.infc.HasId;
import org.kuali.student.r2.common.infc.HasMeta;

/**
 * Degree Map, which has a list of Degree Map Requirements.
 *
 * @Author mguilla
 */
public interface DegreeMap extends HasId, HasMeta {
	
//TODO
// This needs to extend LearningPlan instead
// and it looks like all that it will have is getRequirements?
	
	/**
     * @name ProgramId
     */
	
	public String getCredentialProgramId();
	
	
	/**
     * @name effectiveDate
     */
    public Date getEffectiveDate();
    
    
	/**
     * @name expirationDate
     */
    public Date getExpirationDate();
	
	
	/**
     * @name Requirements
     */
    public List<DegreeMapRequirement> getRequirements();
    
}

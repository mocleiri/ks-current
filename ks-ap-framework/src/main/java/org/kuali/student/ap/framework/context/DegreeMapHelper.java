package org.kuali.student.ap.framework.context;

import java.util.Date;

import org.kuali.student.ap.academicplan.infc.DegreeMap;


/**
 * Helper that handles configurable actions for accessing learning plans and Plan items.
 */
public interface DegreeMapHelper {
	
	/**
	 * Get a degree map by id and effective date.
	 * 
	 * @param id Degree map ID
	 * @param effDt Effective date
	 * 
	 * @return degree map instance
	 */
	DegreeMap getDegreeMap(String id, Date effDt);

    /**
     * Retrieves the display term id.
     *
     * @return Display Term id
     * @throws Exception 
     */
    public String getDisplayTermId() throws Exception;

}

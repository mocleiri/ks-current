package org.kuali.student.ap.academicplan.infc;

import org.kuali.student.r2.common.infc.HasId;

/**
 *  a list of reference items
 *
 * @Author mguilla
 */
public interface ReferenceObjectList extends HasId, TypedObjectReference {  
    

    /**
    * The list id
    * @name The list id
    */
   
	public String getListId();
	  

}

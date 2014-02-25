package org.kuali.student.ap.academicplan.infc;


/**
 * Student's Learning Plan that contains a list of plan items
 *
 * @Author mguilla
 */
public interface TypedObjectReference  {  

    /**
     * Id to the reference object e.g Course, Placeholder etc
     * @name Reference Object Id
     */
    public String getRefObjectId();

    /**
     * Type of the Reference Object
     * @name Reference Object Type
     */
    public String getRefObjectType();
    
  
}

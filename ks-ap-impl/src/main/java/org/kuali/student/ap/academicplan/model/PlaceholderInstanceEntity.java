package org.kuali.student.ap.academicplan.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.student.ap.academicplan.dto.PlaceholderInstanceInfo;
import org.kuali.student.r2.common.entity.BaseVersionEntity;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;

@Entity
@Table(name = "KSPL_PLH_INSTANCE")
public class PlaceholderInstanceEntity extends BaseVersionEntity implements Comparable<PlaceholderInstanceEntity> {

	// id, obj_id, ver_nbr "covered" by BaseVersionEntity
	// by MetaEntity and it's superclass(ses).

	  	    
	  @Column(name="REF_OBJ_ID")
	  private String refObjectId;
	  
	  @Column(name="REF_OBJ_TYPE")
	  private String refObjectType;
	  
	  @Column(name="PLACEHOLDER_ID")
	  private String placeholderId;
	  
	  @Column(name="ADVISOR_ID")
	  private String advisorId;
	  
	  @Column(name="ADVISOR_ACCEPT")
	  private Boolean advisorOK;
	  
	  @Column(name="STUDENT_ACCEPT")
	  private Boolean studentOK;
	  
	  
	  
    public PlaceholderInstanceEntity() {
        super();
    }

    
    public void copyFromDto(PlaceholderInstanceInfo dto) throws MissingParameterException, DataValidationErrorException {
    	
    	if (this.getId() == null) {
    		throw new DataValidationErrorException("Null id in PlaceholderInstanceEntity. Id must be set by caller.");
    	}
   	
    	if (dto == null){
    		throw new MissingParameterException("null placeholderInstanceInfo");
    	}
        
        //TODO
        //Do I need to do any data validation?
        
        this.setRefObjectId(dto.getRefObjectId());
        this.setRefObjectType(dto.getRefObjectType());
        this.setAdvisorId(dto.getAdvisorId());
        this.setAdvisorOK(dto.isAdvisorOK());
        this.setStudentOK(dto.isStudentOK());      

    }

    
	@Override
	public String toString() {
		return String
				.format("PlaceholderInstance id:  %s, PlaceholderId: %s, RefObjectType %s, RefObjectId: %s. ",
						this.getId(), this.getPlaceholderId(),
						this.getRefObjectType(), this.getRefObjectId());
	}
    
	/**
     * Provides and data transfer object representation of the Placeholder Instance.
     * @return PlaceholderInstanceInfo
     */
    public PlaceholderInstanceInfo toDto() {
    	PlaceholderInstanceInfo dto = new PlaceholderInstanceInfo();

        dto.setId(getId());
        dto.setPlaceholderId(getPlaceholderId());
        dto.setRefObjectId(getRefObjectId());
        dto.setRefObjectType(getRefObjectType());
        dto.setAdvisorId(getAdvisorId());
        dto.setAdvisorOK(isAdvisorOK());
        dto.setStudentOK(isStudentOK());         

        return dto;
    }




	public String getRefObjectType() {
		return refObjectType;
	}


	public void setRefObjectType(String refObjectType) {
		this.refObjectType = refObjectType;
	}


	public String getRefObjectId() {
		return refObjectId;
	}


	public void setRefObjectId(String refObjectId) {
		this.refObjectId = refObjectId;
	}


	public String getPlaceholderId() {
		return placeholderId;
	}


	public void setPlaceholderId(String placeholderId) {
		this.placeholderId = placeholderId;
	}


	public String getAdvisorId() {
		return advisorId;
	}


	public void setAdvisorId(String advisorId) {
		this.advisorId = advisorId;
	}


	public boolean isAdvisorOK() {
		return advisorOK;
	}


	public void setAdvisorOK(boolean advisorOK) {
		this.advisorOK = advisorOK;
	}


	public boolean isStudentOK() {
		return studentOK;
	}


	public void setStudentOK(boolean studentOK) {
		this.studentOK = studentOK;
	}


	@Override
    public int compareTo(PlaceholderInstanceEntity other) {

        if (other == null) {
            return -1;
        }

        if (! other.getId().equals(this.getId())) {
            return this.getId().compareTo(other.getId());
        }
        return 0;
    }
}
package org.kuali.student.ap.academicplan.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.kuali.student.ap.academicplan.dto.ReferenceObjectListInfo;
import org.kuali.student.r2.common.entity.BaseVersionEntity;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;

@Entity
@Table(name = "KSPL_REF_LIST")
public class ReferenceObjectListEntity extends BaseVersionEntity implements Comparable<ReferenceObjectListEntity> {

	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(ReferenceObjectListEntity.class);
	
	
	  @Column(name="LIST_ID")
	  private String listId;
	  	    
	  @Column(name="REF_OBJ_ID")
	  private String refObjectId;
	  
	  @Column(name="REF_OBJ_TYPE")
	  private String refObjectType;
	  

	public void copyFromInfo(ReferenceObjectListInfo dto)
			throws DataValidationErrorException, MissingParameterException {
		
	   	if (dto == null){
    		throw new MissingParameterException("null ReferenceObjectListInfo");
    	}

		if (this.getId() == null) {
			this.setId(dto.getId());
		}
	   	
		if (dto.getListId() == null) {
			throw new DataValidationErrorException(
					"list id  must not be null.");
		}

		if (dto.getRefObjectType() == null) {
			throw new DataValidationErrorException(
					"refOjbectType must not be null.");
		}

		if (dto.getRefObjectId() == null) {
			throw new DataValidationErrorException(
					"refOjbectId must not be null.");
		}
		
		if (getListId() == null) {
			setListId(dto.getListId());
		}

		setRefObjectType(dto.getRefObjectType());
		setRefObjectId(dto.getRefObjectId());

	}

    
    @Override
    public String toString() {
        return String.format(" id:  %s, listId: %s . ", this.getId(), this.getId(), this.getListId());
    }

    
	/**
     * Provides and data transfer object representation of the Degree Map Requirement.
     * @return DegreeMapRequirementInfo
     */
    public ReferenceObjectListInfo toDto() {
    	ReferenceObjectListInfo dto = new ReferenceObjectListInfo();

        dto.setId(this.getId());
        dto.setListId(getListId());
        dto.setRefObjectType(getRefObjectType());
        dto.setRefObjectId(getRefObjectId());

        return dto;
    }


	public String getListId() {
		return listId;
	}


	public void setListId(String listId) {
		this.listId = listId;
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


	@Override
    public int compareTo(ReferenceObjectListEntity other) {

        if (other == null) {
            return -1;
        }

        if (! other.getId().equals(this.getId())) {
            return this.getId().compareTo(other.getId());
        }
        
        return 0;
    }

}
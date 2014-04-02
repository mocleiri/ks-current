package org.kuali.student.ap.academicplan.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.kuali.student.ap.academicplan.dto.TypedObjectReferenceInfo;
import org.kuali.student.ap.academicplan.infc.TypedObjectReference;
import org.kuali.student.r2.common.entity.BaseVersionEntity;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.springframework.util.StringUtils;


@Entity
@Table(name = "KSPL_REF_LIST")
public class ReferenceObjectListItemEntity extends BaseVersionEntity implements TypedObjectReference{

	@SuppressWarnings("unused")
	private final static Logger LOG = Logger.getLogger(ReferenceObjectListItemEntity.class);
	
	
	  @Column(name="LIST_ID")
	  private String listId;
	  	    
	  @Column(name="REF_OBJ_ID")
	  private String refObjectId;
	  
	  @Column(name="REF_OBJ_TYPE")
	  private String refObjectType;
	  

	public void copyFromInfo(TypedObjectReference ref, String listId)
			throws DataValidationErrorException, MissingParameterException {
		
	   	if (ref == null){
    		throw new MissingParameterException("null ref");
    	}

		if (this.getId() == null) {
			this.setId(ref.getId());
		}


		if (ref.getRefObjectType() == null) {
			throw new DataValidationErrorException(
					"refOjbectType must not be null.");
		}

		if (ref.getRefObjectId() == null) {
			throw new DataValidationErrorException(
					"refOjbectId must not be null.");
		}
		
		if (StringUtils.isEmpty(listId)) {
			throw new DataValidationErrorException(
					"list id can't be empty.");
		}

		setRefObjectType(ref.getRefObjectType());
		setRefObjectId(ref.getRefObjectId());
		setListId(listId);

	}

    
    @Override
    public String toString() {
        return String.format(" id:  %s, listId: %s . ", this.getId(), this.getId(), this.getListId());
    }

    
	/**
     * Provides and data transfer object representation of the Degree Map Requirement.
     * @return DegreeMapRequirementInfo
     */
    public TypedObjectReferenceInfo toDto() {
    	TypedObjectReferenceInfo dto = new TypedObjectReferenceInfo();
    	
        dto.setId(this.getId());
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
}
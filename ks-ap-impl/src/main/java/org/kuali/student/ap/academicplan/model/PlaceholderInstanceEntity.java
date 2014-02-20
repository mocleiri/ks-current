package org.kuali.student.ap.academicplan.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.student.ap.academicplan.dto.DegreeMapRequirementInfo;
import org.kuali.student.r2.common.entity.BaseVersionEntity;

@Entity
@Table(name = "KSPL_PLH_INSTANCE")
public class PlaceholderInstanceEntity extends BaseVersionEntity implements Comparable<PlaceholderInstanceEntity> {

	// id, obj_id, ver_nbr "covered" by BaseVersionEntity
	// by MetaEntity and it's superclass(ses).

	  	    
	  @Column(name="REQ_REF_OBJ_ID")
	  private String refObjectId;
	  
	  @Column(name="REF_OBJ_TYPE_KEY")
	  private String refObjectTypeKey;
	  
	  @Column(name="PLACEHOLDER_ID")
	  private String placeholderId;
	  
	  @Column(name="ADVISOR_ID")
	  private String advisorId;
	  
	  @Column(name="ADVISOR_ACCEPT")
	  private boolean advisorOK;
	  
	  @Column(name="STUDENT_ACCEPT")
	  private boolean studentOK;
	  
	  
	  
    public PlaceholderInstanceEntity() {
        super();
    }

    
    public PlaceholderInstanceEntity(DegreeMapRequirementInfo dto) {
        super();
 
        //TODO
        // fix this
        this.setId(dto.getId());
//       
//        this.setCredit(dto.getCredit());
//        this.setCritical(dto.isCritical());
//        this.setDegreeMapEffectiveDate(dto.getDegreeMapEffectiveDate());
//        this.setDegreeMapId(dto.getDegreeMapId());
//        this.setDescr(dto.getDescr());
//        this.setDisplayTermId(dto.getDisplayTermId());
//        this.setItemSeq(dto.getItemSeq());
//        this.setMilestone(dto.isMilestone());
//        this.setMininumGrade(dto.getMinimumGrade());
//        this.setNotes(dto.getNotes());
//        this.setRefObjectTypeKey(dto.getRefObjectTypeKey());
//        this.setRefObjectId(dto.getRefObjectId());
//        this.setRequiredTermId(dto.getRequiredTermId());
//        this.setSuggestedTermId(dto.getSuggestedTermId());
//        this.setSeqKey(dto.getSeqKey());
//        this.setSeqNo(dto.getSeqNo());      

    }
    
    
	public static PlaceholderInstanceEntity create(DegreeMapRequirementInfo dto) {

	    return new PlaceholderInstanceEntity(dto);

	}

    
    @Override
    public String toString() {
    	//TODO
    	// FIX THIS
    	
    	return null;
//        return String.format("Requirement id:  %s, degreeMapId: %s, degreeMapEffectiveDate %d, descr: %s. ", this.getId(), this.getDegreeMapId(), this.getDegreeMapId(), this.getDescr());
    }

    
	/**
     * Provides and data transfer object representation of the Degree Map Requirement.
     * @return DegreeMapRequirementInfo
     */
    public DegreeMapRequirementInfo toDto() {
        DegreeMapRequirementInfo dto = new DegreeMapRequirementInfo();
//TODO
 // FIX THIS
        
//        dto.setCredit(getCredit());
//        dto.setCritical(isCritical());
//        dto.setDegreeMapEffectiveDate(getDegreeMapEffectiveDate());
//        dto.setDegreeMapId(getDegreeMapId());
//        dto.setDescr(getDescr());
//        dto.setDisplayTermId(getDisplayTermId());
//        dto.setItemSeq(getItemSeq());
//        dto.setMilestone(isMilestone());
//        dto.setMinimumGrade(getMinimumGrade());
//        dto.setNotes(getNotes());
//        dto.setRefObjectTypeKey(getRefObjectTypeKey());
//        dto.setRefObjectId(getRefObjectId());
//        dto.setRequiredTermId(getRequiredTermId());
//        dto.setSuggestedTermId(getSuggestedTermId());
//        dto.setSeqKey(getSeqKey());
//        dto.setSeqNo(getSeqNo());      

        return dto;
    }






	public String getRefObjectTypeKey() {
		return refObjectTypeKey;
	}


	public void setRefObjectTypeKey(String refObjectTypeKey) {
		this.refObjectTypeKey = refObjectTypeKey;
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

        //  First check id.
        if (! other.getId().equals(this.getId())) {
            return this.getId().compareTo(other.getId());
        }
 //TODO
  //FIX THIS
//        // compare the degree map id
//        if (!other.getDegreeMapId().equals(this.getDegreeMapId())){
//        	return this.getDegreeMapId().compareTo(other.getDegreeMapId());
//        }
//        
//        // compare the degree map effective date
//        if (!other.getDegreeMapEffectiveDate().equals(this.getDegreeMapEffectiveDate())){
//        	return this.getDegreeMapEffectiveDate().compareTo(other.getDegreeMapEffectiveDate());
//        }
        
        // if they are all the same, it's the same requirement.
        return 0;
    }
}
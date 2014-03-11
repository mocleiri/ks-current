package org.kuali.student.ap.academicplan.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.student.ap.academicplan.dto.DegreeMapRequirementInfo;
import org.kuali.student.r2.common.entity.BaseVersionEntity;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;

@Entity
@Table(name = "KSDM_REQUIREMENT")
public class DegreeMapRequirementEntity extends BaseVersionEntity implements Comparable<DegreeMapRequirementEntity> {

	// id, obj_id, ver_nbr "covered" by BaseVersionEntity
	// by MetaEntity and it's superclass(ses).

	
	
// If the OneToMany uses a foreign key in the target object's table JPA requires that the relationship be bi-directional 
// (inverse ManyToOne relationship must be defined in the target object), and the source object must use the mappedBy 
// attribute to define the mapping. 
// so here we are using the degreeMap property.
	
//	  @ManyToOne(fetch=FetchType.LAZY)  
//	    @JoinColumns({
//	        @JoinColumn(name="DM_ID", referencedColumnName="ID"),
//	        @JoinColumn(name="DM_EFF_DT", referencedColumnName="EFF_DT")
//	    })
//	  private DegreeMap degreeMap;
//	
//	  @OneToOne(fetch = FetchType.LAZY)
//	  @JoinColumn(name = "REF_OBJ_ID")
//	  private Placeholder placeholder;
	
	
	  @Column(name="DM_ID")
	  private String degreeMapId;
	  
	  @Column(name="DM_EFFDT")
	  private Date degreeMapEffectiveDate;
	  	    	  
	  @Column(name="ITEM_SEQ")
	  private int itemSeq;
	  	    
	  @Column(name="REF_OBJ_ID")
	  private String refObjectId;
	  
	  @Column(name="REF_OBJ_TYPE")
	  private String refObjectType;
	  
	  @Column(name="DESCR")
	  private String descr;
	  
	  @Column(name="CREDIT")
	  private BigDecimal credit;
	  
	  @Column(name="CRITICAL")
	  private Boolean critical;
	  
	  @Column(name="MILESTONE")
	  private Boolean milestone;
	  
	  @Column(name="MIN_GRADE")
	  private String mininumGrade;
	  
	  @Column(name="SEQ_KEY")
	  private String seqKey;
	  
	  @Column(name="SEQ_NO")
	  private int seqNo;

	  @Column(name="SUGGESTED_TERM_ID")
	  private String suggestedTermId;
	  
	  @Column(name="REQUIRED_TERM_ID")
	  private String requiredTermId;
	  
	  @Column(name="NOTES")
	  private String notes;
	  

	public void copyFromInfo(DegreeMapRequirementInfo dto)
			throws DataValidationErrorException, MissingParameterException {
		
	   	if (dto == null){
    		throw new MissingParameterException("null degreeMapRequirementInfo");
    	}

	   	//TODO
	   	// I had to remove this and set the id because otherwise in DegreeMapServiceImpl.getRequirements I would get requirements
	   	// that had an id of null
		if (this.getId() == null) {
			this.setId(dto.getId());
			// throw new
			// DataValidationErrorException("Null id in DegreeMapRequirementEntity. Id must be set by caller.");
		}

		//TODO
		// Do these checks seem justified?
	   	
		if (dto.getDegreeMapId() == null) {
			throw new DataValidationErrorException(
					"requirement degreeMapId  must not be null.");
		}

		if (dto.getDegreeMapEffectiveDate() == null) {
			throw new DataValidationErrorException(
					"requirement degreeMap effective date must not be null.");
		}

		if (dto.getRefObjectType() == null) {
			throw new DataValidationErrorException(
					"requirement refOjbectType must not be null.");
		}

		if (dto.getRefObjectId() == null) {
			throw new DataValidationErrorException(
					"requirement refOjbectId must not be null.");
		}

		// in a "create" situation, the dm id and effdt will be copied
		// in an "update" situation, they will not be.
		
		if (getDegreeMapId() == null) {
			setDegreeMapId(dto.getDegreeMapId());
		}

		if (getDegreeMapEffectiveDate() == null) {
			setDegreeMapEffectiveDate(dto.getDegreeMapEffectiveDate());
		}

		setCredit(dto.getCredit());
		setCritical(dto.isCritical());
		setDescr(dto.getDescr());
		setItemSeq(dto.getItemSeq());
		setMilestone(dto.isMilestone());
		setMininumGrade(dto.getMinimumGrade());
		setNotes(dto.getNotes());
		setRefObjectType(dto.getRefObjectType());
		setRefObjectId(dto.getRefObjectId());
		setRequiredTermId(dto.getRequiredTermId());
		setSuggestedTermId(dto.getSuggestedTermId());
		setSeqKey(dto.getSeqKey());
		setSeqNo(dto.getSeqNo());

	}

    
    @Override
    public String toString() {
        return String.format("Requirement id:  %s, degreeMapId: %s, degreeMapEffectiveDate %d, descr: %s. ", this.getId(), this.getDegreeMapId(), this.getDegreeMapId(), this.getDescr());
    }

    
	/**
     * Provides and data transfer object representation of the Degree Map Requirement.
     * @return DegreeMapRequirementInfo
     */
    public DegreeMapRequirementInfo toDto() {
        DegreeMapRequirementInfo dto = new DegreeMapRequirementInfo();

        
        dto.setCredit(getCredit());
        dto.setCritical(isCritical());
        dto.setDegreeMapEffectiveDate(getDegreeMapEffectiveDate());
        dto.setDegreeMapId(getDegreeMapId());
        dto.setDescr(getDescr());
        dto.setItemSeq(getItemSeq());
        dto.setMilestone(isMilestone());
        dto.setMinimumGrade(getMinimumGrade());
        dto.setNotes(getNotes());
        dto.setRefObjectType(getRefObjectType());
        dto.setRefObjectId(getRefObjectId());
        dto.setRequiredTermId(getRequiredTermId());
        dto.setSuggestedTermId(getSuggestedTermId());
        dto.setSeqKey(getSeqKey());
        dto.setSeqNo(getSeqNo());      

        return dto;
    }


	public String getDegreeMapId() {
		return degreeMapId;
	}


	public void setDegreeMapId(String degreeMapId) {
		this.degreeMapId = degreeMapId;
	}


	public Date getDegreeMapEffectiveDate() {
		return degreeMapEffectiveDate;
	}


	public void setDegreeMapEffectiveDate(Date degreeMapEffectiveDate) {
		this.degreeMapEffectiveDate = degreeMapEffectiveDate;
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


	public String getDescr() {
		return descr;
	}


	public void setDescr(String descr) {
		this.descr = descr;
	}


	public String getMininumGrade() {
		return mininumGrade;
	}


	public void setMininumGrade(String mininumGrade) {
		this.mininumGrade = mininumGrade;
	}

	public BigDecimal getCredit() {
		return credit;
	}


	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}


	public boolean isCritical() {
		return critical;
	}


	public void setCritical(boolean critical) {
		this.critical = critical;
	}


	public boolean isMilestone() {
		return milestone;
	}


	public void setMilestone(boolean milestone) {
		this.milestone = milestone;
	}


	public String getMinimumGrade() {
		return mininumGrade;
	}


	
	public int getItemSeq() {
		return itemSeq;
	}


	public void setItemSeq(int itemSeq) {
		this.itemSeq = itemSeq;
	}


	public String getSeqKey() {
		return seqKey;
	}


	public void setSeqKey(String seqKey) {
		this.seqKey = seqKey;
	}


	public int getSeqNo() {
		return seqNo;
	}


	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}


	public String getSuggestedTermId() {
		return suggestedTermId;
	}


	public void setSuggestedTermId(String suggestedTermId) {
		this.suggestedTermId = suggestedTermId;
	}


	public String getRequiredTermId() {
		return requiredTermId;
	}


	public void setRequiredTermId(String requiredTermId) {
		this.requiredTermId = requiredTermId;
	}


	public String getNotes() {
		return notes;
	}


	public void setNotes(String notes) {
		this.notes = notes;
	}


	@Override
    public int compareTo(DegreeMapRequirementEntity other) {

        if (other == null) {
            return -1;
        }

        //  First check id.
        if (! other.getId().equals(this.getId())) {
            return this.getId().compareTo(other.getId());
        }
               
        // compare the degree map id
        if (!other.getDegreeMapId().equals(this.getDegreeMapId())){
        	return this.getDegreeMapId().compareTo(other.getDegreeMapId());
        }
        
        // compare the degree map effective date
        if (!other.getDegreeMapEffectiveDate().equals(this.getDegreeMapEffectiveDate())){
        	return this.getDegreeMapEffectiveDate().compareTo(other.getDegreeMapEffectiveDate());
        }
        
        // if they are all the same, it's the same requirement.
        return 0;
    }

}
package org.kuali.student.ap.academicplan.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.student.ap.academicplan.dto.DegreeMapRequirementInfo;
import org.kuali.student.ap.academicplan.infc.DegreeMap;
import org.kuali.student.ap.academicplan.infc.Placeholder;
import org.kuali.student.r2.common.entity.BaseVersionEntity;

@Entity
@Table(name = "KSDM_REQUIREMENT")
public class DegreeMapRequirementEntity extends BaseVersionEntity implements Comparable<DegreeMapRequirementEntity> {

	// id, obj_id, ver_nbr "covered" by BaseVersionEntity
	// by MetaEntity and it's superclass(ses).
	// QUESTION: why is the id set specifically in the toDto? Wouldn't the Meta take care of it? 
	
	
    // If the OneToMany uses a foreign key in the target object's table JPA requires that the relationship be bi-directional 
    // (inverse ManyToOne relationship must be defined in the target object), and the source object must use the mappedBy 
    // attribute to define the mapping. 
    // so here we are using the degreeMap property.
	
	  @ManyToOne(fetch=FetchType.LAZY)  
	    @JoinColumns({
	        @JoinColumn(name="DM_ID", referencedColumnName="ID"),
	        @JoinColumn(name="DM_EFF_DT", referencedColumnName="EFF_DT")
	    })
	  private DegreeMap degreeMap;
	
	  @OneToOne(fetch = FetchType.LAZY)
	  @JoinColumn(name = "REQ_REF_OBJ_ID")
	  private Placeholder placeholder;
	  
	  // what to do about the REF_OJB_TYPE_KEY
	    
	  @Column(name="DISPLAY_TERM_ID")
	  private String displayTermId;
	  
	  @Column(name="ITEM_SEQ")
	  private int itemSeq;
	    
	  @Column(name="DESCR")
	  private String descr;
	  
	  @Column(name="CREDIT")
	    private BigDecimal credit;
	  
	  @Column(name="CRITICAL")
	  private boolean critical;
	  
	  @Column(name="MILESTONE")
	  private boolean milestone;
	  
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
	  
	  
	  
    public DegreeMapRequirementEntity() {
        super();
    }

    
    public DegreeMapRequirementEntity(DegreeMapRequirementInfo dto) {
        super();
        //here I need to do some work.
        // to add all the fields that are not in the super
        // basically the opposite of toDTO
        
        /*
         *         DegreeMapRequirementInfo dto = new DegreeMapRequirementInfo();

        //TODO
        // why am I dealing with this? I thought the meta had the id?
        dto.setId(getId());
        dto.setCredit(getCredit());
        dto.setCritical(isCritical());
        dto.setDegreeMap(getDegreeMap());
        dto.setDescr(getDescr());
        dto.setMinimumGrade(getMinimumGrade());
        dto.setDegreeMap(this.getDegreeMap());
        dto.setMilestone(this.isMilestone());
        dto.setDescr(this.getDescr());
        dto.setSeqKey(this.getSeqKey());
        dto.setSeqNo(this.getSeqNo());
        dto.setYearSeq(this.getYearSeq());
        dto.setTermTypeKey(this.getTermTypeKey());
        return dto;
         */
    }
    
	public static DegreeMapRequirementEntity create(DegreeMapRequirementInfo dto) {

	    return new DegreeMapRequirementEntity(dto);

	}

    
    @Override
    public String toString() {
        return String.format("Requirement id:  %s, descr: % . TODO: Provide more details.", this.getId(), this.getDescr());
    }

    
	/**
     * Provides and data transfer object representation of the Degree Map Requirement.
     * @return DegreeMapRequirementInfo
     */
    public DegreeMapRequirementInfo toDto() {
        DegreeMapRequirementInfo dto = new DegreeMapRequirementInfo();

        dto.setId(getId());
        dto.setCredit(getCredit());
        dto.setCritical(isCritical());
        dto.setDegreeMap(getDegreeMap());
   //     dto.setDescr(getDescr());
        dto.setMinimumGrade(getMinimumGrade());
        dto.setDegreeMap(this.getDegreeMap());
        dto.setMilestone(this.isMilestone());
    ///    dto.setDescr(this.getDescr());
        dto.setSeqKey(this.getSeqKey());
        dto.setSeqNo(this.getSeqNo());
   //     dto.setYearSeq(this.getYearSeq());
   //     dto.setTermTypeKey(this.getTermTypeKey());
        return dto;
    }



	public String getDisplayTermId() {
		return displayTermId;
	}


	public void setDisplayTermId(String displayTermId) {
		this.displayTermId = displayTermId;
	}


	public DegreeMap getDegreeMap() {
		return degreeMap;
	}


	public void setDegreeMap(DegreeMap degreeMap) {
		this.degreeMap = degreeMap;
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


	public Placeholder getPlaceholder() {
		return placeholder;
	}


	public void setPlaceholder(Placeholder placeholder) {
		this.placeholder = placeholder;
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

//TODO
// would we ever have the same id but different degree map id or effective date?
// I'm thinking every time we have a new effective date we would want to generate a new
// set of requirements and would want those to have a new set of ids, which would be unique...?
// Did we discuss what the id for the degree map requirements would look like?
        
       
        DegreeMap this_dm = this.getDegreeMap();
        DegreeMap other_dm = other.getDegreeMap();
        
        // compare the degree map id
        if (!other_dm.getId().equals(this_dm.getId())){
        	return this_dm.getId().compareTo(other_dm.getId());
        }
        
        // compare the degree map effective date
        if (!other_dm.getEffectiveDate().equals(this_dm.getEffectiveDate())){
        	return this_dm.getEffectiveDate().compareTo(other_dm.getEffectiveDate());
        }
        

        return 0;
    }
}
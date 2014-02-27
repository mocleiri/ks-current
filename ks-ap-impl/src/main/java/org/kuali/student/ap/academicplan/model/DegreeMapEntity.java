package org.kuali.student.ap.academicplan.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.kuali.student.ap.academicplan.dto.DegreeMapInfo;
import org.kuali.student.ap.academicplan.infc.DegreeMapRequirement;
import org.kuali.student.r2.common.entity.MetaEntity;

@Entity
@Table(name = "KSDM_DEGREE_MAP")
public class DegreeMapEntity extends MetaEntity implements Comparable<DegreeMapEntity> {

	// id, obj_id, ver_nbr, createid, createtime, updateid, updatetime are "covered" 
	// by MetaEntity and it's superclass(ses).
	// QUESTION: why is the id set specifically in the toDto? Wouldn't the Meta take care of it? 
	
    @Column(name = "PROGRAM_ID")
    private String programId;
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EFF_DT")
    private Date effectiveDate;
    
    
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="EXPIR_DT")
    private Date expirationDate;
 
    // If the OneToMany uses a foreign key in the target object's table JPA requires that the relationship be bi-directional 
    // (inverse ManyToOne relationship must be defined in the target object), and the source object must use the mappedBy 
    // attribute to define the mapping. 
    // so here we are using the degreeMap property, which will be mapped do the DM_REF_OBJ_ID column in KSDM_REQUIREMENT
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "degreeMap", orphanRemoval = true)
    private  List<DegreeMapRequirement> requirements;
    
    

    
    public DegreeMapEntity() {
        super();
    }

    public DegreeMapEntity(DegreeMapInfo dto) {
        super();
        //here I need to do some work.
        // to add all the fields that are not in the super
        // basically the opposite of toDTO
        
        /*
         *         DegreeMapInfo dto = new DegreeMapInfo();

        //TODO
        // why am I dealing with this? I thought the meta had the id?
        dto.setId(getId());
        dto.setMeta(super.toDTO());
        dto.setCredentialProgramId(getCredentialProgramId());
        dto.setEffectiveDate(this.getEffectiveDate());
        dto.setExpirationDate(this.getExpirationDate());
        dto.setRequirements(this.getRequirements());
        return dto;
         */
        
        this.setId(dto.getId());
    //    this.setCreateId(dto.get);
    }
    
	public static DegreeMapEntity create(DegreeMapInfo dto) {

	    return new DegreeMapEntity(dto);

	}

    @Override
    public String toString() {
        return String.format("DegreeMap id: %s , Effective Date: %d , programId: %s", this.getId(),  this.getEffectiveDate(), this.getProgramId());
    }
    
    

    public String getProgramId() {
		return programId;
	}


	public void setProgramId(String programId) {
		this.programId = programId;
	}

	

	public Date getEffectiveDate() {
		return effectiveDate;
	}


	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}


	public Date getExpirationDate() {
		return expirationDate;
	}


	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}


	public List<DegreeMapRequirement> getRequirements() {
		return requirements;
	}


	public void setRequirements(List<DegreeMapRequirement> requirements) {
		this.requirements = requirements;
	}


	

	/**
     * Provides and data transfer object representation of the Degree Map.
     * @return DegreeMapInfo
     */
    public DegreeMapInfo toDto() {
        DegreeMapInfo dto = new DegreeMapInfo();

        //TODO
        // why am I dealing with this? I thought the meta had the id?
        dto.setId(getId());
        dto.setMeta(super.toDTO());
        dto.setProgramId(getProgramId());
        dto.setEffectiveDate(this.getEffectiveDate());
        dto.setExpirationDate(this.getExpirationDate());
        dto.setRequirements(this.getRequirements());
        return dto;
    }


    @Override
    public int compareTo(DegreeMapEntity other) {

        if (other == null) {
            return -1;
        }

        // First check id.

        if ( !other.getId().equals(this.getId())) {
        	return this.getId().compareTo(other.getId()) ;
        }

        // next check effective date
         
        if (!other.getEffectiveDate().equals(this.getEffectiveDate())){
        	return this.getEffectiveDate().compareTo(other.getEffectiveDate()) ;
        }
        
        
        return 0;
    }
}
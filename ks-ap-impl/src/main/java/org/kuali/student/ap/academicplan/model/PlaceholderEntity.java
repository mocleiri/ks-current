package org.kuali.student.ap.academicplan.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.kuali.student.ap.academicplan.dto.PlaceholderInfo;
import org.kuali.student.r2.common.entity.BaseVersionEntity;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;

@Entity
@Table(name = "KSPL_PLACEHOLDER")
public class PlaceholderEntity extends BaseVersionEntity implements Comparable<PlaceholderEntity> {

	// id, obj_id, ver_nbr "covered" by BaseVersionEntity
	// by MetaEntity and it's superclass(ses).
	
    // If the OneToMany uses a foreign key in the target object's table JPA requires that the relationship be bi-directional 
    // (inverse ManyToOne relationship must be defined in the target object), and the source object must use the mappedBy 
    // attribute to define the mapping. 
    // so here we are using the degreeMap property.
	
// got rid of this because it's covered by BaseEntity
//	@Column(name = "ID")
//	private String id;

	@Column(name = "TYPE_KEY")
	private String typeKey;

	@Column(name = "PARM1")
	private String parm1;

	@Column(name = "PARM2")
	private String parm2;

	@Column(name = "PARM3")
	private String parm3;
	
	@Column(name = "PARM4")
	private String parm4;

//	@OneToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "OBJ_ID")
//	private DegreeMapRequirement degreeMapRequirement;

    
    @Override
    public String toString() {
        return String.format("Placeholder: id: %1 typeKey: %s ", this.getId(), this.getTypeKey());
    }
   
    
	/**
     * Provides and data transfer object representation of the Degree Map Requirement.
     * @return PlaceholderInfo
     */
    public PlaceholderInfo toDto() {
    	PlaceholderInfo dto = new PlaceholderInfo();
    	dto.setId(this.getId());
    	dto.setTypeKey(this.getTypeKey());
    	dto.setParm1(this.getParm1());
    	dto.setParm2(this.getParm2());
    	dto.setParm3(this.getParm3());
    	dto.setParm4(this.getParm4());
        return dto;
    }

    
    public void copyFromInfo(PlaceholderInfo dto) throws MissingParameterException, DataValidationErrorException{
    	
    	if (this.getId() == null) {
    		throw new DataValidationErrorException("Null id in PlaceholderEntity. Id must be set by caller.");
    	}

    	
    	if (dto == null){
    		throw new MissingParameterException("Null placeholderInfo");
    	}
    	
    	if (dto.getTypeKey() == null) {
    		throw new DataValidationErrorException("Null typekey in dto");
    	}
		   	
		setTypeKey(dto.getTypeKey());
		
		setParm1(dto.getParm1());
		setParm2(dto.getParm2());
		setParm3(dto.getParm3());
		setParm4(dto.getParm4());
    	
    }
    

//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}

	public String getTypeKey() {
		return typeKey;
	}

	public void setTypeKey(String typeKey) {
		this.typeKey = typeKey;
	}

	public String getParm1() {
		return parm1;
	}

	public void setParm1(String parm1) {
		this.parm1 = parm1;
	}

	public String getParm2() {
		return parm2;
	}

	public void setParm2(String parm2) {
		this.parm2 = parm2;
	}

	public String getParm3() {
		return parm3;
	}

	public void setParm3(String parm3) {
		this.parm3 = parm3;
	}

	public String getParm4() {
		return parm4;
	}


	public void setParm4(String parm4) {
		this.parm4 = parm4;
	}


	@Override
    public int compareTo(PlaceholderEntity other) {

        if (other == null) {
            return -1;
        }

        //  check id.
        if (! other.getId().equals(this.getId())) {
            return this.getId().compareTo(other.getId());
        }

        return 0;
    }
}
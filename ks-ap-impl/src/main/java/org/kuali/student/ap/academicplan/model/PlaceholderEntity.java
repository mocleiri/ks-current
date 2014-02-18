package org.kuali.student.ap.academicplan.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.kuali.student.ap.academicplan.dto.PlaceholderInfo;
import org.kuali.student.ap.academicplan.infc.DegreeMapRequirement;
import org.kuali.student.r2.common.entity.BaseVersionEntity;

@Entity
@Table(name = "KSPL_PLACEHOLDER")
public class PlaceholderEntity extends BaseVersionEntity implements Comparable<PlaceholderEntity> {

	// id, obj_id, ver_nbr "covered" by BaseVersionEntity
	// by MetaEntity and it's superclass(ses).
	
    // If the OneToMany uses a foreign key in the target object's table JPA requires that the relationship be bi-directional 
    // (inverse ManyToOne relationship must be defined in the target object), and the source object must use the mappedBy 
    // attribute to define the mapping. 
    // so here we are using the degreeMap property.
	

	@Column(name = "ID")
	private String id;

	@Column(name = "TYPE_KEY")
	private String typeKey;

	@Column(name = "PARM1")
	private String parm1;

	@Column(name = "PARM2")
	private String parm2;

	@Column(name = "PARM3")
	private String parm3;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OBJ_ID")
	private DegreeMapRequirement degreeMapRequirement;
    

	//TODO
	// why do I have this one?
 
    public PlaceholderEntity() {
        super();
    }

    
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
        return dto;
    }


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public DegreeMapRequirement getDegreeMapRequirement() {
		return degreeMapRequirement;
	}

	public void setDegreeMapRequirement(DegreeMapRequirement degreeMapRequirement) {
		this.degreeMapRequirement = degreeMapRequirement;
	}

	@Override
    public int compareTo(PlaceholderEntity other) {

        if (other == null) {
            return -1;
        }

        //  First check id.
        if (! other.getId().equals(this.getId())) {
            return this.getId().compareTo(other.getId());
        }

        // do I need to check the programId? I'm thinking not.

        return 0;
    }
}
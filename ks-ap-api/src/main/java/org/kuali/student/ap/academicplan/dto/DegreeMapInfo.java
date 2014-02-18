package org.kuali.student.ap.academicplan.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import org.kuali.student.ap.academicplan.infc.DegreeMap;
import org.kuali.student.ap.academicplan.infc.DegreeMapRequirement;
import org.kuali.student.r2.common.dto.MetaInfo;
import org.w3c.dom.Element;

/**
 * Degree Map message structure
 *
 * @Author mguilla
 * Date: 1/21/14
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DegreeMapInfo", propOrder = { "id", "credentialProgramId", "effectiveDate", "expirationDate", "meta", "requirements", "_futureElements"})
public class DegreeMapInfo implements DegreeMap {

    @XmlAttribute
    private String id;
    
    @XmlAttribute
    private String credentialProgramId;
    
    @XmlAttribute
    private Date effectiveDate;
    
    @XmlAttribute
    private Date expirationDate;
    
	@XmlElement
    private MetaInfo meta;

	@XmlElement
	private List<DegreeMapRequirement> requirements;

    @XmlAnyElement
    private List<Element> _futureElements;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getCredentialProgramId() {
		return credentialProgramId;
	}


	public void setCredentialProgramId(String credentialProgramId) {
		this.credentialProgramId = credentialProgramId;
	}


	public MetaInfo getMeta() {
		return meta;
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


	public void setMeta(MetaInfo meta) {
		this.meta = meta;
	}

	
	public List<Element> get_futureElements() {
		return _futureElements;
	}


	public void set_futureElements(List<Element> _futureElements) {
		this._futureElements = _futureElements;
	}


	public List<DegreeMapRequirement> getRequirements() {
		return requirements;
	}


	public void setRequirements(List<DegreeMapRequirement> requirements) {
		this.requirements = requirements;
	}

   
}

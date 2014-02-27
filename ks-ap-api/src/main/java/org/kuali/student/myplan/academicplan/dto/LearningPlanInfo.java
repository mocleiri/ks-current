package org.kuali.student.myplan.academicplan.dto;

import org.kuali.student.myplan.academicplan.infc.LearningPlan;
import org.kuali.student.r2.common.dto.MetaInfo;
import org.kuali.student.r2.common.dto.RichTextInfo;
import org.kuali.student.r2.common.dto.TypeStateEntityInfo;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * LearningPlan message structure
 * 
 * @Author kmuthu Date: 1/5/12
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LearningPlanInfo", propOrder = { "studentId", "id", "typeKey",
		"stateKey", "shared", "programId", "effectiveDate", "expirationDate",
		"descr", "meta", "attributes", "_futureElements" })
public class LearningPlanInfo extends TypeStateEntityInfo implements
		LearningPlan {

	private static final long serialVersionUID = -121328557346975636L;

	@XmlAttribute
	private String id;

	@XmlElement
	private RichTextInfo descr;

	@XmlElement
	private String studentId;

	@XmlElement
	private Boolean shared;

	@XmlAttribute
	private String programId;

	@XmlAttribute
	private Date effectiveDate;

	@XmlAttribute
	private Date expirationDate;

	@XmlAnyElement
	private List<Element> _futureElements;

	public LearningPlanInfo() {
		this.id = null;
		this.descr = null;
		this.studentId = null;
		this._futureElements = null;
	}

	public LearningPlanInfo(LearningPlan plan) {
		super(plan);

		if (null != plan) {
			this.id = plan.getId();
			this.studentId = plan.getStudentId();
			this.descr = (null != plan.getDescr()) ? new RichTextInfo(
					plan.getDescr()) : null;
		}
	}

	@Override
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public RichTextInfo getDescr() {
		return descr;
	}

	public void setDescr(RichTextInfo descr) {
		this.descr = descr;
	}

	@Override
	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	@Override
	public Boolean getShared() {
		return shared;
	}

	public void setShared(Boolean shared) {
		this.shared = shared;
	}

	public String getProgramId() {
		return programId;
	}

	public void setProgramId(String credentialProgramId) {
		this.programId = credentialProgramId;
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

}

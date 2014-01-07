package com.sigmasys.kuali.ksa.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * This class represents a learning unit ("class") taken by a student within a specific period.
 * 
 * @author Sergey
 * @version 1.0
 */
@Entity
@Table(name = "KSSA_LU")
public class LearningUnit extends AccountIdAware implements Identifiable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Unique identifier.
	 */
	private Long id;
	
	/**
	 * Period for which these learning units relate.
	 */
	private LearningPeriod learningPeriod;
	
	/**
	 * The date the learning unit was added by the student.
	 */
	private Date addDate;
	
	/**
	 * If not null, the date the learning unit was dropped by the student.
	 */
	private Date dropDate;
	
	/**
	 * Unit code for the learning unit (often a course code).
	 */
	private String unitCode;
	
	/**
	 * Section code for the learning unit.
	 */
	private String unitSection;
	
	/** 
	 * Campus where the learning unit is taught.
	 */
	private String campus;
	
	/**
	 * The level of the unit. Examples might include undergraduate, graduate, etc.
	 */
	private String level;
	
	/**
	 * Number of credits available for the learning unit.
	 * This is defined as a double since some European courses may 
	 * yield fractional credits (1.75, 2.25 etc).
	 */
	private BigDecimal credit;
	
	/**
	 * Free-form status indicator for the learning unit, used to store a status for the LU in the rules engine. 
	 * This is in addition to the KeyPairs available under the extended section of the object.
	 */
	private String status;
	
	/**
	 * A set of PeriodKeyPair that represent extended elements of the LearningUnit.
	 */
	private Set<KeyPair> extended;
	
	/**
	 * The Account associated with this LearningUnit.
	 */
	private Account account;
	
	

    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @TableGenerator(name = "TABLE_GEN_LEARNING_UNIT",
            table = "KSSA_SEQUENCE_TABLE",
            pkColumnName = "SEQ_NAME",
            valueColumnName = "SEQ_VALUE",
            pkColumnValue = "LEARNING_UNIT_SEQ")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_LEARNING_UNIT")
	@Override
	public Long getId() {
		return id;
	}

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "LEARNING_PERIOD_ID_FK")
	public LearningPeriod getLearningPeriod() {
		return learningPeriod;
	}

    @Column(name="ADD_DATE")
	public Date getAddDate() {
		return addDate;
	}

    @Column(name="DROP_DATE")
	public Date getDropDate() {
		return dropDate;
	}

    @Column(name="UNIT_CODE", length=45)
	public String getUnitCode() {
		return unitCode;
	}

    @Column(name="UNIT_SECTION", length=45)
	public String getUnitSection() {
		return unitSection;
	}

    @Column(name="CAMPUS", length=45)
	public String getCampus() {
		return campus;
	}

    @Column(name="LU_LEVEL", length=256)
	public String getLevel() {
		return level;
	}

	@Column(name="CREDIT", precision=3, scale=2)
	public BigDecimal getCredit() {
		return credit;
	}

	@Column(name="STATUS", length=45)
	public String getStatus() {
		return status;
	}

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "KSSA_LU_KYPR",
            joinColumns = {
                    @JoinColumn(name = "LU_ID_FK")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "KYPR_ID_FK")
            }
    )
	public Set<KeyPair> getExtended() {
		return extended;
	}

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "ACNT_ID_FK")
	public Account getAccount() {
		return account;
	}

	public void setLearningPeriod(LearningPeriod period) {
		this.learningPeriod = period;
	}

	public void setAddDate(Date addDate) {
		this.addDate = addDate;
	}

	public void setDropDate(Date dropDate) {
		this.dropDate = dropDate;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public void setUnitSection(String unitSection) {
		this.unitSection = unitSection;
	}

	public void setCampus(String campus) {
		this.campus = campus;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setExtended(Set<KeyPair> extended) {
		this.extended = extended;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setAccount(Account account) {
		this.account = account;
	}
	
	public boolean equals(Object o) {
		if (o instanceof LearningUnit) {
			LearningUnit luAnother = (LearningUnit)o;
			
			return (luAnother.id != null) && (luAnother.id.equals(this.id))
					&& StringUtils.equals(luAnother.accountId, this.accountId)
					&& StringUtils.equals(luAnother.campus, this.campus)
					&& ObjectUtils.equals(luAnother.learningPeriod, this.learningPeriod) 
					&& StringUtils.equals(luAnother.status, this.status)
					&& StringUtils.equals(luAnother.level, this.level)
					&& StringUtils.equals(luAnother.unitCode, this.unitCode)
					&& StringUtils.equals(luAnother.unitSection, this.unitSection)
					&& DateUtils.isSameDay(luAnother.addDate, this.addDate)
					&& DateUtils.isSameDay(luAnother.dropDate, this.dropDate)
					&& (luAnother.credit != null) && (luAnother.credit.equals(this.credit));
		}
		
		return false;
	}
	
	public int hashCode() {
		return 31 * (((id != null) ? id.hashCode() : 0) +
				31 * ((StringUtils.isNotBlank(accountId) ? accountId.hashCode() : 0) +
				31 * ((StringUtils.isNotBlank(unitCode) ? unitCode.hashCode() : 0) +
				31 * ObjectUtils.hashCode(learningPeriod))));
	}
}

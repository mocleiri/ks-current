package com.sigmasys.kuali.ksa.model;

import com.sigmasys.kuali.ksa.annotation.Auditable;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * IRS 1098T JPA entity.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Auditable
@Entity
@Table(name = "KSSA_IRS_1098T")
public class Irs1098T implements Identifiable {

    /**
     * Document ID
     */
    private Long id;

    /**
     * Creation date
     */
    private Date creationDate;

    /**
     * Creator user ID
     */
    private String creatorId;

    /**
     * Reference to the corresponding account
     */
    private Account account;

    /**
     * Start date
     */
    private Date startDate;

    /**
     * End date
     */
    private Date endDate;

    /**
     * Form year
     */
    private String formYear;

    /**
     * Indicates if the report is void
     */
    private Boolean isVoid;

    /**
     * Indicates if the report has been corrected
     */
    private Boolean isCorrected;

    /**
     * Indicates if the report has been changed
     */
    private Boolean isReportingMethodChanged;

    /**
     * Filer name
     */
    private String filerName;

    /**
     * Filer phone number
     */
    private String filerPhoneNumber;

    /**
     * Filer FEIN
     */
    private String filerFederalId;

    /**
     * Filer Street Address 1
     */
    private String filerStreetAddress1;

    /**
     * Filer Street Address 2
     */
    private String filerStreetAddress2;

    /**
     * Filer Street Address 3
     */
    private String filerStreetAddress3;

    /**
     * Filer City
     */
    private String filerCity;

    /**
     * Filer State
     */
    private String filerState;

    /**
     * Filer Postal Code
     */
    private String filerPostalCode;

    /**
     * Filer Country
     */
    private String filerCountry;

    /**
     * Student Postal Address
     */
    private PostalAddress studentPostalAddress;

    /**
     * Student name
     */
    private String studentName;

    /**
     * Student SSN
     */
    private String studentSsn;

    /**
     * Student account number
     */
    private String studentAccountNumber;

    /**
     * Indicates if the student is half-time
     */
    private Boolean isStudentHalfTime;

    /**
     * Indicates if the student is graduate
     */
    private Boolean isStudentGraduate;

    /**
     * Value of all payments received to be reported.
     */
    private BigDecimal receivedPayments;

    /**
     * Value of appropriate charges billed to the student.
     */
    private BigDecimal billedAmount;

    /**
     * If the amount reported includes charges for the next quarter, this field will be true.
     */
    private Boolean includesNextQuarter;

    /**
     * If the billed amount from the previous year needed to be adjusted, the adjustment amount will be stored here.
     */
    private BigDecimal priorYearAdjustedAmount;

    /**
     * Total amount of scholarships applied to the student’s account.
     */
    private BigDecimal scholarshipAmount;

    /**
     * If the scholarship amount from the previous year had to be adjusted, the amount will be reported here.
     */
    private BigDecimal scholarshipAdjustedAmount;

    /**
     * If there is an insurance reimbursement or refund amount, it will be stored here.
     */
    private BigDecimal insRefundAmount;

    /**
     * Link to the XML document that holds the representation of the 1098T data.
     */
    private XmlDocument xmlDocument;


    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    @GenericGenerator(name = Constants.ID_GENERATOR_NAME, strategy = Constants.ID_GENERATOR_CLASS)
    @GeneratedValue(generator = Constants.ID_GENERATOR_NAME)
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Column(name = "CREATOR_ID", length = 45)
    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACNT_ID_FK")
    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Column(name = "START_DATE")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Column(name = "END_DATE")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Column(name = "FORM_YEAR", length = 4)
    public String getFormYear() {
        return formYear;
    }

    public void setFormYear(String formYear) {
        this.formYear = formYear;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_VOID")
    public Boolean isVoid() {
        return isVoid != null ? isVoid : false;
    }

    public void setVoid(Boolean aVoid) {
        isVoid = aVoid;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_CORRECTED")
    public Boolean isCorrected() {
        return isCorrected != null ? isCorrected : false;
    }

    public void setCorrected(Boolean corrected) {
        isCorrected = corrected;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_REPORTING_METHOD_CHG")
    public Boolean isReportingMethodChanged() {
        return isReportingMethodChanged;
    }

    public void setReportingMethodChanged(Boolean reportingMethodChanged) {
        isReportingMethodChanged = reportingMethodChanged;
    }

    @Column(name = "FILER_NAME", length = 100)
    public String getFilerName() {
        return filerName;
    }

    public void setFilerName(String filerName) {
        this.filerName = filerName;
    }

    @Column(name = "FILER_PHONE_NUMBER", length = 45)
    public String getFilerPhoneNumber() {
        return filerPhoneNumber;
    }

    public void setFilerPhoneNumber(String filerPhoneNumber) {
        this.filerPhoneNumber = filerPhoneNumber;
    }

    @Column(name = "FILER_FEIN", length = 10)
    public String getFilerFederalId() {
        return filerFederalId;
    }

    public void setFilerFederalId(String filerFederalId) {
        this.filerFederalId = filerFederalId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDENT_POSTAL_ADDRESS_ID_FK")
    public PostalAddress getStudentPostalAddress() {
        return studentPostalAddress;
    }

    public void setStudentPostalAddress(PostalAddress studentPostalAddress) {
        this.studentPostalAddress = studentPostalAddress;
    }

    @Column(name = "STUDENT_NAME", length = 100)
    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    @Column(name = "STUDENT_SSN", length = 12)
    public String getStudentSsn() {
        return studentSsn;
    }

    public void setStudentSsn(String studentSsn) {
        this.studentSsn = studentSsn;
    }

    @Column(name = "STUDENT_ACNT_NUMBER", length = 45)
    public String getStudentAccountNumber() {
        return studentAccountNumber;
    }

    public void setStudentAccountNumber(String studentAccountNumber) {
        this.studentAccountNumber = studentAccountNumber;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_STUDENT_HALF_TIME")
    public Boolean isStudentHalfTime() {
        return isStudentHalfTime != null ? isStudentHalfTime : false;
    }

    public void setStudentHalfTime(Boolean studentHalfTime) {
        isStudentHalfTime = studentHalfTime;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "IS_STUDENT_GRADUATE")
    public Boolean isStudentGraduate() {
        return isStudentGraduate != null ? isStudentGraduate : false;
    }

    public void setStudentGraduate(Boolean studentGraduate) {
        isStudentGraduate = studentGraduate;
    }

    @Column(name = "RECEIVED_PAYMENTS")
    public BigDecimal getReceivedPayments() {
        return receivedPayments;
    }

    public void setReceivedPayments(BigDecimal receivedPayments) {
        this.receivedPayments = receivedPayments;
    }

    @Column(name = "BILLED_AMOUNT")
    public BigDecimal getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(BigDecimal billedAmount) {
        this.billedAmount = billedAmount;
    }

    @org.hibernate.annotations.Type(type = "yes_no")
    @Column(name = "INCLUDES_NEXT_QUARTER")
    public Boolean getIncludesNextQuarter() {
        return includesNextQuarter != null ? includesNextQuarter : false;
    }

    public void setIncludesNextQuarter(Boolean includesNextQuarter) {
        this.includesNextQuarter = includesNextQuarter;
    }

    @Column(name = "PRIOR_YEAR_ADJ_AMOUNT")
    public BigDecimal getPriorYearAdjustedAmount() {
        return priorYearAdjustedAmount;
    }

    public void setPriorYearAdjustedAmount(BigDecimal priorYearAdjustedAmount) {
        this.priorYearAdjustedAmount = priorYearAdjustedAmount;
    }

    @Column(name = "SCHOLARSHIP_AMOUNT")
    public BigDecimal getScholarshipAmount() {
        return scholarshipAmount;
    }

    public void setScholarshipAmount(BigDecimal scholarshipAmount) {
        this.scholarshipAmount = scholarshipAmount;
    }

    @Column(name = "SCHOLARSHIP_ADJ_AMOUNT")
    public BigDecimal getScholarshipAdjustedAmount() {
        return scholarshipAdjustedAmount;
    }

    public void setScholarshipAdjustedAmount(BigDecimal scholarshipAdjustedAmount) {
        this.scholarshipAdjustedAmount = scholarshipAdjustedAmount;
    }

    @Column(name = "INS_REFUND_AMOUNT")
    public BigDecimal getInsRefundAmount() {
        return insRefundAmount;
    }

    public void setInsRefundAmount(BigDecimal insRefundAmount) {
        this.insRefundAmount = insRefundAmount;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "XML_ID_FK")
    public XmlDocument getXmlDocument() {
        return xmlDocument;
    }

    public void setXmlDocument(XmlDocument xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    @Column(name = "FILER_ADDRESS_LINE_1", length = 100)
    public String getFilerStreetAddress1() {
        return filerStreetAddress1;
    }

    public void setFilerStreetAddress1(String filerStreetAddress1) {
        this.filerStreetAddress1 = filerStreetAddress1;
    }

    @Column(name = "FILER_ADDRESS_LINE_2", length = 100)
    public String getFilerStreetAddress2() {
        return filerStreetAddress2;
    }

    public void setFilerStreetAddress2(String filerStreetAddress2) {
        this.filerStreetAddress2 = filerStreetAddress2;
    }


    @Column(name = "FILER_ADDRESS_LINE_3", length = 100)
    public String getFilerStreetAddress3() {
        return filerStreetAddress3;
    }

    public void setFilerStreetAddress3(String filerStreetAddress3) {
        this.filerStreetAddress3 = filerStreetAddress3;
    }

    @Column(name = "FILER_ADDRESS_CITY", length = 100)
    public String getFilerCity() {
        return filerCity;
    }

    public void setFilerCity(String filerCity) {
        this.filerCity = filerCity;
    }

    @Column(name = "FILER_ADDRESS_STATE", length = 10)
    public String getFilerState() {
        return filerState;
    }

    public void setFilerState(String state) {
        this.filerState = state;
    }

    @Column(name = "FILER_ADDRESS_POSTAL_CODE", length = 10)
    public String getFilerPostalCode() {
        return filerPostalCode;
    }

    public void setFilerPostalCode(String postalCode) {
        this.filerPostalCode = postalCode;
    }

    @Column(name = "FILER_ADDRESS_COUNTRY", length = 100)
    public String getFilerCountry() {
        return filerCountry;
    }

    public void setFilerCountry(String country) {
        this.filerCountry = country;
    }
}

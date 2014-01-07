package com.sigmasys.kuali.ksa.service.impl;

import static com.sigmasys.kuali.ksa.util.TransactionUtils.getFormattedAmount;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;


import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.jaxb.ElectronicContact;
import com.sigmasys.kuali.ksa.jaxb.Irs1098T;
import com.sigmasys.kuali.ksa.jaxb.PersonName;
import com.sigmasys.kuali.ksa.jaxb.PostalAddress;
import com.sigmasys.kuali.ksa.service.aop.AbstractMethodInterceptor;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.exception.TransactionNotFoundException;
import com.sigmasys.kuali.ksa.exception.UserNotFoundException;
import com.sigmasys.kuali.ksa.jaxb.*;
import com.sigmasys.kuali.ksa.jaxb.AgedBalanceReport.AgedAccountDetail;
import com.sigmasys.kuali.ksa.jaxb.FailedTransactionsReport.FailureGroup;
import com.sigmasys.kuali.ksa.jaxb.GeneralLedgerReport.GeneralLedgerReportEntry;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.service.*;
import com.sigmasys.kuali.ksa.util.*;

/**
 * Report Service implementation.
 *
 * @author Michael Ivanov
 */
@Service("reportService")
@Transactional(readOnly = true)
@WebService(serviceName = ReportService.SERVICE_NAME, portName = ReportService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
@SuppressWarnings("unchecked")
public class ReportServiceImpl extends GenericPersistenceService implements ReportService {

    private static final Log logger = LogFactory.getLog(ReportServiceImpl.class);

    private static final String REPORT_SCHEMA_LOCATION = "classpath*:/xsd/transaction-report.xsd";
    private static final String XML_SCHEMA_LOCATION = "classpath*:/xsd/xml.xsd";


    private XmlSchemaValidator schemaValidator;

    @Autowired
    private GeneralLedgerService glService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CashLimitService cashLimitService;

    @Autowired
    private BillRecordService billRecordService;


    @PostConstruct
    private void postConstruct() {
        schemaValidator = new XmlSchemaValidator(XML_SCHEMA_LOCATION, REPORT_SCHEMA_LOCATION);
    }

    /**
     * Adds AOP advice to the current instance.
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Advice> getAdvices(BeanFactory beanFactory) {
        List<Advice> advices = super.getAdvices(beanFactory);
        if (advices == null) {
            advices = new LinkedList<Advice>();
        }
        MethodInterceptor methodInterceptor = new AbstractMethodInterceptor(this) {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                Method method = invocation.getMethod();
                if (method.getReturnType() != null && method.getReturnType().equals(String.class)) {
                    String reportXml = (String) invocation.proceed();
                    // Validate XML against the schema
                    if (!schemaValidator.validateXml(reportXml)) {
                        String errMsg = "XML content is invalid:\n" + reportXml;
                        logger.error(errMsg);
                        throw new RuntimeException(errMsg);
                    }
                    return reportXml;
                }
                return super.invoke(invocation);
            }
        };
        advices.add(methodInterceptor);
        return advices;
    }


    /**
     * Generates a reconciliation report in XML for KSA transactions to the general ledger.
     *
     * @param glAccountId     GL Account ID
     * @param startDate       Start date
     * @param endDate         End date
     * @param transmittedOnly indicates whether only transmitted GL transactions should be retrieved or not
     * @return The generated report in XML
     */
    @Override
    public String generateGeneralLedgerReport(String glAccountId, Date startDate, Date endDate, boolean transmittedOnly) {

        List<GlTransaction> glTransactions = glService.getGlTransactions(startDate, endDate, glAccountId);

        ObjectFactory objectFactory = ObjectFactory.getInstance();

        GeneralLedgerReport glReport = objectFactory.createGeneralLedgerReport();

        if (CollectionUtils.isNotEmpty(glTransactions)) {
            for (GlTransaction glTransaction : glTransactions) {
                if (!transmittedOnly || GlTransactionStatus.COMPLETED.equals(glTransaction.getStatus())) {

                    GeneralLedgerReportEntry glReportEntry = new GeneralLedgerReportEntry();

                    Set<Transaction> transactions = glTransaction.getTransactions();
                    if (CollectionUtils.isNotEmpty(transactions)) {
                        List<String> transactionIds = new ArrayList<String>(transactions.size());
                        for (Transaction transaction : transactions) {
                            transactionIds.add(String.valueOf(transaction.getId()));
                        }
                        Collections.sort(transactionIds);
                        glReportEntry.getTransactionIdentifier().addAll(transactionIds);
                    }

                    glReportEntry.setGeneralLedgerAccount(glAccountId);
                    glReportEntry.setAmount(getFormattedAmount(glTransaction.getAmount()));
                    glReportEntry.setDate(CalendarUtils.toXmlGregorianCalendar(glTransaction.getDate()));
                    glReportEntry.setSystem(glTransaction.getStatement());

                    if (glTransaction.getStatus() != null) {
                        glReportEntry.setTransactionStatus(glTransaction.getStatus().getId());
                    }

                    if (glTransaction.getGlOperation() != null) {
                        glReportEntry.setGeneralLedgerOperation(glTransaction.getGlOperation().toString().toLowerCase());
                    }

                    GlTransmission glTransmission = glTransaction.getTransmission();
                    if (glTransmission != null) {

                        glReportEntry.setTransmissionIdentifier(String.valueOf(glTransmission.getId()));
                        glReportEntry.setBatch(glTransmission.getBatchId());
                        Date transmissionDate = glTransmission.getDate();
                        glReportEntry.setTransmissionDate(CalendarUtils.toXmlGregorianCalendar(transmissionDate, true));
                        glReportEntry.setTransmissionTime(CalendarUtils.toXmlGregorianCalendar(transmissionDate));
                        GlRecognitionPeriod recognitionPeriod = glTransmission.getRecognitionPeriod();

                        if (recognitionPeriod != null) {
                            glReportEntry.setTransmissionPeriod(recognitionPeriod.getCode());
                        }

                        if (glTransmission.getStatus() != null) {
                            glReportEntry.setTransmissionResult(glTransmission.getStatus().toString());
                        }
                    }

                    glReport.setGeneralLedgerReportEntry(glReportEntry);
                }
            }
        }

        glReport.setReportingPeriod(createReportingPeriod(startDate, endDate));

        return JaxbUtils.toXml(glReport);
    }

    /**
     * Generates a reconciliation report in XML for KSA transactions to the general ledger.
     *
     * @param glAccountId GL Account ID
     * @param startDate   Start date
     * @param endDate     End date
     * @return The generated report in XML
     */
    @Override
    @WebMethod(exclude = true)
    public String generateGeneralLedgerReport(String glAccountId, Date startDate, Date endDate) {
        return generateGeneralLedgerReport(glAccountId, startDate, endDate, false);
    }


    /**
     * An overloaded version of the <code>generate1098TReport</code> method that produces a partial year federal 1098T form.
     *
     * @param accountId               ID of an account for which to produce the report.
     * @param startDate               Payments and Refunds tracking start date.
     * @param endDate                 Payments and Refunds tracking end date date
     * @param numberOfDisplayedDigits The number of final digits of the social security number that will be stored in the local record.
     * @param isTransient             Whether to keep the record of the produces 1098T
     * @return XML representation of IRS 1098T form.
     * @see Irs1098T
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed({Permission.GENERATE_IRS_1098_T, Permission.READ_IRS_1098_T})
    public String generate1098TReport(String accountId, Date startDate, Date endDate, int numberOfDisplayedDigits,
                                      boolean isTransient) {

        com.sigmasys.kuali.ksa.model.Irs1098T newReport = create1098TReport(accountId, startDate, endDate,
                numberOfDisplayedDigits, isTransient);

        return toXml(accountId, newReport);
    }

    /**
     * Converts an IRS 1098T JPA entity to XML
     *
     * @param irs1098TId Irs1098T JPA entity ID
     * @return XML representation of the form.
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed({Permission.GENERATE_IRS_1098_T, Permission.READ_IRS_1098_T})
    public String convertIrs1098TToXml(Long irs1098TId) {

        com.sigmasys.kuali.ksa.model.Irs1098T irs1098T = getIrs1098T(irs1098TId);
        if (irs1098T == null) {
            String errMsg = "IRS 1098T does not exist, ID = " + irs1098TId;
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        String accountId = irs1098T.getAccount().getId();

        return toXml(accountId, irs1098T);
    }

    private com.sigmasys.kuali.ksa.model.Irs1098T getIrs1098T(Long id) {

        Query query = em.createQuery("select irs from Irs1098T irs " +
                " left outer join fetch irs.account account " +
                " left outer join fetch irs.studentPostalAddress address " +
                " where irs.id = :id");
        query.setParameter("id", id);
        List<com.sigmasys.kuali.ksa.model.Irs1098T> results = query.getResultList();
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * Converts the internal representation of IRS 1098T report (Irs1098T JPA entity) into XML format.
     *
     * @param accountId Account ID
     * @param report    Irs1098T instance
     * @return XML representation of IRS 1098T form.
     */
    protected String toXml(String accountId, com.sigmasys.kuali.ksa.model.Irs1098T report) {

        AccountProtectedInfo accountInfo = accountService.getAccountProtectedInfo(accountId);
        if (accountInfo == null) {
            String errMsg = "Cannot find Account Protected Information for Account ID = " + accountId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        final String taxReference = accountInfo.getTaxReference();

        final Irs1098T irs1098T = new Irs1098T();

        final Irs1098T.Student student = new Irs1098T.Student();

        final Irs1098T.Filer filer = new Irs1098T.Filer();

        final Irs1098T.Financial financialEntry = new Irs1098T.Financial();

        final Irs1098T.FormCheckboxes formCheckboxes = new Irs1098T.FormCheckboxes();

        irs1098T.setFormYear(report.getFormYear());

        // Filling in Student
        // Setting the entire SNN
        student.setSocialSecurityNumber(taxReference);
        student.setAccountNumber(accountId);
        student.setName(report.getStudentName());
        student.setHalfTime(report.isStudentHalfTime());
        student.setGraduateStudent(report.isStudentGraduate());

        com.sigmasys.kuali.ksa.model.PostalAddress studentAddress = report.getStudentPostalAddress();

        if (studentAddress != null) {

            PostalAddress postalAddress = ObjectFactory.getInstance().createPostalAddress();
            postalAddress.setAddressLine1(studentAddress.getStreetAddress1());
            postalAddress.setAddressLine2(studentAddress.getStreetAddress2());
            postalAddress.setAddressLine3(studentAddress.getStreetAddress3());
            postalAddress.setCity(studentAddress.getCity());
            postalAddress.setStateCode(studentAddress.getState());
            postalAddress.setPostalCode(studentAddress.getPostalCode());
            postalAddress.setCountryCode(studentAddress.getCountry());

            student.setPostalAddress(postalAddress);
        }

        // Filling in Filer

        filer.setName(report.getFilerName());
        filer.setFein(report.getFilerFederalId());

        PostalAddress filerAddress = ObjectFactory.getInstance().createPostalAddress();
        filerAddress.setAddressLine1(report.getFilerStreetAddress1());
        filerAddress.setAddressLine2(report.getFilerStreetAddress2());
        filerAddress.setAddressLine3(report.getFilerStreetAddress3());

        filerAddress.setCity(report.getFilerCity());
        filerAddress.setStateCode(report.getFilerState());
        filerAddress.setPostalCode(report.getFilerPostalCode());
        filerAddress.setCountryCode(report.getFilerCountry());

        filer.setPostalAddress(filerAddress);

        filer.setTelephoneNumber(report.getFilerPhoneNumber());

        // Filling in FinancialEntry

        financialEntry.setAmountBilled(getFormattedAmount(report.getBilledAmount()));
        financialEntry.setInsuranceContract(getFormattedAmount(report.getInsRefundAmount()));
        financialEntry.setScholarshipsOrGrants(getFormattedAmount(report.getScholarshipAmount()));
        financialEntry.setPriorYearAdjustment(getFormattedAmount(report.getPriorYearAdjustedAmount()));
        financialEntry.setPriorYearScholarshipAdjustment(getFormattedAmount(report.getScholarshipAdjustedAmount()));

        // Filling in FormCheckboxes
        formCheckboxes.setReportingMethodChanged(report.isReportingMethodChanged());
        formCheckboxes.setCorrected(report.isCorrected());
        formCheckboxes.setVoid(report.isVoid());

        irs1098T.setStudent(student);
        irs1098T.setFiler(filer);
        irs1098T.setFinancial(financialEntry);
        irs1098T.setFormCheckboxes(formCheckboxes);

        return JaxbUtils.toXml(irs1098T);
    }

    /**
     * This method creates the federal 1098T form and stores it in the database.
     *
     * @param accountId               ID of an account for which to produce the report.
     * @param startDate               Payments and Refunds tracking start date.
     * @param endDate                 Payments and Refunds tracking end date date
     * @param numberOfDisplayedDigits The number of final digits of the social security number that will be stored in the local record.
     * @param isTransient             Whether to keep the record of the produces 1098T
     * @return Irs1098T instance
     * @see Irs1098T
     */
    @Transactional(readOnly = false)
    protected com.sigmasys.kuali.ksa.model.Irs1098T create1098TReport(String accountId, Date startDate, Date endDate,
                                                                      int numberOfDisplayedDigits, boolean isTransient) {

        logger.debug("1098T Start date = " + startDate);
        logger.debug("1098T End date = " + endDate);

        final int reportYear = CalendarUtils.getYear(startDate);
        if (reportYear != CalendarUtils.getYear(endDate)) {
            String errMsg = "Start and End dates must have the same year";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Account account = accountService.getFullAccount(accountId);
        if (account == null) {
            String errMsg = "Cannot find Account with ID = " + accountId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        AccountProtectedInfo accountInfo = accountService.getAccountProtectedInfo(accountId);
        if (accountInfo == null) {
            String errMsg = "Cannot find Account Protected Information for Account ID = " + accountId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        TaxType taxType = accountInfo.getTaxType();
        if (taxType == null) {
            String errMsg = "Cannot find Tax Type for Account ID = " + accountId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        String ssnTaxTypeCode = configService.getParameter(Constants.KSA_1098_SSN_TAX_TYPE);
        if (StringUtils.isBlank(ssnTaxTypeCode)) {
            String errMsg = "Initial parameter '" + Constants.KSA_1098_SSN_TAX_TYPE + "' must be set";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        final String currentUserId = userSessionManager.getUserId();

        final com.sigmasys.kuali.ksa.model.Irs1098T irs1098T = new com.sigmasys.kuali.ksa.model.Irs1098T();

        irs1098T.setAccount(account);

        irs1098T.setCreatorId(currentUserId);
        irs1098T.setCreationDate(new Date());

        irs1098T.setStartDate(startDate);
        irs1098T.setEndDate(endDate);

        irs1098T.setFormYear(String.valueOf(reportYear));

        String taxReference = accountInfo.getTaxReference();

        if (StringUtils.isNotBlank(taxReference)) {
            irs1098T.setStudentSsn(maskString(taxReference, 'X', numberOfDisplayedDigits));
        }

        irs1098T.setStudentAccountNumber(accountId);

        com.sigmasys.kuali.ksa.model.PersonName personName = account.getDefaultPersonName();

        irs1098T.setStudentName(personName.getFirstName() + " " + personName.getLastName());

        com.sigmasys.kuali.ksa.model.PostalAddress studentAddress = account.getDefaultPostalAddress();

        irs1098T.setStudentPostalAddress(studentAddress);

        // Filling in Filer

        irs1098T.setFilerName(configService.getParameter(Constants.KSA_1098_FILER_NAME));
        irs1098T.setFilerFederalId(configService.getParameter(Constants.KSA_1098_FILER_FEIN));
        irs1098T.setFilerPhoneNumber(configService.getParameter(Constants.KSA_1098_FILER_PHONE));

        irs1098T.setFilerStreetAddress1(configService.getParameter(Constants.KSA_1098_FILER_ADDRESS1));
        irs1098T.setFilerStreetAddress2(configService.getParameter(Constants.KSA_1098_FILER_ADDRESS2));
        irs1098T.setFilerStreetAddress3(configService.getParameter(Constants.KSA_1098_FILER_ADDRESS3));
        irs1098T.setFilerCity(configService.getParameter(Constants.KSA_1098_FILER_CITY));
        irs1098T.setFilerState(configService.getParameter(Constants.KSA_1098_FILER_STATE));
        irs1098T.setFilerPostalCode(configService.getParameter(Constants.KSA_1098_FILER_ZIP));
        irs1098T.setFilerCountry(configService.getParameter(Constants.KSA_1098_FILER_COUNTRY));

        // Filling in FinancialEntry
        final String billedAmountTag = configService.getParameter(Constants.KSA_1098_TAG_BILLED_AMOUNT);
        final String insuranceRefundTag = configService.getParameter(Constants.KSA_1098_TAG_INSURANCE_REFUND);
        final String grantTag = configService.getParameter(Constants.KSA_1098_TAG_GRANTS);
        final int recognitionYear =
                Integer.valueOf(configService.getParameter(Constants.KSA_TRANSACTION_RECOGNITION_YEAR));

        BigDecimal chargeAmount = BigDecimal.ZERO;
        BigDecimal refundPaymentAmount = BigDecimal.ZERO;
        BigDecimal grantPaymentAmount = BigDecimal.ZERO;
        boolean includesNextQuarter = false;

        List<Transaction> transactions = transactionService.getTransactions(accountId, startDate, endDate);

        for (Transaction transaction : transactions) {
            TransactionTypeValue transactionTypeValue = transaction.getTransactionTypeValue();
            BigDecimal transactionAmount = (transaction.getAmount() != null) ? transaction.getAmount() : BigDecimal.ZERO;
            Set<Tag> tags = transaction.getTransactionType().getTags();
            if (CollectionUtils.isNotEmpty(tags)) {
                if (TransactionTypeValue.PAYMENT.equals(transactionTypeValue)) {
                    if (containsTag(insuranceRefundTag, tags)) {
                        refundPaymentAmount = refundPaymentAmount.add(transactionAmount);
                    } else if (containsTag(grantTag, tags)) {
                        grantPaymentAmount = grantPaymentAmount.add(transactionAmount);
                    }
                } else if (TransactionTypeValue.CHARGE.equals(transactionTypeValue) && containsTag(billedAmountTag, tags)) {
                    chargeAmount = chargeAmount.add(transactionAmount);
                    if (!includesNextQuarter && recognitionYear > reportYear) {
                        includesNextQuarter = true;
                    }
                }
            }
        }

        irs1098T.setBilledAmount(chargeAmount);
        irs1098T.setInsRefundAmount(refundPaymentAmount);
        irs1098T.setScholarshipAmount(grantPaymentAmount);

        // Setting the received payments to 0 since the billed amount is already present
        irs1098T.setReceivedPayments(BigDecimal.ZERO);

        irs1098T.setIncludesNextQuarter(includesNextQuarter);

        boolean reportMethodChange = Boolean.valueOf(configService.getParameter(Constants.KSA_1098_REPORTING_METHOD_CHANGE));
        irs1098T.setReportingMethodChanged(reportMethodChange);

        // Getting the report for the same account and year
        com.sigmasys.kuali.ksa.model.Irs1098T oldReport = getIrs1098T(accountId, startDate, endDate);

        irs1098T.setCorrected(oldReport != null);

        if (isTransient) {
            irs1098T.setVoid(true);
        } else {

            // Generating the report (but not persisting!) for the same account and PREVIOUS year
            com.sigmasys.kuali.ksa.model.Irs1098T priorReport = create1098TReportByYear(accountId, reportYear - 1,
                    numberOfDisplayedDigits, true);

            if (priorReport != null) {

                // Calculating adjustments

                BigDecimal billedAmount = irs1098T.getBilledAmount();
                if (billedAmount == null) {
                    billedAmount = BigDecimal.ZERO;
                }

                BigDecimal receivedPayments = irs1098T.getReceivedPayments();
                if (receivedPayments == null) {
                    receivedPayments = BigDecimal.ZERO;
                }

                BigDecimal priorBilledAmount = priorReport.getBilledAmount();
                if (priorBilledAmount == null) {
                    priorBilledAmount = BigDecimal.ZERO;
                }

                BigDecimal priorReceivedPayments = priorReport.getReceivedPayments();
                if (priorReceivedPayments == null) {
                    priorReceivedPayments = BigDecimal.ZERO;
                }

                BigDecimal billedAmountAdjustment = billedAmount.subtract(priorBilledAmount);
                BigDecimal paymentAmountAdjustment = receivedPayments.subtract(priorReceivedPayments);

                BigDecimal priorYearAdjustment = billedAmountAdjustment.add(paymentAmountAdjustment);
                if (priorYearAdjustment.compareTo(BigDecimal.ZERO) != 0) {
                    irs1098T.setPriorYearAdjustedAmount(priorYearAdjustment);
                }

                // Calculating scholarship or grant adjustment
                BigDecimal scholarshipAdjustment = irs1098T.getScholarshipAmount().subtract(priorReport.getScholarshipAmount());
                if (scholarshipAdjustment.compareTo(BigDecimal.ZERO) != 0) {
                    irs1098T.setScholarshipAdjustedAmount(scholarshipAdjustment);
                }
            }

            // Persisting the new report in the database
            persistEntity(irs1098T);

        }

        return irs1098T;
    }

    /**
     * Looks for an existing IRS 1098T report in the KSA database and returns the Irs1098T JPA entity.
     *
     * @param accountId ID of an account for which to produce the report.
     * @param startDate Payments and Refunds tracking start date.
     * @param endDate   Payments and Refunds tracking end date date
     * @return Irs1098T instance
     */
    protected com.sigmasys.kuali.ksa.model.Irs1098T getIrs1098T(String accountId, Date startDate, Date endDate) {

        startDate = CalendarUtils.removeTime(startDate);
        endDate = CalendarUtils.removeTime(endDate);

        Query query = em.createQuery("select irs from Irs1098T irs " +
                " left outer join fetch irs.account account " +
                " left outer join fetch irs.studentPostalAddress address " +
                " where account.id = :accountId and " +
                " irs.startDate <= :startDate and irs.endDate >= :endDate order by irs.creationDate desc");

        query.setParameter("accountId", accountId);
        query.setParameter("startDate", startDate, TemporalType.DATE);
        query.setParameter("endDate", endDate, TemporalType.DATE);
        query.setMaxResults(1);

        List<com.sigmasys.kuali.ksa.model.Irs1098T> results = query.getResultList();
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * Looks for an existing IRS 1098T report in the KSA database and returns the result in XML format.
     *
     * @param accountId ID of an account for which to produce the report.
     * @param startDate Payments and Refunds tracking start date.
     * @param endDate   Payments and Refunds tracking end date date
     * @return XML representation of an IRS 1098T form.
     */
    @Override
    @PermissionsAllowed(Permission.READ_IRS_1098_T)
    public String getIrs1098TReport(String accountId, Date startDate, Date endDate) {

        AccountProtectedInfo accountInfo = accountService.getAccountProtectedInfo(accountId);
        if (accountInfo == null) {
            String errMsg = "Cannot find Account Protected Information for Account ID = " + accountId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        com.sigmasys.kuali.ksa.model.Irs1098T report = getIrs1098T(accountId, startDate, endDate);
        if (report == null) {
            String errMsg = "Cannot find Irs1098T record for Account ID '" + accountId +
                    "' and dates [" + startDate + ", " + endDate + "]";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        return toXml(accountId, report);
    }

    /**
     * Looks for an existing IRS 1098T report in the KSA database and returns the result in XML format.
     *
     * @param accountId ID of an account for which to produce the report.
     * @param year      Tax year
     * @return XML representation of an IRS 1098T form.
     */
    @Override
    @PermissionsAllowed(Permission.READ_IRS_1098_T)
    public String getIrs1098TReportByYear(String accountId, int year) {
        Date firstDate = CalendarUtils.getFirstDateOfYear(year);
        Date lastDate = CalendarUtils.getLastDateOfYear(year);
        return getIrs1098TReport(accountId, firstDate, lastDate);
    }

    private boolean containsTag(String tagCode, Collection<Tag> tags) {
        for (Tag tag : tags) {
            if (tagCode.equalsIgnoreCase(tag.getCode())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns a list of previously generated and saved IRS Forms 1098T for a particular account.
     *
     * @param accountId ID of an Account for which to return its saved IRS Forms 1098T.
     * @return A <code>List</code> of previously generated and saved IRS Forms 1098T.
     */
    @Override
    @PermissionsAllowed(Permission.READ_IRS_1098_T)
    public List<com.sigmasys.kuali.ksa.model.Irs1098T> getIrs1098TReportsForAccount(String accountId) {
        Query query = em.createQuery("select irs from Irs1098T irs " +
                " left outer join fetch irs.account account " +
                " left outer join fetch irs.studentPostalAddress address " +
                " where account.id = :accountId " +
                " order by irs.creationDate desc");
        query.setParameter("accountId", accountId);
        return query.getResultList();
    }

    /**
     * Returns an XML representation of a complete year federal 1098T form. Note that many of the parameters for producing a correct 1098T
     * must be established in the system preferences, and transactions must carry the appropriate tags in order to be counted correctly.
     * <br>
     * <code>numberOfDisplayedDigits<code> is the number of final digits of the social security number that will be stored in the local record.
     * The XML file produced will contain the entire social security number, and it is imperative that the institution provide appropriate controls over this data.<br>
     * If <code>isTransient</code> is passed as true, then the system will not keep a record of the produced 1098T.
     * This option is ONLY to be used if the 1098T is being used to verify previous year's data, as the system does in order to complete the current year's return.
     * This form will automatically be returned as void, and a paper 1098T should not be produced from this data.
     * <br>
     *
     * @param accountId               ID of an account for which to produce the report.
     * @param year                    Tax year.
     * @param numberOfDisplayedDigits The number of final digits of the social security number that will be stored in the local record.
     * @param isTransient             Whether to keep the record of the produces 1098T
     * @return String representation of an IRS 1098T form.
     * @see Irs1098T
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed({Permission.GENERATE_IRS_1098_T, Permission.READ_IRS_1098_T})
    public String generate1098TReportByYear(String accountId, int year, int numberOfDisplayedDigits, boolean isTransient) {
        Date firstDate = CalendarUtils.getFirstDateOfYear(year);
        Date lastDate = CalendarUtils.getLastDateOfYear(year);
        return generate1098TReport(accountId, firstDate, lastDate, numberOfDisplayedDigits, isTransient);
    }

    /**
     * Returns an XML representation of a complete year federal 1098T form. Note that many of the parameters for producing a correct 1098T
     * must be established in the system preferences, and transactions must carry the appropriate tags in order to be counted correctly.
     * <br>
     *
     * @param accountId   ID of an account for which to produce the report.
     * @param year        Tax year.
     * @param isTransient Whether to keep the record of the produces 1098T
     * @return String representation of an IRS 1098T form.
     * @see Irs1098T
     */
    @Override
    @WebMethod(exclude = true)
    @Transactional(readOnly = false)
    @PermissionsAllowed({Permission.GENERATE_IRS_1098_T, Permission.READ_IRS_1098_T})
    public String generate1098TReportByYear(String accountId, int year, boolean isTransient) {
        String paramValue = configService.getParameter(Constants.KSA_1098_SSN_DISPLAY_DIGITS);
        if (StringUtils.isBlank(paramValue)) {
            String errMsg = "Configuration parameter '" + Constants.KSA_1098_SSN_DISPLAY_DIGITS + "' is required";
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }
        return generate1098TReportByYear(accountId, year, Integer.parseInt(paramValue), isTransient);
    }

    protected com.sigmasys.kuali.ksa.model.Irs1098T create1098TReportByYear(String accountId, int year,
                                                                            int numberOfDisplayedDigits, boolean isTransient) {
        Date firstDate = CalendarUtils.getFirstDateOfYear(year);
        Date lastDate = CalendarUtils.getLastDateOfYear(year);
        return create1098TReport(accountId, firstDate, lastDate, numberOfDisplayedDigits, isTransient);
    }

    protected String maskString(String value, Character mask, int numberOfCharsToPreserve) {

        if (StringUtils.isEmpty(value)) {
            return value;
        }

        int length = value.length();
        if (numberOfCharsToPreserve < length) {
            length -= numberOfCharsToPreserve;
        }

        StringBuilder builder = new StringBuilder(value.length());

        for (int i = 0; i < length; i++) {
            builder.append(mask);
        }

        if (length < value.length()) {
            builder.append(value.substring(length));
        }

        return builder.toString();
    }


    /**
     * Produce an XML aged balance report for the accounts in the list. If <code>ageAccounts</code> is <code>true</code>, then each
     * account will be aged before the report is run. If <code>ignoreDeferments</code> is <code>true</code>, then deferments will be
     * ignored in the calculation of the balances, ONLY if <code>ageAccounts</code> is set to <code>true</code>.
     *
     * @param accountIds       List of account IDs for which to produce an Aged Balance report.
     * @param ignoreDeferments Whether to ignore deferments in calculations.
     * @param ageAccount       Whether to age account prior to producing this report.
     * @return String representation of an XML form report.
     * @see AgedBalanceReport
     */
    @Override
    public String generateAgedBalanceReport(List<String> accountIds, boolean ignoreDeferments, boolean ageAccount) {

        // Create a new report object:
        AgedBalanceReport report = new AgedBalanceReport();
        List<Object> detailsList = report.getAgeBreakdownAndAgedAccountDetail();
        XMLGregorianCalendar reportDate = CalendarUtils.toXmlGregorianCalendar(new Date());

        // Set the report date to current date:
        report.setDateOfReport(reportDate);

        // Iterate through the account IDs:
        for (String accountId : accountIds) {

            // Get the account:
            Account account = accountService.getFullAccount(accountId);

            if (account == null) {
                String errMsg = "Account with ID = " + accountId + " does not exist";
                logger.error(errMsg);
                throw new UserNotFoundException(errMsg);
            }

            if (!(account instanceof ChargeableAccount)) {
                String errMsg = "Account '" + accountId + "' must be ChargeableAccount";
                logger.error(errMsg);
                throw new UserNotFoundException(errMsg);
            }

            ChargeableAccount chargeableAccount = (ChargeableAccount) account;


            // Optionally age the account's debt:
            if (ageAccount) {
                accountService.ageDebt(accountId, ignoreDeferments);
            }

            // Try to find a matching AgeBreakdown:
            int indexOfExistingAgeBreakdown = indexOfAgeBreakdown(detailsList, chargeableAccount);

            // Create a new AgeBreakdown if there is no match:
            if (indexOfExistingAgeBreakdown == -1) {

                LatePeriod latePeriod = chargeableAccount.getLatePeriod();

                if (latePeriod != null) {
                    AgedBalanceReport.AgeBreakdown ageBreakdown = new AgedBalanceReport.AgeBreakdown();
                    ageBreakdown.setPeriod1(latePeriod.getDaysLate1());
                    ageBreakdown.setPeriod2(latePeriod.getDaysLate2());
                    ageBreakdown.setPeriod3(latePeriod.getDaysLate3());
                    detailsList.add(ageBreakdown);
                }
            }

            // Create a new AgedAccountDetail:
            AgedAccountDetail agedAccountDetail = new AgedAccountDetail();

            agedAccountDetail.setAccountIdentifier(accountId);
            agedAccountDetail.setAccountName(account.getCompositeDefaultPersonName());

            AgedAccountDetail.AccountBalances accountBalances = new AgedAccountDetail.AccountBalances();
            accountBalances.setPeriod1Balance(getFormattedAmount(chargeableAccount.getAmountLate1()));
            accountBalances.setPeriod2Balance(getFormattedAmount(chargeableAccount.getAmountLate2()));
            accountBalances.setPeriod3Balance(getFormattedAmount(chargeableAccount.getAmountLate3()));
            agedAccountDetail.setAccountBalances(accountBalances);

            // Add the ageAccountDetail to the list of all details:
            if (indexOfExistingAgeBreakdown == -1) {
                // Append at the end:
                detailsList.add(agedAccountDetail);
            } else {
                // Insert after the AgeBreakdown object:
                detailsList.add(indexOfExistingAgeBreakdown + 1, agedAccountDetail);
            }
        }

        return JaxbUtils.toXml(report);
    }


    /**
     * An overloaded method to produce a failed transaction report for all batches.
     *
     * @param startDate  Start date of transaction period tracking.
     * @param endDate    End date of transaction period tracking.
     * @param failedOnly Include only transactions that failed on their own.
     * @return String representation of an XML report.
     * @see FailedTransactionsReport
     */
    @Override
    @WebMethod(exclude = true)
    public String generateRejectedTransactionsReport(Date startDate, Date endDate, boolean failedOnly) {
        return generateRejectedTransactionsReport(null, startDate, endDate, failedOnly);
    }


    /**
     * Checks the batch records for transactions that have been sent and rejected. The method can filter by
     * date range, and by entity. If no entity is passed, all batches in the date range will be included. If
     * failedOnly is set to true, only the transactions that failed on their own will be reported (i.e. if a whole
     * batch was rejected due to a failure, the transactions that would have posted ok will not be reported.) If
     * failedOnly is true, all transactions that were not accepted, even those which were inherently "valid" will
     * be reported.
     *
     * @param externalId External ID.
     * @param startDate  Start date of transaction period tracking.
     * @param endDate    End date of transaction period tracking.
     * @param failedOnly Include only transactions that failed on their own.
     * @return String representation of an XML report.
     * @see FailedTransactionsReport
     */
    @Override
    public String generateRejectedTransactionsReport(String externalId, Date startDate, Date endDate, boolean failedOnly) {

        // Create a new report object:
        FailedTransactionsReport report = new FailedTransactionsReport();

        // Set the report date to current date:
        report.setDateOfReport(CalendarUtils.toXmlGregorianCalendar(new Date()));

        // Get all matching Failed or Partial BatchReceipts:
        List<BatchReceipt> batchReceipts = findFailedAndPartialBatchReceipts(startDate, endDate, externalId);

        // Iterate through the list of matching BatchReceipts:
        for (BatchReceipt batchReceipt : batchReceipts) {

            // Create a new FailureGroup:
            FailureGroup failureGroup = new FailureGroup();

            // Get the Batch transaction response object:
            KsaBatchTransactionResponse batchResponse = getBatchTransactionResponse(batchReceipt);

            // Copy the "Failed" element from the response to the FailureGroup:
            if (batchResponse != null) {

                // Get the "Failed" element from the batchResponse:
                KsaBatchTransactionResponse.Failed responseFailed = batchResponse.getFailed();

                // Copy "Failed" from the response to the report:
                if (responseFailed != null) {
                    // Create a new Failed object:
                    FailureGroup.Failed failed = new FailureGroup.Failed();
                    failed.getKsaTransactionAndReason().addAll(responseFailed.getKsaTransactionAndReason());
                    failureGroup.setFailed(failed);
                }

                // If "onlyFailed" is set to false, copy "Accepted" from receipt to the "BatchRejection" of report:
                if (!failedOnly && BatchReceiptStatus.FAILED.equals(batchReceipt.getStatus())) {
                    // Create a new "BatchRejection" element:
                    FailureGroup.BatchRejection batchRejection = new FailureGroup.BatchRejection();
                    List<KsaTransaction> ksaTransactions = extractKsaTransactions(batchResponse.getAccepted());
                    batchRejection.getKsaTransaction().addAll(ksaTransactions);
                    failureGroup.setBatchRejection(batchRejection);
                }
            }

            // Add the new FailureGroup to the report:
            report.getFailureGroup().add(failureGroup);
            failureGroup.setPostingEntity(externalId);
        }

        return JaxbUtils.toXml(report);
    }


    /**
     * Returns an XML account report for the given account in the <code>accountId</code> list, between the dates listed.
     * If <code>details</code> is <code>true</code>, then all transactions will also be reported.
     * If <code>paymentApplication</code> is <code>true</code> then <code>paymentApplication</code> method will be run
     * before the account is reported.
     * If <code>ageAccount</code> is <code>true</code>, the account will be aged before the report is generated.
     *
     * @param accountId          List of Account IDs for which to produce a report.
     * @param startDate          Start date of reporting period.
     * @param endDate            End date of reporting period.
     * @param details            Whether to report all transactions.
     * @param paymentApplication Whether to run "paymentApplication" before executing the report.
     * @param ageAccount         Whether to age the account before generating a report.
     * @param ignoreDeferments   Whether to ignore deferments.
     * @return String representation of an account report.
     * @see AccountReport
     */
    @Override
    public String generateAccountReport(String accountId, Date startDate, Date endDate, boolean details,
                                        boolean paymentApplication, boolean ageAccount, boolean ignoreDeferments) {

        if (startDate == null || endDate == null) {
            String errMsg = "Start Date and End Date cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (startDate.after(endDate)) {
            String errMsg = "Start Date cannot be greater than End Date: Start Date = " + startDate +
                    ", End Date = " + endDate;
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        Account account = accountService.getFullAccount(accountId);
        if (account == null) {
            String errMsg = "Account with ID = " + accountId + " does not exist";
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        if (paymentApplication) {
            paymentService.paymentApplication(accountId);
        }

        if (ageAccount) {
            accountService.ageDebt(accountId, ignoreDeferments);
        }

        // Getting all transactions for the given account
        List<Transaction> transactions = transactionService.getTransactions(accountId);

        // Filter out deferments if ignoreDeferment
        if (ignoreDeferments) {
            List<Transaction> paymentsAndCharges = new LinkedList<Transaction>();
            for (Transaction transaction : transactions) {
                TransactionTypeValue transactionType = transaction.getTransactionTypeValue();
                if (!TransactionTypeValue.DEFERMENT.equals(transactionType)) {
                    paymentsAndCharges.add(transaction);
                }
                transactions = paymentsAndCharges;
            }
        }

        BigDecimal priorBalance = BigDecimal.ZERO;
        BigDecimal futureBalance = BigDecimal.ZERO;
        BigDecimal netBalance = BigDecimal.ZERO;
        BigDecimal chargeAmount = BigDecimal.ZERO;
        BigDecimal paymentAmount = BigDecimal.ZERO;
        BigDecimal defermentAmount = BigDecimal.ZERO;
        BigDecimal unallocatedPaymentAmount = BigDecimal.ZERO;
        BigDecimal unallocatedChargeAmount = BigDecimal.ZERO;
        BigDecimal writtenOffAmount = BigDecimal.ZERO;

        List<Transaction> transactionWithinDateRange = new LinkedList<Transaction>();

        // Calculating the prior balance and setting the unallocated amount for transactions within the date range
        for (Transaction transaction : transactions) {
            Date effectiveDate = transaction.getEffectiveDate();
            BigDecimal unallocatedTransactionAmount = transaction.getUnallocatedAmount();
            if (startDate.after(effectiveDate)) {
                if (transaction instanceof Debit) {
                    priorBalance = priorBalance.add(unallocatedTransactionAmount);
                } else {
                    priorBalance = priorBalance.subtract(unallocatedTransactionAmount);
                }
            } else if (endDate.before(effectiveDate)) {
                if (transaction instanceof Debit) {
                    futureBalance = futureBalance.add(unallocatedTransactionAmount);
                } else {
                    futureBalance = futureBalance.subtract(unallocatedTransactionAmount);
                }
            } else {
                transactionWithinDateRange.add(transaction);
            }
        }

        ObjectFactory objectFactory = ObjectFactory.getInstance();

        AccountReport accountReport = objectFactory.createAccountReport();

        for (Transaction transaction : transactionWithinDateRange) {
            BigDecimal unallocatedTransactionAmount = transaction.getUnallocatedAmount();
            if (transaction instanceof Debit) {
                if (TransactionStatus.WRITING_OFF.equals(transaction.getStatus())) {
                    writtenOffAmount = writtenOffAmount.add(transaction.getAmount());
                }
                netBalance = netBalance.add(unallocatedTransactionAmount);
                chargeAmount = chargeAmount.add(transaction.getAmount());
                unallocatedChargeAmount = unallocatedChargeAmount.add(unallocatedTransactionAmount);
            } else {
                netBalance = netBalance.subtract(unallocatedTransactionAmount);
                if (transaction instanceof Payment) {
                    paymentAmount = paymentAmount.add(transaction.getAmount());
                    unallocatedPaymentAmount = unallocatedPaymentAmount.add(unallocatedTransactionAmount);
                } else {
                    defermentAmount = defermentAmount.add(transaction.getAmount());
                }
            }

            if (details) {

                AccountReport.TransactionDetail transactionDetail = accountReport.getTransactionDetail();
                if (transactionDetail == null) {
                    transactionDetail = new AccountReport.TransactionDetail();
                    accountReport.setTransactionDetail(transactionDetail);
                }

                ListTransaction listTransaction = new ListTransaction();

                listTransaction.setTransactionType(transaction.getTransactionType().getId().getId());
                listTransaction.setChargeOrPayment(transaction.getTransactionTypeValue().toString().toLowerCase());
                listTransaction.setAmount(getFormattedAmount(transaction.getAmount()));
                listTransaction.setNativeAmount(getFormattedAmount(transaction.getNativeAmount()));

                if (transaction.getCurrency() != null) {
                    listTransaction.setCurrency(transaction.getCurrency().getCode());
                }

                listTransaction.setEffectiveDate(CalendarUtils.toXmlGregorianCalendar(transaction.getEffectiveDate(), true));
                listTransaction.setStatementText(transaction.getStatementText());

                transactionDetail.getListTransaction().add(listTransaction);
            }
        }

        accountReport.setAccountIdentifier(accountId);

        AccountReport.Name name = new AccountReport.Name();
        com.sigmasys.kuali.ksa.model.PersonName defaultPersonName = account.getDefaultPersonName();
        if (defaultPersonName != null) {
            logger.debug("Default person name = " + defaultPersonName);
            PersonName personName = new PersonName();
            personName.setTitle(defaultPersonName.getTitle());
            personName.setSuffix(defaultPersonName.getSuffix());
            personName.setFirstName(defaultPersonName.getFirstName());
            personName.setLastName(defaultPersonName.getLastName());
            personName.setMiddleName(defaultPersonName.getMiddleName());
            personName.setKimType(defaultPersonName.getKimNameType());
            personName.setDefault(true);
            name.setPersonName(personName);
        } else {
            OrgName orgName = account.getOrgName();
            if (orgName != null) {
                name.setCompanyName(orgName.getName());
            }
        }

        accountReport.setName(name);

        accountReport.setReportingPeriod(createReportingPeriod(startDate, endDate));

        AccountReport.Balances balances = objectFactory.createAccountReportBalances();

        if (ageAccount && account instanceof ChargeableAccount) {
            ChargeableAccount chargeableAccount = (ChargeableAccount) account;
            LatePeriod latePeriod = chargeableAccount.getLatePeriod();
            if (latePeriod != null) {
                AgedBalance agedBalance = objectFactory.createAgedBalance();
                agedBalance.getPeriodLengthAndPeriodBalance().add(latePeriod.getDaysLate1());
                agedBalance.getPeriodLengthAndPeriodBalance().add(getFormattedAmount(chargeableAccount.getAmountLate1()));
                agedBalance.getPeriodLengthAndPeriodBalance().add(latePeriod.getDaysLate2());
                agedBalance.getPeriodLengthAndPeriodBalance().add(getFormattedAmount(chargeableAccount.getAmountLate2()));
                agedBalance.getPeriodLengthAndPeriodBalance().add(latePeriod.getDaysLate3());
                agedBalance.getPeriodLengthAndPeriodBalance().add(getFormattedAmount(chargeableAccount.getAmountLate3()));
                balances.setAgedBalance(agedBalance);
            }
        }

        balances.setNetBalance(getFormattedAmount(netBalance));
        balances.setTotalCharges(getFormattedAmount(chargeAmount));
        balances.setTotalPayments(getFormattedAmount(paymentAmount));
        balances.setTotalDeferments(getFormattedAmount(defermentAmount));
        balances.setWrittenOff(getFormattedAmount(writtenOffAmount));
        balances.setUnallocatedCharges(getFormattedAmount(unallocatedChargeAmount));
        balances.setUnallocatedPayments(getFormattedAmount(unallocatedPaymentAmount));

        balances.setPriorBalance(getFormattedAmount(priorBalance));
        balances.setFutureBalance(getFormattedAmount(futureBalance));

        accountReport.setBalances(balances);

        return JaxbUtils.toXml(accountReport);

    }


    /**
     * Produces a receipt for a transaction with the specified ID.
     *
     * @param transactionId ID of a transaction for which to produce a receipt.
     * @return String representation of a transaction XML receipt.
     * @see com.sigmasys.kuali.ksa.jaxb.TransactionReceipt
     */
    @Override
    public String generateTransactionReceipt(Long transactionId) {

        // Load the transaction:
        Transaction transaction = transactionService.getTransaction(transactionId);

        // Verify transaction exists or throw an exception:
        if (transaction == null) {
            String errMsg = "Transaction does not exist for the given ID = " + transactionId;
            logger.error(errMsg);
            throw new TransactionNotFoundException(errMsg);
        }

        // Verify the Transaction is a Payment:
        if (transaction.getTransactionTypeValue() != TransactionTypeValue.PAYMENT) {
            String errMsg = "Cannot generate receipt for a non-payment transaction with ID = " + transactionId;
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        // Create a new Receipt object:
        TransactionReceipt transactionReceipt = new TransactionReceipt();
        String currentUserId = userSessionManager.getUserId();

        Date transactionCreationDate = transaction.getCreationDate();
        Date receiptDate = (transactionCreationDate != null) ? transactionCreationDate : new Date();

        transactionReceipt.setAmount(getFormattedAmount(transaction.getAmount()));
        transactionReceipt.setAuthorization(transaction.getExternalId());
        transactionReceipt.setPostedToAccountIdentifier(transaction.getAccountId());
        transactionReceipt.setPostingUserIdentifier(currentUserId);
        transactionReceipt.setReceiptDate(CalendarUtils.toXmlGregorianCalendar(receiptDate, true));
        transactionReceipt.setReceiptTime(CalendarUtils.toXmlGregorianCalendar(receiptDate, false));
        transactionReceipt.setTransactionIdentifier(String.valueOf(transactionId));

        // Create a new TransactionType object:
        TransactionReceipt.TransactionType transactionType = new TransactionReceipt.TransactionType();

        transactionType.setTransactionTypeIdentifier(transaction.getTransactionType().getId().getId());
        transactionType.setTransactionTypeName(transaction.getStatementText());
        transactionReceipt.setTransactionType(transactionType);

        // Get the system currency:
        String systemCurrencyCode = java.util.Currency.getInstance(Locale.getDefault()).getCurrencyCode();

        // Compare system currency to the transaction currency:
        com.sigmasys.kuali.ksa.model.Currency transactionCurrency = transaction.getCurrency();

        if ((transactionCurrency != null) && !StringUtils.equalsIgnoreCase(transactionCurrency.getCode(), systemCurrencyCode)) {
            // Create a ForeignTransactionNode:
            TransactionReceipt.ForeignTransaction foreignTransaction = new TransactionReceipt.ForeignTransaction();

            foreignTransaction.setCurrency(transactionCurrency.getCode());
            foreignTransaction.setNativeAmount(getFormattedAmount(transaction.getNativeAmount()));
            transactionReceipt.setForeignTransaction(foreignTransaction);
        }

        return JaxbUtils.toXml(transactionReceipt);
    }

    /**
     * Generates IRS 8300 report based on the CashLimitEvent object specified by ID.
     *
     * @param cashLimitEventId CashLimitEvent ID
     * @return String representation of Irs8300 XML report.
     */
    @Override
    @PermissionsAllowed({Permission.GENERATE_IRS_8300, Permission.READ_IRS_8300})
    public String generateIrs8300Report(Long cashLimitEventId) {

        CashLimitEvent cashLimitEvent = cashLimitService.getCashLimitEvent(cashLimitEventId);
        if (cashLimitEvent == null) {
            String errMsg = "Cannot find CashLimitEvent with ID = " + cashLimitEventId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        if (!CashLimitEventStatus.QUEUED.equals(cashLimitEvent.getStatus())) {
            String errMsg = "Only CashLimitEvent objects with QUEUED status can be exported, ID = " + cashLimitEventId;
            logger.error(errMsg);
            throw new IllegalStateException(errMsg);
        }

        Account accountEntity = accountService.getFullAccount(cashLimitEvent.getAccountId());

        // Create a copy of the Account object:
        accountEntity = BeanUtils.getDeepCopy(accountEntity);

        DirectChargeAccount account = (DirectChargeAccount) accountEntity;
        if (account == null) {
            String errMsg = "Account with ID = " + cashLimitEvent.getAccountId() + " does not exist";
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        AccountProtectedInfo accountInfo = accountService.getAccountProtectedInfo(cashLimitEvent.getAccountId());

        Set<Payment> payments = cashLimitEvent.getPayments();

        List<CashLimitParameter> activeParameters = cashLimitService.getCashLimitParameters(true);

        Irs8300 irs8300 = new Irs8300();

        // Filling out Filer info
        Irs8300.Filer filer = new Irs8300.Filer();
        filer.setBusinessName(configService.getParameter(Constants.KSA_8300_FILER_NAME));
        filer.setFein(configService.getParameter(Constants.KSA_8300_FILER_FEIN));
        filer.setNatureOfBusiness(configService.getParameter(Constants.KSA_8300_BUSINESS_NATURE));

        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setAddressLine1(configService.getParameter(Constants.KSA_8300_FILER_ADDRESS1));
        postalAddress.setAddressLine2(configService.getParameter(Constants.KSA_8300_FILER_ADDRESS2));
        postalAddress.setAddressLine3(configService.getParameter(Constants.KSA_8300_FILER_ADDRESS3));
        postalAddress.setCity(configService.getParameter(Constants.KSA_8300_FILER_CITY));
        postalAddress.setPostalCode(configService.getParameter(Constants.KSA_8300_FILER_ZIP));
        postalAddress.setCountryCode(configService.getParameter(Constants.KSA_8300_FILER_COUNTRY));

        filer.setPostalAddress(postalAddress);

        irs8300.setFiler(filer);

        // Filling out TransactionDescription
        Irs8300.TransactionDescription transactionDesc = new Irs8300.TransactionDescription();
        transactionDesc.setDate(CalendarUtils.toXmlGregorianCalendar(cashLimitEvent.getEventDate()));
        transactionDesc.setMultiplePayments(cashLimitEvent.isMultiple());
        transactionDesc.setTotalAmount(TransactionUtils.getFormattedAmount(cashLimitEvent.getPaymentAmount()));
        transactionDesc.setTotalPrice(TransactionUtils.getFormattedAmount(cashLimitEvent.getChargeAmount()));

        // Filling out Payer
        Irs8300.Payer payer = new Irs8300.Payer();
        Irs8300.Payer.Identity identity = new Irs8300.Payer.Identity();

        identity.setDateOfBirth(CalendarUtils.toXmlGregorianCalendar(account.getDateOfBirth()));

        if (accountInfo != null) {

            TaxType taxType = accountInfo.getTaxType();
            if (taxType != null && taxType.getCode().equals(configService.getParameter(Constants.KSA_8300_SSN_TAX_TYPE))) {
                identity.setTaxIdentifier(accountInfo.getTaxReference());
            }

            Irs8300.Payer.Identity.IdentityDocument identityDocument = new Irs8300.Payer.Identity.IdentityDocument();

            identityDocument.setIdIssuer(accountInfo.getIdentityIssuer());
            identityDocument.setIdSerial(accountInfo.getIdentitySerial());
            identityDocument.setIdType(accountInfo.getIdentityType().getCode());

            identity.setIdentityDocument(identityDocument);
        }

        payer.setIdentity(identity);

        com.sigmasys.kuali.ksa.model.PersonName defaultPersonName = account.getDefaultPersonName();

        PersonName personName = new PersonName();
        personName.setTitle(defaultPersonName.getTitle());
        personName.setSuffix(defaultPersonName.getSuffix());
        personName.setFirstName(defaultPersonName.getFirstName());
        personName.setLastName(defaultPersonName.getLastName());
        personName.setMiddleName(defaultPersonName.getMiddleName());
        personName.setKimType(defaultPersonName.getKimNameType());
        personName.setDefault(true);

        payer.setPersonName(personName);

        com.sigmasys.kuali.ksa.model.PostalAddress defaultPostalAddress = account.getDefaultPostalAddress();

        postalAddress = new PostalAddress();
        postalAddress.setAddressLine1(defaultPostalAddress.getStreetAddress1());
        postalAddress.setAddressLine2(defaultPostalAddress.getStreetAddress2());
        postalAddress.setAddressLine3(defaultPostalAddress.getStreetAddress3());
        postalAddress.setCity(defaultPostalAddress.getCity());
        postalAddress.setStateCode(defaultPostalAddress.getState());
        postalAddress.setPostalCode(defaultPostalAddress.getPostalCode());
        postalAddress.setCountryCode(defaultPostalAddress.getCountry());

        payer.setPostalAddress(postalAddress);

        irs8300.setPayer(payer);

        Irs8300.TransactionDescription.Detail transactionDetail = new Irs8300.TransactionDescription.Detail();

        // Retrieving IRS 8300 default type
        String defaultType = configService.getParameter(Constants.KSA_8300_DEFAULT_TYPE);
        List<String> standardTypes = Irs8300Type.getNames();
        if (standardTypes.contains(defaultType)) {
            transactionDetail.setType(defaultType);
        } else {
            transactionDetail.setOtherType(defaultType);
        }

        // Creating FinancialBreakdown
        Irs8300.TransactionDescription.Detail.FinancialBreakdown breakdown =
                new Irs8300.TransactionDescription.Detail.FinancialBreakdown();

        StringBuilder serials = new StringBuilder();

        for (CashLimitParameter parameter : activeParameters) {

            Tag tag = parameter.getTag();

            serials.append(parameter.getAuthorityName());
            serials.append(",");

            BigDecimal transactionAmount = BigDecimal.ZERO;
            BigDecimal transactionNativeAmount = BigDecimal.ZERO;

            Set<Payment> paymentsByTag = getTransactionsByTag(payments, tag);
            for (Payment payment : paymentsByTag) {
                populateSerials(serials, payment);
                transactionAmount = transactionAmount.add(payment.getAmount());
                transactionNativeAmount = transactionNativeAmount.add(payment.getNativeAmount());
            }

            String xmlElement = parameter.getXmlElement();
            BigDecimal amount = TransactionUtils.getFormattedAmount(transactionAmount);
            BigDecimal nativeAmount = TransactionUtils.getFormattedAmount(transactionNativeAmount);

            JAXBElement<String> jaxbType =
                    ObjectFactory.getInstance().createIrs8300FinancialBreakdownType(xmlElement);

            JAXBElement<BigDecimal> jaxbAmount =
                    ObjectFactory.getInstance().createIrs8300FinancialBreakdownAmount(amount);

            JAXBElement<BigDecimal> jaxbNativeAmount =
                    ObjectFactory.getInstance().createIrs8300FinancialBreakdownNativeAmount(nativeAmount);

            // Adding the type, amount and native amount to
            breakdown.getTypeAndAmountAndNativeAmount().add(jaxbType);
            breakdown.getTypeAndAmountAndNativeAmount().add(jaxbAmount);
            breakdown.getTypeAndAmountAndNativeAmount().add(jaxbNativeAmount);
        }

        // Setting the breakdown and serials
        transactionDetail.setFinancialBreakdown(breakdown);
        transactionDetail.setSerials(serials.toString());

        // Setting TransactionDetail to TransactionDescription
        transactionDesc.setDetail(transactionDetail);

        // Setting TransactionDescription to Irs8300
        irs8300.setTransactionDescription(transactionDesc);

        return JaxbUtils.toXml(irs8300);

    }


    /**
     * Produces a new bill based on the given parameters.
     *
     * @param accountId                    Account ID
     * @param message                      Bill message
     * @param billDate                     Bill date
     * @param startDate                    Start date
     * @param endDate                      End date
     * @param rollupIdsOnSameDate          Rollup IDs on the same date
     * @param rollupIdsOnSameStatement     Rollup IDs on the same statement
     * @param showOnlyUnbilledTransactions true if only unbilled transactions have to be shown
     * @param showDeferments               true if deferments have to be shown
     * @param showDependents               true if dependents have to be shown
     * @param showInternalTransactions     true if internal transactions have to be shown
     * @param runPaymentApplication        if true then Payment Application will be run
     * @return String representation of BillRecord XML
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.GENERATE_BILL)
    public String generateBill(String accountId,
                               String message,
                               Date billDate,
                               Date startDate,
                               Date endDate,
                               Set<Long> rollupIdsOnSameDate,
                               Set<Long> rollupIdsOnSameStatement,
                               boolean showOnlyUnbilledTransactions,
                               boolean showDeferments,
                               boolean showDependents,
                               boolean showInternalTransactions,
                               boolean runPaymentApplication) {

        KsaBill ksaBill = createBill(accountId,
                message,
                billDate,
                startDate,
                endDate,
                rollupIdsOnSameDate,
                rollupIdsOnSameStatement,
                showOnlyUnbilledTransactions,
                showDeferments,
                showDependents,
                showInternalTransactions,
                runPaymentApplication);

        return JaxbUtils.toXml(ksaBill);
    }

    /**
     * Produces bills for multiple KSA accounts based on the given parameters.
     *
     * @param accountIds                   List of Account IDs
     * @param message                      Bill message
     * @param billDate                     Bill date
     * @param startDate                    Start date
     * @param endDate                      End date
     * @param rollupIdsOnSameDate          Rollup IDs on the same date
     * @param rollupIdsOnSameStatement     Rollup IDs on the same statement
     * @param showOnlyUnbilledTransactions true if only unbilled transactions have to be shown
     * @param showDeferments               true if deferments have to be shown
     * @param showDependents               true if dependents have to be shown
     * @param showInternalTransactions     true if internal transactions have to be shown
     * @param runPaymentApplication        if true then Payment Application will be run
     * @return BatchKsaBill XML
     */
    @Override
    @Transactional(readOnly = false, timeout = 3600)
    @PermissionsAllowed(Permission.GENERATE_BILL)
    public String generateBills(List<String> accountIds,
                                String message,
                                Date billDate,
                                Date startDate,
                                Date endDate,
                                Set<Long> rollupIdsOnSameDate,
                                Set<Long> rollupIdsOnSameStatement,
                                boolean showOnlyUnbilledTransactions,
                                boolean showDeferments,
                                boolean showDependents,
                                boolean showInternalTransactions,
                                boolean runPaymentApplication) {

        BatchKsaBill batchKsaBill = ObjectFactory.getInstance().createBatchKsaBill();

        for (String accountId : accountIds) {

            KsaBill ksaBill = createBill(accountId,
                    message,
                    billDate,
                    startDate,
                    endDate,
                    rollupIdsOnSameDate,
                    rollupIdsOnSameStatement,
                    showOnlyUnbilledTransactions,
                    showDeferments,
                    showDependents,
                    showInternalTransactions,
                    runPaymentApplication);

            batchKsaBill.getKsaBill().add(ksaBill);
        }

        return JaxbUtils.toXml(batchKsaBill);
    }


    protected KsaBill createBill(String accountId,
                                 String message,
                                 Date billDate,
                                 Date startDate,
                                 Date endDate,
                                 Set<Long> rollupIdsOnSameDate,
                                 Set<Long> rollupIdsOnSameStatement,
                                 boolean showOnlyUnbilledTransactions,
                                 boolean showDeferments,
                                 boolean showDependents,
                                 boolean showInternalTransactions,
                                 boolean runPaymentApplication) {

        Account account = accountService.getFullAccount(accountId);
        if (account == null) {
            String errMsg = "Account with ID = " + accountId + " does not exist";
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        if (billDate == null) {
            String errMsg = "Bill date cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        billDate = CalendarUtils.removeTime(billDate);

        if (billDate.after(new Date())) {
            String errMsg = "Bill date cannot be the future date";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (runPaymentApplication) {
            paymentService.paymentApplication(accountId);
        }

        List<Transaction> transactions = transactionService.getTransactions(accountId, startDate, endDate);

        if (CollectionUtils.isNotEmpty(transactions)) {

            TransactionUtils.orderByEffectiveDate(transactions, false);

            if (showOnlyUnbilledTransactions && startDate == null) {
                startDate = transactions.get(transactions.size() - 1).getEffectiveDate();
            }

            if (endDate == null) {
                endDate = transactions.get(0).getEffectiveDate();
            }
        }

        if (startDate == null) {
            BillRecord billRecord = billRecordService.getLatestBillRecord(accountId);
            if (billRecord != null) {
                startDate = billRecord.getEndDate();
            }
        }

        if (startDate == null) {
            String errMsg = "Start date cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (endDate == null) {
            String errMsg = "End date cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (startDate != null && endDate != null && startDate.after(endDate)) {
            String errMsg = "Start date cannot be greater than End date";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        // Removing transactions that are outside the date range
        List<Transaction> tempTransactions = new ArrayList<Transaction>(transactions);
        for (ListIterator<Transaction> iterator = tempTransactions.listIterator(); iterator.hasNext(); ) {
            Transaction transaction = iterator.next();
            Date effectiveDate = transaction.getEffectiveDate();
            if (effectiveDate.before(startDate) || effectiveDate.after(endDate)) {
                iterator.remove();
            }
        }

        final ObjectFactory objectFactory = ObjectFactory.getInstance();

        KsaBill ksaBill = objectFactory.createKsaBill();

        KsaBill.AccountOwner accountOwner = objectFactory.createKsaBillAccountOwner();

        accountOwner.setAccountIdentifier(accountId);

        com.sigmasys.kuali.ksa.model.PersonName defaultPersonName = account.getDefaultPersonName();

        PersonName personName = objectFactory.createPersonName();
        personName.setDefault(true);
        personName.setTitle(defaultPersonName.getTitle());
        personName.setSuffix(defaultPersonName.getSuffix());
        personName.setFirstName(defaultPersonName.getFirstName());
        personName.setMiddleName(defaultPersonName.getMiddleName());
        personName.setLastName(defaultPersonName.getLastName());
        personName.setKimType(defaultPersonName.getKimNameType());

        accountOwner.setPersonName(personName);

        com.sigmasys.kuali.ksa.model.PostalAddress defaultPostalAddress = account.getDefaultPostalAddress();

        PostalAddress postalAddress = objectFactory.createPostalAddress();
        postalAddress.setAddressLine1(defaultPostalAddress.getStreetAddress1());
        postalAddress.setAddressLine2(defaultPostalAddress.getStreetAddress2());
        postalAddress.setAddressLine3(defaultPostalAddress.getStreetAddress3());
        postalAddress.setCity(defaultPostalAddress.getCity());
        postalAddress.setStateCode(defaultPostalAddress.getState());
        postalAddress.setPostalCode(defaultPostalAddress.getPostalCode());
        postalAddress.setCountryCode(defaultPostalAddress.getCountry());

        accountOwner.setPostalAddress(postalAddress);

        com.sigmasys.kuali.ksa.model.ElectronicContact defaultContact = account.getDefaultElectronicContact();

        ElectronicContact contact = objectFactory.createElectronicContact();

        ElectronicContact.TelephoneNumber phoneNumber = objectFactory.createElectronicContactTelephoneNumber();
        phoneNumber.setNumber(defaultContact.getPhoneNumber());
        phoneNumber.setExtension(defaultContact.getPhoneExtension());
        phoneNumber.setCountryCode(defaultContact.getPhoneCountry());

        contact.setEmailAddress(defaultContact.getEmailAddress());
        contact.setTelephoneNumber(phoneNumber);

        accountOwner.setElectronicContact(contact);

        ksaBill.setAccountOwner(accountOwner);

        KsaBill.Cycle billCycle = objectFactory.createKsaBillCycle();
        billCycle.setBillGenerated(CalendarUtils.toXmlGregorianCalendar(new Date()));
        billCycle.setBillDate(CalendarUtils.toXmlGregorianCalendar(billDate, true));

        if (startDate != null) {
            billCycle.setBillStart(CalendarUtils.toXmlGregorianCalendar(startDate, true));
        }

        if (endDate != null) {
            billCycle.setBillEnd(CalendarUtils.toXmlGregorianCalendar(endDate, true));
        }

        ksaBill.setCycle(billCycle);

        // Calling ageDebt() with deferments
        ChargeableAccount chargeableAccount = accountService.ageDebt(accountId, billDate, false);

        KsaBill.Balances balances = objectFactory.createKsaBillBalances();

        KsaBill.Balances.WithDeferments withDeferments = objectFactory.createKsaBillBalancesWithDeferments();

        AgedBalance agedBalance = objectFactory.createAgedBalance();

        LatePeriod latePeriod = chargeableAccount.getLatePeriod();

        if (latePeriod != null) {

            agedBalance.getPeriodLengthAndPeriodBalance().add(latePeriod.getDaysLate1());
            agedBalance.getPeriodLengthAndPeriodBalance().add(getFormattedAmount(chargeableAccount.getAmountLate1()));

            agedBalance.getPeriodLengthAndPeriodBalance().add(latePeriod.getDaysLate2());
            agedBalance.getPeriodLengthAndPeriodBalance().add(getFormattedAmount(chargeableAccount.getAmountLate2()));

            agedBalance.getPeriodLengthAndPeriodBalance().add(latePeriod.getDaysLate3());
            agedBalance.getPeriodLengthAndPeriodBalance().add(getFormattedAmount(chargeableAccount.getAmountLate3()));
        }

        withDeferments.setAgedBalance(agedBalance);

        withDeferments.setOverdueBalance(getFormattedAmount(chargeableAccount.getAmountLate1().add(
                chargeableAccount.getAmountLate2()).add(chargeableAccount.getAmountLate3())));

        withDeferments.setDueBalance(getFormattedAmount(accountService.getDueBalance(accountId, billDate, false)));
        withDeferments.setFutureBalance(getFormattedAmount(accountService.getFutureBalance(accountId, billDate, false)));
        withDeferments.setTotalBalance(getFormattedAmount(accountService.getOutstandingBalance(accountId, billDate, false)));
        withDeferments.setTotalDeferred(getFormattedAmount(accountService.getDeferredBalance(accountId, billDate)));

        // Checking if deferments exist
        Query query = em.createQuery("select 1 from Deferment " +
                " where expirationDate != null and expirationDate between :billDate and CURRENT_DATE");

        query.setParameter("billDate", billDate);
        query.setMaxResults(1);

        if (CollectionUtils.isNotEmpty(query.getResultList())) {
            withDeferments.setHasExpiredDeferment(true);
        }

        balances.setWithDeferments(withDeferments);

        // Calling ageDebt() without deferments
        chargeableAccount = accountService.ageDebt(accountId, billDate, false);

        KsaBill.Balances.WithoutDeferments withoutDeferments = objectFactory.createKsaBillBalancesWithoutDeferments();

        agedBalance = objectFactory.createAgedBalance();

        latePeriod = chargeableAccount.getLatePeriod();

        if (latePeriod != null) {

            agedBalance.getPeriodLengthAndPeriodBalance().add(latePeriod.getDaysLate1());
            agedBalance.getPeriodLengthAndPeriodBalance().add(getFormattedAmount(chargeableAccount.getAmountLate1()));

            agedBalance.getPeriodLengthAndPeriodBalance().add(latePeriod.getDaysLate2());
            agedBalance.getPeriodLengthAndPeriodBalance().add(getFormattedAmount(chargeableAccount.getAmountLate2()));

            agedBalance.getPeriodLengthAndPeriodBalance().add(latePeriod.getDaysLate3());
            agedBalance.getPeriodLengthAndPeriodBalance().add(getFormattedAmount(chargeableAccount.getAmountLate3()));
        }

        withoutDeferments.setAgedBalance(agedBalance);

        withoutDeferments.setOverdueBalance(getFormattedAmount(chargeableAccount.getAmountLate1().add(
                chargeableAccount.getAmountLate2()).add(chargeableAccount.getAmountLate3())));

        withoutDeferments.setDueBalance(getFormattedAmount(accountService.getDueBalance(accountId, billDate, true)));
        withoutDeferments.setFutureBalance(getFormattedAmount(accountService.getFutureBalance(accountId, billDate, true)));
        withoutDeferments.setTotalBalance(getFormattedAmount(accountService.getOutstandingBalance(accountId, billDate, true)));

        balances.setWithoutDeferments(withoutDeferments);

        ksaBill.setBalances(balances);

        if (showOnlyUnbilledTransactions) {

            query = em.createQuery("select b.transactions from BillRecord b where b.account.id = :accountId");
            query.setParameter("accountId", accountId);

            List<Transaction> transactionsToExclude = query.getResultList();

            if (CollectionUtils.isNotEmpty(transactionsToExclude)) {

                Set<Long> transactionIds = new HashSet<Long>(transactionsToExclude.size());
                for (Transaction transaction : transactionsToExclude) {
                    transactionIds.add(transaction.getId());
                }

                ListIterator<Transaction> iterator = tempTransactions.listIterator();
                while (iterator.hasNext()) {
                    Long transactionId = iterator.next().getId();
                    if (transactionIds.contains(transactionId)) {
                        iterator.remove();
                    }
                }
            }

        }

        ListIterator<Transaction> iterator = tempTransactions.listIterator();
        while (iterator.hasNext()) {
            Transaction transaction = iterator.next();
            if ((!showDeferments && transaction.getTransactionTypeValue() == TransactionTypeValue.DEFERMENT) ||
                    (!showInternalTransactions && transaction.isInternal())) {
                iterator.remove();
            }
        }

        TransactionList transactionList = objectFactory.createTransactionList();

        Set<Long> processedTransactionIds = new HashSet<Long>();

        for (iterator = tempTransactions.listIterator(); iterator.hasNext(); ) {

            Transaction transaction = iterator.next();

            String transactionTypeValue = transaction.getTransactionTypeValue().toString().toLowerCase();

            ListTransaction listTransaction = objectFactory.createListTransaction();

            listTransaction.setChargeOrPayment(transactionTypeValue);
            listTransaction.setTransactionType(transaction.getTransactionType().getId().getId());
            listTransaction.setEffectiveDate(CalendarUtils.toXmlGregorianCalendar(transaction.getEffectiveDate(), true));
            listTransaction.setCurrency(transaction.getCurrency().getCode());
            listTransaction.setAmount(transaction.getAmount());
            listTransaction.setNativeAmount(transaction.getNativeAmount());
            listTransaction.setStatementText(transaction.getStatementText());

            Rollup rollup = transaction.getRollup();

            if (rollup != null && !processedTransactionIds.contains(transaction.getId())) {

                boolean rollupOnSameDate = rollupIdsOnSameDate.contains(rollup.getId());
                boolean rollupOnSameStatement = rollupIdsOnSameStatement.contains(rollup.getId());

                if (rollupOnSameDate || rollupOnSameStatement) {

                    TransactionList.Rollup listRollup = objectFactory.createTransactionListRollup();

                    listRollup.setRollupName(rollup.getName());
                    listRollup.setDate(CalendarUtils.toXmlGregorianCalendar(transaction.getEffectiveDate(), true));
                    listRollup.setChargeOrPayment(transactionTypeValue);

                    BigDecimal rollupAmount = BigDecimal.ZERO;

                    for (Transaction t : tempTransactions) {

                        boolean processTransaction =
                                !processedTransactionIds.contains(t.getId()) &&
                                        t.getRollup() != null &&
                                        t.getRollup().getId().equals(rollup.getId()) &&
                                        (rollupOnSameDate && t.getEffectiveDate().equals(transaction.getEffectiveDate()) || rollupOnSameStatement);

                        if (processTransaction) {

                            if (t.getTransactionTypeValue() == transaction.getTransactionTypeValue()) {
                                rollupAmount = rollupAmount.add(t.getAmount());
                            } else {
                                rollupAmount = rollupAmount.subtract(t.getAmount());
                            }

                            processedTransactionIds.add(t.getId());
                        }
                    }

                    listRollup.setAmount(getFormattedAmount(rollupAmount));

                    listRollup.setListTransaction(listTransaction);

                    transactionList.getRollup().add(listRollup);

                } else {

                    transactionList.getListTransaction().add(listTransaction);
                }
            }
        }

        ksaBill.setTransactionList(transactionList);
        ksaBill.setPreferredDeliveryMode(configService.getParameter(Constants.BILL_DELIVERY_METHOD));

        Set<Long> transactionIds = new HashSet<Long>(transactions.size());
        for (Transaction transaction : tempTransactions) {
            transactionIds.add(transaction.getId());
        }

        BillRecord billRecord = billRecordService.createBillRecord(accountId, message, billDate, startDate, endDate,
                showOnlyUnbilledTransactions, showInternalTransactions, showDeferments, showDependents, transactionIds);

        ksaBill.setBillId(billRecord.getId().toString());
        ksaBill.setMessage(message);

        return ksaBill;
    }


/* =====================================================================
 *
 * Helper methods.
 *
 * ====================================================================
 */

    /**
     * Used by IRS 8300 report generator.
     *
     * @param transactions a set of transactions
     * @param tag          Tag instance
     * @param <T>          Any subclass of Transaction
     * @return transactions filtered by tag
     */
    private <T extends Transaction> Set<T> getTransactionsByTag(Set<T> transactions, Tag tag) {
        Set<T> transactionsForTag = new HashSet<T>();
        for (T transaction : transactions) {
            Set<Tag> tags = transaction.getTags();
            if (CollectionUtils.isNotEmpty(tags)) {
                boolean tagIsPresent = false;
                for (Tag t : tags) {
                    if (tag.getId().equals(t.getId())) {
                        tagIsPresent = true;
                        break;
                    }
                }
                if (tagIsPresent) {
                    transactionsForTag.add(transaction);
                }
            }
        }
        return transactionsForTag;
    }

    /**
     * Used by IRS 8300 report generator.
     *
     * @param builder serial value builder
     * @param payment Payment instance
     */
    private void populateSerials(StringBuilder builder, Payment payment) {
        String defaultCurrencyCode = java.util.Currency.getInstance(Locale.getDefault()).getCurrencyCode();
        builder.append(payment.getId());
        builder.append("(");
        builder.append(defaultCurrencyCode);
        builder.append(" ");
        builder.append(TransactionUtils.formatAmount(payment.getAmount()));
        builder.append(")");
        String transactionCurrencyCode = payment.getCurrency().getCode();
        if (!transactionCurrencyCode.equalsIgnoreCase(defaultCurrencyCode)) {
            builder.append("(");
            builder.append(transactionCurrencyCode);
            builder.append(" ");
            builder.append(TransactionUtils.formatAmount(payment.getNativeAmount()));
            builder.append(")");
        }
        builder.append(payment.getExternalId() != null ? payment.getExternalId() : "no serial");
        builder.append(".");
    }

    /**
     * Tries to find an object of type <code>AgedBalanceReport.AgeBreakdown</code> that
     * has Late Dates 1 through 3 matching the given Account. Returns the index of a
     * matching object in the given list.
     *
     * @param ageBreakdownAndAgeAccountDetailList
     *                A list of report detail objects.
     * @param account ChargeableAccount to match its late date periods.
     * @return Index of a matching <code>AgedBalanceReport.AgeBreakdown</code> object.
     */
    private int indexOfAgeBreakdown(List<Object> ageBreakdownAndAgeAccountDetailList, ChargeableAccount account) {
        // Get the Account's LatePeriod object:
        LatePeriod latePeriod = account.getLatePeriod();

        if (latePeriod != null) {
            // Iterate through the list and try to match an AgeBreakdown:
            for (int i = 0, sz = ageBreakdownAndAgeAccountDetailList.size(); i < sz; i++) {
                Object o = ageBreakdownAndAgeAccountDetailList.get(i);

                if (o instanceof AgedBalanceReport.AgeBreakdown) {
                    AgedBalanceReport.AgeBreakdown ageBreakdown = (AgedBalanceReport.AgeBreakdown) o;

                    if ((safeCompare(ageBreakdown.getPeriod1(), latePeriod.getDaysLate1()) == 0)
                            && (safeCompare(ageBreakdown.getPeriod2(), latePeriod.getDaysLate2()) == 0)
                            && (safeCompare(ageBreakdown.getPeriod3(), latePeriod.getDaysLate3()) == 0)) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Safely compares a primitive int to an Integer wrapper object.
     * If Integer is null, returns 1.
     *
     * @param i  Primitive
     * @param ii Integer.
     * @return Comparison result.
     */
    private int safeCompare(int i, Integer ii) {
        return (ii != null) ? ii.compareTo(i) : 1;
    }

    /**
     * Finds all <code>BatchReceipt</code> objects that fall into the specified date range,
     * have the status of "Failed" or "Partial" and have the specified entity ID (optionally).
     *
     * @param fromDate   Beginning of the search date range.
     * @param toDate     End of the search date range.
     * @param externalId External identifier (optional).
     * @return List of matching <code>BatchReceipt</code> objects.
     */
    private List<BatchReceipt> findFailedAndPartialBatchReceipts(Date fromDate, Date toDate, String externalId) {

        // Build a query for BatchReceipts:
        String queryStr = "select br from BatchReceipt br " +
                " where br.batchDate between :fromDate and :toDate and br.statusCode in (:statuses)";

        // Add "externalId" condition if exists:
        if (StringUtils.isNotBlank(externalId)) {
            queryStr += " and br.externalId = :externalId";
        }

        // Create a new Query object:
        Query query = em.createQuery(queryStr);

        query.setParameter("fromDate", fromDate);
        query.setParameter("toDate", toDate);

        List<String> statuses = Arrays.asList(BatchReceiptStatus.FAILED_CODE, BatchReceiptStatus.PARTIALLY_ACCEPTED_CODE);
        query.setParameter("statuses", statuses);

        // Set the "externalId" parameter if exists:
        if (StringUtils.isNotBlank(externalId)) {
            query.setParameter("externalId", externalId);
        }

        // Execute the query:
        return query.getResultList();
    }

    /**
     * Extracts the XML from a BatchReceipt and marshals to a <code>KsaBatchTransactionResponse</code> object.
     *
     * @param batchReceipt A BatchReceipt to extract its receipt XML from.
     * @return Marshaled <code>KsaBatchTransactionResponse</code> object from the given BatchReceipt.
     */
    private KsaBatchTransactionResponse getBatchTransactionResponse(BatchReceipt batchReceipt) {
        // Get the response XML, which is the "outgoingXml" attribute:
        XmlDocument receiptXml = batchReceipt.getOutgoingXml();
        return receiptXml != null ? JaxbUtils.fromXml(receiptXml.getXml(), KsaBatchTransactionResponse.class) : null;
    }

    /**
     * Extracts only <code>KsaTransaction</code> objects from the specified <code>Accepted</code> object.
     *
     * @param accepted KsaBatchTransactionResponse.Accepted object.
     * @return A List of only <code>KsaTransaction</code> objects from the given <code>Accepted</code> object.
     */
    private List<KsaTransaction> extractKsaTransactions(KsaBatchTransactionResponse.Accepted accepted) {

        // Create an empty List:
        List<KsaTransaction> result = new LinkedList<KsaTransaction>();

        if (accepted != null) {
            // Iterate through the List of KsaTransactions and TransactionDetails objects:
            for (Object object : accepted.getKsaTransactionAndTransactionDetails()) {
                // Pick only KsaTransaction objects:
                if (object instanceof KsaTransaction) {
                    result.add((KsaTransaction) object);
                }
            }
        }

        return result;
    }

    private ReportingPeriod createReportingPeriod(Date startDate, Date endDate) {
        ReportingPeriod reportingPeriod = ObjectFactory.getInstance().createReportingPeriod();
        reportingPeriod.setStartDate(CalendarUtils.toXmlGregorianCalendar(startDate, true));
        reportingPeriod.setEndDate(CalendarUtils.toXmlGregorianCalendar(endDate, true));
        return reportingPeriod;
    }
}

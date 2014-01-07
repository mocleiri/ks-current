package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.exception.InvalidGeneralLedgerAccountException;
import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.export.*;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.*;
import com.sigmasys.kuali.ksa.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.sigmasys.kuali.ksa.model.Constants.*;
import static com.sigmasys.kuali.ksa.util.CommonUtils.nvl;
import static com.sigmasys.kuali.ksa.util.TransactionUtils.*;

/**
 * Transaction Export Service implementation.
 *
 * @author Michael Ivanov
 */
@Service("transactionExportService")
@Transactional(timeout = 3600)
@WebService(serviceName = TransactionExportService.SERVICE_NAME, portName = TransactionExportService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
@SuppressWarnings("unchecked")
public class TransactionExportServiceImpl extends GenericPersistenceService implements TransactionExportService {

    private static final Log logger = LogFactory.getLog(TransactionExportServiceImpl.class);

    private static final String EXPORT_SCHEMA_LOCATION = "classpath*:/xsd/export/transaction-export.xsd";
    private static final String EXPORT_TYPES_LOCATION = "classpath*:/xsd/export/types.xsd";
    private static final String EXPORT_DD_TYPES_LOCATION = "classpath*:/xsd/export/ddTypes.xsd";
    private static final String XML_SCHEMA_LOCATION = "classpath*:/xsd/xml.xsd";

    @Autowired
    private GeneralLedgerService glService;

    @Autowired
    private ConfigService configService;

    private XmlSchemaValidator schemaValidator;


    @PostConstruct
    private void postConstruct() {
        schemaValidator = new XmlSchemaValidator(
                XML_SCHEMA_LOCATION,
                EXPORT_TYPES_LOCATION,
                EXPORT_DD_TYPES_LOCATION,
                EXPORT_SCHEMA_LOCATION);
    }

    /**
     * Creates all the GlTransmission objects and
     * returns the completed XML file that will be uploaded to KFS or any other external system.
     * It also creates GL Baseline objects to persist transaction summary information.
     *
     * @return XML content that contains the transactions to be exported
     */
    @Override
    @PermissionsAllowed(Permission.EXPORT_GL_TRANSACTIONS)
    public String exportTransactions() {
        List<GlTransmission> transmissions = glService.createGlTransmissions(null, null, true);
        return convertGlTransmissionsToXml(null, transmissions, true);
    }

    /**
     * Gets GlTransmission objects persisted in the database as XML for the given batch ID
     *
     * @param batchId GL transmission batch ID
     * @return XML content that contains the transactions to be exported
     */
    @Override
    @PermissionsAllowed(Permission.EXPORT_GL_TRANSACTIONS)
    public String exportTransactionsForBatch(String batchId) {
        return convertGlTransmissionsToXml(batchId, glService.getGlTransmissionsForBatch(batchId, GlTransmissionStatus.TRANSMITTED));
    }

    /**
     * Creates GlTransmission objects for the given effective/recognition dates with a result that is blank and
     * returns the completed XML file that will be uploaded to KFS or any other external system.
     *
     * @param startDate       Transaction effective or recognition start date
     * @param endDate         Transaction effective or recognition end date
     * @param isEffectiveDate if "true" this parameter indicates that transaction effective date should be used,
     *                        if "false" - transaction recognition date
     * @return XML content that contains the transactions to be exported
     */
    @Override
    @PermissionsAllowed(Permission.EXPORT_GL_TRANSACTIONS)
    public String exportTransactionsForDates(Date startDate, Date endDate, boolean isEffectiveDate) {
        return convertGlTransmissionsToXml(glService.createGlTransmissions(startDate, endDate, isEffectiveDate));
    }

    /**
     * Retrieves GL transmissions and exports them into XML for the given GL recognition periods.
     *
     * @param recognitionPeriods an array of GL recognition period codes
     * @return XML content that contains the transactions to be exported
     */
    @Override
    @PermissionsAllowed(Permission.EXPORT_GL_TRANSACTIONS)
    public String exportTransactionsForPeriods(String... recognitionPeriods) {
        return convertGlTransmissionsToXml(glService.createGlTransmissions(null, null, true, recognitionPeriods));
    }

    /**
     * Parses the GL account and returns the list of <code>java.util.String</code> values.
     * The format of GL account is organization-specific. The current implementation supports the UMD format.
     *
     * @param glAccount GL account number
     * @return list of String values
     */
    @Override
    public List<String> parseGlAccount(String glAccount) {

        if (!glService.isGlAccountValid(glAccount)) {
            String errMsg = "GL Account '" + glAccount + "' is invalid";
            logger.error(errMsg);
            throw new InvalidGeneralLedgerAccountException(errMsg);
        }

        glAccount = StringUtils.deleteAny(glAccount, " \n\t\r-");

        List<String> accountParts = new ArrayList<String>(3);

        accountParts.add(glAccount.substring(0, 2));
        accountParts.add(glAccount.substring(2, 9));
        accountParts.add(glAccount.substring(9));

        return accountParts;
    }

    private BigInteger generateBatchId() {
        return BigInteger.valueOf(CommonUtils.generateUuid());
    }

    protected String convertGlTransmissionsToXml(List<GlTransmission> glTransmissions) {
        return convertGlTransmissionsToXml(null, glTransmissions);
    }

    protected String convertGlTransmissionsToXml(String batchId, List<GlTransmission> glTransmissions) {
        return convertGlTransmissionsToXml(batchId, glTransmissions, false);
    }

    protected String convertGlTransmissionsToXml(String batchId, List<GlTransmission> glTransmissions,
                                                 boolean createGlBaselineAmounts) {

        if (batchId == null) {
            // Generating the batch ID
            batchId = String.valueOf(generateBatchId());
            // Setting the result and batch ID for each GL transmission from the list
            for (GlTransmission glTransmission : glTransmissions) {
                glTransmission.setBatchId(batchId);
                glTransmission.setStatus(GlTransmissionStatus.TRANSMITTED);
            }
        }

        // Creating GlBatchBaseline objects if "createGlBaselineAmounts" is true
        if (createGlBaselineAmounts && CollectionUtils.isNotEmpty(glTransmissions)) {
            glService.createGlBaselineAmounts(batchId);
        }

        final ObjectFactory objectFactory = new ObjectFactory();

        final String currentUserId = userSessionManager.getUserId();

        final Date currentDate = new Date();
        final String currentStringDate = new SimpleDateFormat(Constants.DATE_FORMAT_EXPORT).format(currentDate);
        final XMLGregorianCalendar currentXmlDate = CalendarUtils.toXmlGregorianCalendar(currentDate);

        // Creating BatchType instance
        final BatchType batchType = objectFactory.createBatchType();

        // Creating TrailerType instance
        final TrailerType trailerType = objectFactory.createTrailerType();

        final String balanceTypeCode = nvl(configService.getParameter(KFS_BALANCE_TYPE_CODE_PARAM_NAME));
        final String objectTypeCode = nvl(configService.getParameter(KFS_OBJECT_TYPE_CODE_PARAM_NAME));
        final String documentTypeCode = nvl(configService.getParameter(KFS_DOCUMENT_TYPE_CODE_PARAM_NAME));
        final String documentNumberPrefix = nvl(configService.getParameter(KFS_DOCUMENT_NUMBER_PREFIX_PARAM_NAME));
        final String originationCode = nvl(configService.getParameter(KFS_ORIGINATION_CODE_PARAM_NAME));
        final String glEntryDesc = nvl(configService.getParameter(KFS_TRANSACTION_GL_ENTRY_DESCRIPTION_PARAM_NAME));

        final List<GeneralLedgerType> glTypes = glService.getGeneralLedgerTypes();
        final Set<String> glTypeAccounts = new HashSet<String>(glTypes.size());
        for (GeneralLedgerType glType : glTypes) {
            glTypeAccounts.add(glType.getGlAccountId());
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        for (GlTransmission glTransmission : glTransmissions) {

            totalAmount = totalAmount.add(glTransmission.getAmount());

            // Creating GLEntryType instance
            GlEntryType glEntryType = objectFactory.createGlEntryType();

            final String recognitionPeriodCode = glTransmission.getRecognitionPeriod().getCode();

            final Integer fiscalYear = glTransmission.getRecognitionPeriod().getFiscalYear();

            String glTransmissionId = String.valueOf(glTransmission.getId());

            int glTransmissionIdLength = glTransmissionId.length();
            int documentNumberPrefixLength = documentNumberPrefix.length();

            while (glTransmissionIdLength + documentNumberPrefixLength > 14) {
                glTransmissionId = glTransmissionId.substring(1);
                glTransmissionIdLength = glTransmissionId.length();
            }

            final String documentNumber = documentNumberPrefix + glTransmissionId;

            final List<String> parsedGlAccount = parseGlAccount(glTransmission.getGlAccountId());

            glEntryType.setChartOfAccountsCode(parsedGlAccount.get(0));
            glEntryType.setAccountNumber(parsedGlAccount.get(1));
            glEntryType.setObjectCode(parsedGlAccount.get(2));

            glEntryType.setBalanceTypeCode(balanceTypeCode);
            glEntryType.setDebitOrCreditCode(glTransmission.getGlOperation().getId());
            glEntryType.setDocumentNumber(documentNumber);

            // TODO Mapping to what ?????
            //glEntryType.setDocumentReversalDate();

            glEntryType.setDocumentTypeCode(documentTypeCode);

            if (!StringUtils.isEmpty(objectTypeCode)) {
                glEntryType.setObjectTypeCode(objectTypeCode);
            }

            // TODO Mapping to what ?????
            //glEntryType.setEncumbranceUpdateCode();
            // TODO Mapping to what ?????
            //glEntryType.setOrganizationDocumentNumber();
            // TODO Mapping to what ?????
            //glEntryType.setOrganizationReferenceId();
            glEntryType.setOriginationCode(originationCode);
            // TODO Mapping to what ?????
            //glEntryType.setReferenceOriginationCode();
            // TODO Mapping to what ?????
            //glEntryType.setProjectCode();
            // TODO Mapping to what ?????
            //glEntryType.setReferenceDocumentNumber();
            // TODO Mapping to what ?????
            //glEntryType.setReferenceDocumentTypeCode();
            // TODO Mapping to what ?????
            //glEntryType.setSubAccountNumber();
            // TODO Mapping to what ?????
            //glEntryType.setSubObjectCode();
            glEntryType.setTransactionDate(currentStringDate);
            // TODO Mapping to what ?????
            //glEntryType.setTransactionEntrySequenceId();

            glEntryType.setTransactionLedgerEntryAmount(formatAmount(glTransmission.getAmount()));
            glEntryType.setTransactionLedgerEntryDescription(glEntryDesc);

            if (!StringUtils.isEmpty(recognitionPeriodCode)) {
                glEntryType.setUniversityFiscalAccountingPeriod(recognitionPeriodCode);
            }

            if (fiscalYear != null) {
                glEntryType.setUniversityFiscalYear(String.valueOf(fiscalYear));
            }

            // Adding GLEntryType and DetailType instances to "glEntryAndDetail" list of BatchType
            batchType.getGlEntryAndDetail().add(glEntryType);

            if (fiscalYear != null &&
                    !StringUtils.isEmpty(recognitionPeriodCode) &&
                    !glTypeAccounts.contains(glTransmission.getGlAccountId())) {

                // Creating DetailType instance
                DetailType detailType = objectFactory.createDetailType();

                detailType.setChartOfAccountsCode(parsedGlAccount.get(0));
                detailType.setAccountNumber(parsedGlAccount.get(1));
                detailType.setObjectCode(parsedGlAccount.get(2));

                detailType.setAmount(glTransmission.getAmount());
                detailType.setBalanceTypeCode(balanceTypeCode);
                // TODO Mapping to what ?????
                //detailType.setCollectorDetailSequenceNumber();
                detailType.setCreateDate(currentXmlDate);
                // TODO Mapping to what ?????
                //detailType.setDetailText();

                if (!StringUtils.isEmpty(objectTypeCode)) {
                    detailType.setObjectTypeCode(objectTypeCode);
                }

                detailType.setDocumentTypeCode(documentTypeCode);
                detailType.setDocumentNumber(documentNumber);

                // TODO Mapping to what ?????
                //detailType.setSubObjectCode();
                // TODO Mapping to what ?????
                //detailType.setSubAccountNumber();
                detailType.setOriginationCode(originationCode);

                detailType.setUniversityFiscalAccountingPeriod(recognitionPeriodCode);
                detailType.setUniversityFiscalYear(String.valueOf(fiscalYear));

                // TODO: figure out if we need DetailType here
                //batchType.getGlEntryAndDetail().add(detailType);
            }

        }

        // Creating HeaderType instance
        HeaderType headerType = objectFactory.createHeaderType();
        headerType.setBatchSequenceNumber(new BigInteger(batchId));
        headerType.setCampusCode(nvl(configService.getParameter(KFS_CAMPUS_CODE_PARAM_NAME)));
        headerType.setChartOfAccountsCode(nvl(configService.getParameter(KFS_CHART_OF_ACCOUNTS_CODE_PARAM_NAME)));
        headerType.setDepartmentName(nvl(configService.getParameter(KFS_DEPARTMENT_NAME_PARAM_NAME)));
        headerType.setEmailAddress(nvl(configService.getParameter(KFS_EMAIL_ADDRESS_PARAM_NAME)));
        headerType.setMailingAddress(nvl(configService.getParameter(KFS_POSTAL_ADDRESS_PARAM_NAME)));
        headerType.setOrganizationCode(nvl(configService.getParameter(KFS_ORGANIZATION_CODE_PARAM_NAME)));
        headerType.setPersonUserId(currentUserId);
        headerType.setPhoneNumber(nvl(configService.getParameter(KFS_PHONE_NUMBER_PARAM_NAME)));
        headerType.setTransmissionDate(CalendarUtils.toXmlGregorianCalendar(currentDate, true));

        batchType.setHeader(headerType);

        trailerType.setTotalAmount(getFormattedAmount(totalAmount));
        trailerType.setTotalRecords(BigInteger.valueOf(glTransmissions.size()));

        batchType.setTrailer(trailerType);

        // Converting BatchType to XML
        String xml = JaxbUtils.toXml(batchType);

        // Validate XML against the schema
        if (schemaValidator.validateXml(xml)) {
            return xml;
        }

        String errMsg = "XML content is invalid:\n" + xml;
        logger.error(errMsg);
        throw new RuntimeException(errMsg);
    }


}

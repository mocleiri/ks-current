package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.util.ErrorUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.service.*;
import com.sigmasys.kuali.ksa.jaxb.*;

import com.sigmasys.kuali.ksa.util.CalendarUtils;
import com.sigmasys.kuali.ksa.util.JaxbUtils;
import com.sigmasys.kuali.ksa.util.XmlSchemaValidator;


import javax.annotation.PostConstruct;
import javax.jws.WebService;
import javax.persistence.Query;
import javax.xml.bind.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * TransactionImportService implementation
 *
 * @author Michael Ivanov
 */
@Service("transactionImportService")
@WebService(serviceName = TransactionImportService.SERVICE_NAME, portName = TransactionImportService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
@SuppressWarnings("unchecked")
@Transactional(timeout = 3600)
public class TransactionImportServiceImpl extends GenericPersistenceService implements TransactionImportService {

    private static final Log logger = LogFactory.getLog(TransactionImportServiceImpl.class);

    private static final String IMPORT_SCHEMA_LOCATION = "classpath*:/xsd/transaction-import.xsd";
    private static final String XML_SCHEMA_LOCATION = "classpath*:/xsd/xml.xsd";


    @Autowired
    private AccountService accountService;

    @Autowired
    private AuditableEntityService auditableEntityService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private GeneralLedgerService glService;

    @Autowired
    private RefundService refundService;


    private XmlSchemaValidator schemaValidator;


    @PostConstruct
    private void postConstruct() {
        schemaValidator = new XmlSchemaValidator(XML_SCHEMA_LOCATION, IMPORT_SCHEMA_LOCATION);
    }

    /**
     * Returns BatchReceipt by the given ID
     *
     * @param id batchReceipt ID
     * @return BatchReceipt instance
     */
    protected BatchReceipt getBatchReceipt(Long id) {
        Query query = em.createQuery("select br from BatchReceipt br " +
                " left outer join fetch br.account a " +
                " where br.id = :id ");
        query.setParameter("id", id);
        List<BatchReceipt> batchReceipts = query.getResultList();
        return (batchReceipts != null && !batchReceipts.isEmpty()) ? batchReceipts.get(0) : null;
    }

    /**
     * This is the service method exposed to up load and process transactions
     *
     * @param xml XML content
     * @return XML response
     */
    @Override
    @PermissionsAllowed(Permission.IMPORT_TRANSACTIONS)
    public String importTransactions(String xml) {

        // Validate XML against the schema
        String errorMessage = schemaValidator.validateXmlAndGetErrorMessage(xml);

        if (errorMessage != null) {
            errorMessage = "XML validation error: " + errorMessage;
            logger.error(errorMessage + ":\n" + xml);
            throw new RuntimeException(errorMessage);
        }

        return parseTransactions(xml);
    }

    /**
     * Convert an XML input transaction representation to a Transaction and persist
     * the transaction. Separate filed reject KsaTransactions from valid acceptable
     * KsaTransactions for further processing
     *
     * @param xml String
     * @return XML content as String
     */
    private String parseTransactions(String xml) {

        BatchReceiptStatus batchReceiptStatus = BatchReceiptStatus.IN_PROCESS;
        String batchIdentifier = null;

        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal acceptedValue = BigDecimal.ZERO;
        BigDecimal failedValue = BigDecimal.ZERO;
        BigDecimal totalValueCredit = BigDecimal.ZERO;
        BigDecimal totalValueDebits = BigDecimal.ZERO;
        int numberOfAccepted = 0;
        int numberOfFailed = 0;
        XMLGregorianCalendar creationDate;

        ObjectFactory objectFactory = ObjectFactory.getInstance();

        KsaBatchTransactionResponse.Accepted accepted = objectFactory.createKsaBatchTransactionResponseAccepted();
        KsaBatchTransactionResponse.Failed failed = objectFactory.createKsaBatchTransactionResponseFailed();
        KsaBatchTransactionResponse.BatchSummary batchSummary = objectFactory.createKsaBatchTransactionResponseBatchSummary();
        KsaBatchTransactionResponse ksaBatchTransactionResponse = objectFactory.createKsaBatchTransactionResponse();

        List<KsaTransaction> acceptedKsaTransactionList = new LinkedList<KsaTransaction>();

        ksaBatchTransactionResponse.setBatchSummary(batchSummary);
        ksaBatchTransactionResponse.setAccepted(accepted);
        ksaBatchTransactionResponse.setFailed(failed);

        String failureValue = configService.getParameter(Constants.IMPORT_SINGLE_BATCH_FAILURE);
        Boolean singleBatchFailure = Boolean.valueOf(failureValue);

        // determine if batch or single userTransaction
        Object object = parseXml(new StringReader(xml));

        KsaBatchTransaction ksaBatchTransaction;
        if (object instanceof KsaTransaction) {
            ksaBatchTransaction = new KsaBatchTransaction();
            ksaBatchTransaction.getKsaTransaction().add((KsaTransaction) object);
        } else {
            ksaBatchTransaction = (KsaBatchTransaction) object;
        }


        boolean failureNoted = false;

        boolean batchIsQualified = false;

        final String currentUserId = userSessionManager.getUserId();
        final Account currentAccount = accountService.getFullAccount(currentUserId);

        List<KsaTransaction> ksaTransactions = ksaBatchTransaction.getKsaTransaction();

        if (!CollectionUtils.isEmpty(ksaTransactions)) {

            // Get the external batch ID and generate one if it is null
            batchIdentifier = ksaBatchTransaction.getBatchIdentifier();
            if (batchIdentifier == null) {
                batchIdentifier = UUID.randomUUID().toString();
            } else {
                // If the batch ID already exists add a failure reason and set the batch ID to null
                if (batchIdExists(batchIdentifier)) {
                    failed.getKsaTransactionAndReason().add(null);
                    failed.getKsaTransactionAndReason().add("Batch ID = '" + batchIdentifier + "' already exists");
                    batchIdentifier = null;
                    batchReceiptStatus = BatchReceiptStatus.FAILED;
                }
            }

            ksaBatchTransactionResponse.setResponseToBatchIdentifier(batchIdentifier);
            ksaBatchTransactionResponse.setResponseIdentifier(UUID.randomUUID().toString());

            for (KsaTransaction ksaTransaction : ksaTransactions) {

                // pre determine, qualify the userTransaction create accepted and rejected lists of transactions
                Date effectiveDate = CalendarUtils.toDate(ksaTransaction.getEffectiveDate());
                String errMsg = "";
                if (!verifyRequiredValues(ksaTransaction)) {
                    errMsg = "Required values are missing from userTransaction, External ID = " +
                            ksaTransaction.getIncomingIdentifier();
                } else if (!accountService.accountExists(ksaTransaction.getAccountIdentifier())) {
                    errMsg = "Account '" + ksaTransaction.getAccountIdentifier() + "' does not exist";
                } else if (!transactionService.isTransactionAllowed(currentUserId,
                        ksaTransaction.getTransactionType(), effectiveDate)) {
                    errMsg = "Transaction is not allowed. Transaction Type = '" +
                            ksaTransaction.getTransactionType() + "', Effective Date = '" + effectiveDate +
                            "', Account ID = '" + currentUserId + "'";
                }

                if (!isTransactionTypeValid(ksaTransaction.getTransactionType(), effectiveDate)) {
                    errMsg = "No such Transaction Type = '" + ksaTransaction.getTransactionType() +
                            "' with Effective Date = '" + effectiveDate + "'";
                }

                KsaTransaction.Override override = ksaTransaction.getOverride();

                if (override != null) {

                    if (!refundService.isRefundRuleValid(override.getOverrideRefundRule())) {
                        // applies to Payments
                        errMsg = "Invalid refund rule '" + override.getOverrideRefundRule() + "'";
                    }

                }

                if (errMsg.length() > 0) {
                    // place the userTransaction in the failed list
                    failed.getKsaTransactionAndReason().add(ksaTransaction);
                    failed.getKsaTransactionAndReason().add(errMsg);
                    numberOfFailed++;
                    failedValue = failedValue.add(ksaTransaction.getAmount());
                    totalValue = totalValue.add(ksaTransaction.getAmount());
                } else {
                    // place the userTransaction in the accepted list
                    acceptedKsaTransactionList.add(ksaTransaction);
                }

            }

            batchIsQualified = CollectionUtils.isEmpty(failed.getKsaTransactionAndReason());

            if (batchReceiptStatus != BatchReceiptStatus.FAILED && (batchIsQualified || !singleBatchFailure)) {

                for (KsaTransaction ksaTransaction : acceptedKsaTransactionList) {

                    try {

                        Transaction transaction = persistTransaction(ksaTransaction);

                        totalValue = totalValue.add(ksaTransaction.getAmount());

                        // refund applies to credit type only
                        if (transaction.getTransactionTypeValue() == TransactionTypeValue.PAYMENT ||
                                transaction.getTransactionTypeValue() == TransactionTypeValue.DEFERMENT) {
                            totalValueCredit = totalValueCredit.add(transaction.getAmount());
                        } else {
                            totalValueDebits = totalValueDebits.add(transaction.getAmount());
                        }

                        creationDate = CalendarUtils.toXmlGregorianCalendar(transaction.getCreationDate());

                        // add the KsaTransaction object to the accepted KsaTransactionAndTransactionDetails
                        accepted.getKsaTransactionAndTransactionDetails().add(ksaTransaction);

                        numberOfAccepted++;
                        acceptedValue = acceptedValue.add(transaction.getAmount());

                        // add the ID and Creation Date to the TransactionDetails and then to the KsaTransactionAndTransactionDetails
                        KsaBatchTransactionResponse.Accepted.TransactionDetails transactionDetails =
                                new KsaBatchTransactionResponse.Accepted.TransactionDetails();

                        transactionDetails.setTransactionId(transaction.getId().toString());
                        transactionDetails.setAcceptedDate(creationDate);

                        accepted.getKsaTransactionAndTransactionDetails().add(transactionDetails);

                    } catch (Exception e) {

                        logger.error(e.getMessage(), e);

                        failureNoted = true;

                        // add the KsaTransactionAndReason object to the failed KsaTransactionAndReason
                        failed.getKsaTransactionAndReason().add(ksaTransaction);

                        numberOfFailed++;
                        failedValue = failedValue.add(ksaTransaction.getAmount());

                        // add the reason to the KsaTransactionAndReason
                        failed.getKsaTransactionAndReason().add(ErrorUtils.getMessage(e).replaceAll("\\[|\\]", ""));
                    }
                }

            } else {
                for (KsaTransaction acceptedTrans : acceptedKsaTransactionList) {
                    accepted.getKsaTransactionAndTransactionDetails().add(acceptedTrans);
                }
            }
        }

        if (batchReceiptStatus != BatchReceiptStatus.FAILED) {
            if (batchIsQualified && !failureNoted) {
                batchReceiptStatus = BatchReceiptStatus.ACCEPTED;
            } else if (!batchIsQualified && !failureNoted) {
                batchReceiptStatus = BatchReceiptStatus.PARTIALLY_ACCEPTED;
            } else {
                batchReceiptStatus = BatchReceiptStatus.FAILED;
            }
        }

        ksaBatchTransactionResponse.setBatchStatus(batchReceiptStatus.toString());

        // fill in the response

        int batchSize = ksaTransactions.size();

        batchSummary.setTransactionsInBatch(batchSize);
        batchSummary.setValueOfBatch(totalValue);
        batchSummary.setTransactionsAccepted(numberOfAccepted);
        batchSummary.setTransactionsFailed(numberOfFailed);
        batchSummary.setValueAccepted(acceptedValue);
        batchSummary.setValueFailed(failedValue);

        // log the batch summary statics and values

        String responseXml = JaxbUtils.toXml(ksaBatchTransactionResponse);

        // Create a new BatchReceipt and persist
        BatchReceipt batchReceipt = new BatchReceipt();
        batchReceipt.setAccount(currentAccount);
        batchReceipt.setBatchDate(new Date());
        batchReceipt.setCreditOfAcceptedTransactions(totalValueCredit);
        batchReceipt.setDebitOfAcceptedTransactions(totalValueDebits);
        batchReceipt.setExternalId(batchIdentifier);
        batchReceipt.setNumberOfAcceptedTransactions(numberOfAccepted);
        batchReceipt.setNumberOfRejectedTransactions(numberOfFailed);
        batchReceipt.setNumberOfTransactions(batchSize);
        batchReceipt.setReceiptDate(new Date());
        batchReceipt.setStatus(batchReceiptStatus);
        batchReceipt.setTotalVolume(totalValue);
        batchReceipt.setVolumeOfRejectedTransactions(failedValue);

        batchReceipt.setIncomingXml(createXmlDocument(xml));

        batchReceipt.setOutgoingXml(createXmlDocument(responseXml));

        persistEntity(batchReceipt);

        return responseXml;
    }

    private XmlDocument createXmlDocument(String xml) {
        XmlDocument xmlDocument = new XmlDocument();
        xmlDocument.setXml(xml);
        xmlDocument.setCreationDate(new Date());
        xmlDocument.setCreatorId(userSessionManager.getUserId());
        persistEntity(xmlDocument);
        return xmlDocument;
    }

    /**
     * Attempt to unmarshal a batch input XML file
     * return true if a batch XML input
     *
     * @param reader StringReader
     */
    private Object parseXml(StringReader reader) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(KsaTransaction.class, KsaBatchTransaction.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return unmarshaller.unmarshal(reader);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

    }


    /**
     * Validate inputs are provided.
     *
     * @param ksaTransaction KsaTransaction instance
     * @return true/false
     */
    private boolean verifyRequiredValues(KsaTransaction ksaTransaction) {
        return (ksaTransaction.getAccountIdentifier() != null &&
                ksaTransaction.getIncomingIdentifier() != null &&
                ksaTransaction.getEffectiveDate() != null &&
                ksaTransaction.getAmount() != null &&
                ksaTransaction.getCurrency() != null &&
                ksaTransaction.getTransactionType() != null);
    }

    /**
     * Checks if the given batch ID already exists in the database
     *
     * @param batchId Batch ID
     * @return true if the given batch ID already exists, false - otherwise
     */
    @Transactional(readOnly = true)
    public boolean batchIdExists(String batchId) {
        Query query = em.createQuery("select 1 from BatchReceipt where externalId = :batchId and statusCode <> :statusCode");
        query.setParameter("batchId", batchId);
        query.setParameter("statusCode", BatchReceiptStatus.FAILED_CODE);
        query.setMaxResults(1);
        return CollectionUtils.isNotEmpty(query.getResultList());
    }

    /**
     * Check if the given TransactionType ID is defined
     *
     * @param transactionType Transaction Type
     * @param effectiveDate   Effective Date
     * @return boolean value
     */
    private boolean isTransactionTypeValid(String transactionType, Date effectiveDate) {
        return transactionService.getTransactionType(transactionType, effectiveDate) != null;
    }


    /**
     * Persists the Transaction instance created from the incoming KsaTransaction
     *
     * @param ksaTransaction KsaTransaction instance
     * @return Transaction instance
     */
    private Transaction persistTransaction(KsaTransaction ksaTransaction) {

        Date effectiveDate = CalendarUtils.toDate(ksaTransaction.getEffectiveDate(), true);

        Transaction transaction = transactionService.createTransaction(
                ksaTransaction.getTransactionType(),
                ksaTransaction.getIncomingIdentifier(),
                ksaTransaction.getAccountIdentifier(),
                effectiveDate,
                CalendarUtils.toDate(ksaTransaction.getRecognitionDate(), true),
                CalendarUtils.toDate(ksaTransaction.getExpirationDate(), true),
                ksaTransaction.getAmount(),
                false);

        transaction.setNativeAmount(ksaTransaction.getNativeAmount());
        transaction.setOriginationDate(CalendarUtils.toDate(ksaTransaction.getOriginationDate(), true));
        transaction.setCurrency(auditableEntityService.getCurrency(ksaTransaction.getCurrency()));

        KsaTransaction.Override override = ksaTransaction.getOverride();

        // Setting general ledger type
        String glTypeCode = (override != null && override.getGeneralLedgerType() != null) ?
                override.getGeneralLedgerType() :
                configService.getParameter(Constants.DEFAULT_GL_TYPE);


        if (glTypeCode != null) {
            transaction.setGeneralLedgerType(glService.getGeneralLedgerType(glTypeCode));
        }

        if (transaction instanceof Payment) {

            TransactionType transactionType =
                    transactionService.getTransactionType(ksaTransaction.getTransactionType(), effectiveDate);

            if (transactionType == null) {
                String errMsg = "Cannot find Transaction Type for ID = " + ksaTransaction.getTransactionType() +
                        " and Effective Date = " + effectiveDate;
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            CreditType creditType = (CreditType) transactionType;
            Payment payment = (Payment) transaction;

            // Setting payment clear date
            Integer clearPeriod = null;
            if (override != null) {
                if (override.getOverrideClearPeriod() != null) {
                    clearPeriod = override.getOverrideClearPeriod();
                }
                payment.setRefundable(override.isRefundable());
                String refundRule = override.getOverrideRefundRule();
                if (StringUtils.isBlank(refundRule)) {
                    refundRule = override.getOverrideRefundRule();
                }
                payment.setRefundRule(refundRule);
            }

            if (clearPeriod == null) {
                clearPeriod = (creditType.getClearPeriod() != null) ? creditType.getClearPeriod() : 0;
            }

            payment.setClearDate(CalendarUtils.addCalendarDays(effectiveDate, clearPeriod));

        }

        final Long transactionId = transactionService.persistTransaction(transaction);

        if (override != null) {
            KsaTransaction.Override.GeneralLedgerOverride glOverride = override.getGeneralLedgerOverride();
            if (glOverride != null) {
                List<Serializable> breakdowns = glOverride.getGeneralLedgerAccountAndPercentageAllocation();
                if (CollectionUtils.isNotEmpty(breakdowns)) {
                    List<String> glAccounts = new LinkedList<String>();
                    List<BigDecimal> glBreakdowns = new LinkedList<BigDecimal>();
                    for (Serializable breakdown : breakdowns) {
                        if (breakdown instanceof BigDecimal) {
                            glBreakdowns.add((BigDecimal) breakdown);
                        } else {
                            glAccounts.add((String) breakdown);
                        }
                    }
                    List<GlBreakdownOverride> breakdownOverrides = new ArrayList<GlBreakdownOverride>(glAccounts.size());
                    for (int i = 0; i < glAccounts.size(); i++) {
                        GlBreakdownOverride breakdownOverride = new GlBreakdownOverride();
                        breakdownOverride.setGlAccount(glAccounts.get(i));
                        breakdownOverride.setBreakdown(glBreakdowns.get(i));
                        breakdownOverrides.add(breakdownOverride);
                    }
                    glService.createGlBreakdownOverrides(transactionId, breakdownOverrides);
                }
            }
        }

        return transaction;
    }
}

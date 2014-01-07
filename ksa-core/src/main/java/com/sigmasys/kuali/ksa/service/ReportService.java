package com.sigmasys.kuali.ksa.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.sigmasys.kuali.ksa.annotation.Url;
import com.sigmasys.kuali.ksa.jaxb.*;
import com.sigmasys.kuali.ksa.model.Constants;

/**
 * This interface represents a collection of useful KSA reporting methods to
 * produce reports as described in the "Process Diagrams" document.
 * Reports being produces are targeted to show the current state of the accounts
 * and transactions as well as payments for the purposes of IRS reporting.
 * <p/>
 *
 * @author Michael Ivanov
 * @author Sergey Godunov
 */
@Url(ReportService.SERVICE_URL)
@WebService(serviceName = ReportService.SERVICE_NAME, portName = ReportService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
public interface ReportService {

    String SERVICE_URL = "report.webservice";
    String SERVICE_NAME = "ReportService";
    String PORT_NAME = SERVICE_NAME + "Port";

    /**
     * Generates a reconciliation report in XML for KSA transactions to the general ledger.
     *
     * @param glAccountId     GL Account ID
     * @param startDate       Start date
     * @param endDate         End date
     * @param transmittedOnly indicates whether only transmitted GL transactions should be retrieved or not
     * @return The generated report in XML
     */
    String generateGeneralLedgerReport(String glAccountId, Date startDate, Date endDate, boolean transmittedOnly);

    /**
     * Generates a reconciliation report in XML for KSA transactions to the general ledger.
     *
     * @param glAccountId GL Account ID
     * @param startDate   Start date
     * @param endDate     End date
     * @return The generated report in XML
     */
    @WebMethod(exclude = true)
    String generateGeneralLedgerReport(String glAccountId, Date startDate, Date endDate);

    /**
     * An overloaded version of the <code>generate1098TReport</code> method that produces a partial year federal 1098T form.
     *
     * @param accountId               ID of an account for which to produce the report.
     * @param startDate               Payments and Refunds tracking start date.
     * @param endDate                 Payments and Refunds tracking end date date
     * @param numberOfDisplayedDigits The number of final digits of the social security number that will be stored in the local record.
     * @param isTransient             Whether to keep the record of the produces 1098T
     * @return String representation of an IRS 1098T form.
     * @throws IllegalArgumentException If <code>dateFrom</code> or <code>dateTo</code> are invalid or do not fall within the <code>year</code>.
     * @see Irs1098T
     */
    String generate1098TReport(String accountId, Date startDate, Date endDate, int numberOfDisplayedDigits, boolean isTransient);

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
    String generate1098TReportByYear(String accountId, int year, int numberOfDisplayedDigits, boolean isTransient);

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
    @WebMethod(exclude = true)
    String generate1098TReportByYear(String accountId, int year, boolean isTransient);


    /**
     * Looks for an existing IRS 1098T report in the KSA database and returns the result in XML format.
     *
     * @param accountId ID of an account for which to produce the report.
     * @param startDate Payments and Refunds tracking start date.
     * @param endDate   Payments and Refunds tracking end date date
     * @return XML representation of an IRS 1098T form.
     */
    String getIrs1098TReport(String accountId, Date startDate, Date endDate);

    /**
     * Looks for an existing IRS 1098T report in the KSA database and returns the result in XML format.
     *
     * @param accountId ID of an account for which to produce the report.
     * @param year      Tax year
     * @return XML representation of an IRS 1098T form.
     */
    String getIrs1098TReportByYear(String accountId, int year);

    /**
     * Returns a list of previously generated and saved IRS Forms 1098T for a particular account.
     *
     * @param accountId ID of an Account for which to return its saved IRS Forms 1098T.
     * @return A <code>List</code> of previously generated and saved IRS Forms 1098T.
     */
    List<com.sigmasys.kuali.ksa.model.Irs1098T> getIrs1098TReportsForAccount(String accountId);

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
    String generateAgedBalanceReport(List<String> accountIds, boolean ignoreDeferments, boolean ageAccount);

    /**
     * An overloaded method to produce a failed transaction report for all batches.
     *
     * @param startDate  Start date of transaction period tracking.
     * @param endDate    End date of transaction period tracking.
     * @param failedOnly Include only transactions that failed on their own.
     * @return String representation of an XML report.
     * @see FailedTransactionsReport
     */
    @WebMethod(exclude = true)
    String generateRejectedTransactionsReport(Date startDate, Date endDate, boolean failedOnly);

    /**
     * Checks the batch records for transactions that have been sent and rejected. The method can filter by
     * date range, and by entity. If no entity is passed, all batches in the date range will be included. If
     * failedOnly is set to true, only the transactions that failed on their own will be reported (i.e. if a whole
     * batch was rejected due to a failure, the transactions that would have posted ok will not be reported.) If
     * failedOnly is true, all transactions that were not accepted, even those which were inherently "valid" will
     * be reported.
     *
     * @param entityId   External ID.
     * @param startDate  Start date of transaction period tracking.
     * @param endDate    End date of transaction period tracking.
     * @param failedOnly Include only transactions that failed on their own.
     * @return String representation of an XML report.
     * @see FailedTransactionsReport
     */
    String generateRejectedTransactionsReport(String entityId, Date startDate, Date endDate, boolean failedOnly);

    /**
     * Return an XML account report for the account id, between the dates listed.
     * If <code>details</code> is <code>true</code>, then all transactions will also be reported.
     * If <code>paymentApplication</code> is <code>true</code> then <code>paymentApplication</code> method will be run before the account is reported.
     * If <code>ageAccount</code> is <code>true</code>, the account will be aged before the report is generated.
     *
     * @param accountId          ID of an account for which to produce a report.
     * @param startDate          Start date of reporting period.
     * @param endDate            End date of reporting period.
     * @param details            Whether to report all transactions.
     * @param paymentApplication Whether to run "paymentApplication" before executing the report.
     * @param ageAccount         Whether to age the account before generating a report.
     * @param ignoreDeferments   Whether to ignore deferments.
     * @return String representation of an account report.
     * @see AccountReport
     */
    String generateAccountReport(String accountId, Date startDate, Date endDate, boolean details,
                                 boolean paymentApplication, boolean ageAccount, boolean ignoreDeferments);

    /**
     * Produces a receipt for a transaction with the specified ID.
     *
     * @param transactionId ID of a transaction for which to produce a receipt.
     * @return String representation of a transaction receipt.
     * @see com.sigmasys.kuali.ksa.jaxb.TransactionReceipt
     */
    String generateTransactionReceipt(Long transactionId);

    /**
     * Converts an IRS 1098T JPA entity to XML
     *
     * @param irs1098TId Irs1098T JPA entity ID
     * @return XML representation of the form.
     */
    String convertIrs1098TToXml(Long irs1098TId);

    /**
     * Generates IRS 8300 report based on the CashLimitEvent object specified by ID.
     *
     * @param cashLimitEventId CashLimitEvent ID
     * @return String representation of Irs8300 XML report.
     */
    String generateIrs8300Report(Long cashLimitEventId);

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
    String generateBill(String accountId,
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
                        boolean runPaymentApplication);

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
    String generateBills(List<String> accountIds,
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
                         boolean runPaymentApplication);

}

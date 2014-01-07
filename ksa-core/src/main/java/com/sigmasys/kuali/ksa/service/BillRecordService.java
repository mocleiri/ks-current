package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Bill Record Service
 * <p/>
 *
 * @author Michael Ivanov
 */
public interface BillRecordService {

    /**
     * Creates and persist a new BillRecord instance based on the given parameters.
     *
     * @param accountId                    Account ID
     * @param message                      Bill message
     * @param billDate                     Bill date
     * @param startDate                    Start date
     * @param endDate                      End date
     * @param showOnlyUnbilledTransactions true if only unbilled transactions have to be shown
     * @param showInternalTransactions     true if internal transactions have to be shown
     * @param showDeferments               true if deferments have to be shown
     * @param showDependents               true if dependents have to be shown
     * @param transactionIds               Set of Transaction IDs associated with this BillRecord
     * @return BillRecord instance
     */
    BillRecord createBillRecord(String accountId,
                                String message,
                                Date billDate,
                                Date startDate,
                                Date endDate,
                                boolean showOnlyUnbilledTransactions,
                                boolean showInternalTransactions,
                                boolean showDeferments,
                                boolean showDependents,
                                Set<Long> transactionIds);

    /**
     * Returns BillRecord by ID
     *
     * @param id BillRecord ID
     * @return BillRecord instance
     */
    BillRecord getBillRecord(Long id);

    /**
     * Returns all BillRecord entities for the given Account ID sorted by ID in the descendant order
     *
     * @param accountId Account ID
     * @return List of Information instances
     */
    List<BillRecord> getBillRecords(String accountId);

    /**
     * Return the latest by endDate BillRecord objects for the account or null if it does not exist.
     *
     * @param accountId Account ID
     * @return BillRecord instance
     */
    BillRecord getLatestBillRecord(String accountId);

    /**
     * Persists BillRecord instance in the database.
     *
     * @param billRecord BillRecord instance
     * @return BillRecord ID
     */
    Long persistBillRecord(BillRecord billRecord);

    /**
     * Removes BillRecord from the database.
     *
     * @param id BillRecord ID
     * @return true if BillRecord entity has been deleted
     */
    boolean deleteBillRecord(Long id);

}

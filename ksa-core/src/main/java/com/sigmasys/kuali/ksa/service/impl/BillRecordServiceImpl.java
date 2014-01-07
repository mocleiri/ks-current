package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.annotation.PermissionsAllowed;
import com.sigmasys.kuali.ksa.exception.UserNotFoundException;
import com.sigmasys.kuali.ksa.model.Account;
import com.sigmasys.kuali.ksa.model.BillRecord;
import com.sigmasys.kuali.ksa.model.Transaction;
import com.sigmasys.kuali.ksa.model.security.Permission;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.service.BillRecordService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.*;


/**
 * BillRecordService implementation.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Service("billRecordService")
@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class BillRecordServiceImpl extends GenericPersistenceService implements BillRecordService {


    private static final Log logger = LogFactory.getLog(BillRecordServiceImpl.class);


    private static final String GET_BILL_RECORD_SELECT = "select b from BillRecord b " +
            "left outer join fetch b.account a " +
            "left outer join fetch b.transactions t ";


    @Autowired
    private AccountService accountService;


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
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.CREATE_BILL_RECORD)
    public BillRecord createBillRecord(String accountId,
                                       String message,
                                       Date billDate,
                                       Date startDate,
                                       Date endDate,
                                       boolean showOnlyUnbilledTransactions,
                                       boolean showInternalTransactions,
                                       boolean showDeferments,
                                       boolean showDependents,
                                       Set<Long> transactionIds) {

        Account account = accountService.getFullAccount(accountId);
        if (account == null) {
            String errMsg = "Account with ID = " + accountId + " does not exist";
            logger.error(errMsg);
            throw new UserNotFoundException(errMsg);
        }

        if (StringUtils.isBlank(message)) {
            String errMsg = "Bill message cannot be empty";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        if (billDate == null) {
            String errMsg = "Bill date cannot be null";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
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

        if (startDate.after(endDate)) {
            String errMsg = "Start date cannot be greater than End date";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }

        BillRecord billRecord = new BillRecord();

        billRecord.setAccount(account);
        billRecord.setMessage(message);
        billRecord.setBillDate(billDate);
        billRecord.setStartDate(startDate);
        billRecord.setEndDate(endDate);
        billRecord.setShowOnlyUnbilledTransactions(showOnlyUnbilledTransactions);
        billRecord.setShowInternalTransactions(showInternalTransactions);
        billRecord.setShowDeferments(showDeferments);
        billRecord.setShowDependents(showDependents);

        if (CollectionUtils.isNotEmpty(transactionIds)) {

            Query query = em.createQuery("select t from Transaction t where t.id in (:transactionIds)");

            query.setParameter("transactionIds", new ArrayList<Long>(transactionIds));


            List<Transaction> transactions = query.getResultList();

            if (transactionIds.size() != transactions.size()) {
                String errMsg = "Some Transaction IDs do not exist from [" + transactionIds + "]";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            billRecord.setTransactions(new HashSet<Transaction>(transactions));
        }


        billRecord.setCreationDate(new Date());
        billRecord.setCreatorId(userSessionManager.getUserId());

        persistEntity(billRecord);

        return billRecord;
    }

    /**
     * Returns BillRecord by ID
     *
     * @param id BillRecord ID
     * @return Information instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_BILL)
    public BillRecord getBillRecord(Long id) {

        Query query = em.createQuery(GET_BILL_RECORD_SELECT + " where b.id = :id");

        query.setParameter("id", id);

        List<BillRecord> billRecords = query.getResultList();

        return CollectionUtils.isNotEmpty(billRecords) ? billRecords.get(0) : null;
    }

    /**
     * Returns all BillRecord entities for the given Account ID sorted by ID in the descendant order
     *
     * @param accountId Account ID
     * @return List of Information instances
     */
    @Override
    @PermissionsAllowed(Permission.READ_BILL)
    public List<BillRecord> getBillRecords(String accountId) {

        Query query = em.createQuery(GET_BILL_RECORD_SELECT + " where a.id = :accountId order by b.id desc");

        query.setParameter("accountId", accountId);

        return query.getResultList();
    }

    /**
     * Return the latest by endDate BillRecord objects for the account or null if it does not exist.
     *
     * @param accountId Account ID
     * @return BillRecord instance
     */
    @Override
    @PermissionsAllowed(Permission.READ_BILL)
    public BillRecord getLatestBillRecord(String accountId) {

        Query query = em.createQuery(GET_BILL_RECORD_SELECT + " where a.id = :accountId order by b.endDate desc");

        query.setParameter("accountId", accountId);

        query.setMaxResults(1);

        List<BillRecord> billRecords = query.getResultList();

        return CollectionUtils.isNotEmpty(billRecords) ? billRecords.get(0) : null;
    }

    /**
     * Persists BillRecord instance in the database.
     *
     * @param billRecord BillRecord instance
     * @return BillRecord ID
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.UPDATE_BILL)
    public Long persistBillRecord(BillRecord billRecord) {
        return persistEntity(billRecord);
    }

    /**
     * Removes BillRecord from the database.
     *
     * @param id BillRecord ID
     * @return true if BillRecord entity has been deleted
     */
    @Override
    @Transactional(readOnly = false)
    @PermissionsAllowed(Permission.DELETE_BILL)
    public boolean deleteBillRecord(Long id) {
        return deleteEntity(id, BillRecord.class);
    }


}

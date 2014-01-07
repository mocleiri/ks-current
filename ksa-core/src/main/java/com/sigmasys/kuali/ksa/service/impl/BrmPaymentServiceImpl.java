package com.sigmasys.kuali.ksa.service.impl;

import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.BrmPaymentService;
import com.sigmasys.kuali.ksa.service.GeneralLedgerService;
import com.sigmasys.kuali.ksa.service.PaymentService;
import com.sigmasys.kuali.ksa.service.TransactionService;
import com.sigmasys.kuali.ksa.service.brm.BrmContext;
import com.sigmasys.kuali.ksa.service.brm.BrmMethodInterceptor;
import com.sigmasys.kuali.ksa.util.TransactionUtils;
import org.aopalliance.aop.Advice;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.sigmasys.kuali.ksa.util.TransactionUtils.*;

/**
 * BRM service implementation.
 *
 * @author Michael Ivanov
 */
@Service("brmPaymentService")
@Transactional
@SuppressWarnings("unchecked")
public class BrmPaymentServiceImpl extends GenericPersistenceService implements BrmPaymentService {

    private static final Log logger = LogFactory.getLog(BrmPaymentServiceImpl.class);


    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private GeneralLedgerService glService;


    /**
     * Adds an AOP proxy to the current instance.
     */
    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<Advice> getAdvices(BeanFactory beanFactory) {
        List<Advice> advices = super.getAdvices(beanFactory);
        if (advices == null) {
            advices = new LinkedList<Advice>();
        }
        advices.add(new BrmMethodInterceptor(this));
        return advices;
    }

    private <T> List<T> toList(String listNames, BrmContext context, boolean initialize) {
        if (StringUtils.isBlank(listNames)) {
            String errMsg = "List name(s) cannot be null or empty";
            logger.error(errMsg);
            throw new IllegalArgumentException(errMsg);
        }
        String[] listNamesArray = listNames.split(",");
        List<T> resultList = new LinkedList<T>();
        for (String listName : listNamesArray) {
            listName = listName.trim();
            if (StringUtils.isBlank(listName)) {
                String errMsg = "List name cannot be null or empty";
                logger.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            }
            List<T> items = (List<T>) context.getAttributes().get(listName);
            if (items == null) {
                if (initialize) {
                    items = new LinkedList<T>();
                    context.getAttributes().put(listName, items);
                } else {
                    String errMsg = "List '" + listName + "' must be initialized in BRM context prior to usage";
                    logger.error(errMsg);
                    throw new IllegalStateException(errMsg);
                }
            }
            if (listNamesArray.length == 1) {
                return items;
            }
            resultList.addAll(items);
        }
        return resultList;
    }

    /**
     * An overridden version of applyPayments() that takes the names of in-transaction and out-GL-transaction lists
     * as well as maxAmount and BRM context
     *
     * @param maxAmount          Maximum amount allowed
     * @param inTransactionLists The name(s) of the input transaction list stored in the BRM context
     * @param outTransactionList The name of the output GL transaction list stored in the BRM context
     * @param context            BRM context
     */
    @Override
    public void applyPayments(BigDecimal maxAmount, String inTransactionLists, String outTransactionList, BrmContext context) {
        List<Transaction> transactions = toList(inTransactionLists, context, false);
        List<GlTransaction> glTransactions = paymentService.applyPayments(transactions, maxAmount);
        toList(outTransactionList, context, true).addAll(glTransactions);
    }

    /**
     * An overridden version of applyPayments() that takes the names of in-transaction and out-GL-transaction lists
     * as well as maxAmount and BRM context
     *
     * @param inTransactionLists The name(s) of the input transaction list stored in the BRM context
     * @param outTransactionList The name of the output GL transaction list stored in the BRM context
     * @param context            BRM context
     */
    @Override
    public void applyPayments(String inTransactionLists, String outTransactionList, BrmContext context) {
        List<Transaction> transactions = toList(inTransactionLists, context, false);
        List<GlTransaction> glTransactions = paymentService.applyPayments(transactions);
        toList(outTransactionList, context, true).addAll(glTransactions);
    }

    /**
     * Removes non-locked allocations for the given transaction list(s) and stores the result in "outTransactionList" list.
     *
     * @param inTransactionLists The name(s) of the input transaction list stored in the BRM context
     * @param outTransactionList The name of the output GL transaction list stored in the BRM context
     * @param context            BRM context
     */
    @Override
    public void removeAllocations(String inTransactionLists, String outTransactionList, BrmContext context) {
        List<Transaction> transactions = toList(inTransactionLists, context, false);
        List<GlTransaction> glTransactions = transactionService.removeAllocations(transactions);
        toList(outTransactionList, context, true).addAll(glTransactions);
    }

    /**
     * Removes specified transaction list(s) from "outTransactionList" list.
     *
     * @param inTransactionLists The name(s) of the input transaction list stored in the BRM context
     * @param outTransactionList The name of the output transaction list stored in the BRM context
     * @param context            BRM context
     */
    @Override
    public void removeTransactions(String inTransactionLists, String outTransactionList, BrmContext context) {
        List<Transaction> inTransactions = toList(inTransactionLists, context, false);
        List<Transaction> outTransactions = toList(outTransactionList, context, false);
        logger.debug("Number of transactions before removal " + outTransactions.size());
        logger.debug("Removing " + inTransactions.size() + " transactions...");
        outTransactions = TransactionUtils.subtract(outTransactions, inTransactions);
        logger.debug("Number of transactions after removal " + outTransactions.size());
        context.getAttributes().put(outTransactionList, outTransactions);
    }

    /**
     * Allocates reversals for the given transaction list(s) and stores the result in "outTransactionList" list.
     *
     * @param inTransactionLists The name(s) of the input transaction list stored in the BRM context
     * @param outTransactionList The name of the output GL transaction list stored in the BRM context
     * @param context            BRM context
     */
    @Override
    public void allocateReversals(String inTransactionLists, String outTransactionList, BrmContext context) {
        List<Transaction> transactions = toList(inTransactionLists, context, false);
        List<GlTransaction> glTransactions = transactionService.allocateReversals(transactions);
        toList(outTransactionList, context, true).addAll(glTransactions);
    }

    /**
     * Filters the lists of transactions by the given parameters and stores the result in "outTransactionList" list.
     *
     * @param type               Transaction type
     * @param year               Year
     * @param inTransactionLists The name(s) of the input transaction list stored in the BRM context
     * @param outTransactionList The name of the output transaction list stored in the BRM context
     * @param context            BRM context
     */
    @Override
    public void filterTransactions(TransactionTypeValue type, int year, String inTransactionLists,
                                   String outTransactionList, BrmContext context) {
        filterTransactions(type, inTransactionLists, outTransactionList, context);
        List<Transaction> transactions = toList(outTransactionList, context, false);
        if (CollectionUtils.isNotEmpty(transactions)) {
            Map<Integer, List<Transaction>> transactionsByYear = sortTransactionsByYears(transactions, year);
            transactions = transactionsByYear.get(year);
        }
        context.getAttributes().put(outTransactionList, new ArrayList<Transaction>(transactions));
    }

    /**
     * Filters the lists of transactions by the given parameters and stores the result in "outTransactionList" list.
     *
     * @param type               Transaction type
     * @param year               Year
     * @param tag                Transaction tag
     * @param inTransactionLists The name(s) of the input transaction list stored in the BRM context
     * @param outTransactionList The name of the output transaction list stored in the BRM context
     * @param context            BRM context
     */
    @Override
    public void filterTransactions(TransactionTypeValue type, int year, String tag, String inTransactionLists,
                                   String outTransactionList, BrmContext context) {
        filterTransactions(type, year, inTransactionLists, outTransactionList, context);
        List<Transaction> transactions = toList(outTransactionList, context, false);
        if (CollectionUtils.isNotEmpty(transactions)) {
            Map<String, List<Transaction>> transactionsByTag = sortTransactionsByTags(transactions, tag);
            transactions = transactionsByTag.get(tag);
        }
        context.getAttributes().put(outTransactionList, new ArrayList<Transaction>(transactions));
    }

    /**
     * Filters the lists of transactions by the given parameters and stores the result in "outTransactionList" list.
     *
     * @param type               Transaction type
     * @param inTransactionLists The name(s) of the input transaction list stored in the BRM context
     * @param outTransactionList The name of the output transaction list stored in the BRM context
     * @param context            BRM context
     */
    @Override
    public void filterTransactions(TransactionTypeValue type, String inTransactionLists, String outTransactionList,
                                   BrmContext context) {
        List<Transaction> transactions = toList(inTransactionLists, context, false);
        Map<TransactionTypeValue, List<Transaction>> transactionsByType = sortTransactionsByTypes(transactions, type);
        transactions = transactionsByType.get(type);
        context.getAttributes().put(outTransactionList, new ArrayList<Transaction>(transactions));
    }

    /**
     * Calculates matrix scores for the given transaction list.
     *
     * @param inTransactionList The name of the input transaction list stored in the BRM context
     * @param context           BRM context
     */
    @Override
    public void calculateMatrixScores(String inTransactionList, BrmContext context) {
        List<Transaction> transactions = toList(inTransactionList, context, false);
        TransactionUtils.calculateMatrixScores(transactions);
        context.getAttributes().put(inTransactionList, transactions);
    }

    /**
     * Sorts the given transaction list by transaction matrix score in the specified order.
     *
     * @param inTransactionList The name of the input transaction list stored in the BRM context
     * @param order             Indicates ascending order if true and descending otherwise
     * @param context           BRM context
     */
    @Override
    public void sortByMatrixScore(String inTransactionList, boolean order, BrmContext context) {
        List<Transaction> transactions = toList(inTransactionList, context, false);
        transactions = TransactionUtils.orderByMatrixScore(transactions, order);
        context.getAttributes().put(inTransactionList, transactions);
    }

    /**
     * Sorts the given transaction list by transaction priority in the specified order.
     *
     * @param inTransactionList The name of the input transaction list stored in the BRM context
     * @param order             Indicates ascending order if true and descending otherwise
     * @param context           BRM context
     */
    @Override
    public void sortByPriority(String inTransactionList, boolean order, BrmContext context) {
        List<Transaction> transactions = toList(inTransactionList, context, false);
        transactions = TransactionUtils.orderByPriority(transactions, order);
        context.getAttributes().put(inTransactionList, transactions);
    }

    /**
     * Sorts the given transaction list by effective date in the specified order.
     *
     * @param inTransactionList The name of the input transaction list stored in the BRM context
     * @param order             Indicates ascending order if true and descending otherwise
     * @param context           BRM context
     */
    @Override
    public void sortByEffectiveDate(String inTransactionList, boolean order, BrmContext context) {
        List<Transaction> transactions = toList(inTransactionList, context, false);
        transactions = TransactionUtils.orderByEffectiveDate(transactions, order);
        context.getAttributes().put(inTransactionList, transactions);
    }


    /**
     * Summarizes the given lists of GL transactions.
     *
     * @param inTransactionLists The name(s) of the input GL transaction list stored in the BRM context
     * @param context            BRM context
     */
    @Override
    public void summarizeGlTransactions(String inTransactionLists, BrmContext context) {
        List<GlTransaction> glTransactions = toList(inTransactionLists, context, false);
        glTransactions = glService.summarizeGlTransactions(glTransactions);
        context.getAttributes().put(inTransactionLists, glTransactions);
    }

    /**
     * Gets the list of active transactions for the given parameters and stores the result in "outTransactionList" list.
     *
     * @param startDate          Start date
     * @param endDate            End date
     * @param outTransactionList The name of the output transaction list stored in the BRM context
     * @param context            BRM context
     */
    @Override
    public void getActiveTransactions(String startDate, String endDate, String outTransactionList, BrmContext context) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);
            Date fromDate = dateFormat.parse(startDate);
            Date toDate = dateFormat.parse(endDate);
            String userId = context.getAccount().getId();
            List<Transaction> transactions = transactionService.getTransactions(userId, fromDate, toDate,
                    TransactionStatus.ACTIVE);
            context.getAttributes().put(outTransactionList, transactions);
        } catch (ParseException pe) {
            logger.error("Date format is incorrect: " + pe.getMessage(), pe);
            throw new IllegalArgumentException("Date format is incorrect: " + pe.getMessage(), pe);
        }
    }

    /**
     * Sets the global variable to the BrmContext attribute value.
     *
     * @param globalVariableName BRM global variable name
     * @param attributeName      BRM context attribute name
     * @param context            BRM context
     */
    public void setGlobalVariableToAttributeValue(String globalVariableName, String attributeName, BrmContext context) {
        Object attributeValue = context.getAttributes().get(attributeName);
        context.getGlobalVariables().put(globalVariableName, attributeValue);
    }

}

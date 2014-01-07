package com.sigmasys.kuali.ksa.util;

import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.service.TransactionService;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * TransactionUtils
 *
 * @author Michael Ivanov
 */
@SuppressWarnings("unchecked")
public class TransactionUtils {

    private static TransactionService transactionService;

    private TransactionUtils() {
    }

    private static TransactionService getTransactionService() {
        if (transactionService == null) {
            transactionService = ContextUtils.getBean(TransactionService.class);
        }
        return transactionService;
    }

    public static String formatAmount(BigDecimal amount) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        return new DecimalFormat("#.##").format(amount);
    }

    public static BigDecimal getFormattedAmount(BigDecimal amount) {
        return new BigDecimal(formatAmount(amount));
    }

    public static BigDecimal getTotalAmount(Collection<Transaction> transactions) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            totalAmount = totalAmount.add(transaction.getAmount());
        }
        return totalAmount;
    }

    public static BigDecimal getUnallocatedAmount(Transaction transaction) {

        BigDecimal allocatedAmount = transaction.getAllocatedAmount();
        if (allocatedAmount == null) {
            allocatedAmount = BigDecimal.ZERO;
        }

        BigDecimal lockedAllocatedAmount = transaction.getLockedAllocatedAmount();
        if (lockedAllocatedAmount == null) {
            lockedAllocatedAmount = BigDecimal.ZERO;
        }

        BigDecimal amount = transaction.getAmount();
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }

        return amount.abs().subtract(allocatedAmount.add(lockedAllocatedAmount));
    }

    /**
     * Remove all transactions from the list that are now fully allocated (amount = allocatedAmount +
     * lockedAllocationAmount)
     *
     * @param transactions a list of transactions
     * @return a modified list of transactions
     */
    public static List<Transaction> removeFullyAllocatedTransactions(List<Transaction> transactions) {
        List<Transaction> newTransactions = getCopy(transactions);
        for (Iterator<Transaction> iterator = newTransactions.iterator(); iterator.hasNext(); ) {
            if (getUnallocatedAmount(iterator.next()).compareTo(BigDecimal.ZERO) == 0) {
                iterator.remove();
            }
        }
        return newTransactions;
    }

    /**
     * Returns the values of the unallocated amount of payments, deferments and charges in the given list
     *
     * @param transactions a list of transactions
     * @return an instance of Map<TransactionTypeValue, BigDecimal> that contains unallocated values for charges,
     *         payments and deferments
     */
    public static Map<TransactionTypeValue, BigDecimal> getUnallocatedTransactionAmounts(List<Transaction> transactions) {

        BigDecimal chargeAmount = BigDecimal.ZERO;
        BigDecimal paymentAmount = BigDecimal.ZERO;
        BigDecimal defermentAmount = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {

            BigDecimal unallocatedAmount = getUnallocatedAmount(transaction);
            switch (transaction.getTransactionTypeValue()) {
                case CHARGE:
                    chargeAmount = chargeAmount.add(unallocatedAmount);
                    break;
                case PAYMENT:
                    paymentAmount = paymentAmount.add(unallocatedAmount);
                    break;
                case DEFERMENT:
                    defermentAmount = defermentAmount.add(unallocatedAmount);
                    break;
            }

        }

        Map<TransactionTypeValue, BigDecimal> valueMap = new HashMap<TransactionTypeValue, BigDecimal>(3);
        valueMap.put(TransactionTypeValue.CHARGE, chargeAmount);
        valueMap.put(TransactionTypeValue.PAYMENT, paymentAmount);
        valueMap.put(TransactionTypeValue.DEFERMENT, defermentAmount);

        return valueMap;
    }

    /**
     * Returns the value of the unallocated amount of all charges in the list
     *
     * @param transactions a list of transactions
     * @return BigDecimal of the sum of the unallocated charge amount
     */
    public static BigDecimal getUnallocatedChargeAmount(List<Transaction> transactions) {
        return getUnallocatedTransactionAmounts(transactions).get(TransactionTypeValue.CHARGE);
    }

    /**
     * Returns the value of the unallocated amount of all payments in the list
     *
     * @param transactions a list of transactions
     * @return BigDecimal of the sum of the unallocated payment amount
     */
    public static BigDecimal getUnallocatedPaymentAmount(List<Transaction> transactions) {
        return getUnallocatedTransactionAmounts(transactions).get(TransactionTypeValue.PAYMENT);
    }

    /**
     * Returns the value of the unallocated amount of all deferments in the list
     *
     * @param transactions a list of transactions
     * @return BigDecimal of the sum of the unallocated deferment amount
     */
    public static BigDecimal getUnallocatedDefermentAmount(List<Transaction> transactions) {
        return getUnallocatedTransactionAmounts(transactions).get(TransactionTypeValue.DEFERMENT);
    }

    /**
     * Return the unallocated amount of all restricted payments. Unrestricted payments have a
     * permissible debit type mask of “.*”.
     *
     * @param transactions a list of transactions
     * @return BigDecimal of the sum of the restricted unallocated payment amount
     */
    public static BigDecimal getRestrictedUnallocatedPaymentAmount(List<Transaction> transactions) {
        return getTransactionService().getUnallocatedAmount(transactions, TransactionTypeValue.PAYMENT, true);
    }

    /**
     * Return the unallocated amount of all unrestricted payments. Unrestricted payments have a
     * permissible debit type mask of “.*”.
     *
     * @param transactions a list of transactions
     * @return BigDecimal of the sum of the unrestricted unallocated payment amount
     */
    public static BigDecimal getUnrestrictedUnallocatedPaymentAmount(List<Transaction> transactions) {
        return getTransactionService().getUnallocatedAmount(transactions, TransactionTypeValue.PAYMENT, false);
    }

    /**
     * Return the unallocated amount of all restricted deferments. Unrestricted deferments have a
     * permissible debit type mask of “.*”.
     *
     * @param transactions a list of transactions
     * @return BigDecimal of the sum of the restricted unallocated deferment amount
     */
    public static BigDecimal getRestrictedUnallocatedDefermentAmount(List<Transaction> transactions) {
        return getTransactionService().getUnallocatedAmount(transactions, TransactionTypeValue.DEFERMENT, true);
    }

    /**
     * Return the unallocated amount of all unrestricted deferments. Unrestricted deferments have a
     * permissible debit type mask of “.*”.
     *
     * @param transactions a list of transactions
     * @return BigDecimal of the sum of the unrestricted unallocated deferment amount
     */
    public static BigDecimal getUnrestrictedUnallocatedDefermentAmount(List<Transaction> transactions) {
        return getTransactionService().getUnallocatedAmount(transactions, TransactionTypeValue.DEFERMENT, false);
    }

    public static void calculateMatrixScores(List<Transaction> transactions) {

        Set<Credit> credits = new HashSet<Credit>();
        Set<Debit> debits = new HashSet<Debit>();

        for (Transaction transaction : transactions) {
            if (transaction instanceof Credit) {
                credits.add((Credit) transaction);
            } else if (transaction instanceof Debit) {
                debits.add((Debit) transaction);
            }
        }

        for (Transaction transaction : transactions) {
            int matrixScore = 0;
            if (transaction instanceof Credit) {
                for (Debit debit : debits) {
                    if (getTransactionService().canPay(transaction, debit)) {
                        matrixScore++;
                    }
                }
            } else if (transaction instanceof Debit) {
                for (Credit credit : credits) {
                    if (getTransactionService().canPay(credit, transaction)) {
                        matrixScore++;
                    }
                }
            }

            transaction.setMatrixScore(matrixScore);
        }
    }

    private static List<Transaction> orderTransactions(List<Transaction> transactions,
                                                       Comparator<Transaction> comparator,
                                                       boolean ascending) {
        if (!ascending) {
            comparator = Collections.reverseOrder(comparator);
        }

        Collections.sort(transactions, comparator);

        return transactions;

    }

    public static List<Transaction> orderByEffectiveDate(List<Transaction> transactions, boolean ascending) {
        return orderTransactions(transactions, new EffectiveDateComparator(), ascending);
    }

    public static List<Transaction> orderByAmount(List<Transaction> transactions, boolean ascending) {
        return orderTransactions(transactions, new AmountComparator(), ascending);
    }

    public static List<Transaction> orderByUnallocatedAmount(List<Transaction> transactions, boolean ascending) {
        return orderTransactions(transactions, new UnallocatedAmountComparator(), ascending);
    }

    public static List<Transaction> orderByMatrixScore(List<Transaction> transactions, boolean ascending) {
        return orderTransactions(transactions, new MatrixScoreComparator(), ascending);
    }

    public static List<Transaction> orderByPriority(List<Transaction> transactions, boolean ascending) {
        return orderTransactions(transactions, new PriorityComparator(), ascending);
    }

    public static List<Transaction> reverseOrder(List<Transaction> transactions) {
        Collections.reverse(transactions);
        return transactions;
    }

    public static List<Transaction> getCopy(List<Transaction> transactions) {
        return new ArrayList<Transaction>(transactions);
    }

    private static List<Transaction> removeTransactions(List<Transaction> transactions, TransactionTypeValue transactionType) {
        List<Transaction> newTransactions = getCopy(transactions);
        for (Iterator<Transaction> iterator = newTransactions.iterator(); iterator.hasNext(); ) {
            Transaction transaction = iterator.next();
            if (transactionType == transaction.getTransactionTypeValue()) {
                iterator.remove();
            }
        }
        return newTransactions;
    }

    public static List<Transaction> removeCharges(List<Transaction> transactions) {
        return removeTransactions(transactions, TransactionTypeValue.CHARGE);
    }

    public static List<Transaction> removePayments(List<Transaction> transactions) {
        return removeTransactions(transactions, TransactionTypeValue.PAYMENT);
    }

    public static List<Transaction> removeDeferments(List<Transaction> transactions) {
        return removeTransactions(transactions, TransactionTypeValue.DEFERMENT);
    }

    public static List<Transaction> union(List<Transaction> transactions1, List<Transaction> transactions2) {
        return new ArrayList<Transaction>(CollectionUtils.union(transactions1, transactions2));
    }

    public static List<Transaction> intersection(List<Transaction> transactions1, List<Transaction> transactions2) {
        return new ArrayList<Transaction>(CollectionUtils.intersection(transactions1, transactions2));
    }

    public static List<Transaction> exclusiveDisjunction(List<Transaction> transactions1, List<Transaction> transactions2) {
        return new ArrayList<Transaction>(CollectionUtils.disjunction(transactions1, transactions2));
    }

    public static List<Transaction> subtract(List<Transaction> transactions1, List<Transaction> transactions2) {
        return new ArrayList<Transaction>(CollectionUtils.subtract(transactions1, transactions2));
    }

    public static List<Transaction> filterByEffectiveDate(List<Transaction> transactions, Date fromDate, Date toDate) {
        List<Transaction> newTransactions = new LinkedList<Transaction>();
        for (Transaction transaction : transactions) {
            Date effectiveDate = transaction.getEffectiveDate();
            if (effectiveDate != null) {
                if (effectiveDate.compareTo(fromDate) >= 0 && effectiveDate.compareTo(toDate) <= 0) {
                    newTransactions.add(transaction);
                }
            }
        }
        return newTransactions;
    }

    public static List<Transaction> filterByAmount(List<Transaction> transactions, BigDecimal fromAmount,
                                                   BigDecimal toAmount) {
        List<Transaction> newTransactions = new LinkedList<Transaction>();
        for (Transaction transaction : transactions) {
            BigDecimal amount = transaction.getAmount();
            if (amount != null) {
                if (amount.compareTo(fromAmount) >= 0 && amount.compareTo(toAmount) <= 0) {
                    newTransactions.add(transaction);
                }
            }
        }
        return newTransactions;
    }

    public static List<Transaction> filterByUnallocatedAmount(List<Transaction> transactions, BigDecimal fromAmount,
                                                              BigDecimal toAmount) {
        List<Transaction> newTransactions = new LinkedList<Transaction>();
        for (Transaction transaction : transactions) {
            BigDecimal amount = getUnallocatedAmount(transaction);
            if (amount.compareTo(fromAmount) >= 0 && amount.compareTo(toAmount) <= 0) {
                newTransactions.add(transaction);
            }
        }
        return newTransactions;
    }

    public static List<Transaction> filterByPriority(List<Transaction> transactions, int minPriority, int maxPriority) {
        List<Transaction> newTransactions = new LinkedList<Transaction>();
        for (Transaction transaction : transactions) {
            TransactionType transactionType = transaction.getTransactionType();
            if (transactionType.getPriority() != null) {
                int priority = transactionType.getPriority();
                if (priority >= minPriority && priority <= maxPriority) {
                    newTransactions.add(transaction);
                }
            }
        }
        return newTransactions;
    }

    public static List<Transaction> filterByMatrixScore(List<Transaction> transactions, int minScore, int maxScore) {
        List<Transaction> newTransactions = new LinkedList<Transaction>();
        for (Transaction transaction : transactions) {
            if (transaction.getMatrixScore() != null) {
                int matrixScore = transaction.getMatrixScore();
                if (matrixScore >= minScore && matrixScore <= maxScore) {
                    newTransactions.add(transaction);
                }
            }
        }
        return newTransactions;
    }

    public static List<Transaction> filterByTags(List<Transaction> transactions, List<Tag> tags) {

        List<Transaction> newTransactions = new LinkedList<Transaction>();
        for (Transaction transaction : transactions) {

            List<Tag> transactionTags = new ArrayList<Tag>(transaction.getTags());

            transactionTags.retainAll(tags);

            if (!transactionTags.isEmpty()) {
                newTransactions.add(transaction);
            }
        }
        return newTransactions;
    }

    public static Map<Integer, List<Transaction>> sortTransactionsByYears(List<Transaction> transactions, Integer... years) {
        Map<Integer, List<Transaction>> transactionMap = new HashMap<Integer, List<Transaction>>(years.length);
        for (int year : years) {
            transactionMap.put(year, new LinkedList<Transaction>());
        }
        for (int year : years) {
            for (Transaction transaction : transactions) {
                Date effectiveDate = transaction.getEffectiveDate();
                if (effectiveDate != null) {
                    if (year == CalendarUtils.getYear(effectiveDate)) {
                        List<Transaction> transactionsForYear = transactionMap.get(year);
                        transactionsForYear.add(transaction);
                    }
                }
            }
        }
        return transactionMap;
    }

    public static Map<String, List<Transaction>> sortTransactionsByTags(List<Transaction> transactions, String... tagCodes) {
        Map<String, List<Transaction>> transactionMap = new HashMap<String, List<Transaction>>(tagCodes.length);
        for (String tagCode : tagCodes) {
            transactionMap.put(tagCode, new LinkedList<Transaction>());
        }
        for (String tagCode : tagCodes) {
            for (Transaction transaction : transactions) {
                Set<Tag> tags = transaction.getTags();
                if (tags != null) {
                    for (Tag tag : tags) {
                        if (tagCode.equals(tag.getCode())) {
                            List<Transaction> transactionsForTag = transactionMap.get(tagCode);
                            transactionsForTag.add(transaction);
                            break;
                        }
                    }
                }
            }
        }
        return transactionMap;
    }

    public static Map<TransactionTypeValue, List<Transaction>> sortTransactionsByTypes(List<Transaction> transactions,
                                                                                       TransactionTypeValue... types) {
        Map<TransactionTypeValue, List<Transaction>> transactionMap =
                new HashMap<TransactionTypeValue, List<Transaction>>(types.length);
        for (TransactionTypeValue type : types) {
            transactionMap.put(type, new LinkedList<Transaction>());
        }
        for (TransactionTypeValue type : types) {
            for (Transaction transaction : transactions) {
                if (type == transaction.getTransactionTypeValue()) {
                    List<Transaction> transactionsForType = transactionMap.get(type);
                    transactionsForType.add(transaction);
                }
            }
        }
        return transactionMap;
    }

    // Needed by Drools
    public static List<Transaction> newTransactionList() {
        return new LinkedList<Transaction>();
    }

    // Needed by Drools
    public static List<GlTransaction> newGlTransactionList() {
        return new LinkedList<GlTransaction>();
    }

    // ------------- Various Transaction comparators -------------------------------------
    public static class EffectiveDateComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            int compare = t1.getEffectiveDate().compareTo(t2.getEffectiveDate());
            if (compare == 0) {
                return t1.getCreationDate().compareTo(t2.getCreationDate());
            } else {
                return compare;
            }
        }
    }

    public static class AmountComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            return t1.getAmount().compareTo(t2.getAmount());
        }
    }

    public static class PriorityComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            TransactionType transactionType1 = t1.getTransactionType();
            TransactionType transactionType2 = t2.getTransactionType();
            if (transactionType1.getPriority() != null && transactionType2.getPriority() != null) {
                return transactionType1.getPriority().compareTo(transactionType2.getPriority());
            } else if (transactionType1.getPriority() != null && transactionType2.getPriority() == null) {
                return 1;
            } else if (transactionType1.getPriority() == null && transactionType2.getPriority() != null) {
                return -1;
            }
            return 0;
        }
    }

    public static class UnallocatedAmountComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            return getUnallocatedAmount(t1).compareTo(getUnallocatedAmount(t2));
        }
    }

    public static class MatrixScoreComparator implements Comparator<Transaction> {
        @Override
        public int compare(Transaction t1, Transaction t2) {
            Integer matrixScore1 = (t1.getMatrixScore() != null) ? t1.getMatrixScore() : 0;
            Integer matrixScore2 = (t2.getMatrixScore() != null) ? t2.getMatrixScore() : 0;
            return matrixScore1.compareTo(matrixScore2);
        }
    }

}




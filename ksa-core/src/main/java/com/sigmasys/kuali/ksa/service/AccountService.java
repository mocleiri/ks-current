package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.*;
import com.sigmasys.kuali.ksa.jaxb.Ach;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Account service declares business operations on Student Accounts and related
 * objects
 * <p/>
 *
 * @author Michael Ivanov
 */
public interface AccountService {

    /**
     * This process creates a temporary subset of the account as if the account were being administered
     * as a balance forward account. This permits aging the account in a way that is not affected by the
     * payment application methodology. This temporary array is passed to the ageDebt() method.
     *
     * @param userId          Account ID
     * @param ignoreDeferment boolean value
     * @return a list of pairs [Debit, BigDecimal]
     */
    List<Pair<Debit, BigDecimal>> rebalance(String userId, boolean ignoreDeferment);

    /**
     * Aging debts for all chargeable accounts in KSA
     *
     * @param ignoreDeferment boolean value
     */
    void ageDebt(boolean ignoreDeferment);

    /**
     * Aging debts for a chargeable account.
     *
     * @param userId          Account ID
     * @param ignoreDeferment boolean value
     * @return a chargeable account being updated
     */
    ChargeableAccount ageDebt(String userId, boolean ignoreDeferment);

    /**
     * Aging debts for a chargeable account.
     *
     * @param userId          Account ID
     * @param ageDate         Age date
     * @param ignoreDeferment boolean value
     * @return a chargeable account being updated
     */
    ChargeableAccount ageDebt(String userId, Date ageDate, boolean ignoreDeferment);

    /**
     * Aging debts for a chargeable account.
     *
     * @param userId          Account ID
     * @param ageDebtMethod   Age Debt method
     * @param ignoreDeferment boolean value
     * @return a chargeable account being updated
     */
    ChargeableAccount ageDebt(String userId, AgeDebtMethod ageDebtMethod, boolean ignoreDeferment);

    /**
     * Aging debts for a chargeable account.
     *
     * @param userId          Account ID
     * @param ageDebtMethod   Age Debt method
     * @param ageDate         Age date
     * @param ignoreDeferment boolean value
     * @return a chargeable account being updated
     */
    ChargeableAccount ageDebt(String userId, AgeDebtMethod ageDebtMethod, Date ageDate, boolean ignoreDeferment);

    /**
     * Returns the difference between the outstanding and due balances.
     *
     * @param userId          Account ID
     * @param ignoreDeferment boolean value
     * @return The future balance amount
     */
    BigDecimal getFutureBalance(String userId, boolean ignoreDeferment);

    /**
     * Returns the difference between the outstanding and due balances.
     *
     * @param userId          Account ID
     * @param balanceDate     Balance date
     * @param ignoreDeferment Boolean value
     * @return The future balance amount
     */
    BigDecimal getFutureBalance(String userId, Date balanceDate, boolean ignoreDeferment);

    /**
     * Returns the total balance due of all active transactions.
     *
     * @param userId          Account ID
     * @param ignoreDeferment boolean value
     * @return total amount of balance due
     */
    BigDecimal getDueBalance(String userId, boolean ignoreDeferment);

    /**
     * Returns the total balance due of all active transactions.
     *
     * @param userId          Account ID
     * @param balanceDate     Balance date
     * @param ignoreDeferment Boolean value
     * @return total amount of balance due
     */
    BigDecimal getDueBalance(String userId, Date balanceDate, boolean ignoreDeferment);

    /**
     * Returns the total balance to date for the given user ID.
     *
     * @param userId Account ID
     * @param toDate To Date
     * @return Amount
     */
    BigDecimal getBalance(String userId, Date toDate);

    /**
     * Returns the outstanding balance for the given account
     *
     * @param userId          Account ID
     * @param ignoreDeferment boolean value
     * @return total amount of outstanding balance
     */
    BigDecimal getOutstandingBalance(String userId, boolean ignoreDeferment);

    /**
     * Returns the outstanding balance for the given account
     *
     * @param userId          Account ID
     * @param balanceDate     Balance date
     * @param ignoreDeferment Boolean value
     * @return total amount of outstanding balance
     */
    BigDecimal getOutstandingBalance(String userId, Date balanceDate, boolean ignoreDeferment);

    /**
     * Returns unallocated balance for the given Account ID
     *
     * @param userId Account ID
     * @return unallocated balance
     */
    BigDecimal getUnallocatedBalance(String userId);

    /**
     * Returns the deferred amount
     *
     * @param userId Account ID
     * @return deferred amount
     */
    BigDecimal getDeferredBalance(String userId);

    /**
     * Returns the deferred amount.
     *
     * @param userId      Account ID
     * @param balanceDate Balance date
     * @return deferred amount
     */
    BigDecimal getDeferredBalance(String userId, Date balanceDate);

    /**
     * This method is used to verify that an account exists before a transaction or other operations are
     * performed on the account. There is an initial inquiry into the KSA store. If no account exists, then there is
     * an inquiry into KIM. If KIM also returns no result, then false is returned. If a KIM account does exist, then
     * a KSA account is created, using the KIM information as a template.
     *
     * @param userId Account ID
     * @return the account instance or null if the account does not exist
     */
    Account getOrCreateAccount(String userId);


    /**
     * Checks if KSA account exists
     *
     * @param userId Account ID
     * @return true if the account exists, false otherwise
     */
    boolean ksaAccountExists(String userId);

    /**
     * Checks if KSA account exists. If the KSA account does not exist, it tries to look for the existing KIM account
     * and create a new KSA account, if the account does not exist returns false, otherwise true.
     *
     * @param userId Account ID
     * @return true if the account exists, false otherwise
     */
    boolean accountExists(String userId);

    /**
     * This methods fetches Account and all its associations by account ID.
     *
     * @param userId Account ID
     * @return the account instance or null if the account does not exist
     */
    Account getFullAccount(String userId);

    /**
     * This methods fetches all KSA accounts and all their associations.
     *
     * @return the list account instances
     */
    List<Account> getFullAccounts();

    /**
     * This method fetches all KSA accounts that match the substring %name%
     *
     * @param pattern Name pattern
     * @return the list of Account instances
     */
    List<Account> getAccountsByNamePattern(String pattern);

    /**
     * This method fetches all KSA accounts that match the substring %name% and Account subclass.
     *
     * @param pattern      Name pattern
     * @param accountClass Account subclass
     * @return the list of Account instances
     */
    <T extends Account> List<T> getAccountsByNamePattern(String pattern, Class<T> accountClass);

    /**
     * Creates and associates a new person name object with the given Account ID.
     *
     * @param userId     Account ID
     * @param personName Person name
     * @return new PersonName instance with ID
     */
    PersonName addPersonName(String userId, PersonName personName);

    /**
     * Creates and associates a new postal address with the given Account ID.
     *
     * @param userId        Account ID
     * @param postalAddress Postal address
     * @return new PostalAddress instance with ID
     */
    PostalAddress addPostalAddress(String userId, PostalAddress postalAddress);

    /**
     * Creates and associates a new electronic contact with the given Account ID.
     *
     * @param userId            Account ID
     * @param electronicContact Electronic contact
     * @return new ElectronicContact instance with ID
     */
    ElectronicContact addElectronicContact(String userId, ElectronicContact electronicContact);

    /**
     * Persists PersonName instance in the persistent store.
     *
     * @param personName PersonName instance
     * @return PersonName ID
     */
    Long persistPersonName(PersonName personName);

    /**
     * Persists PostalAddress instance in the persistent store.
     *
     * @param postalAddress PostalAddress instance
     * @return PostalAddress ID
     */
    Long persistPostalAddress(PostalAddress postalAddress);

    /**
     * Persists ElectronicContact instance in the persistent store.
     *
     * @param electronicContact ElectronicContact instance
     * @return ElectronicContact ID
     */
    Long persistElectronicContact(ElectronicContact electronicContact);

    /**
     * Get ACH looks into the AccountProtectedInformation class (which triggers a system event) to look for
     * the ACH information for the user
     *
     * @param userId Account ID
     * @return Ach for associated ID.
     */
    Ach getAch(String userId);

    /**
     * Returns the list of matching accounts for the given name pattern.
     *
     * @param namePattern Name pattern
     * @return List of Account instances
     */
    List<Account> findAccountsByNamePattern(String namePattern);

    /**
     * Expanded search for Accounts using multiple search criteria.
     *
     * @param searchPatterns Multiple search patterns.
     * @return List of matching Accounts.
     */
    List<Account> findAccountsByExpandedSearchPatterns(String... searchPatterns);

    /**
     * Returns the account protected information by user ID.
     *
     * @param userId Account ID
     * @return AccountProtectedInfo instance
     */
    AccountProtectedInfo getAccountProtectedInfo(String userId);

    /**
     * Updates the KSA account in both places - KSA and KIM
     *
     * @param account  Account instance to be updated
     * @param password User password
     */
    void updateAccount(Account account, String password);

    /**
     * Creates the user account in both places - KSA and KIM
     *
     * @param account        Account instance to be created
     * @param defaultName    Default PersonName
     * @param defaultAddress Default PersonAddress
     * @param defaultContact Default ElectronicContact
     * @param password       User password
     * @return Account instance
     */
    Account createAccount(Account account, PersonName defaultName, PostalAddress defaultAddress,
                          ElectronicContact defaultContact, String password);

}

package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.model.*;

import java.util.Date;
import java.util.List;

/**
 * Information Service
 * <p/>
 *
 * @author Michael Ivanov
 */
public interface InformationService {

    /**
     * Returns Information by ID
     *
     * @param id Information ID
     * @return Information instance
     */
    Information getInformation(Long id);

    /**
     * Returns Memo by ID
     *
     * @param id Memo ID
     * @return Memo instance
     */
    Memo getMemo(Long id);

    /**
     * Returns Alert by ID
     *
     * @param id Alert ID
     * @return Alert instance
     */
    Alert getAlert(Long id);

    /**
     * Returns Flag by ID
     *
     * @param id Flag ID
     * @return Flag instance
     */
    Flag getFlag(Long id);

    /**
     * Returns all Information entities sorted by ID in the descendant order
     *
     * @return List of Information instances
     */
    List<Information> getInformationEntities();

    /**
     * Returns all Memo entities sorted by ID in the descendant order
     *
     * @return List of memos
     */
    List<Memo> getMemos();

    /**
     * Returns all Flag entities sorted by ID in the descendant order
     *
     * @return List of flags
     */
    List<Flag> getFlags();

    /**
     * Returns all Alert entities sorted by ID in the descendant order
     *
     * @return List of alerts
     */
    List<Alert> getAlerts();

    /**
     * Returns all Memo entities by Transaction ID
     *
     * @param transactionId Transaction ID
     * @return List of memos for the given transaction
     */
    List<Memo> getMemos(Long transactionId);


    /**
     * Returns all Alert entities by Account ID
     *
     * @param userId Account ID
     * @return List of alerts
     */
    List<Alert> getAlerts(String userId);

    /**
     * Returns all Flag entities by Account ID
     *
     * @param userId Account ID
     * @return List of flags
     */
    List<Flag> getFlags(String userId);

    /**
     * Returns all Memo entities by Account ID
     *
     * @param userId Account ID
     * @return List of memos
     */
    List<Memo> getMemos(String userId);

    /**
     * Persists Information in the database.
     * Creates a new entity when ID is null and updates the existing one otherwise.
     *
     * @param information Information instance
     * @return Information ID
     */
    Long persistInformation(Information information);

    /**
     * Removes Information from the database.
     *
     * @param id Information ID
     * @return true if Information entity has been deleted
     */
    boolean deleteInformation(Long id);

    /**
     * Memos can be generated in a number of ways. If a memo is generated
     * against a transaction, it is placed in the main memo account, and also
     * the memoReference is set to point to that memo. This allows the CSR to
     * see the most recent memo associated with a certain transaction. Certain
     * transaction instantiations will generate a memo as soon as they are
     * created.
     *
     * @param transactionId   Transaction ID
     * @param memoText        Memo text
     * @param accessLevelCode InformationAccessLevel code
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @param prevMemoId      Previous Memo ID
     * @return new Memo instance
     */
    Memo createMemo(Long transactionId, String memoText, String accessLevelCode,
                    Date effectiveDate, Date expirationDate, Long prevMemoId);


    /**
     * Creates a memo with the default access level code for the given transaction.
     *
     * @param transactionId  Transaction ID
     * @param memoText       Memo text
     * @param effectiveDate  Effective date
     * @param expirationDate Expiration date
     * @param prevMemoId     Previous Memo ID
     * @return new Memo instance
     */
    Memo createMemo(Long transactionId, String memoText, Date effectiveDate, Date expirationDate, Long prevMemoId);

    /**
     * Memos can be generated in a number of ways. If a memo is generated
     * against a transaction, it is placed in the main memo account, and also
     * the memoReference is set to point to that memo. This allows the CSR to
     * see the most recent memo associated with a certain transaction. Certain
     * transaction instantiations will generate a memo as soon as they are
     * created.
     *
     * @param accountId       Account ID
     * @param memoText        Memo text
     * @param accessLevelCode InformationAccessLevel code
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @param prevMemoId      Previous Memo ID
     * @return new Memo instance
     */
    Memo createMemo(String accountId, String memoText, String accessLevelCode,
                    Date effectiveDate, Date expirationDate, Long prevMemoId);

    /**
     * Creates a memo with the default access level code for the given account.
     *
     * @param accountId      Account ID
     * @param memoText       Memo text
     * @param effectiveDate  Effective date
     * @param expirationDate Expiration date
     * @param prevMemoId     Previous Memo ID
     * @return new Memo instance
     */
    Memo createMemo(String accountId, String memoText, Date effectiveDate, Date expirationDate, Long prevMemoId);


    /**
     * Creates a new flag based on the given parameters
     *
     * @param transactionId   Transaction ID
     * @param flagTypeId      Flag Type ID
     * @param accessLevelCode InformationAccessLevel code
     * @param severity        Severity
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @return new Flag instance
     */
    Flag createFlag(Long transactionId, Long flagTypeId, String accessLevelCode,
                    Integer severity, Date effectiveDate, Date expirationDate);

    /**
     * Creates a new flag based on the given parameters
     *
     * @param accountId       Account ID
     * @param flagTypeId      Flag Type ID
     * @param accessLevelCode InformationAccessLevel code
     * @param severity        Severity
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @return new Flag instance
     */
    Flag createFlag(String accountId, Long flagTypeId, String accessLevelCode,
                    Integer severity, Date effectiveDate, Date expirationDate);

    /**
     * Creates a new flag based on the given parameters
     *
     * @param accountId       Account ID
     * @param flagTypeCode    Flag Type code
     * @param accessLevelCode InformationAccessLevel code
     * @param severity        Severity
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @return new Flag instance
     */
    Flag createFlag(String accountId, String flagTypeCode, String accessLevelCode,
                    Integer severity, Date effectiveDate, Date expirationDate);

    /**
     * Creates a new alert based on the given parameters
     *
     * @param transactionId   Transaction ID
     * @param alertText       Alert text
     * @param accessLevelCode InformationAccessLevel code
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @return new Alert instance
     */
    Alert createAlert(Long transactionId, String alertText, String accessLevelCode, Date effectiveDate, Date expirationDate);

    /**
     * Creates a new alert based on the given parameters
     *
     * @param accountId       Account ID
     * @param alertText       Alert text
     * @param accessLevelCode InformationAccessLevel code
     * @param effectiveDate   Effective date
     * @param expirationDate  Expiration date
     * @return new Alert instance
     */
    Alert createAlert(String accountId, String alertText, String accessLevelCode, Date effectiveDate, Date expirationDate);

    /**
     * Creates a new FlagType based on the given parameters
     *
     * @param code            FlagType code
     * @param name            FlagType name
     * @param description     FlagType description
     * @param accessLevelCode InformationAccessLevel code
     * @return new FlagType instance
     */
    FlagType createFlagType(String code, String name, String description, String accessLevelCode);

    /**
     * Persists FlagType instance in the persistent store.
     *
     * @param flagType FlagType instance to persist
     * @return FlagType ID
     */
    Long persistFlagType(FlagType flagType);

    /**
     * Removes FlagType entity from the persistent store.
     *
     * @param flagTypeId FlagType ID
     * @return true if FlagType entity has been deleted
     */
    boolean deleteFlagType(Long flagTypeId);

    /**
     * Retrieves FlagType entity from the persistent store by ID.
     *
     * @param flagTypeId FlagType ID
     * @return FlagType instance
     */
    FlagType getFlagType(Long flagTypeId);

    /**
     * Retrieves FlagType entity from the persistent store by code.
     *
     * @param flagTypeCode FlagType code
     * @return FlagType instance
     */
    FlagType getFlagType(String flagTypeCode);

    /**
     * Returns all existing flag types from the persistent store.
     *
     * @return list of FlagType instances
     */
    List<FlagType> getFlagTypes();

    /**
     * Returns the default Memo's InformationAccessLevel code.
     *
     * @return InformationAccessLevel code
     */
    String getDefaultMemoLevel();

    /**
     * Associates the information represented by Information ID with the given Account ID
     *
     * @param informationId Information ID
     * @param accountId     Account ID
     * @return an updated instance of Information
     */
    Information associateWithAccount(Long informationId, String accountId);

    /**
     * Associates the information represented by Information ID with the Transaction specified by ID
     *
     * @param informationId Information ID
     * @param transactionId Transaction ID
     * @return an updated instance of Information
     */
    Information associateWithTransaction(Long informationId, Long transactionId);

    /**
     * Returns InformationAccessLevel instance by code.
     *
     * @param code InformationAccessLevel code
     * @return InformationAccessLevel instance
     */
    InformationAccessLevel getInformationAccessLevel(String code);

    /**
     * Returns InformationAccessLevel instance by ID.
     *
     * @param id InformationAccessLevel ID
     * @return InformationAccessLevel instance
     */
    InformationAccessLevel getInformationAccessLevel(Long id);

    /**
     * Persists InformationAccessLevel instance in the persistent store.
     *
     * @param accessLevel InformationAccessLevel instance to persist
     * @return InformationAccessLevel ID
     */
    Long persistInformationAccessLevel(InformationAccessLevel accessLevel);

    /**
     * Creates and persists a new InformationAccessLevel instance.
     *
     * @param code             InformationAccessLevel code
     * @param name             InformationAccessLevel name
     * @param description      InformationAccessLevel description
     * @param createPermission InformationAccessLevel Create permission
     * @param readPermission   InformationAccessLevel Read permission
     * @param updatePermission InformationAccessLevel Update permission
     * @param deletePermission InformationAccessLevel Delete permission
     * @param expirePermission InformationAccessLevel Expire permission
     * @return InformationAccessLevel instance
     */
    InformationAccessLevel createInformationAccessLevel(String code,
                                                        String name,
                                                        String description,
                                                        String createPermission,
                                                        String readPermission,
                                                        String updatePermission,
                                                        String deletePermission,
                                                        String expirePermission);

    /**
     * Creates and persists a new InformationAccessLevel instance.
     *
     * @param code        InformationAccessLevel code
     * @param name        InformationAccessLevel name
     * @param description InformationAccessLevel description
     * @return InformationAccessLevel instance
     */
    InformationAccessLevel createMemoAccessLevel(String code, String name, String description);

    /**
     * Creates and persists a new InformationAccessLevel instance.
     *
     * @param code        InformationAccessLevel code
     * @param name        InformationAccessLevel name
     * @param description InformationAccessLevel description
     * @return InformationAccessLevel instance
     */
    InformationAccessLevel createAlertAccessLevel(String code, String name, String description);

    /**
     * Creates and persists a new InformationAccessLevel instance.
     *
     * @param code        InformationAccessLevel code
     * @param name        InformationAccessLevel name
     * @param description InformationAccessLevel description
     * @return InformationAccessLevel instance
     */
    InformationAccessLevel createFlagAccessLevel(String code, String name, String description);

    /**
     * Removes InformationAccessLevel entity from the persistent store by ID.
     *
     * @param id InformationAccessLevel ID
     * @return true if InformationAccessLevel entity has been deleted
     */
    boolean deleteInformationAccessLevel(Long id);

    /**
     * Returns the default Memo Access Level code defined in the KSA configuration.
     *
     * @return Memo Access Level code
     */
    String getDefaultMemoAccessLevelCode();

}

package com.sigmasys.kuali.ksa.service.fm;

import com.sigmasys.kuali.ksa.model.fm.*;
import com.sigmasys.kuali.ksa.service.brm.BrmContext;

import java.math.BigDecimal;


/**
 * BRM FeeManagementService.
 * <p/>
 * This interface defines the new methods used by Fee Management rules based on the FM DSL mapping.
 *
 * @author Michael Ivanov
 */

public interface BrmFeeManagementService {

    /**
     * Executes the rule-based logic to assess fees for the specified FeeManagementSession.
     *
     * @param sessionId FeeManagementSession ID
     * @return FeeManagementManifest instance
     */
    FeeManagementSession assessFees(Long sessionId);

    /**
     * Fires the rule set specified by name for the entire FeeManagementSession object.
     *
     * @param ruleSetName Rule Set name
     * @param context     BRM context
     */
    void fireSessionRuleSet(String ruleSetName, BrmContext context);

    /**
     * Fires the rule set specified by name for each FeeManagementSignup object within the current FeeManagementSession.
     *
     * @param ruleSetName Rule Set name
     * @param context     BRM context
     */
    void fireSignupRuleSet(String ruleSetName, BrmContext context);

    /**
     * Compares the value of the Account KeyPair specified by "key" to the given "value".
     *
     * @param key      KeyPair's key
     * @param value    Value to compare
     * @param operator Relational operator. For example, "==" or "!="
     * @param context  BRM context
     * @return true if the KeyPair's value satisfies the given value and relational operator, false - otherwise.
     */
    boolean compareAccountKeyPair(String key, String value, String operator, BrmContext context);

    /**
     * Compares the value of the FeeManagementSession KeyPair specified by "key" to the given "value".
     *
     * @param key      KeyPair's key
     * @param value    Value to compare
     * @param operator Relational operator. For example, "==" or "!="
     * @param context  BRM context
     * @return true if the KeyPair's value satisfies the given value and relational operator, false - otherwise.
     */
    boolean compareSessionKeyPair(String key, String value, String operator, BrmContext context);

    /**
     * Compares the value of any FeeManagementSignup KeyPair specified by "key" to the given "value".
     *
     * @param key      KeyPair's key
     * @param value    Value to compare
     * @param operator Relational operator. For example, "==" or "!="
     * @param context  BRM context
     * @return true if the KeyPair's value satisfies the given value and relational operator, false - otherwise.
     */
    boolean compareSignupKeyPair(String key, String value, String operator, BrmContext context);

    /**
     * Compares the value of any FeeManagementSignup's Rate KeyPair specified by "key" to the given "value".
     *
     * @param key      KeyPair's key
     * @param value    Value to compare
     * @param operator Relational operator. For example, "==" or "!="
     * @param context  BRM context
     * @return true if the KeyPair's value satisfies the given value and relational operator, false - otherwise.
     */
    boolean compareSignupRateKeyPair(String key, String value, String operator, BrmContext context);

    /**
     * Compares the current account type code to the given value.
     *
     * @param accountTypeCode AccountType code
     * @param operator        Relational operator. For example, "==" or "!="
     * @param context         BRM context
     * @return boolean value
     */
    boolean compareAccountType(String accountTypeCode, String operator, BrmContext context);

    /**
     * Compares the current account status type code to the given value.
     *
     * @param accountStatusCode AccountStatusType code
     * @param operator          Relational operator. For example, "==" or "!="
     * @param context           BRM context
     * @return boolean value
     */
    boolean compareAccountStatus(String accountStatusCode, String operator, BrmContext context);

    /**
     * Compares the FeeManagementSession ATP ID to the given value.
     *
     * @param atpId    ATP ID
     * @param operator Relational operator. For example, "==" or "!="
     * @param context  BRM context
     * @return boolean value
     */
    boolean compareSessionAtp(String atpId, String operator, BrmContext context);

    /**
     * Checks if the account has a flag specified by FlagType code with the given severity value.
     *
     * @param flagTypeCode FlagType code
     * @param severity     Severity value
     * @param operator     Relational operator. For example, "==" or "!="
     * @param context      BRM context
     * @return boolean value
     */
    boolean accountHasFlag(String flagTypeCode, Integer severity, String operator, BrmContext context);

    /**
     * Compares the FeeManagementSession ATP ID to the given value.
     *
     * @param holdIssueName HoldIssue name
     * @param context       BRM context
     * @return boolean value
     */
    boolean accountHasAppliedHold(String holdIssueName, BrmContext context);

    /**
     * Compares the FeeManagementSignup operation to the list of values separated by "," in one line.
     *
     * @param signupOperations List of signup operation values separated by ","
     * @param context          BRM context
     * @return boolean value
     */
    boolean compareSignupOperations(String signupOperations, BrmContext context);

    /**
     * Compares the FeeManagementSignup effective date to the given date.
     *
     * @param date     Date in "MM/dd/yyyy"format
     * @param operator Relational operator. For example, "==" or "!="
     * @param context  BRM context
     * @return boolean value
     */
    boolean compareSignupEffectiveDate(String date, String operator, BrmContext context);

    /**
     * Compares the FeeManagementSignup effective date to the signup or session ATP milestone.
     *
     * @param milestoneName Milestone name
     * @param operator      Relational operator. For example, "==" or "!="
     * @param context       BRM context
     * @return boolean value
     */
    boolean compareSignupEffectiveDateToAtpMilestone(String milestoneName, String operator, BrmContext context);

    /**
     * Compares the number of FeeManagementSignup objects to the given number.
     *
     * @param numberOfSignups  Number of FeeManagementSignup objects in the current FeeManagementSession
     * @param rateCodes        List of Rate codes separated by ","
     * @param rateTypeCodes    List of RateType codes separated by ","
     * @param signupOperations List of signup operation values separated by ","
     * @param operator         Relational operator. For example, "==" or "!="
     * @param context          BRM context
     * @return boolean value
     */
    boolean compareNumberOfSignups(int numberOfSignups, String rateCodes, String rateTypeCodes,
                                   String signupOperations, String operator, BrmContext context);

    /**
     * Compares the number of all units in the current FeeManagementSignup or FeeManagementSession to the given number.
     *
     * @param numberOfUnits    Number of units
     * @param rateCodes        List of Rate codes separated by ","
     * @param rateTypeCodes    List of RateType codes separated by ","
     * @param signupOperations List of signup operation values separated by ","
     * @param operator         Relational operator. For example, "==" or "!="
     * @param context          BRM context
     * @return boolean value
     */
    boolean compareNumberOfUnits(int numberOfUnits, String rateCodes, String rateTypeCodes,
                                 String signupOperations, String operator, BrmContext context);

    /**
     * Compares the number of taken units in the current FeeManagementSession to the given number.
     *
     * @param numberOfUnits    Number of units
     * @param rateCodes        List of Rate codes separated by ","
     * @param rateTypeCodes    List of RateType codes separated by ","
     * @param signupOperations List of signup operation values separated by ","
     * @param operator         Relational operator. For example, "==" or "!="
     * @param context          BRM context
     * @return boolean value
     */
    boolean compareNumberOfTakenUnits(int numberOfUnits, String rateCodes, String rateTypeCodes,
                                      String signupOperations, String operator, BrmContext context);

    /**
     * Compares the number of units to the number of taken units.
     *
     * @param rateCodes      List of Rate codes of all signups separated by ","
     * @param takenRateCodes List of Rate codes of taken signups separated by ","
     * @param operator       Relational operator. For example, "==" or "!="
     * @param context        BRM context
     * @return boolean value
     */
    boolean compareNumberOfTakenUnits(String rateCodes, String takenRateCodes, String operator, BrmContext context);

    /**
     * Compares the amount of all session signups to the amount of taken signups.
     *
     * @param rateCodes      List of Rate codes of all signups separated by ","
     * @param takenRateCodes List of Rate codes of taken signups separated by ","
     * @param operator       Relational operator. For example, "==" or "!="
     * @param context        BRM context
     * @return boolean value
     */
    boolean compareAmountOfTakenSignups(String rateCodes, String takenRateCodes, String operator, BrmContext context);

    /**
     * Checks if the current signup in the context is taken.
     *
     * @param context BRM context
     * @return boolean value
     */
    boolean signupIsTaken(BrmContext context);

    /**
     * Checks if the current signup in the context is complete.
     *
     * @param context BRM context
     * @return boolean value
     */
    boolean signupIsComplete(BrmContext context);

    /**
     * Checks if the signup has rates specified by the codes.
     *
     * @param rateCodes List of Rate codes separated by ","
     * @param context   BRM context
     * @return boolean value
     */
    boolean signupHasRates(String rateCodes, BrmContext context);

    /**
     * Checks if the signup has rate types specified by the codes.
     *
     * @param rateTypeCodes List of RateType codes separated by ","
     * @param context       BRM context
     * @return boolean value
     */
    boolean signupHasRateTypes(String rateTypeCodes, BrmContext context);

    /**
     * Checks if the signup has rate catalogs specified by the codes.
     *
     * @param rateCatalogCodes List of RateCatalog codes separated by ","
     * @param context          BRM context
     * @return boolean value
     */
    boolean signupHasRateCatalogs(String rateCatalogCodes, BrmContext context);

    /**
     * Checks if the signup has Offering IDs.
     *
     * @param offeringIds List of Offering IDs separated by ","
     * @param context     BRM context
     * @return boolean value
     */
    boolean signupHasOfferingIds(String offeringIds, BrmContext context);

    /**
     * Checks if the signup has Offering Types.
     *
     * @param offeringTypes List of OfferingType values by ","
     * @param context       BRM context
     * @return boolean value
     */
    boolean signupHasOfferingTypes(String offeringTypes, BrmContext context);


    // TODO

    // RHS method declarations

    /**
     * Sets an Account KeyPair specified by "key" and "value".
     *
     * @param key     KeyPair key
     * @param value   KeyPair value
     * @param context BRM context
     */
    void setAccountKeyPair(String key, String value, BrmContext context);

    /**
     * Sets a FeeManagementSession KeyPair specified by "key" and "value".
     *
     * @param key     KeyPair key
     * @param value   KeyPair value
     * @param context BRM context
     */
    void setSessionKeyPair(String key, String value, BrmContext context);

    /**
     * Sets a FeeManagementSession KeyPair specified by "key" to the unit number of signups with
     * "includedSignupOperations" minus "excludedSignupOperations"
     *
     * @param key                      KeyPair key
     * @param includedSignupOperations List of included signup operation values separated by ","
     * @param excludedSignupOperations List of excluded signup operation values separated by ","
     * @param context                  BRM context
     */
    void setSessionKeyPairToUnitNumber(String key,
                                       String includedSignupOperations,
                                       String excludedSignupOperations,
                                       BrmContext context);

    /**
     * Sets a FeeManagementSession KeyPair specified by "key" to the unit number of signups on which a boolean method
     * specified by "signupBooleanMethod" method name returns true.
     * It throws IllegalArgumentException if the boolean method does not exist or returns a non-boolean value.
     *
     * @param key                 KeyPair key
     * @param signupBooleanMethod The name of the boolean method on FeeManagementSignup class.
     * @param context             BRM context
     * @throws IllegalArgumentException
     */
    void setSessionKeyPairToUnitNumberWithSignupMethod(String key, String signupBooleanMethod, BrmContext context);

    /**
     * Sets a FeeManagementSignup KeyPair specified by "key" and "value".
     *
     * @param key     KeyPair key
     * @param value   KeyPair value
     * @param context BRM context
     */
    void setSignupKeyPair(String key, String value, BrmContext context);

    /**
     * Sets "isReviewRequired" to true or false on FeeManagementSession.
     *
     * @param isReviewRequired Boolean value
     * @param context          BRM context
     */
    void setSessionReviewRequired(boolean isReviewRequired, BrmContext context);

    /**
     * Sets "isReviewComplete" to true or false on FeeManagementSession.
     *
     * @param isReviewComplete Boolean value
     * @param context          BRM context
     */
    void setSessionReviewComplete(boolean isReviewComplete, BrmContext context);

    /**
     * Sets "isComplete" to true or false on FeeManagementSignup.
     *
     * @param isComplete Boolean value
     * @param context    BRM context
     */
    void setSignupComplete(boolean isComplete, BrmContext context);

    /**
     * Sets "isComplete" to true or false on all FeeManagementSignup objects from FeeManagementSession
     * that have certain signup operations.
     *
     * @param isComplete       Boolean value
     * @param signupOperations List of signup operation values separated by ","
     * @param context          BRM context
     */
    void setSessionSignupsComplete(boolean isComplete, String signupOperations, BrmContext context);

    /**
     * Sets "isTaken" to true or false on FeeManagementSignup.
     *
     * @param isTaken Boolean value
     * @param context BRM context
     */
    void setSignupTaken(boolean isTaken, BrmContext context);

    /**
     * Sets "isTaken" to true or false on all FeeManagementSignup objects from FeeManagementSession
     * that have certain signup operations.
     *
     * @param isTaken          Boolean value
     * @param signupOperations List of signup operation values separated by ","
     * @param context          BRM context
     */
    void setSessionSignupsTaken(boolean isTaken, String signupOperations, BrmContext context);

    /**
     * Sets "isComplete" to true or false on all preceding FeeManagementSignup objects from FeeManagementSession
     * that have certain signup operations with the same Offering ID.
     *
     * @param isComplete       Boolean value
     * @param signupOperations List of signup operation values separated by ","
     * @param context          BRM context
     */
    void setPrecedingOfferingsComplete(boolean isComplete, String signupOperations, BrmContext context);

    /**
     * Sets "isTaken" to true or false on all preceding FeeManagementSignup objects from FeeManagementSession
     * that have certain signup operations with the same Offering ID.
     *
     * @param isTaken          Boolean value
     * @param signupOperations List of signup operation values separated by ","
     * @param context          BRM context
     */
    void setPrecedingOfferingsTaken(boolean isTaken, String signupOperations, BrmContext context);

    /**
     * Removes rates from all preceding signups (offerings) based on the given parameters.
     *
     * @param rateCodes        List of rate codes separated by ","
     * @param rateTypeCodes    List of rate type codes separated by ","
     * @param rateCatalogCodes List of rate catalog codes separated by ","
     * @param context          BRM context
     */
    void removeRatesFromPrecedingOfferings(String rateCodes,
                                           String rateTypeCodes,
                                           String rateCatalogCodes,
                                           BrmContext context);

    /**
     * Removes all rates on the current signup or session based on the given parameters.
     *
     * @param rateCodes        List of rate codes separated by ","
     * @param rateTypeCodes    List of rate type codes separated by ","
     * @param rateCatalogCodes List of rate catalog codes separated by ","
     * @param context          BRM context
     */
    void removeRates(String rateCodes, String rateTypeCodes, String rateCatalogCodes, BrmContext context);

    /**
     * Adds a rate to a FeeManagementSignup(s) object from the BRM context.
     *
     * @param rateCode    Rate code
     * @param rateSubCode Rate sub-code
     */
    void addRate(String rateCode, String rateSubCode, BrmContext context);

    /**
     * Replaces rates on FeeManagementSignup(s) object with the new rate specified by code and sub-code.
     *
     * @param rateCodes      List of rate codes separated by ","
     * @param rateSubCodes   List of rate sub-codes separated by ","
     * @param newRateCode    Code of the new rate
     * @param newRateSubCode Sub-code of the new rate
     */
    void replaceRates(String rateCodes, String rateSubCodes, String newRateCode, String newRateSubCode, BrmContext context);

    /**
     * Charges rates on the signups from FeeManagementSession.
     *
     * @param rateCodes        List of rate codes separated by ","
     * @param rateSubCodes     List of rate sub-codes separated by ","
     * @param rateTypeCodes    List of rate type codes separated by ","
     * @param rateCatalogCodes List of rate catalog codes separated by ","
     * @param context          BRM context
     */
    void chargeSignupRates(String rateCodes,
                           String rateSubCodes,
                           String rateTypeCodes,
                           String rateCatalogCodes,
                           BrmContext context);

    /**
     * Charges the incidental rate on the FeeManagementSession.
     *
     * @param rateCode         List of rate codes separated by ","
     * @param rateSubCode      List of rate sub-codes separated by ","
     * @param internalChargeId Manifest internal charge ID
     * @param numberOfUnits    Number of units
     * @param amount           Manifest amount
     * @param context          BRM context
     */
    void chargeIncidentalRate(String rateCode,
                              String rateSubCode,
                              String internalChargeId,
                              int numberOfUnits,
                              BigDecimal amount,
                              BrmContext context);

    /**
     * Creates a discount against a rate that has already been charged to the manifest.
     *
     * @param rateCode1    Charge Rate code
     * @param rateSubCode1 Charge Rate sub-code
     * @param rateCode2    Discount Rate code
     * @param rateSubCode2 Discount Rate sub-code
     * @param amount       Rate amount
     * @param isPercentage Indicates whether the "amount" is a percentage or not
     * @param context      BRM context
     */
    void discountManifestRate(String rateCode1,
                              String rateSubCode1,
                              String rateCode2,
                              String rateSubCode2,
                              BigDecimal amount,
                              boolean isPercentage,
                              BrmContext context);

    /**
     * Adds tags to FM manifests filtering them by rate, rate type and catalog codes.
     *
     * @param tagCodes         List of tag codes separated by ","
     * @param rateCodes        List of rate codes separated by ","
     * @param rateTypeCodes    List of rate type codes separated by ","
     * @param rateCatalogCodes List of rate catalog codes separated by ","
     * @param context          BRM context
     */
    void addTagsToManifests(String tagCodes, String rateCodes, String rateTypeCodes, String rateCatalogCodes, BrmContext context);

    /**
     * Adds tags to FM manifests that have the specified internal charge ID.
     *
     * @param tagCodes         List of tag codes separated by ","
     * @param internalChargeId Manifest internal charge ID
     * @param context          BRM context
     */
    void addTagsToManifests(String tagCodes, String internalChargeId, BrmContext context);

    /**
     * Adds a rollup to FM manifests filtering them by rate, rate type and catalog codes.
     *
     * @param rollupCode       Rollup code
     * @param rateCodes        List of rate codes separated by ","
     * @param rateTypeCodes    List of rate type codes separated by ","
     * @param rateCatalogCodes List of rate catalog codes separated by ","
     * @param context          BRM context
     */
    void addRollupToManifests(String rollupCode, String rateCodes, String rateTypeCodes, String rateCatalogCodes, BrmContext context);

    /**
     * Adds a rollup to FM manifests that have the specified internal charge ID.
     *
     * @param rollupCode       Rollup code
     * @param internalChargeId Manifest internal charge ID
     * @param context          BRM context
     */
    void addRollupToManifests(String rollupCode, String internalChargeId, BrmContext context);

    /**
     * Sets the effective date on FM manifests filtering them by rate, rate type and catalog codes.
     *
     * @param effectiveDate    Manifest effective date
     * @param rateCodes        List of rate codes separated by ","
     * @param rateTypeCodes    List of rate type codes separated by ","
     * @param rateCatalogCodes List of rate catalog codes separated by ","
     * @param context          BRM context
     */
    void setManifestEffectiveDate(String effectiveDate, String rateCodes, String rateTypeCodes, String rateCatalogCodes, BrmContext context);

    /**
     * Sets the effective date on FM manifests that have the specified internal charge ID.
     *
     * @param effectiveDate    Manifest effective date
     * @param internalChargeId Manifest internal charge ID
     * @param context          BRM context
     */
    void setManifestEffectiveDate(String effectiveDate, String internalChargeId, BrmContext context);

    /**
     * Sets the recognition date on FM manifests filtering them by rate, rate type and catalog codes.
     *
     * @param recognitionDate  Manifest recognition date
     * @param rateCodes        List of rate codes separated by ","
     * @param rateTypeCodes    List of rate type codes separated by ","
     * @param rateCatalogCodes List of rate catalog codes separated by ","
     * @param context          BRM context
     */
    void setManifestRecognitionDate(String recognitionDate, String rateCodes, String rateTypeCodes, String rateCatalogCodes, BrmContext context);

    /**
     * Sets the recognition date on FM manifests that have the specified internal charge ID.
     *
     * @param recognitionDate  Manifest recognition date
     * @param internalChargeId Manifest internal charge ID
     * @param context          BRM context
     */
    void setManifestRecognitionDate(String recognitionDate, String internalChargeId, BrmContext context);

    /**
     * Sets the GL account ID on FM manifests filtering them by rate, rate type and catalog codes.
     *
     * @param glAccountId      Manifest GL Account ID
     * @param rateCodes        List of rate codes separated by ","
     * @param rateTypeCodes    List of rate type codes separated by ","
     * @param rateCatalogCodes List of rate catalog codes separated by ","
     * @param context          BRM context
     */
    void setManifestGlAccount(String glAccountId, String rateCodes, String rateTypeCodes, String rateCatalogCodes, BrmContext context);

    /**
     * Sets the GL account on FM manifests that have the specified internal charge ID.
     *
     * @param glAccountId      Manifest GL Account ID
     * @param internalChargeId Manifest internal charge ID
     * @param context          BRM context
     */
    void setManifestGlAccount(String glAccountId, String internalChargeId, BrmContext context);

    /**
     * Sets the amount on FM manifests filtering them by rate, rate type and catalog codes.
     *
     * @param amount           Manifest amount
     * @param rateCodes        List of rate codes separated by ","
     * @param rateTypeCodes    List of rate type codes separated by ","
     * @param rateCatalogCodes List of rate catalog codes separated by ","
     * @param context          BRM context
     */
    void setManifestAmount(BigDecimal amount, String rateCodes, String rateTypeCodes, String rateCatalogCodes, BrmContext context);

    /**
     * Sets the amount on FM manifests that have the specified internal charge ID.
     *
     * @param amount           Manifest amount
     * @param internalChargeId Manifest internal charge ID
     * @param context          BRM context
     */
    void setManifestAmount(BigDecimal amount, String internalChargeId, BrmContext context);


}

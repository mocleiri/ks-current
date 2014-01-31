package com.sigmasys.kuali.ksa.krad.form;

import java.util.Date;

/**
 * The form object behind the "Rate Rollover" page.
 * User: Sergey
 * Date: 12/4/13
 * Time: 10:45 PM
 */
public class RateRolloverForm extends AbstractViewModel {

    /**
     * Date option - Null dates.
     */
    public static final String OPTION_NULL_DATES = "nullDatesOption";

    /**
     * Date option - Auto-Advanced dates.
     */
    public static final String OPTION_AUTO_ADVANCED_DATES = "autoAdvancedDatesOption";

    /**
     * Date option - Custom dates.
     */
    public static final String OPTION_CUSTOM_DATES = "customDatesOption";

    /**
     * Value that indicates that a radio-button must be selected.
     */
    public static final String OPTION_RADIO_BUTTON_SELECTED = "1";


    /**
     * ID of an ATP to roll over from.
     */
    private String atpRolloverFrom;

    /**
     * ID of an ATP to roll over to.
     */
    private String atpRolloverTo;

    /**
     * Roll over from ATP information, such as amount of ATP.
     */
    private String atpRolloverFromInfo;

    /**
     * Roll over from ATP error message. For example, an error selecting ATPs.
     */
    private String atpRolloverFromError;

    /**
     * Selected option of Date adjustment.
     */
    private String selectedDateOption;

    /**
     * Property associated with the "Null Dates" option.
     */
    private String nullDatesOption;

    /**
     * Property associated with the "Auto Advanced Dates" option.
     */
    private String autoAdvancedDatesOption;

    /**
     * Property associated with the "Custom Dates" option.
     */
    private String customDatesOption;

    /**
     * Number of days to advance dates. Used when the "Automatic Advancement" option is selected.
     */
    private int numOfDaysToAdvance;

    /**
     * Custom Transaction Date.
     */
    private Date customTransactionDate;

    /**
     * Custom Recognition Date. Used when the "Custom Dates" option is selected.
     */
    private Date customRecognitionDate;

    /**
     * Error message resulting from pressing the Rollover Rate button.
     */
    private String rolloverErrorMessage;


    public int getNumOfDaysToAdvance() {
        return numOfDaysToAdvance;
    }

    public void setNumOfDaysToAdvance(int numOfDaysToAdvance) {
        this.numOfDaysToAdvance = numOfDaysToAdvance;
    }

    public Date getCustomTransactionDate() {
        return customTransactionDate;
    }

    public void setCustomTransactionDate(Date customTransactionDate) {
        this.customTransactionDate = customTransactionDate;
    }

    public Date getCustomRecognitionDate() {
        return customRecognitionDate;
    }

    public void setCustomRecognitionDate(Date customRecognitionDate) {
        this.customRecognitionDate = customRecognitionDate;
    }

    public String getAtpRolloverFrom() {
        return atpRolloverFrom;
    }

    public void setAtpRolloverFrom(String atpRolloverFrom) {
        this.atpRolloverFrom = atpRolloverFrom;
    }

    public String getAtpRolloverTo() {
        return atpRolloverTo;
    }

    public void setAtpRolloverTo(String atpRolloverTo) {
        this.atpRolloverTo = atpRolloverTo;
    }

    public String getAtpRolloverFromInfo() {
        return atpRolloverFromInfo;
    }

    public void setAtpRolloverFromInfo(String atpRolloverFromInfo) {
        this.atpRolloverFromInfo = atpRolloverFromInfo;
    }

    public String getRolloverErrorMessage() {
        return rolloverErrorMessage;
    }

    public void setRolloverErrorMessage(String rolloverErrorMessage) {
        this.rolloverErrorMessage = rolloverErrorMessage;
    }

    public String getSelectedDateOption() {
        return selectedDateOption;
    }

    public void setSelectedDateOption(String selectedDateOption) {
        this.selectedDateOption = selectedDateOption;
    }

    public String getNullDatesOption() {
        return nullDatesOption;
    }

    public void setNullDatesOption(String nullDatesOption) {
        this.nullDatesOption = nullDatesOption;
    }

    public String getAutoAdvancedDatesOption() {
        return autoAdvancedDatesOption;
    }

    public void setAutoAdvancedDatesOption(String autoAdvancedDatesOption) {
        this.autoAdvancedDatesOption = autoAdvancedDatesOption;
    }

    public String getCustomDatesOption() {
        return customDatesOption;
    }

    public void setCustomDatesOption(String customDatesOption) {
        this.customDatesOption = customDatesOption;
    }

    public String getAtpRolloverFromError() {
        return atpRolloverFromError;
    }

    public void setAtpRolloverFromError(String atpRolloverFromError) {
        this.atpRolloverFromError = atpRolloverFromError;
    }
}

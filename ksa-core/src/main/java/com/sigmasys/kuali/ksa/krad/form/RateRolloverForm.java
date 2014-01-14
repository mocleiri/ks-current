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
     * Selected option of Date settings.
     */
    private String dateSettingsOption;

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

    public String getDateSettingsOption() {
        return dateSettingsOption;
    }

    public void setDateSettingsOption(String dateSettingsOption) {
        this.dateSettingsOption = dateSettingsOption;
    }
}

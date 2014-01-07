package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.fm.RateAmountType;
import com.sigmasys.kuali.ksa.model.fm.RateType;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;

/**
 * A model object to store information about a single row in the RateType table.
 *
 * User: Sergey
 * Date: 12/5/13
 * Time: 10:31 PM
 */
public class RateTypeModel {

    /**
     * Date formatter for displayable dates.
     */
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * The RateType object.
     */
    private RateType rateType;

    /**
     * RateType code.
     */
    private String code;

    /**
     * RateType name.
     */
    private String name;

    /**
     * RateType description.
     */
    private String description;


    private boolean grouping;

    private RateAmountType rateAmountType;


    /**
     * Creates a new RateTypeModel without an underlying RateType.
     */
    public RateTypeModel() {
    }

    /**
     * Creates a new RateTypeModel with an underlying RateType.
     * @param rateType A RateType object.
     */
    public RateTypeModel(RateType rateType) {
        setRateType(rateType);
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;

        if (rateType != null) {
            this.code = rateType.getCode();
            this.name = rateType.getName();
            this.description = rateType.getDescription();
            this.grouping = rateType.isGrouping();
            this.rateAmountType = rateType.getRateAmountType();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the ID of the given model object. If a new object, the ID is <code>null</code>.
     * @return ID of the underlying RateType object or null if a new object.
     */
    public Long getId() {
        return (rateType != null) ? rateType.getId() : null;
    }

    /**
     * Returns the RateType auditing info in the HTML format.
     *
     * @return Auditing info as HTML.
     */
    public String getAuditingInfo() {
        return (rateType != null)
                ? String.format("<b>Created By: </b>%s<br><b>Created On: </b>%s<br><b>Last Edited By: </b>%s<br><b>Last Edited On: </b>%s",
                    StringUtils.defaultString(rateType.getCreatorId()),
                    (rateType.getCreationDate() != null) ? DATE_FORMAT.format(rateType.getCreationDate()) : "",
                    StringUtils.defaultString(rateType.getEditorId()),
                    (rateType.getLastUpdate() != null) ? DATE_FORMAT.format(rateType.getLastUpdate()) : "")
                : "";
    }

    public boolean isGrouping() {
        return grouping;
    }

    public boolean getGrouping() {
        return isGrouping();
    }

    public void setGrouping(boolean grouping) {
        this.grouping = grouping;
    }

    public String getRateAmountType() {
        return (rateAmountType != null) ? rateAmountType.getId().toString() : "";
    }

    public void setRateAmountType(String rateAmountType) {
        this.rateAmountType = EnumUtils.findById(RateAmountType.class, rateAmountType);
    }

    public RateAmountType getRateAmountTypeType() {
        return rateAmountType;
    }
}

package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.krad.util.AuditTooltipUtil;
import com.sigmasys.kuali.ksa.krad.util.HighPrecisionPercentageFormatter;
import com.sigmasys.kuali.ksa.model.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: timb
 * Date: 11/29/12
 * Time: 11:43 AM
 */
public class TransactionTypeModel implements Serializable {

    private TransactionType parentEntity;

    private List<Tag> tags;
    private String tagList;

    private String rollupText;

    private List<GlBreakdown> glBreakdowns;
    private String glBreakdownType;

    private String glBreakdownTooltip;

    public TransactionTypeModel() {
    }

    public TransactionTypeModel(TransactionType entity) {
        setTransactionType(entity);
    }

    public void setTransactionType(TransactionType entity) {
        parentEntity = entity;

        Rollup r = parentEntity.getRollup();
        if (r == null) {
            setRollupText("");
        } else {
            setRollupText(r.getDescription());
        }

        this.setGlBreakdownType("Default");
        this.setGlBreakdownTooltip("No GL Breakdown");

        if (entity instanceof CreditType) {
            this.setGlBreakdownType("None");
        }

        if (parentEntity.getTags() != null) {
            setTags(new ArrayList<Tag>(parentEntity.getTags()));
        }

    }

    public TransactionType getTransactionType() {
        return parentEntity;
    }

    public String getTagList() {
        return tagList;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;

        if (tags == null || tags.size() == 0) {
            this.tagList = "None";
            return;
        }

        StringBuilder tagList = new StringBuilder();
        boolean first = true;
        for (Tag tag : tags) {
            if (!first) {
                tagList.append(", ");
            } else {
                first = false;
            }
            tagList.append(tag.getCode());
        }

        this.tagList = tagList.toString();
    }

    //@Override
    public String getCode() {
        if (parentEntity == null || parentEntity.getCode() == null) {
            return "";
        }
        return parentEntity.getCode();
    }

    //@Override
    public void setCode(String code) {
        parentEntity.setCode(code);
    }

    //@Override
    public String getName() {
        return parentEntity.getName();
    }

    //@Override
    public void setName(String name) {
        parentEntity.setName(name);
    }

    //@Override
    public String getCreatorId() {
        return parentEntity.getCreatorId();
    }

    //@Override
    public void setCreatorId(String creatorId) {
        parentEntity.setCreatorId(creatorId);
    }

    //@Override
    public String getEditorId() {
        return parentEntity.getEditorId();
    }

    //@Override
    public void setEditorId(String editorId) {
        parentEntity.setEditorId(editorId);
    }

    //@Override
    public Date getLastUpdate() {
        return parentEntity.getLastUpdate();
    }

    //@Override
    public void setLastUpdate(Date lastUpdate) {
        parentEntity.setLastUpdate(lastUpdate);
    }

    //@Override
    public String getDescription() {
        return parentEntity.getDescription();
    }

    //@Override
    public void setDescription(String description) {
        parentEntity.setDescription(description);
    }

    //@Override
    public Date getCreationDate() {
        return parentEntity.getCreationDate();
    }

    //@Override
    public void setCreationDate(Date creationDate) {
        parentEntity.setCreationDate(creationDate);
    }

    @Override
    public String toString() {
        String str = "Auditable Entity Model parent: ";
        if (parentEntity == null) {
            return str + null;
        } else {
            return str + parentEntity.toString();
        }
    }

    //@Override
    public TransactionTypeId getId() {
        try {
            return parentEntity.getId();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            return null;
        }
    }

    public String getType() {
        if (parentEntity instanceof CreditType) {
            return "Credit";
        } else if (parentEntity instanceof DebitType) {
            return "Debit";
        } else {
            return "Unknown";
        }
    }

    public Integer getPriority() {
        return parentEntity.getPriority();
    }

    public void setPriority(Integer priority) {
        parentEntity.setPriority(priority);
    }

    public String getRollupText() {
        return rollupText;
    }

    public void setRollupText(String rollupText) {
        this.rollupText = rollupText;
    }

    public String getGlBreakdownType() {
        return glBreakdownType;
    }

    public void setGlBreakdownType(String glBreakdownType) {
        this.glBreakdownType = glBreakdownType;
    }

    public String getGlBreakdownTooltip() {
        return glBreakdownTooltip;
    }

    public void setGlBreakdownTooltip(String glBreakdownTooltip) {
        this.glBreakdownTooltip = glBreakdownTooltip;
    }

    public List<GlBreakdown> getGlBreakdowns() {
        return glBreakdowns;
    }

    public void setGlBreakdowns(List<GlBreakdown> glBreakdowns) {
        this.glBreakdowns = glBreakdowns;

        HighPrecisionPercentageFormatter percentageFormatter = new HighPrecisionPercentageFormatter();

        this.setGlBreakdownType("Default");
        String firstType = null;

        String tooltip = "";

        for (GlBreakdown breakdown : glBreakdowns) {
            String type = breakdown.getGeneralLedgerType().getDescription();
            if (firstType == null) {
                firstType = type;
                tooltip += "<h4>" + type + "</h4>";
            } else if (!firstType.equals(type)) {
                this.setGlBreakdownType("Complex");
                tooltip += "<h4>" + type + "</h4>";
            }
            String account = breakdown.getGlAccount();
            String operation = breakdown.getGlOperation().name();

            String percent;
            BigDecimal amount = breakdown.getBreakdown();
            if (amount.equals(BigDecimal.ZERO)) {
                percent = "Remainder";
            } else {
                percent = (String) percentageFormatter.format(amount);
            }

            tooltip += account + ", " + operation + ", " + percent + "<br/>";


        }

        this.setGlBreakdownTooltip(tooltip);
    }

    public String getAuditTooltip() {
        return AuditTooltipUtil.getAuditTooltip(this);
    }
}

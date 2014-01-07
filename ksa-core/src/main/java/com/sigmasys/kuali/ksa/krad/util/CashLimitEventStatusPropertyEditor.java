package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.model.CashLimitEventStatus;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyEditorSupport;

/**
 * Handles conversion of Strings into the CashLimitEventStatus enumeration types.
 *
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 11/10/13
 * Time: 4:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class CashLimitEventStatusPropertyEditor extends PropertyEditorSupport {

    private static final Log logger = LogFactory.getLog(TransactionStatusPropertyEditor.class);


    @Override
    public void setAsText(String text) throws IllegalArgumentException {

        // Find the enumeration element by its textual representation:
        CashLimitEventStatus status = null;

        if (StringUtils.isNotBlank(text)) {
            if ("Q".equals(text)) {
                status = CashLimitEventStatus.QUEUED;
            } else if ("I".equals(text)) {
                status = CashLimitEventStatus.IGNORED;
            } else if ("C".equals(text)) {
                status = CashLimitEventStatus.COMPLETED;
            }
        }

        setValue(status);
    }

    @Override
    public String getAsText() {
        return (getValue() != null) ? ((CashLimitEventStatus)getValue()).getId() : "";
    }
}

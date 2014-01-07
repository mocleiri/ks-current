package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.model.TransactionStatus;
import com.sigmasys.kuali.ksa.util.EnumUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.PropertyEditorSupport;

/**
 * Handles conversion of Strings into the TransactionStatus enumeration types.
 *
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 11/5/13
 * Time: 2:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class TransactionStatusPropertyEditor extends PropertyEditorSupport {

    private static final Log logger = LogFactory.getLog(TransactionStatusPropertyEditor.class);


    @Override
    public void setAsText(String text) throws IllegalArgumentException {

        // Find the enumeration element by its textual representation:
        TransactionStatus transactionStatus = null;

        try {
            transactionStatus = TransactionStatus.valueOf(text);
        } catch (Exception e) {
            logger.error(String.format("Cannot convert value %s into TransactionStatus", text));
        }

        setValue(transactionStatus);
    }
}

package com.sigmasys.kuali.ksa.krad.util;

import java.beans.PropertyEditorSupport;

/**
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 11/4/13
 * Time: 2:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class MultipartFilePropertyEditor extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(null);
    }
}

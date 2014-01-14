package com.sigmasys.kuali.ksa.krad.model;

import java.io.Serializable;

/**
 * Represents a single ATP.
 *
 * Created with IntelliJ IDEA.
 * User: Sergey
 * Date: 1/14/14
 * Time: 2:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class AtpModel implements Serializable {

    /**
     * ID of this ATP.
     */
    private String atpId;


    public String getAtpId() {
        return atpId;
    }

    public void setAtpId(String atpId) {
        this.atpId = atpId;
    }
}

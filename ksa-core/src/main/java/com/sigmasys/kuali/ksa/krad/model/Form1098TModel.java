package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.model.Irs1098T;

import java.io.Serializable;

/**
 * Form1098TModel is used by KRAD components.
 *
 * @author  Sergey
 */
public class Form1098TModel implements Serializable {

    /**
     * An instance of the <code>Irs1098T</code> class.
     */
    private Irs1098T form1098T;

    /**
     * Is it the final revision for the tax year?
     */
    private boolean finalRevision;


    public boolean getFinalRevision() {
        return finalRevision;
    }

    public boolean isFinalRevision() {
        return finalRevision;
    }

    public void setFinalRevision(boolean finalRevision) {
        this.finalRevision = finalRevision;
    }

    public Irs1098T getForm1098T() {
        return form1098T;
    }

    public void setForm1098T(Irs1098T form1098T) {
        this.form1098T = form1098T;
    }
}

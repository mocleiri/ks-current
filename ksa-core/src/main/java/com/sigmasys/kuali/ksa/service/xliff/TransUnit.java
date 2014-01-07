package com.sigmasys.kuali.ksa.service.xliff;

import java.io.Serializable;

/**
 * This class encapsulates an &lt;trans-unit&gt; tag content.
 *
 * @author Michael Ivanov
 */
public class TransUnit implements Serializable {

    private String id;
    private String source;
    private String target;
    private Integer maxBytes;

    /**
     * Setter for id property.
     *
     * @param id ID according to XLIFF specs
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Getter for id property.
     *
     * @return ID
     */
    public String getId() {
        return id;
    }


    /**
     * Setter for source property.
     *
     * @param param source according to XLIFF specs
     */
    public void setSource(String param) {
        this.source = param;
    }


    /**
     * Getter for source property.
     *
     * @return source content
     */
    public String getSource() {
        return source;
    }


    /**
     * Setter for target property.
     *
     * @param param target
     */
    public void setTarget(String param) {
        this.target = param;
    }


    /**
     * Getter for target property.
     *
     * @return target according to XLIFF specs
     */
    public String getTarget() {
        return target;
    }

    public Integer getMaxBytes() {
        return maxBytes;
    }

    public void setMaxBytes(Integer maxBytes) {
        this.maxBytes = maxBytes;
    }
}

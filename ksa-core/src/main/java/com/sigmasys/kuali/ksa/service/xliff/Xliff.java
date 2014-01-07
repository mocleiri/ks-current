package com.sigmasys.kuali.ksa.service.xliff;

import java.io.Serializable;
import java.util.Map;

/**
 * XLIFF model
 *
 * @author Michael Ivanov
 *         Date: 6/18/12
 */
public class Xliff implements Serializable {

    private Map<String, TransUnit> transUnits;
    private String sourceLanguage;
    private String sourceCountry;
    private String targetLanguage;
    private String targetCountry;


    public Map<String, TransUnit> getTransUnits() {
        return transUnits;
    }

    public void setTransUnits(Map<String, TransUnit> transUnits) {
        this.transUnits = transUnits;
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getSourceCountry() {
        return sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getTargetCountry() {
        return targetCountry;
    }

    public void setTargetCountry(String targetCountry) {
        this.targetCountry = targetCountry;
    }

    public String getSourceLocale() {
        return (sourceLanguage != null && sourceCountry != null) ? sourceLanguage + "_" + sourceCountry : null;
    }

    public String getTargetLocale() {
        return (targetLanguage != null && targetCountry != null) ? targetLanguage + "_" + targetCountry : null;
    }
}

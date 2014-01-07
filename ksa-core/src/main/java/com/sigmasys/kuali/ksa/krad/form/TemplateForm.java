package com.sigmasys.kuali.ksa.krad.form;


import com.sigmasys.kuali.ksa.model.Constants;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * A template that can be used as an example to create other forms.
 *
 * @author Michael Ivanov
 */
public class TemplateForm extends AbstractViewModel {

    public static class StringModel {

        private String id;
        private String value;

        public StringModel() {}

        public StringModel(String id, String value) {
            this.id = id;
            this.value = value;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    private List<String> stringValues;
    private List<Integer> integerValues;
    private List<Long> longValues;
    private List<Double> doubleValues;
    private List<StringModel> stringModels;

    private int intValue;
    private long longValue;
    private double doubleValue;
    private String stringValue;
    private Date dateValue;


    public TemplateForm() {

        try {

            stringValues = Arrays.asList("Value 1", "Value 2", "Value 3", "Value 4");
            integerValues = Arrays.asList(0, 1, 2, 3, 60000000);
            longValues = Arrays.asList(0L, 1L, 2L, 3L, 60000000L);
            doubleValues = Arrays.asList(0.345, 1000.01, 20.45, 31.34567, 10e9);
            intValue = 245;
            longValue = 5790000L;
            doubleValue = 590.99;
            stringValue = "String Value";

            dateValue = new SimpleDateFormat(Constants.DATE_FORMAT_US).parse("09/23/1999");

            stringModels = new ArrayList<StringModel>();
            stringModels.add(new StringModel("id1", "value1"));
            stringModels.add(new StringModel("id2", "value2"));
            stringModels.add(new StringModel("id3", "value3"));
            stringModels.add(new StringModel("id4", "value4"));


        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<String> getStringValues() {
        return stringValues;
    }

    public void setStringValues(List<String> stringValues) {
        this.stringValues = stringValues;
    }

    public List<Integer> getIntegerValues() {
        return integerValues;
    }

    public void setIntegerValues(List<Integer> integerValues) {
        this.integerValues = integerValues;
    }

    public List<Long> getLongValues() {
        return longValues;
    }

    public void setLongValues(List<Long> longValues) {
        this.longValues = longValues;
    }

    public List<Double> getDoubleValues() {
        return doubleValues;
    }

    public void setDoubleValues(List<Double> doubleValues) {
        this.doubleValues = doubleValues;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Date getDateValue() {
        return dateValue;
    }

    public void setDateValue(Date dateValue) {
        this.dateValue = dateValue;
    }

    public String getFormattedDate() {
        if (dateValue != null) {
            DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_US);
            return dateFormat.format(dateValue);
        }
        return "";
    }

    public List<StringModel> getStringModels() {
        return stringModels;
    }

    public void setStringModels(List<StringModel> stringModels) {
        this.stringModels = stringModels;
    }
}

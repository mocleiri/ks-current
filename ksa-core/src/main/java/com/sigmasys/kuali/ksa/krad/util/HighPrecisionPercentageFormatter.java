package com.sigmasys.kuali.ksa.krad.util;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.FormatException;
import org.kuali.rice.core.web.format.Formatter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class HighPrecisionPercentageFormatter extends Formatter {

    private static final Log logger = LogFactory.getLog(HighPrecisionPercentageFormatter.class);

    /**
     * The default scale for percentage values
     */
    public final static int PERCENTAGE_SCALE = 10;

    /**
     * Unformats its argument and returns a BigDecimal instance initialized with the resulting string value
     *
     * @return a BigDecimal initialized with the provided string
     */
    public Object convertToObject(String target) {

        try {

            NumberFormat percentFormat = NumberFormat.getPercentInstance();
            percentFormat.setMaximumFractionDigits(PERCENTAGE_SCALE);


            Number parsedNumber = percentFormat.parse(target.trim());
            return new BigDecimal(parsedNumber.doubleValue());
        }
        catch (NumberFormatException e) {
            logger.error("NumberFormatException: ", e);
            throw new FormatException("parsing", RiceKeyConstants.ERROR_PERCENTAGE, target, e);
        }
        catch (ParseException e) {
            // If they didn't include the % then try it as a number
            NumberFormat decimalFormat = NumberFormat.getNumberInstance();
            decimalFormat.setMaximumFractionDigits(PERCENTAGE_SCALE);


            try{
                Number parsedNumber = decimalFormat.parse(target.trim());
                // Percent Format will already divide by 100
                return new BigDecimal(parsedNumber.doubleValue() / 100);
            } catch(Exception e2){
                // ParseException would have already been caught, safe to ignore here
                logger.error("NumberFormatException: ", e2);
                throw new FormatException("parsing", RiceKeyConstants.ERROR_PERCENTAGE, target, e2);
            }
        }
    }

    /**
     * Returns a string representation of its argument, formatted as a percentage value.
     *
     * @return a formatted String
     */
    public Object format(Object value) {
        if (value == null)
            return "N/A";

        String stringValue = "";
        try {
            if (!(value instanceof BigDecimal)) {
                throw new FormatException("parsing", "Value not BigDecimal", value.getClass().getName(), null);
            }
            BigDecimal bigDecValue = (BigDecimal) value;
            bigDecValue = bigDecValue.setScale(PERCENTAGE_SCALE, BigDecimal.ROUND_HALF_UP);
            NumberFormat percentFormat = NumberFormat.getPercentInstance();
            percentFormat.setMaximumFractionDigits(PERCENTAGE_SCALE);
            stringValue = percentFormat.format(bigDecValue.doubleValue());
        }
        catch (IllegalArgumentException iae) {
            throw new FormatException("formatting", RiceKeyConstants.ERROR_PERCENTAGE, value.toString(), iae);
        }

        return stringValue;
    }
}

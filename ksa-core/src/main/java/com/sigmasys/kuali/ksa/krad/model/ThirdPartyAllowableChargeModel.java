package com.sigmasys.kuali.ksa.krad.model;

import com.sigmasys.kuali.ksa.krad.util.HighPrecisionPercentageFormatter;
import com.sigmasys.kuali.ksa.model.tp.ThirdPartyAllowableCharge;

import java.math.BigDecimal;

/**
 * User: tbornholtz
 * Date: 10/31/13
 */
public class ThirdPartyAllowableChargeModel extends ThirdPartyAllowableCharge {
    // Theoretically it is possible to get KRAD to validate the breakdown without
    // converting it to a string here and to use this formatter directly in the XML view
    // This current implementation is more complicated than it needs to be but the hope is
    // that this whole class can go away and use the facilities of KRAD directly.
    private HighPrecisionPercentageFormatter formatter = new HighPrecisionPercentageFormatter();


    public String getMaxPercentageString() {
        BigDecimal maxPercentage = getMaxPercentage();
        if(maxPercentage == null) {
            return "";
        }
        return (String)formatter.format(getMaxPercentage());
    }

    public void setMaxPercentageString(String maxPercentageString) {
        if(maxPercentageString != null && maxPercentageString.trim().length() > 0) {
            Object percent;
            percent = formatter.convertToObject(maxPercentageString);
            setMaxPercentage((BigDecimal)percent);
        }
    }


}

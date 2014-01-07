package com.sigmasys.kuali.ksa.krad.util;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: dmulderink
 * Date: 4/21/12
 * Time: 2:02 PM
 */
public class TransactionZeroBalanceKeyValues extends KeyValuesBase {

    private boolean blankOption;

    private List<Date> zeroBalance;

    @Override
    public List<KeyValue> getKeyValues() {

        List<KeyValue> keyValues = new LinkedList<KeyValue>();

        if (blankOption) {
            keyValues.add(new ConcreteKeyValue("", ""));
        }

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


        GregorianCalendar allTransactionsDate = new GregorianCalendar(1900, Calendar.JANUARY, 1);
        String zeroString = df.format(allTransactionsDate.getTime());

        keyValues.add(new ConcreteKeyValue(zeroString, "All Transactions"));
        if (this.zeroBalance == null) {
            return keyValues;
        }

        for (Date zero : zeroBalance) {
            if (zero != null) {
                zeroString = df.format(zero);
                keyValues.add(new ConcreteKeyValue(zeroString, "Zero Balance on " + zeroString));
            }
        }

        return keyValues;
    }

    /**
     * @return the blankOption
     */
    public boolean isBlankOption() {
        return blankOption;
    }

    /**
     * @param blankOption the blankOption to set
     */
    public void setBlankOption(boolean blankOption) {
        this.blankOption = blankOption;
    }

    public void setValues(List<Date> zeroBalanceDates) {
        this.zeroBalance = zeroBalanceDates;
    }
}

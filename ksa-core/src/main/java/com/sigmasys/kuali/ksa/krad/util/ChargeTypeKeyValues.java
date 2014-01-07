package com.sigmasys.kuali.ksa.krad.util;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import java.util.ArrayList;
import java.util.List;

/**
 * User: dmulderink
 * Date: 4/21/12
 * Time: 2:02 PM
 */
public class ChargeTypeKeyValues  extends KeyValuesBase {

   private boolean blankOption;

   /**
    * This is a fake implementation of a key value finder, normally this would make a request to
    * a database to obtain the necessary values.  Used only for testing.
    *
    * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
    */
   @Override
   public List<KeyValue> getKeyValues() {
      List<KeyValue> keyValues = new ArrayList<KeyValue>();
      if(blankOption){
         keyValues.add(new ConcreteKeyValue("", ""));
      }
      keyValues.add(new ConcreteKeyValue("0", "Auxillary Facilities Fee"));// 1214
      keyValues.add(new ConcreteKeyValue("1", "Health Fee"));              // 1206
      keyValues.add(new ConcreteKeyValue("2", "Mandatory Fees"));          // 1001
      keyValues.add(new ConcreteKeyValue("3", "MBA Tuition"));             // 1072
      keyValues.add(new ConcreteKeyValue("4", "Student Activity - GR"));   // 1213
      keyValues.add(new ConcreteKeyValue("5", "Student Activity - UG"));   // 1212
      keyValues.add(new ConcreteKeyValue("6", "Tuition"));                 // 1020

      return keyValues;
   }

   /**
    * @return the blankOption
    */
   public boolean isBlankOption() {
      return this.blankOption;
   }

   /**
    * @param blankOption the blankOption to set
    */
   public void setBlankOption(boolean blankOption) {
      this.blankOption = blankOption;
   }
}

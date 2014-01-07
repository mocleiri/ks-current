package com.sigmasys.kuali.ksa.krad.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * User: dmulderink
 * Date: 4/8/12
 * Time: 3:12 PM
 */
public class QueryRangeKeyValues extends KeyValuesBase {

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
      keyValues.add(new ConcreteKeyValue("1", "10 rows"));
      keyValues.add(new ConcreteKeyValue("2", "20 rows"));
      keyValues.add(new ConcreteKeyValue("3", "30 rows"));
      keyValues.add(new ConcreteKeyValue("4", "40 rows"));
      keyValues.add(new ConcreteKeyValue("5", "50 rows"));

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



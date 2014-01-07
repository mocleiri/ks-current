package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.model.InformationTypeValue;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

import java.util.ArrayList;
import java.util.List;

/**
 * User: dmulderink
 * Date: 4/19/12
 * Time: 10:01 AM
 */
public class InformationTypeKeyValues extends KeyValuesBase {

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
      keyValues.add(new ConcreteKeyValue(InformationTypeValue.ALERT_CODE, InformationTypeValue.ALERT.toString()));
      keyValues.add(new ConcreteKeyValue(InformationTypeValue.FLAG_CODE, InformationTypeValue.FLAG.toString()));
      keyValues.add(new ConcreteKeyValue(InformationTypeValue.MEMO_CODE, InformationTypeValue.MEMO.toString()));

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

package com.sigmasys.kuali.ksa.krad.util;

import com.sigmasys.kuali.ksa.model.AccountTypeValue;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class AccountTypeKeyValuesFinder extends GenericKeyValuesFinder {

	@Override
	protected List<KeyValue> buildKeyValues() {
		// Add all AccountTypeValue enum elements with translations:
		List<KeyValue> result = new ArrayList<KeyValue>();
		
		result.add(new ConcreteKeyValue(AccountTypeValue.DIRECT_CHARGE_CODE, AccountTypeValue.DIRECT_CHARGE.toString()));
        result.add(new ConcreteKeyValue(AccountTypeValue.DELEGATE_CODE, AccountTypeValue.DELEGATE.toString()));
        result.add(new ConcreteKeyValue(AccountTypeValue.THIRD_PARTY_CODE, AccountTypeValue.THIRD_PARTY.toString()));
        result.add(new ConcreteKeyValue(AccountTypeValue.COLLECTION_AGENCY_CODE, AccountTypeValue.COLLECTION_AGENCY.toString()));
		
		return result;
	}

}

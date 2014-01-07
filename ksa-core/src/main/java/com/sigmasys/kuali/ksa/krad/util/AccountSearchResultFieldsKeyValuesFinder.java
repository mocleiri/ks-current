package com.sigmasys.kuali.ksa.krad.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

@SuppressWarnings("serial")
public class AccountSearchResultFieldsKeyValuesFinder extends KeyValuesBase {

	@Override
	public List<KeyValue> getKeyValues() {
		// Add all Account Search Result fields:
		List<KeyValue> result = new ArrayList<KeyValue>();
		
		result.add(new ConcreteKeyValue("Date of Birth", "Date of Birth"));
		result.add(new ConcreteKeyValue("Identity Type", "Identity Type"));
		result.add(new ConcreteKeyValue("Identity Serial", "Identity Serial"));
		result.add(new ConcreteKeyValue("Identity Issuer", "Identity Issuer"));
		result.add(new ConcreteKeyValue("Name Type", "Name Type"));
		result.add(new ConcreteKeyValue("First Name", "First Name"));
		result.add(new ConcreteKeyValue("Middle Name", "Middle Name"));
		result.add(new ConcreteKeyValue("Name Suffix", "Name Suffix"));
		result.add(new ConcreteKeyValue("Name Title", "Name Title"));
		result.add(new ConcreteKeyValue("Address Type", "Address Type"));
		result.add(new ConcreteKeyValue("Address Line 1", "Address Line 1"));
		result.add(new ConcreteKeyValue("Address Line 2", "Address Line 2"));
		result.add(new ConcreteKeyValue("Address Line 3", "Address Line 3"));
		result.add(new ConcreteKeyValue("Address City", "Address City"));
		result.add(new ConcreteKeyValue("Address State Code", "Address State Code"));
		result.add(new ConcreteKeyValue("Address Postal Code", "Address Postal Code"));
		result.add(new ConcreteKeyValue("Address Country Code", "Address Country Code"));
		result.add(new ConcreteKeyValue("Email Address Type", "Email Address Type"));
		result.add(new ConcreteKeyValue("Email Address", "Email Address"));
		result.add(new ConcreteKeyValue("Phone Type", "Phone Type"));
		result.add(new ConcreteKeyValue("Phone Country", "Phone Country"));
		result.add(new ConcreteKeyValue("Phone Number", "Phone Number"));
		result.add(new ConcreteKeyValue("Phone Extension", "Phone Extension"));
		result.add(new ConcreteKeyValue("Account Status", "Account Status"));
		result.add(new ConcreteKeyValue("Account Type", "Account Type"));
		result.add(new ConcreteKeyValue("Credit Limit", "Credit Limit"));
		result.add(new ConcreteKeyValue("Bank Type", "Bank Type"));
		result.add(new ConcreteKeyValue("Bank Detail", "Bank Detail"));
		result.add(new ConcreteKeyValue("Tax Type", "Tax Type"));
		result.add(new ConcreteKeyValue("Tax Detail", "Tax Detail"));
		result.add(new ConcreteKeyValue("Account Creator ID", "Account Creator ID"));
		result.add(new ConcreteKeyValue("Account Editor ID", "Account Editor ID"));
		result.add(new ConcreteKeyValue("Account Creation Date", "Account Creation Date"));
		result.add(new ConcreteKeyValue("Account Last Updated", "Account Last Updated"));
		result.add(new ConcreteKeyValue("KIM Account", "KIM Account"));
		result.add(new ConcreteKeyValue("Account can Authenticate", "Account can Authenticate"));
		result.add(new ConcreteKeyValue("User preferences", "User preferences"));
		result.add(new ConcreteKeyValue("Late Period", "Late Period"));
		
		return result;
	}

}

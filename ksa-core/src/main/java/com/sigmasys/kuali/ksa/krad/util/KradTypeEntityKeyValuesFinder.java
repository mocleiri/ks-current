package com.sigmasys.kuali.ksa.krad.util;

import java.util.*;

import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.identity.CodedAttributeContract;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KRADServiceLocator;

@SuppressWarnings("all")
public class KradTypeEntityKeyValuesFinder<T extends BusinessObject> extends GenericKeyValuesFinder {

	private Class<T> type;
	
	
	public KradTypeEntityKeyValuesFinder(Class<T> type) {
		this.type = type;
	}
	
	@Override
	protected List<KeyValue> buildKeyValues() {
		// Get object of the generic type from the BusinessObjectService:
		BusinessObjectService businessObjectService = KRADServiceLocator.getBusinessObjectService();
		Collection<T> allEntities = businessObjectService.findAll(type);
		List<KeyValue> result = new ArrayList<KeyValue>();
		
		// Iterate through the entities and add KeyValue pairs:
		for (T entity : allEntities) {
			CodedAttributeContract coded = CodedAttributeContract.class.cast(entity);
			String key = coded.getCode();
			String value = coded.getName();
			ConcreteKeyValue keyValue = new ConcreteKeyValue(key, value);
			
			result.add(keyValue);
		}
		
		return result;
	}

}

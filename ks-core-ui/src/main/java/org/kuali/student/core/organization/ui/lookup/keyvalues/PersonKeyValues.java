package org.kuali.student.core.organization.ui.lookup.keyvalues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.impl.identity.PersonServiceImpl;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.student.common.util.ContextBuilder;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;

public class PersonKeyValues extends UifKeyValuesFinderBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient PersonService personService;

    public List<KeyValue> getKeyValues(ViewModel model) {
    	List<KeyValue> keyValues = new ArrayList<KeyValue>();       

        try {
        	HashMap criteria = new HashMap();
        	criteria.put("principalName", "*");
            List<Person> persons = getPersonService().findPeople(criteria);            
            for (Person person : persons) {
                ConcreteKeyValue keyValue = new ConcreteKeyValue();
                keyValue.setKey(person.getPrincipalId());
                keyValue.setValue(person.getName());
                keyValues.add(keyValue);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }       

        return keyValues;
    }

    public KeyValue getOrganizationKeyValue(String typeKey) {
        ConcreteKeyValue keyValue = new ConcreteKeyValue();

        try {
        	HashMap criteria = new HashMap();
        	criteria.put("principalName", "*");
            List<Person> persons = getPersonService().findPeople(criteria);            
            for (Person person : persons) {
                keyValue.setKey(person.getPrincipalId());
                keyValue.setValue(person.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return keyValue;
    }

    protected PersonService getPersonService() {
        if(personService == null) {
        	personService = ((PersonServiceImpl) GlobalResourceLoader.getService(new QName("personService")));
        }
        return this.personService;
    }
    private ContextInfo getContextInfo() {
    	return ContextBuilder.loadContextInfo();
	}
}
package org.kuali.student.core.organization.ui.lookup.keyvalues;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.student.common.util.ContextBuilder;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.constants.OrganizationServiceConstants;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.service.OrganizationService;

public class DepartmentNameKeyValues extends UifKeyValuesFinderBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient OrganizationService organizationService;

    public List<KeyValue> getKeyValues(ViewModel model) {
    	List<KeyValue> keyValues = new ArrayList<KeyValue>();       

        try {
        	QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
        	List<Predicate> predicates = new ArrayList<Predicate>();
        	predicates.add(PredicateFactory.equal("orgType", OrganizationServiceConstants.ORGANIZATION_DEPARTMENT_TYPE_KEY));
        	predicates.add(PredicateFactory.like("longName","*"));
        	qBuilder.setPredicates(PredicateFactory.and(predicates.toArray(new Predicate[predicates.size()])));
            List<OrgInfo> orgs = getOrganizationService().searchForOrgs(qBuilder.build(), getContextInfo());
            for (OrgInfo type : orgs) {
                ConcreteKeyValue keyValue = new ConcreteKeyValue();
                keyValue.setKey(type.getId());
                keyValue.setValue(type.getLongName());
                keyValues.add(keyValue);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }       

        return keyValues;
    }

    public KeyValue getDepartmentKeyValue(String typeKey) {
        ConcreteKeyValue keyValue = new ConcreteKeyValue();

        try {
        	QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
        	List<Predicate> predicates = new ArrayList<Predicate>();
        	predicates.add(PredicateFactory.equal("typeKey", OrganizationServiceConstants.ORGANIZATION_DEPARTMENT_TYPE_KEY));
        	predicates.add(PredicateFactory.like("longName","*"));
        	qBuilder.setPredicates(PredicateFactory.and(predicates.toArray(new Predicate[predicates.size()])));
            List<OrgInfo> orgs = getOrganizationService().searchForOrgs(qBuilder.build(), getContextInfo());
            for (OrgInfo type : orgs) {
                keyValue.setKey(type.getId());
                keyValue.setValue(type.getLongName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return keyValue;
    }

    protected OrganizationService getOrganizationService() {
        if(organizationService == null) {
        	organizationService = (OrganizationService) GlobalResourceLoader.getService(new QName(OrganizationServiceConstants.NAMESPACE, OrganizationServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return this.organizationService;
    }
    private ContextInfo getContextInfo() {
    	return ContextBuilder.loadContextInfo();
	}
}

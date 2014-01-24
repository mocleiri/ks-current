package org.kuali.student.core.organization.ui.lookup.keyvalues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.uif.control.UifKeyValuesFinderBase;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.student.common.util.ContextBuilder;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.class1.state.dto.StateInfo;
import org.kuali.student.r2.core.class1.state.service.StateService;
import org.kuali.student.r2.core.constants.OrganizationServiceConstants;
import org.kuali.student.r2.core.constants.StateServiceConstants;


public class OrgInfoStateKeyValues extends UifKeyValuesFinderBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient StateService stateService;

    @Override
    public List<KeyValue> getKeyValues(ViewModel model) {
        List<KeyValue> keyValues = new ArrayList<KeyValue>();

        try {
            List<StateInfo> states = getStateService().getStatesByLifecycle(OrganizationServiceConstants.GENERAL_ORGANIZATION_LIFECYCLE, getContextInfo());
            for (StateInfo state : states) {
                ConcreteKeyValue keyValue = new ConcreteKeyValue();
                keyValue.setKey(state.getKey());
                keyValue.setValue(state.getName());
                keyValues.add(keyValue);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return keyValues;
    }

    public KeyValue getStateKeyValue(String stateKey) {
        ConcreteKeyValue keyValue = new ConcreteKeyValue();

        try {
            List<StateInfo> states = getStateService().getStatesByLifecycle(OrganizationServiceConstants.GENERAL_ORGANIZATION_LIFECYCLE, getContextInfo());
            for (StateInfo state : states) {
                keyValue.setKey(state.getKey());
                keyValue.setValue(state.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return keyValue;
    }

    protected StateService getStateService() {
        if(stateService == null) {
            stateService = (StateService) GlobalResourceLoader.getService(new QName(StateServiceConstants.NAMESPACE, StateServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return this.stateService;
    }
    
    private ContextInfo getContextInfo() {
    	return ContextBuilder.loadContextInfo();
	}
}
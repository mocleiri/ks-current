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
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.class1.type.service.TypeService;
import org.kuali.student.r2.core.constants.OrganizationServiceConstants;
import org.kuali.student.r2.core.constants.TypeServiceConstants;

public class OrgInfoTypeKeyValues extends UifKeyValuesFinderBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private transient TypeService typeService;

    public List<KeyValue> getKeyValues(ViewModel model) {
    	List<KeyValue> keyValues = new ArrayList<KeyValue>();       

        try {
            List<TypeInfo> types = getTypeService().getTypesByRefObjectUri(OrganizationServiceConstants.REF_OBJECT_URI_ORG , getContextInfo());
            for (TypeInfo type : types) {
                ConcreteKeyValue keyValue = new ConcreteKeyValue();
                keyValue.setKey(type.getKey());
                keyValue.setValue(type.getName());
                keyValues.add(keyValue);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }       

        return keyValues;
    }

    public KeyValue getTypeKeyValue(String typeKey) {
        ConcreteKeyValue keyValue = new ConcreteKeyValue();

        try {
            List<TypeInfo> types = getTypeService().getTypesByRefObjectUri(OrganizationServiceConstants.REF_OBJECT_URI_ORG, getContextInfo());
            for (TypeInfo type : types) {
                keyValue.setKey(type.getKey());
                keyValue.setValue(type.getName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return keyValue;
    }

    protected TypeService getTypeService() {
        if(typeService == null) {
            typeService = (TypeService) GlobalResourceLoader.getService(new QName(TypeServiceConstants.NAMESPACE, TypeServiceConstants.SERVICE_NAME_LOCAL_PART));
        }
        return this.typeService;
    }
    private ContextInfo getContextInfo() {
    	return ContextBuilder.loadContextInfo();
	}
}
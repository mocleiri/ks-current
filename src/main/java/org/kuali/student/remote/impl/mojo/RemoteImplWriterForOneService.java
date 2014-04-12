/**
 * Copyright 2004-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.remote.impl.mojo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.ServiceMethodReturnValue;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.impl.ServiceContractModelPescXsdLoader;
import org.kuali.student.contract.model.util.ModelFinder;
import org.kuali.student.contract.model.validation.DictionaryValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 */
public class RemoteImplWriterForOneService {
    
    private static Logger log = LoggerFactory.getLogger(RemoteImplWriterForOneService.class);

    private ServiceContractModel model;
    private ModelFinder finder;
    private String directory;
    private String rootPackage;
    private String servKey;

    public RemoteImplWriterForOneService(ServiceContractModel model,
            String directory,
            String rootPackage,
            String servKey) {
        this.model = model;
        this.finder = new ModelFinder(model);
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.servKey = servKey;
    }

    /**
     * Write out the entire file
     * @param out
     */
    public void write() {
        List<ServiceMethod> methods = finder.getServiceMethodsInService(servKey);
        if (methods.size() == 0) {
            log.warn("No methods defined for servKey: " + servKey);
            return;
        }

        // the main servKey
        log.info("Generating remote impl and search unit test for " + servKey + " directory = " + directory);
        new RemoteImplServiceWriter(model, directory, rootPackage, servKey, methods).write();
//        new RemoteImplServiceTestWriter(model, directory, rootPackage, servKey, methods).write();
        
    }

    private Set<XmlType> getXmlTypesUsedJustByService() {
        Set<XmlType> set = new HashSet();
        for (XmlType type : model.getXmlTypes()) {
            if (type.getService().equalsIgnoreCase(servKey)) {
                if (type.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
                    set.add(type);
                }
            }
        }
        return set;
    }

    private Set<XmlType> getXmlTypesUsedByService(List<ServiceMethod> methods) {
        Set<XmlType> set = new HashSet();
        for (ServiceMethod method : methods) {
            if (method.getReturnValue() != null) {
                ServiceMethodReturnValue ret = method.getReturnValue();
                XmlType xmlType = finder.findXmlType(stripListFromType(ret.getType()));
                if (xmlType == null) {
                    throw new DictionaryValidationException("Method " + method.getService()
                            + "." + method.getName()
                            + "returns an unknown type, "
                            + ret.getType());
                }
                addTypeAndAllSubTypes(set, xmlType);
            }
            for (ServiceMethodParameter param : method.getParameters()) {
                XmlType xmlType = finder.findXmlType(stripListFromType(param.getType()));
                if (xmlType == null) {
                    throw new DictionaryValidationException("Parameter "
                            + method.getService() + "."
                            + method.getName() + "."
                            + param.getName()
                            + "has an unknown type, "
                            + param.getType());
                }
                addTypeAndAllSubTypes(set, xmlType);
            }
        }
        return set;
    }

    private void addTypeAndAllSubTypes(Set<XmlType> set, XmlType xmlType) {
        if (xmlType.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
            if (set.add(xmlType)) {
                addXmlTypesUsedByMessageStructure(set, xmlType);
            }
        }
    }

    private String stripListFromType(String type) {
        if (type.endsWith("List")) {
            type = type.substring(0, type.length() - "List".length());
        }
        return type;
    }

    private void addXmlTypesUsedByMessageStructure(Set<XmlType> set,
            XmlType xmlType) {
        ModelFinder finder = new ModelFinder(model);
        for (MessageStructure ms : finder.findMessageStructures(xmlType.getName())) {
            XmlType subType = finder.findXmlType(stripListFromType(ms.getType()));
            if (subType == null) {
                throw new DictionaryValidationException("MessageStructure field "
                        + ms.getId()
                        + " has an unknown type, "
                        + ms.getType());
            }
            addTypeAndAllSubTypes(set, subType);
        }
    }
}

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
package org.kuali.student.contract.model.impl;

import org.kuali.student.contract.model.MessageStructure;
import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.XmlType;

import java.util.List;

/**
 * This reads the supplied spreadsheet but then caches it so it doesn't have to
 * re-read it again.
 * @author nwright
 */
public class ServiceContractModelCache implements ServiceContractModel {

    private ServiceContractModel model;

    public ServiceContractModelCache(ServiceContractModel model) {
        this.model = model;
    }
    private List<ServiceMethod> serviceMethods = null;

    @Override
    public List<ServiceMethod> getServiceMethods() {
        if (serviceMethods == null) {
            serviceMethods = model.getServiceMethods();
        }
        return serviceMethods;
    }
    private List<XmlType> xmlTypes;

    @Override
    public List<XmlType> getXmlTypes() {
        if (xmlTypes == null) {
            xmlTypes = model.getXmlTypes();
        }
        return xmlTypes;
    }
    private List<MessageStructure> messageStructures = null;

    @Override
    public List<MessageStructure> getMessageStructures() {
        if (messageStructures == null) {
            messageStructures = model.getMessageStructures();
        }
        return messageStructures;
    }
    private List<Service> services = null;

    @Override
    public List<Service> getServices() {
        if (services == null) {
            services = model.getServices();
        }
        return services;
    }

    @Override
    public List<String> getSourceNames() {
        return model.getSourceNames();
    }

    @Override
    public String toString() {
        return "ServiceContractModelCache{" +
                "model=" + model +
                ", serviceMethods=" + serviceMethods +
                ", xmlTypes=" + xmlTypes +
                ", messageStructures=" + messageStructures +
                ", services=" + services +
                '}';
    }
}

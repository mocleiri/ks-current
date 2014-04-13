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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodError;
import org.kuali.student.contract.model.XmlType;
import org.kuali.student.contract.model.impl.ServiceContractModelPescXsdLoader;
import org.kuali.student.contract.model.util.ServicesFilter;
import org.kuali.student.contract.model.validation.DictionaryValidationException;
import org.kuali.student.contract.model.validation.ServiceContractModelValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author nwright
 */
public class RemoteImplWriter {

    private static Logger log = LoggerFactory.getLogger(RemoteImplWriter.class);

    private ServiceContractModel model;
    private String directory;
    private String rootPackage;
    public static final String DEFAULT_ROOT_PACKAGE = "org.kuali.student.service.remote.impl";
    private ServicesFilter filter;

    public RemoteImplWriter(ServiceContractModel model,
            String directory,
            String rootPackage,
            ServicesFilter filter) {
        this.model = model;
        this.directory = directory;
        this.rootPackage = rootPackage;
        this.filter = filter;
    }

    /**
     * Write out the entire file
     *
     * @param out
     */
    public void write() {
        this.validate();

        for (Service service : filterServices()) {
            new RemoteImplWriterForOneService(model, directory, rootPackage, service.getKey()).write();
            new RemoteImplServiceSpringBeanWriter(model, directory, rootPackage, service.getKey()).write();
        }
//        // the Info interfaces's
//        System.out.println("Generating common Info interfaces");
//        for (XmlType xmlType : getXmlTypesUsedByMoreThanOneByService()) {
//            System.out.println("Generating info interface for " + xmlType.getName());
//            new PureJavaInfcInfcWriter(model, directory, rootPackage, xmlType.getService(), xmlType).write();
//            new PureJavaInfcBeanWriter(model, directory, rootPackage, xmlType.getService(), xmlType).write();
//        }
//  exceptions
        // Decided to just use the exisiting exceptions that are hand crafted
        // no need to generate
//  for (ServiceMethodError error : getServiceMethodErrors ().values ())
//  {
//   System.out.println ("generating exception class: " + error.getType ());
//   new ServiceExceptionWriter (model, directory, rootPackage, error).write ();
//  }
    }

    private Set<XmlType> getXmlTypesUsedByMoreThanOneByService() {
        Set<XmlType> set = new HashSet();
        for (XmlType type : model.getXmlTypes()) {
            if (type.getService().contains(",")) {
                if (type.getPrimitive().equalsIgnoreCase(XmlType.COMPLEX)) {
                    log.info(type.getName() + "==>" + type.getService());
                    set.add(type);
                }
            }
        }
        return set;
    }

    private Map<String, ServiceMethodError> getServiceMethodErrors() {
        Map<String, ServiceMethodError> errors = new HashMap();
        for (ServiceMethod method : model.getServiceMethods()) {
            for (ServiceMethodError error : method.getErrors()) {
                errors.put(error.getType(), error);
            }
        }
        return errors;
    }

    private List<Service> filterServices() {
        if (filter == null) {
            return model.getServices();
        }
        return filter.filter(model.getServices());
    }

    private void validate() {
        Collection<String> errors
                = new ServiceContractModelValidator(model).validate();
        if (errors.size() > 0) {
            StringBuffer buf = new StringBuffer();
            buf.append(errors.size() + " errors found while validating the data.");
            int cnt = 0;
            for (String msg : errors) {
                cnt++;
                buf.append("\n");
                buf.append("*error*" + cnt + ":" + msg);
            }

            throw new DictionaryValidationException(buf.toString());
        }
    }
}

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
package org.kuali.student.contract.model.validation;

import org.kuali.student.contract.model.DictionaryModel;
import org.kuali.student.contract.model.Service;
import org.kuali.student.contract.model.ServiceContractModel;
import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodError;
import org.kuali.student.contract.model.ServiceMethodParameter;
import org.kuali.student.contract.model.util.ModelFinder;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This validates a single serviceMethodinoary entry
 * @author nwright
 */
public class ServiceMethodValidator implements ModelValidator {

    private ServiceMethod method;
    private ServiceContractModel model;

    public ServiceMethodValidator(ServiceMethod method, ServiceContractModel model) {
        this.method = method;
        this.model = model;
    }
    private Collection<String> errors;

    @Override
    public Collection<String> validate() {
        errors = new ArrayList<String>();
        basicValidation();
        for (ServiceMethodParameter param : method.getParameters()) {
            errors.addAll(new ServiceMethodParameterValidator(param, method).validate());
        }
        errors.addAll(new ServiceMethodReturnValueValidator(method.getReturnValue(), method).validate());
        for (ServiceMethodError param : method.getErrors()) {
            errors.addAll(new ServiceMethodErrorValidator(param, method).validate());
        }
        return errors;
    }

    private void basicValidation() {
        if (method.getService().equals("")) {
            addError("Service is required");
        } else {
            if (findService(method.getService()) == null) {
                addError("Service, [" + method.getService()
                        + "] could not be found in the list of services");
            }
        }
        if (method.getName().equals("")) {
            addError("Name is required");
        }
        if (method.getDescription().equals("")) {
            addError("Description is required");
        }
        if (method.getReturnValue() == null) {
            addError("Return value is required");
        }
    }

    private Service findService(String service) {
        // if we are only working with the searchModel then can't validate service
        if (!(model instanceof ServiceContractModel)) {
            return null;
        }
        return new ModelFinder(model).findService(service);
    }

    private void addError(String msg) {
        String error = "Error in service method: " + method.getService() + "."
                + method.getName() + ": " + msg;
        if (!errors.contains(error)) {
            errors.add(error);
        }
    }
}

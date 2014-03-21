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

import org.kuali.student.contract.model.ServiceMethod;
import org.kuali.student.contract.model.ServiceMethodParameter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This validates a single criteriainoary entry
 * @author nwright
 */
public class ServiceMethodParameterValidator implements ModelValidator {

    private ServiceMethodParameter parameter;
    private ServiceMethod method;

    public ServiceMethodParameterValidator(ServiceMethodParameter parameter,
            ServiceMethod method) {
        this.parameter = parameter;
        this.method = method;
    }
    private Collection<String> errors;

    @Override
    public Collection<String> validate() {
        errors = new ArrayList<String>();
        basicValidation();
        return errors;
    }

    private void basicValidation() {
    	String methodName = method.getService() + "." + method.getName();
    	
    	
        if (parameter.getName().equals("")) {
            addError(methodName + ": parameter name is required");
        }
        if (parameter.getDescription().equals("")) {
            addError(methodName + ": parameter description is required");
        }
        if (parameter.getType().equals("")) {
            addError(methodName + ": parameter Type is required");
        }
    }

    private void addError(String msg) {
        String error = "Error in parameter: " + method.getService() + "."
                + method.getName() + "(" + parameter.getName()
                + "): " + msg;
        if (!errors.contains(error)) {
            errors.add(error);
        }
    }
}

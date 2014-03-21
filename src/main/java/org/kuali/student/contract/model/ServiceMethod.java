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
package org.kuali.student.contract.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a method in the contract (service).
 * @author nwright
 */
public class ServiceMethod {

    private String service;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }
    private String name;

    /**
     * Get the value of name
     *
     * @return the value of name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String name) {
        this.name = name;
    }
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    private List<ServiceMethodParameter> parameters;

    public List<ServiceMethodParameter> getParameters() {
        if (parameters == null) {
            parameters = new ArrayList();
        }
        return parameters;
    }

    public void setParameters(List<ServiceMethodParameter> parameters) {
        this.parameters = parameters;
    }
    private ServiceMethodReturnValue returnValue;

    public ServiceMethodReturnValue getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(ServiceMethodReturnValue returnValue) {
        this.returnValue = returnValue;
    }
    private List<ServiceMethodError> errors;

    public List<ServiceMethodError> getErrors() {
        if (errors == null) {
            errors = new ArrayList();
        }
        return errors;
    }

    public void setErrors(List<ServiceMethodError> errors) {
        this.errors = errors;
    }
    

    private String implNotes;

    public String getImplNotes() {
        return implNotes;
    }

    public void setImplNotes(String implNotes) {
        this.implNotes = implNotes;
    }
    
    private boolean deprecated;

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    @Override
    public String toString() {
        return "ServiceMethod{" +
                "service='" + service + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                ", parameters=" + parameters +
                ", returnValue=" + returnValue +
                ", errors=" + errors +
                ", implNotes='" + implNotes + '\'' +
                ", deprecated=" + deprecated +
                '}';
    }
}

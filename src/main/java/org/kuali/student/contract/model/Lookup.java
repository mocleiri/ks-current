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

import org.kuali.rice.core.api.criteria.QueryByCriteria;

/**
 *
 * @author nwright
 */
public class Lookup {

    private String serviceKey;
    private String xmlTypeName;
    private String getMethod;
    private String searchMethod;
    private QueryByCriteria additionalCriteria;

    public Lookup() {
    }

    public Lookup(String serviceKey, String xmlTypeName) {
        this.serviceKey = serviceKey;
        this.xmlTypeName = xmlTypeName;
    }

    
    
    public String getServiceKey() {
        return serviceKey;
    }

    public void setServiceKey(String serviceKey) {
        this.serviceKey = serviceKey;
    }

    public String getXmlTypeName() {
        return xmlTypeName;
    }

    public void setXmlTypeName(String xmlTypeName) {
        this.xmlTypeName = xmlTypeName;
    }
    
    public String getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(String getMethod) {
        this.getMethod = getMethod;
    }

    public String getSearchMethod() {
        return searchMethod;
    }

    public void setSearchMethod(String searchMethod) {
        this.searchMethod = searchMethod;
    }

    public QueryByCriteria getAdditionalCriteria() {
        return additionalCriteria;
    }

    public void setAdditionalCriteria(QueryByCriteria additionalCriteria) {
        this.additionalCriteria = additionalCriteria;
    }
    
    
    
	@Override
	public String toString() {
		return "Lookup [lookup=" + serviceKey + "." + xmlTypeName + "]";
	}
   
    
}

/*
 * Copyright 2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.core.ui.admin.organization;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.lookup.LookupableImpl;
import org.kuali.rice.krad.web.form.LookupForm;
import org.kuali.student.common.util.ContextBuilder;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.constants.OrganizationServiceConstants;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.service.OrganizationService;


public class OrgPersonRelationInfoAdminLookupableImpl extends LookupableImpl
{
	private static final Logger LOG = Logger.getLogger(OrgPersonRelationInfoAdminLookupableImpl.class);
	private transient OrganizationService organizationService;
	private static final long serialVersionUID = 1L;

	@Override
	protected List<OrgPersonRelationInfo> getSearchResults(LookupForm lookupForm, Map<String, String> fieldValues, boolean unbounded)
	{
		QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
		List<Predicate> pList = new ArrayList<Predicate>();
		for (String fieldName : fieldValues.keySet())
		{
			String value = fieldValues.get(fieldName);
			if (value != null && !value.isEmpty())
			{
				if (fieldName.equals("maxResultsToReturn"))
				{
					qBuilder.setMaxResults (Integer.parseInt(value));
					continue;
				}
				pList.add(PredicateFactory.equal(fieldName, value));
			}
		}
		if (!pList.isEmpty())
		{
			qBuilder.setPredicates(PredicateFactory.and(pList.toArray(new Predicate[pList.size()])));
		}
		try
		{
			List<OrgPersonRelationInfo> list = this.getOrganizationService().searchForOrgPersonRelations(qBuilder.build(), getContextInfo());
			return list;
		}
		catch (Exception ex) {
		    throw new RuntimeException(ex);
		}
	}

	public void setOrganizationService(OrganizationService organizationService)
	{
		    this.organizationService = organizationService;
	}

	public OrganizationService getOrganizationService()
	{
		if (organizationService == null)
		{
			QName qname = new QName(OrganizationServiceConstants.NAMESPACE,OrganizationServiceConstants.SERVICE_NAME_LOCAL_PART);
			organizationService = (OrganizationService) GlobalResourceLoader.getService(qname);
		}
		return this.organizationService;
	}

	private ContextInfo getContextInfo() {
	    return ContextBuilder.loadContextInfo();
	}
	
	public List<OrgInfo> suggestOrganizationsByName(String orgPartialName) {
        List<OrgInfo> matchingOrgs = new ArrayList<OrgInfo>();
        QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
        try
		{
        	qBuilder.setPredicates(PredicateFactory.like("longName", orgPartialName));
        	matchingOrgs = this.getOrganizationService().searchForOrgs(qBuilder.build(), getContextInfo()); 
		}
		catch (Exception ex) {
		    throw new RuntimeException(ex);
		}
        return matchingOrgs;
    }
}

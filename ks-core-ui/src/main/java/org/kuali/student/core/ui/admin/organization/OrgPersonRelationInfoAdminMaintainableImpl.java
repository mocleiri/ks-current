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


import java.util.Locale;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.maintenance.MaintainableImpl;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.LocaleInfo;
import org.kuali.student.r2.common.util.constants.LuiServiceConstants;
import org.kuali.student.r2.core.constants.OrganizationServiceConstants;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.service.OrganizationService;


public class OrgPersonRelationInfoAdminMaintainableImpl extends MaintainableImpl
{	
    private final Logger logger = Logger.getLogger(OrgPersonRelationInfoAdminMaintainableImpl.class);
	private transient OrganizationService organizationService;
	private ContextInfo contextInfo;	
	private static final long serialVersionUID = 1L;	
	private final String DEFAULT_DOCUMENT_DESC_FOR_CREATING_ORGPERSONRELATIONINFO = "Creating OrgPersonRelationInfo";
	private final String DEFAULT_DOCUMENT_DESC_FOR_COPYING_ORGPERSONRELATIONINFO = "Copying OrgPersonRelationInfo";
	private final String DEFAULT_DOCUMENT_DESC_FOR_EDITING_ORGPERSONRELATIONINFO = "Editing OrgPersonRelationInfo";

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
	
	@Override
	 public void saveDataObject() {
	    System.out.println (">>> in save ");
	    OrgPersonRelationInfo orgPersonRelationInfoMaintenance = (OrgPersonRelationInfo) getDataObject();
	    if(getMaintenanceAction().equals(KRADConstants.MAINTENANCE_NEW_ACTION) ||
	            getMaintenanceAction().equals(KRADConstants.MAINTENANCE_COPY_ACTION)) {
	        try {
	 
	            OrgPersonRelationInfo orgPersonRelationInfoCreated = getOrganizationService().createOrgPersonRelation(orgPersonRelationInfoMaintenance.getOrgId(), orgPersonRelationInfoMaintenance.getPersonId(), orgPersonRelationInfoMaintenance.getTypeKey(), orgPersonRelationInfoMaintenance, getContextInfo());
	            //setDataObject(new OrgPersonRelationInfo(orgPersonRelationInfoCreated));
	        } catch (Exception e) {
	            logger.error("OrgPersonRelationInfoMaintenance - create new failed. ", e);
	            throw new RuntimeException("OrgPersonRelationInfoMaintenance - create new failed. ", e);
	        }
	    }
	    else {   //should be edit action
	        try {
	            OrgPersonRelationInfo orgPersonRelationInfoUpdated = getOrganizationService().updateOrgPersonRelation(orgPersonRelationInfoMaintenance.getId(), orgPersonRelationInfoMaintenance,  getContextInfo());
	        } catch (Exception e) {
	            logger.error("OrgPersonRelationInfoMaintenance - edit failed. ", e);
	            throw new RuntimeException("OrgPersonRelationInfoMaintenance - edit failed. ", e);
	        }
	    }
	}
	 
	/**
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#prepareForSave()
	 */
	@Override
	public void prepareForSave() {
	    System.out.println (">>> in prepareForSave ");
	    if (getMaintenanceAction().equalsIgnoreCase(KRADConstants.MAINTENANCE_NEW_ACTION)) {
	        OrgPersonRelationInfo orgPersonRelationInfoMaintenance = (OrgPersonRelationInfo) getDataObject();
	        //orgPersonRelationInfoMaintenance.setTypeKey(LuiServiceConstants.FORMAT_OFFERING_TYPE_KEY);
	        //orgPersonRelationInfoMaintenance.setStateKey(LuiServiceConstants.LUI_DRAFT_STATE_KEY);
	    }
	    super.prepareForSave();
	}
	@Override
	public void processAfterNew(MaintenanceDocument document, Map<String, String[]> requestParameters) {
	    //set documentDescription to document.documentHeader.documentDescription
	    document.getDocumentHeader().setDocumentDescription(DEFAULT_DOCUMENT_DESC_FOR_CREATING_ORGPERSONRELATIONINFO);
	 
	}


@Override
public Object retrieveObjectForEditOrCopy(MaintenanceDocument document, Map<String, String> dataObjectKeys) {
    try {
        OrgPersonRelationInfo info = getOrganizationService().getOrgPersonRelation(dataObjectKeys.get("id"), getContextInfo());
        return info;
    } catch (Exception e) {
        logger.error("OrgPersonRelationInfoMaintenance - edit/copy failed. ", e);
        throw new RuntimeException("OrgPersonRelationInfoMaintenance - edit/copy failed. ", e);
    }
}
 
/**
 * @see org.kuali.rice.krad.maintenance.Maintainable#processAfterCopy
 */
@Override
public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> requestParameters) {
    //set documentDescription to document.documentHeader.documentDescription
    document.getDocumentHeader().setDocumentDescription(DEFAULT_DOCUMENT_DESC_FOR_COPYING_ORGPERSONRELATIONINFO);
}
 
/**
 * @see org.kuali.rice.krad.maintenance.Maintainable#processAfterEdit
 */
@Override
public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> requestParameters) {
    //set documentDescription to document.documentHeader.documentDescription
    document.getDocumentHeader().setDocumentDescription(DEFAULT_DOCUMENT_DESC_FOR_EDITING_ORGPERSONRELATIONINFO);
 
}

public ContextInfo getContextInfo() {
    if (null == contextInfo) {
        contextInfo = new ContextInfo();
        contextInfo.setAuthenticatedPrincipalId(GlobalVariables.getUserSession().getPrincipalId());
        contextInfo.setPrincipalId(GlobalVariables.getUserSession().getPrincipalId());
        LocaleInfo localeInfo = new LocaleInfo();
        localeInfo.setLocaleLanguage(Locale.getDefault().getLanguage());
        localeInfo.setLocaleRegion(Locale.getDefault().getCountry());
        contextInfo.setLocale(localeInfo);
    }
    return contextInfo;
}
}


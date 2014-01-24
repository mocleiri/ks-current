package org.kuali.student.core.organization.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.principal.EntityNamePrincipalName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.identity.PersonServiceImpl;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.student.common.util.ContextBuilder;
import org.kuali.student.core.organization.ui.form.OrgPersonRelationInfoAdminSearchForm;
import org.kuali.student.core.organization.ui.form.model.OrgPersonRelationUIModel;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.core.class1.state.dto.StateInfo;
import org.kuali.student.r2.core.class1.state.service.StateService;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.class1.type.service.TypeService;
import org.kuali.student.r2.core.constants.OrganizationServiceConstants;
import org.kuali.student.r2.core.constants.StateServiceConstants;
import org.kuali.student.r2.core.constants.TypeServiceConstants;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.service.OrganizationService;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/orgPersonRelationInfoAdminManage")
public class OrgPersonRelationInfoAdminSearchController extends UifControllerBase {
	private static final Logger LOG = Logger.getLogger(OrgPersonRelationInfoAdminSearchController.class);
	
	private transient OrganizationService organizationService;
	private transient TypeService typeService;
	private transient StateService stateService;
	
	{   
		// Beanutils config
		java.util.Date defaultValue = null;        
		DateConverter converter = new DateConverter(defaultValue);        
		ConvertUtils.register(converter, java.util.Date.class);
        // config ends
	}
	
	List<OrgPersonRelationUIModel> results = new ArrayList<OrgPersonRelationUIModel>();
	
	protected UifFormBase createInitialForm(HttpServletRequest request) {
		return new OrgPersonRelationInfoAdminSearchForm();
	}
	
	@RequestMapping(method = RequestMethod.GET, params = "methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		OrgPersonRelationInfoAdminSearchForm orgPersonRelationInfoAdminSearchForm = (OrgPersonRelationInfoAdminSearchForm)form;
		
		return super.start(form, result, request, response);
	}
	
	@RequestMapping(params = "methodToCall=search")
	public ModelAndView search(@ModelAttribute("KualiForm") OrgPersonRelationInfoAdminSearchForm searchForm, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
        String organizationName = searchForm.getKeywordSearch();
        String personName = searchForm.getPersonName();
        
        ArrayList<Predicate> predicates = new ArrayList<Predicate>();        
        QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
        
        if(StringUtils.isNotEmpty(personName)){
        	HashMap<String,String> criteriaMap = new HashMap<String,String>();
        	
        	if(personName.contains(",")){
        		String[] personTokens = personName.split(",");
        		if(StringUtils.isNotEmpty(personTokens[0])){
        			criteriaMap.put("lastName", personTokens[0]);
        		}
        		if(StringUtils.isNotEmpty(personTokens[1])){
        			criteriaMap.put("firstName", personTokens[1]);
        		}
        	
	    		List<Person> persons = ((PersonServiceImpl) GlobalResourceLoader.getService(new QName("personService"))).findPeople(criteriaMap);
	    		
	    		if(!persons.isEmpty()){
		    		String[] personIds = new String[persons.size()];
		    		for(int index = 0 ; index < personIds.length ; index++){
		    			personIds[index] = persons.get(index).getPrincipalId();
		    		}
		    		predicates.add(PredicateFactory.in("personId",personIds));
	    		}
        	}
        }
        
        resetForm(searchForm);
        
        if (StringUtils.isNotBlank(organizationName) && !organizationName.isEmpty()) {
        	qBuilder.setPredicates(PredicateFactory.like("longName","*" + organizationName + "*"));
        	List<OrgInfo> orgInfos = this.getOrganizationService().searchForOrgs(qBuilder.build(), getContextInfo());
        	LOG.info("Organizations found : " + orgInfos.size());
        	if(!orgInfos.isEmpty()){
	    		String[] orgIds = new String[orgInfos.size()];
	    		for(int index = 0 ; index < orgIds.length ; index++){
	    			orgIds[index] = orgInfos.get(index).getId();
	    		}
	    		predicates.add(PredicateFactory.in("orgId",orgIds));
        	} else {
        		LOG.info("No organizations found.");
        		return getUIFModelAndView(searchForm, null);
        	}
        } else {
        	predicates.add(PredicateFactory.like("orgId","*"));
        }
        
        if (!predicates.isEmpty())
		{
			qBuilder.setPredicates(PredicateFactory.and(predicates.toArray(new Predicate[predicates.size()])));
		}
        
        try {
        	
        	if(!searchForm.getResultSize().equalsIgnoreCase("All")){
        		qBuilder.setMaxResults (Integer.parseInt(searchForm.getResultSize()));
        	} 
            
        	LOG.info("Predicates : " + predicates);
        	
        	List<OrgPersonRelationInfo> relations = this.getOrganizationService().searchForOrgPersonRelations(qBuilder.build(), getContextInfo());
            
        	LOG.info("Relations found : " + relations.size());
        	
            results.clear();
            
            if (!CollectionUtils.isEmpty(relations)) {
	            for (OrgPersonRelationInfo orgPersonRelationInfo : relations) {
	            	
	            	OrgPersonRelationUIModel orgPersonUIModel = new OrgPersonRelationUIModel();
	            	
	            	BeanUtils.copyProperties(orgPersonUIModel, orgPersonRelationInfo);
	                
	            	this.setOtherInfo(orgPersonUIModel);
	            	
	            	results.add(orgPersonUIModel);
				}
            } else {
            	GlobalVariables.getMessageMap().putInfo("KS-OrgPersonRelationSearch-CriteriaSection","noSearchResults");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error Performing Search",e); //To change body of catch statement use File | Settings | File Templates.
        }
       
       searchForm.setOrgPersonRelationUIModel((ArrayList<OrgPersonRelationUIModel>) results);
 
       return getUIFModelAndView(searchForm, null);
	}
	@RequestMapping(params = "methodToCall=view")
	public ModelAndView view(@ModelAttribute("KualiForm") OrgPersonRelationInfoAdminSearchForm viewForm, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String orgPersonRelationId = viewForm.getActionParamaterValue("orgPersonRelationId");
		
		LOG.info("orgPersonRelationId " + orgPersonRelationId);
		
		LOG.info("Number of form contained relation : " + results.size());
		for(OrgPersonRelationUIModel relation : results){			
			if(orgPersonRelationId.equals(relation.getId())){
				LOG.info("match found");
				viewForm.setOrgPersonRelation(relation);
				break;
			}
		}
		return getUIFModelAndView(viewForm, "KS-OrgPersonRelationInfoDetail-View");
	}
	
	@RequestMapping(params = "methodToCall=cancel")
	public ModelAndView cancel(@ModelAttribute("KualiForm") UifFormBase uifForm, BindingResult result, HttpServletRequest request, HttpServletResponse response) {		
		OrgPersonRelationInfoAdminSearchForm viewForm = (OrgPersonRelationInfoAdminSearchForm)uifForm;
		String returnViewName = viewForm.getActionParamaterValue("returnViewName");
		return getUIFModelAndView(viewForm, returnViewName);
	}
	
	@RequestMapping(params = "methodToCall=create")
	public ModelAndView create(@ModelAttribute("KualiForm") OrgPersonRelationInfoAdminSearchForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		form.setOrgPersonRelation(new OrgPersonRelationUIModel());
		return getUIFModelAndView(form,"KS-OrgPersonRelationInfoCreate-View");
	}
	
	private void resetForm(OrgPersonRelationInfoAdminSearchForm searchForm) {
		searchForm.setOrgPersonRelationUIModel(new ArrayList<OrgPersonRelationUIModel>());
	}
	
	@RequestMapping(params = "methodToCall=save")
	public ModelAndView save(@ModelAttribute("KualiForm") OrgPersonRelationInfoAdminSearchForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		KRADServiceLocatorWeb.getViewValidationService().validateView(form);
		
		String selectedOrgPersonRelationId = form.getActionParamaterValue("orgPersonRelationId");
		
		QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
		
		if(!GlobalVariables.getMessageMap().hasErrors()){
			
			OrgPersonRelationUIModel orgPersonRelationUi = form.getOrgPersonRelation();
			
			try{
				qBuilder.setPredicates(PredicateFactory.equal("longName",orgPersonRelationUi.getOrgInfo().getLongName()));
	        	List<OrgInfo> orgInfos = this.getOrganizationService().searchForOrgs(qBuilder.build(), getContextInfo());
	        	orgPersonRelationUi.setOrgId(orgInfos.get(0).getId());
	        	orgPersonRelationUi.setOrgInfo(orgInfos.get(0));
			}catch (Exception e){
				GlobalVariables.getMessageMap().putError("KS-OrgPersonRelationInfoCreate-Section","unknownError",e.getMessage());
				return getUIFModelAndView(form);
			}
					
			try{
				OrgPersonRelationInfo orgPersonRelation = getOrganizationService().createOrgPersonRelation(
									orgPersonRelationUi.getOrgId(), orgPersonRelationUi.getPersonId(), 
									orgPersonRelationUi.getTypeKey(), orgPersonRelationUi, getContextInfo());
				
				resetForm(form);
				
				BeanUtils.copyProperties(orgPersonRelationUi, orgPersonRelation);
				this.setOtherInfo(orgPersonRelationUi);
				form.setOrgPersonRelation(orgPersonRelationUi);
				
				GlobalVariables.getMessageMap().putInfo("KS-OrgPersonRelationInfoDetail-Section","databaseChnageSuccess");
				
				return getUIFModelAndView(form, "KS-OrgPersonRelationInfoDetail-View");
			}catch (Exception e){
				e.printStackTrace();
				GlobalVariables.getMessageMap().putError("KS-OrgPersonRelationInfoCreate-Section","unknownError",e.getMessage());
				return getUIFModelAndView(form);
			}	
		} 
		
		return getUIFModelAndView(form);		
	}
	
	@RequestMapping(params = "methodToCall=update")
	public ModelAndView update(@ModelAttribute("KualiForm") OrgPersonRelationInfoAdminSearchForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
		KRADServiceLocatorWeb.getViewValidationService().validateView(form);
		
		String selectedOrgPersonRelationId = form.getActionParamaterValue("orgPersonRelationId");
		
		QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
		
		if(!GlobalVariables.getMessageMap().hasErrors()){
			
			OrgPersonRelationUIModel orgPersonRelationUi = form.getOrgPersonRelation();
			
			try {
				
				OrgPersonRelationInfo orgPersonRelation = getOrganizationService().updateOrgPersonRelation(orgPersonRelationUi.getId(), orgPersonRelationUi, getContextInfo());
				
				resetForm(form);
				
				BeanUtils.copyProperties(orgPersonRelationUi, orgPersonRelation);
				this.setOtherInfo(orgPersonRelationUi);
				LOG.info("After update relation is : " + orgPersonRelationUi.getOrgInfo().getLongName());
				form.setOrgPersonRelation(orgPersonRelationUi);
				
				GlobalVariables.getMessageMap().putInfo("KS-OrgPersonRelationInfoDetail-Section","databaseChnageSuccess");
				
				return getUIFModelAndView(form, "KS-OrgPersonRelationInfoDetail-View");
				
			} catch(Exception e){
				e.printStackTrace();
				GlobalVariables.getMessageMap().putError("KS-OrgPersonRelationInfoEdit-Section","unknownError",e.getMessage());
				return getUIFModelAndView(form);
			} 
		} 
		
		return getUIFModelAndView(form);
	}
	
	@RequestMapping(params = "methodToCall=delete")
	public ModelAndView delete(@ModelAttribute("KualiForm") OrgPersonRelationInfoAdminSearchForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String selectedOrgPersonRelationId = form.getActionParamaterValue("orgPersonRelationId");
		
		try {
			this.getOrganizationService().deleteOrgPersonRelation(selectedOrgPersonRelationId, getContextInfo());
			
			resetForm(form);
			
			GlobalVariables.getMessageMap().putInfo("KS-OrgPersonRelationSearch-CriteriaSection","databaseChnageSuccess");
			
			return getUIFModelAndView(form, "KS-OrgPersonRelationInfoSearch-View");
		} catch(Exception e){
			GlobalVariables.getMessageMap().putError("KS-OrgPersonRelationInfoDetail-Section","unknownError",e.getMessage());				
		}
		return getUIFModelAndView(form);		
	}
	
	@RequestMapping(params = "methodToCall=copy")
	public ModelAndView copy(@ModelAttribute("KualiForm") OrgPersonRelationInfoAdminSearchForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String selectedOrgPersonRelationId = form.getActionParamaterValue("orgPersonRelationId");
		
		OrgPersonRelationUIModel orgPersonRelationUi = form.getOrgPersonRelation();
		
		orgPersonRelationUi.setId(null);
		
		return getUIFModelAndView(form, "KS-OrgPersonRelationInfoCreate-View");
	}
	
	private void setOtherInfo(OrgPersonRelationUIModel orgPersonRelation) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
		
		EntityNamePrincipalName principalName = KimApiServiceLocator.getIdentityService().getDefaultNamesForPrincipalId(orgPersonRelation.getPersonId());
		String personName = (principalName != null  && principalName.getDefaultName() != null) ? principalName.getDefaultName().getCompositeName() : StringUtils.EMPTY;
		orgPersonRelation.setPersonName(personName);
    	
    	OrgInfo orgInfo = this.getOrganizationService().getOrg(orgPersonRelation.getOrgId(), getContextInfo());
    	orgPersonRelation.setOrgInfo(orgInfo);
    	
    	TypeInfo typeInfo = this.getTypeService().getType(orgPersonRelation.getTypeKey(), getContextInfo());
    	orgPersonRelation.setTypeInfo(typeInfo);
    	
    	StateInfo stateInfo = this.getStateService().getState(orgPersonRelation.getStateKey(), getContextInfo());
    	orgPersonRelation.setStateInfo(stateInfo);
	}
	
	private ContextInfo getContextInfo() {
    	return ContextBuilder.loadContextInfo();
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

	public TypeService getTypeService() {
		if (typeService == null)
		{
			QName qname = new QName(TypeServiceConstants.NAMESPACE,TypeServiceConstants.SERVICE_NAME_LOCAL_PART);
			typeService = (TypeService) GlobalResourceLoader.getService(qname);
		}
		return this.typeService;
	}

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	public StateService getStateService() {
		if (stateService == null)
		{
			QName qname = new QName(StateServiceConstants.NAMESPACE,StateServiceConstants.SERVICE_NAME_LOCAL_PART);
			stateService = (StateService) GlobalResourceLoader.getService(qname);
		}
		return this.stateService;
	}

	public void setStateService(StateService stateService) {
		this.stateService = stateService;
	}
	
	
}
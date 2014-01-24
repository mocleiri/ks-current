package org.kuali.student.core.organization.ui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.namespace.QName;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.springframework.util.StringUtils;
import org.apache.log4j.Logger;
import org.displaytag.util.CollectionUtil;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.student.common.util.ContextBuilder;
import org.kuali.student.core.organization.ui.form.OrgInfoAdminSearchForm;
import org.kuali.student.core.organization.ui.form.OrgPersonRelationInfoAdminSearchForm;
import org.kuali.student.core.organization.ui.form.model.OrgPersonRelationUIModel;
import org.kuali.student.core.organization.ui.form.model.OrgUIModel;
import org.kuali.student.r2.common.dto.ContextInfo;
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
@RequestMapping(value = "/orgInfoAdminManage")
public class OrgInfoAdminSearchController extends UifControllerBase {
	private static final Logger LOG = Logger.getLogger(OrgInfoAdminSearchController.class);
	
	private transient OrganizationService organizationService;
	private transient TypeService typeService;
	private transient StateService stateService;
	
	List<OrgUIModel> results = new ArrayList<OrgUIModel>();
	OrgInfo orgInfo = new OrgInfo();
	
	{   
		// Beanutils config
		java.util.Date defaultValue = null;        
		DateConverter converter = new DateConverter(defaultValue);        
		ConvertUtils.register(converter, java.util.Date.class);
        // config ends
	}
	
	protected UifFormBase createInitialForm(HttpServletRequest request) {
		return new OrgInfoAdminSearchForm();
	}
	
	@RequestMapping(method = RequestMethod.GET, params = "methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		OrgInfoAdminSearchForm orgInfoAdminSearchForm = (OrgInfoAdminSearchForm)form;
		
		return super.start(form, result, request, response);
	}
	
	@RequestMapping(params = "methodToCall=search")
	public ModelAndView search(@ModelAttribute("KualiForm") OrgInfoAdminSearchForm searchForm, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {	   
	    
		String organizationName = "*" + searchForm.getKeywordSearch() + "*";
        String typeKey = searchForm.getTypeInfoKey();
        String stateKey = searchForm.getStateInfoKey();
        
        resetForm(searchForm);
 
        ArrayList<Predicate> predicates = new ArrayList<Predicate>();        
        QueryByCriteria.Builder qBuilder = QueryByCriteria.Builder.create();
        
        if(StringUtils.hasText(organizationName)){
        	predicates.add(PredicateFactory.like("longName",organizationName));
        }
		if(StringUtils.hasText(typeKey)){
			predicates.add(PredicateFactory.like("orgType",typeKey));	
		}
		if(StringUtils.hasText(stateKey)){
			predicates.add(PredicateFactory.like("orgState",stateKey));
		}
        
        results.clear();
        
        if(!searchForm.getResultSize().equalsIgnoreCase("All")){
    		qBuilder.setMaxResults (Integer.parseInt(searchForm.getResultSize()));
    	}
        
        if (!predicates.isEmpty())
		{
			qBuilder.setPredicates(PredicateFactory.and(predicates.toArray(new Predicate[predicates.size()])));
		}
        
        List<OrgInfo> orgInfos = this.getOrganizationService().searchForOrgs(qBuilder.build(), getContextInfo());
                	
        if (!CollectionUtils.isEmpty(orgInfos)) {
           
            for (OrgInfo orgInfo : orgInfos) {

                OrgUIModel orgUIModel = new OrgUIModel();

                BeanUtils.copyProperties(orgUIModel, orgInfo);

                TypeInfo typeInfo = this.getTypeService().getType(orgInfo.getTypeKey(), getContextInfo());

                orgUIModel.setTypeInfo(typeInfo);

                StateInfo stateInfo = new StateInfo();

                stateInfo = this.getStateService().getState(orgInfo.getStateKey(), getContextInfo());

                orgUIModel.setStateInfo(stateInfo);

                results.add(orgUIModel);
            }
        } else {
        	GlobalVariables.getMessageMap().putInfo("KS-OrgInfoSearch-CriteriaSection","noSearchResults");
        }
       
       searchForm.setOrgUIModel((ArrayList<OrgUIModel>) results);
 
       return getUIFModelAndView(searchForm, null);
	}
	
	@RequestMapping(params = "methodToCall=save")
    public ModelAndView save(@ModelAttribute("KualiForm") OrgInfoAdminSearchForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
       
	    KRADServiceLocatorWeb.getViewValidationService().validateView(form);       
        
        if(!GlobalVariables.getMessageMap().hasErrors()){
            
            OrgUIModel orgUi = form.getOrgInfo();   
            if(orgUi != null) {
            	try{
	                OrgInfo org = getOrganizationService().createOrg(orgUi.getTypeKey(), orgUi, getContextInfo());
	                
	                BeanUtils.copyProperties(orgUi, org);
	                
	                TypeInfo typeInfo = this.getTypeService().getType(orgUi.getTypeKey(), getContextInfo());
	
	                orgUi.setTypeInfo(typeInfo);
	
	                StateInfo stateInfo = new StateInfo();
	
	                stateInfo = this.getStateService().getState(orgUi.getStateKey(), getContextInfo());
	
	                orgUi.setStateInfo(stateInfo);
	                
	                form.setOrgInfo(orgUi);
	                
	                GlobalVariables.getMessageMap().putInfo("KS-OrgInfoDetail-Section","databaseChnageSuccess");
					
					return getUIFModelAndView(form, "KS-OrgInfoDetail-View");
            	}catch (Exception e){
    				e.printStackTrace();
    				GlobalVariables.getMessageMap().putError("KS-OrgInfoCreate-Section","unknownError",e.getMessage());
    				return getUIFModelAndView(form);
    			}	
            }
        } else {
            return getUIFModelAndView(form);
        }
        
        return getUIFModelAndView(form);
    }
	
	@RequestMapping(params = "methodToCall=update")
    public ModelAndView update(@ModelAttribute("KualiForm") OrgInfoAdminSearchForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
       
        KRADServiceLocatorWeb.getViewValidationService().validateView(form);       
        
        if(!GlobalVariables.getMessageMap().hasErrors()){
            
            OrgUIModel orgUi = form.getOrgInfo();   
            if(orgUi != null) {
            	try{
	            	OrgInfo org = getOrganizationService().updateOrg(orgUi.getId(), orgUi, getContextInfo());
	                
	                BeanUtils.copyProperties(orgUi, org);
	                
	                TypeInfo typeInfo = this.getTypeService().getType(orgUi.getTypeKey(), getContextInfo());
	
	                orgUi.setTypeInfo(typeInfo);
	
	                StateInfo stateInfo = new StateInfo();
	
	                stateInfo = this.getStateService().getState(orgUi.getStateKey(), getContextInfo());
	
	                orgUi.setStateInfo(stateInfo);
	                
	                form.setOrgInfo(orgUi);
	                GlobalVariables.getMessageMap().putInfo("KS-OrgInfoDetail-Section","databaseChnageSuccess");
					
					return getUIFModelAndView(form, "KS-OrgInfoDetail-View");
	        	}catch (Exception e){
					e.printStackTrace();
					GlobalVariables.getMessageMap().putError("KS-OrgInfoEdit-Section","unknownError",e.getMessage());
					return getUIFModelAndView(form);
				}	
            }
        } else {
            return getUIFModelAndView(form);
        }
        return getUIFModelAndView(form);
    }
	
	@RequestMapping(params = "methodToCall=delete")
    public ModelAndView delete(@ModelAttribute("KualiForm") OrgInfoAdminSearchForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String selectedOrgId = form.getActionParamaterValue("selectedOrgId");
        try{
	        this.getOrganizationService().deleteOrg(selectedOrgId, getContextInfo());
	      
	        resetForm(form);
	        
	        GlobalVariables.getMessageMap().putInfo("KS-OrgInfoSearch-CriteriaSection","databaseChnageSuccess");
			
			return getUIFModelAndView(form, "KS-OrgInfoSearch-View");
		} catch(Exception e){
			GlobalVariables.getMessageMap().putError("KS-OrgInfoDetail-Section","unknownError",e.getMessage());				
		}
        return getUIFModelAndView(form, "KS-OrgInfoSearch-View");
    }
	
	@RequestMapping(params = "methodToCall=create")
    public ModelAndView create(@ModelAttribute("KualiForm") OrgInfoAdminSearchForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
		form.setOrgInfo(new OrgUIModel());
        return getUIFModelAndView(form, "KS-OrgInfoCreate-View");
    }
    
    @RequestMapping(params = "methodToCall=copy")
    public ModelAndView copy(@ModelAttribute("KualiForm") OrgInfoAdminSearchForm form, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	
    	OrgUIModel orgInfo = form.getOrgInfo();
    	
    	orgInfo.setId(null);        
        
        return getUIFModelAndView(form, "KS-OrgInfoCreate-View");
    }
		
    @RequestMapping(params = "methodToCall=view")
	public ModelAndView view(@ModelAttribute("KualiForm") OrgInfoAdminSearchForm viewForm, BindingResult result, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String orgId = viewForm.getActionParamaterValue("selectedOrgId");

		for(OrgUIModel relation : results){			
			if(orgId.equals(relation.getId())){
				OrgUIModel copy = new OrgUIModel();
				BeanUtils.copyProperties(copy, relation);
				viewForm.setOrgInfo(copy);
				break;
			}
		}
		return getUIFModelAndView(viewForm, "KS-OrgInfoDetail-View");
	}
	
	@RequestMapping(params = "methodToCall=cancel")
	public ModelAndView cancel(@ModelAttribute("KualiForm") UifFormBase uifForm, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
		OrgInfoAdminSearchForm viewForm = (OrgInfoAdminSearchForm)uifForm;
		String returnViewName = viewForm.getActionParamaterValue("returnViewName");
		return getUIFModelAndView(viewForm, returnViewName);
	}
	
	private void resetForm(OrgInfoAdminSearchForm searchForm) {
		searchForm.setOrgUIModel(new ArrayList<OrgUIModel>());
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
/*
 * Copyright 2013 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may	obtain a copy of the License at
 *
 * 	http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.student.r2.core.organization.service.decorators;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.student.kim.permission.mock.KimPermissionConstants;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.dto.ValidationResultInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.common.exceptions.VersionMismatchException;
import org.kuali.student.r2.common.infc.HasId;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.organization.dto.OrgHierarchyInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgOrgRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPositionRestrictionInfo;
import org.kuali.student.r2.core.organization.dto.OrgTreeViewInfo;

import javax.xml.namespace.QName;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.kuali.student.common.util.security.SecurityUtils;

/**
 * @author Matthew S Edgren
 */
public class OrganizationServiceAuthorizationDecorator extends OrganizationServiceDecorator {

    private PermissionService permissionService;

    public PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService = (PermissionService) GlobalResourceLoader.getService(new QName(PermissionServiceConstants.PERMISSION_SERVICE_NAMESPACE,
                    PermissionServiceConstants.PERMISSION_SERVICE_NAME_LOCAL_PART));
        }
        return permissionService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * Add Org permission details that are needed to decide if user can access this particular object
     * <p/>
     * This method is intended to be overridden so implementing institutions can
     * add different information to the details
     *
     * @return A non null map of permission details.
     */
    protected Map<String, String> addPermissionDetails(OrgInfo info, ContextInfo context)
            throws OperationFailedException {
        return Collections.emptyMap();
    }

    /**
     * Check Org permission
     *
     * @param permService
     * @param info        if null then does basic permission checking
     * @param permission
     * @param context
     * @throws PermissionDeniedException
     */
    protected void checkPermission(PermissionService permService,
                                   OrgInfo info,
                                   String permission,
                                   ContextInfo context) throws PermissionDeniedException, OperationFailedException {
        checkPermission(permService, info, addPermissionDetails(info, context), permission, context);
    }

    /**
     * Add OrgHierarchyInfo permission details that are needed to decide if user can access this particular object
     * <p/>
     * This method is intended to be overridden so implementing institutions can
     * add different information to the details
     *
     * @return A non null map of permission details.
     */
    protected Map<String, String> addPermissionDetails(OrgHierarchyInfo info, ContextInfo context)
            throws OperationFailedException {
        return Collections.emptyMap();
    }
  
    /**
     * Check OrgHierarchy permission
     *
     * @param permService
     * @param info        if null then does basic permission checking
     * @param permission
     * @param context
     * @throws PermissionDeniedException
     */
    protected void checkPermission(PermissionService permService,
                                   OrgHierarchyInfo info,
                                   String permission,
                                   ContextInfo context) throws PermissionDeniedException, OperationFailedException {
        checkPermission(permService, info, addPermissionDetails(info, context), permission, context);
    }

    /**
     * Add OrgOrgRelationInfo permission details that are needed to decide if user can access this particular object
     * <p/>
     * This method is intended to be overridden so implementing institutions can
     * add different information to the details
     *
     * @return A non null map of permission details.
     */
    protected Map<String, String> addPermissionDetails(OrgOrgRelationInfo info, ContextInfo context)
            throws OperationFailedException {
        return Collections.emptyMap();
    }

    /**
     * Check OrgOrgRelationInfo permission
     *
     * @param permService
     * @param info        if null then does basic permission checking
     * @param permission
     * @param context
     * @throws PermissionDeniedException
     */
    protected void checkPermission(PermissionService permService,
                                   OrgOrgRelationInfo info,
                                   String permission,
                                   ContextInfo context) throws PermissionDeniedException, OperationFailedException {
        checkPermission(permService, info, addPermissionDetails(info, context), permission, context);
    }

    /**
     * Add OrgPersonRelationInfo permission details that are needed to decide if user can access this particular object
     * <p/>
     * This method is intended to be overridden so implementing institutions can
     * add different information to the details
     *
     * @return A non null map of permission details.
     */
    protected Map<String, String> addPermissionDetails(OrgPersonRelationInfo info, ContextInfo context)
            throws OperationFailedException {
        return Collections.emptyMap();
    }

    /**
     * Check OrgPersonRelationInfo permission
     *
     * @param permService
     * @param info        if null then does basic permission checking
     * @param permission
     * @param context
     * @throws PermissionDeniedException
     */
    protected void checkPermission(PermissionService permService,
                                   OrgPersonRelationInfo info,
                                   String permission,
                                   ContextInfo context) throws PermissionDeniedException, OperationFailedException {
        checkPermission(permService, info, addPermissionDetails(info, context), permission, context);
    }

    private void fixContext (ContextInfo contextInfo) {
        if (contextInfo.getPrincipalId() == null) {
            contextInfo.setPrincipalId(SecurityUtils.getCurrentUserId());
        }
        // TODO: KSCM-1467 find out why the current user is null in the security utils
        if (contextInfo.getPrincipalId() == null) {
            contextInfo.setPrincipalId("admin");
        }
    }
    
    /*
     * Check permissions to any object that implements HasId.  If null only the permission is checked.
     */
    private void checkPermission(PermissionService permService,
                                 HasId info,
                                 Map<String, String> details,
                                 String permission,
                                 ContextInfo context) throws PermissionDeniedException, OperationFailedException {
        fixContext(context);
        if (!getPermissionService().isAuthorized(context.getPrincipalId(),
                PermissionServiceConstants.KS_SYS_NAMESPACE, permission,
                details)) {
            StringBuilder sb = new StringBuilder();
            sb.append(context.getPrincipalId());
            sb.append(" does not have ");
            sb.append(permission);
            if (info != null) {
                sb.append(info.getId());
                sb.append(" because permission details don't match: ");
                sb.append(details);
            }
            throw new PermissionDeniedException(sb.toString());
        }
    }

    /**
     * Add Org permission details that are needed to decide if user can access this particular object
     * <p/>
     * This method is intended to be overridden so implementing institutions can
     * add different information to the details
     *
     * @param info org position restriction to use to pull  in details
     * @param context of the user making the call
     * @return A non null map of permission details.
     * @throws OperationFailedException
     */
    protected Map<String, String> addPermissionDetails(OrgPositionRestrictionInfo info, ContextInfo context)
            throws OperationFailedException {
        return Collections.emptyMap();
    }

    /**
     * Check Org permission
     *
     * @param permService
     * @param info        if null then does basic permission checking
     * @param permission
     * @param context
     * @throws PermissionDeniedException
     * @throws org.kuali.student.r2.common.exceptions.OperationFailedException
     */
    protected void checkPermission(PermissionService permService,
                                   OrgPositionRestrictionInfo info,
                                   String permission,
                                   ContextInfo context) throws PermissionDeniedException, OperationFailedException {
        checkPermission(permService, info, addPermissionDetails(info, context), permission, context);
    }


    @Override
    public OrgHierarchyInfo getOrgHierarchy(String orgHierarchyId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgHierarchy(orgHierarchyId, contextInfo);
    }

    @Override
    public List<OrgHierarchyInfo> getOrgHierarchiesByIds(List<String> orgHierarchyIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgHierarchiesByIds(orgHierarchyIds, contextInfo);
    }

    @Override
    public List<String> getOrgHierarchyIdsByType(String orgHierarchyTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgHierarchyIdsByType(orgHierarchyTypeKey, contextInfo);
    }

    @Override
    public List<OrgHierarchyInfo> getOrgHierarchies(ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgHierarchies(contextInfo);
    }

    @Override
    public List<String> searchForOrgHierarchyIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().searchForOrgHierarchyIds(criteria, contextInfo);
    }

    @Override
    public List<OrgHierarchyInfo> searchForOrgHierarchies(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().searchForOrgHierarchies(criteria, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateOrgHierarchy(String validationTypeKey, String orgHierarchyTypeKey, OrgHierarchyInfo orgHierarchyInfo, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().validateOrgHierarchy(validationTypeKey, orgHierarchyTypeKey, orgHierarchyInfo, contextInfo);
    }

    @Override
    public OrgHierarchyInfo createOrgHierarchy(String orgHierarchyTypeKey, OrgHierarchyInfo orgHierarchyInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        checkPermission(getPermissionService(), orgHierarchyInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().createOrgHierarchy(orgHierarchyTypeKey, orgHierarchyInfo, contextInfo);
    }

    @Override
    public OrgHierarchyInfo updateOrgHierarchy(String orgHierarchyId, OrgHierarchyInfo orgHierarchyInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException, VersionMismatchException {
        checkPermission(getPermissionService(), orgHierarchyInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().updateOrgHierarchy(orgHierarchyId, orgHierarchyInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteOrgHierarchy(String orgHierarchyId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().deleteOrgHierarchy(orgHierarchyId, contextInfo);
    }

    /**
     * This method does not currently provide any authorization checks.
     */
    @Override
    public List<TypeInfo> getOrgTypes(ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return getNextDecorator().getOrgTypes(contextInfo);
    }

    @Override
    public OrgInfo getOrg(String orgId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrg(orgId, contextInfo);
    }

    @Override
    public List<OrgInfo> getOrgsByIds(List<String> orgIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgsByIds(orgIds, contextInfo);
    }

    @Override
    public List<String> getOrgIdsByType(String orgTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgIdsByType(orgTypeKey, contextInfo);
    }

    @Override
    public List<String> searchForOrgIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().searchForOrgIds(criteria, contextInfo);
    }

    @Override
    public List<OrgInfo> searchForOrgs(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().searchForOrgs(criteria, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateOrg(String validationTypeKey, String orgTypeKey, OrgInfo orgInfo, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), orgInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().validateOrg(validationTypeKey, orgTypeKey, orgInfo, contextInfo);
    }

    @Override
    public OrgInfo createOrg(String orgTypeKey, OrgInfo orgInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        checkPermission(getPermissionService(), orgInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().createOrg(orgTypeKey, orgInfo, contextInfo);
    }

    @Override
    public OrgInfo updateOrg(String orgId, OrgInfo orgInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException, VersionMismatchException {
        checkPermission(getPermissionService(), orgInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().updateOrg(orgId, orgInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteOrg(String orgId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().deleteOrg(orgId, contextInfo);
    }

    /**
     * This method does not currently provide any authorization checks.
     */
    @Override
    public List<TypeInfo> getOrgOrgRelationTypes(ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return getNextDecorator().getOrgOrgRelationTypes(contextInfo);
    }

    /**
     * This method does not currently provide any authorization checks.
     */
    @Override
    public List<TypeInfo> getOrgOrgRelationTypesForOrgType(String orgTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return getNextDecorator().getOrgOrgRelationTypesForOrgType(orgTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getOrgOrgRelationTypesForOrgHierarchy(String orgHierarchyId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgOrgRelationTypesForOrgHierarchy(orgHierarchyId, contextInfo);
    }

    @Override
    public Boolean hasOrgOrgRelation(String orgId, String comparisonOrgId, String orgOrgRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().hasOrgOrgRelation(orgId, comparisonOrgId, orgOrgRelationTypeKey, contextInfo);
    }

    @Override
    public OrgOrgRelationInfo getOrgOrgRelation(String orgOrgRelationId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgOrgRelation(orgOrgRelationId, contextInfo);
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByIds(List<String> orgOrgRelationIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgOrgRelationsByIds(orgOrgRelationIds, contextInfo);
    }

    @Override
    public List<String> getOrgOrgRelationIdsByType(String orgOrgRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgOrgRelationIdsByType(orgOrgRelationTypeKey, contextInfo);
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByOrg(String orgId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgOrgRelationsByOrg(orgId, contextInfo);
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByOrgs(String orgId, String peerOrgId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgOrgRelationsByOrgs(orgId, peerOrgId, contextInfo);
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByTypeAndOrg(String orgId, String orgOrgRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgOrgRelationsByTypeAndOrg(orgId, orgOrgRelationTypeKey, contextInfo);
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByTypeAndRelatedOrg(String relatedOrgId, String orgOrgRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgOrgRelationsByTypeAndRelatedOrg(relatedOrgId, orgOrgRelationTypeKey, contextInfo);
    }

    @Override
    public List<String> searchForOrgOrgRelationIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().searchForOrgOrgRelationIds(criteria, contextInfo);
    }

    @Override
    public List<OrgOrgRelationInfo> searchForOrgOrgRelations(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().searchForOrgOrgRelations(criteria, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateOrgOrgRelation(String validationTypeKey, String orgId, String orgPeerId, String orgOrgRelationTypeKey, OrgOrgRelationInfo orgOrgRelationInfo, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), orgOrgRelationInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().validateOrgOrgRelation(validationTypeKey, orgId, orgPeerId, orgOrgRelationTypeKey, orgOrgRelationInfo, contextInfo);
    }

    @Override
    public OrgOrgRelationInfo createOrgOrgRelation(String orgId, String orgPeerId, String orgOrgRelationTypeKey, OrgOrgRelationInfo orgOrgRelationInfo, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        checkPermission(getPermissionService(), orgOrgRelationInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().createOrgOrgRelation(orgId, orgPeerId, orgOrgRelationTypeKey, orgOrgRelationInfo, contextInfo);
    }

    @Override
    public OrgOrgRelationInfo updateOrgOrgRelation(String orgOrgRelationId, OrgOrgRelationInfo orgOrgRelationInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException, VersionMismatchException {
        checkPermission(getPermissionService(), orgOrgRelationInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().updateOrgOrgRelation(orgOrgRelationId, orgOrgRelationInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteOrgOrgRelation(String orgOrgRelationId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgOrgRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().deleteOrgOrgRelation(orgOrgRelationId, contextInfo);
    }

    /**
     * This method does not currently provide any authorization checks.
     */
    @Override
    public List<TypeInfo> getOrgPersonRelationTypes(ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return getNextDecorator().getOrgPersonRelationTypes(contextInfo);
    }

    /**
     * This method does not currently provide any authorization checks.
     */
    @Override
    public List<TypeInfo> getOrgPersonRelationTypesForOrgType(String orgTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return getNextDecorator().getOrgPersonRelationTypesForOrgType(orgTypeKey, contextInfo);
    }

    @Override
    public Boolean hasOrgPersonRelation(String orgId, String personId, String orgPersonRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().hasOrgPersonRelation(orgId, personId, orgPersonRelationTypeKey, contextInfo);
    }

    @Override
    public OrgPersonRelationInfo getOrgPersonRelation(String orgPersonRelationId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPersonRelation(orgPersonRelationId, contextInfo);
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByIds(List<String> orgPersonRelationIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPersonRelationsByIds(orgPersonRelationIds, contextInfo);
    }

    @Override
    public List<String> getOrgPersonRelationIdsByType(String orgPersonRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPersonRelationIdsByType(orgPersonRelationTypeKey, contextInfo);
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByOrg(String orgId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPersonRelationsByOrg(orgId, contextInfo);
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByTypeAndOrg(String orgPersonRelationTypeKey, String orgId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPersonRelationsByTypeAndOrg(orgPersonRelationTypeKey, orgId, contextInfo);
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByPerson(String personId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPersonRelationsByPerson(personId, contextInfo);
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByTypeAndPerson(String orgPersonRelationTypeKey, String personId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPersonRelationsByTypeAndPerson(orgPersonRelationTypeKey, personId, contextInfo);
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByOrgAndPerson(String orgId, String personId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPersonRelationsByOrgAndPerson(orgId, personId, contextInfo);
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByTypeAndOrgAndPerson(String orgPersonRelationTypeKey, String orgId, String personId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPersonRelationsByTypeAndOrgAndPerson(orgPersonRelationTypeKey, orgId, personId, contextInfo);
    }

    @Override
    public List<String> searchForOrgPersonRelationIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().searchForOrgPersonRelationIds(criteria, contextInfo);
    }

    @Override
    public List<OrgPersonRelationInfo> searchForOrgPersonRelations(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().searchForOrgPersonRelations(criteria, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateOrgPersonRelation(String validationTypeKey, String orgId, String personId, String orgPersonRelationTypeKey, OrgPersonRelationInfo orgPersonRelationInfo, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().validateOrgPersonRelation(validationTypeKey, orgId, personId, orgPersonRelationTypeKey, orgPersonRelationInfo, contextInfo);
    }

    @Override
    public OrgPersonRelationInfo createOrgPersonRelation(String orgId, String personId, String orgPersonRelationTypeKey, OrgPersonRelationInfo orgPersonRelationInfo, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        checkPermission(getPermissionService(), orgPersonRelationInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().createOrgPersonRelation(orgId, personId, orgPersonRelationTypeKey, orgPersonRelationInfo, contextInfo);
    }

    @Override
    public OrgPersonRelationInfo updateOrgPersonRelation(String orgPersonRelationId, OrgPersonRelationInfo orgPersonRelationInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException, VersionMismatchException {
        checkPermission(getPermissionService(), orgPersonRelationInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().updateOrgPersonRelation(orgPersonRelationId, orgPersonRelationInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteOrgPersonRelation(String orgPersonRelationId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPersonRelationInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_PERSON_RELATION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().deleteOrgPersonRelation(orgPersonRelationId, contextInfo);
    }

    @Override
    public OrgPositionRestrictionInfo getOrgPositionRestriction(String orgPositionRestrictionId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPositionRestrictionInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPositionRestriction(orgPositionRestrictionId, contextInfo);
    }

    @Override
    public List<OrgPositionRestrictionInfo> getOrgPositionRestrictionsByIds(List<String> orgPositionRestrictionIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPositionRestrictionInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPositionRestrictionsByIds(orgPositionRestrictionIds, contextInfo);
    }

    @Override
    public List<String> getOrgPositionRestrictionIdsByType(String orgPersonRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPositionRestrictionInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPositionRestrictionIdsByType(orgPersonRelationTypeKey, contextInfo);
    }

    @Override
    public List<String> getOrgPositionRestrictionIdsByOrg(String orgId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPositionRestrictionInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgPositionRestrictionIdsByOrg(orgId, contextInfo);
    }

    @Override
    public List<String> searchForOrgPositionRestrictionIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPositionRestrictionInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().searchForOrgPositionRestrictionIds(criteria, contextInfo);
    }

    @Override
    public List<OrgPositionRestrictionInfo> searchForOrgPositionRestrictions(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPositionRestrictionInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().searchForOrgPositionRestrictions(criteria, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateOrgPositionRestriction(String validationTypeKey, String orgId, String orgPersonRelationTypeKey, OrgPositionRestrictionInfo orgPositionRestrictionInfo, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), orgPositionRestrictionInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().validateOrgPositionRestriction(validationTypeKey, orgId, orgPersonRelationTypeKey, orgPositionRestrictionInfo, contextInfo);
    }

    @Override
    public OrgPositionRestrictionInfo createOrgPositionRestriction(String orgId, String orgPersonRelationTypeKey, OrgPositionRestrictionInfo orgPositionRestrictionInfo, ContextInfo contextInfo) throws AlreadyExistsException, DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        checkPermission(getPermissionService(), orgPositionRestrictionInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().createOrgPositionRestriction(orgId, orgPersonRelationTypeKey, orgPositionRestrictionInfo, contextInfo);
    }

    @Override
    public OrgPositionRestrictionInfo updateOrgPositionRestriction(String orgPositionRestrictionId, OrgPositionRestrictionInfo orgPositionRestrictionInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException, VersionMismatchException {
        checkPermission(getPermissionService(), orgPositionRestrictionInfo, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_UPDATE_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().updateOrgPositionRestriction(orgPositionRestrictionId, orgPositionRestrictionInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteOrgPositionRestriction(String orgPositionRestrictionId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgPositionRestrictionInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_DELETE_ORGANIZATION_POSITION_RESTRICTION_DATA_PERMISSION, contextInfo);
        return getNextDecorator().deleteOrgPositionRestriction(orgPositionRestrictionId, contextInfo);
    }

    @Override
    public Boolean isDescendant(String orgId, String descendantOrgId, String orgHierarchyId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().isDescendant(orgId, descendantOrgId, orgHierarchyId, contextInfo);
    }

    @Override
    public List<String> getAllDescendants(String orgId, String orgHierarchyId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getAllDescendants(orgId, orgHierarchyId, contextInfo);
    }

    @Override
    public List<String> getAllAncestors(String orgId, String orgHierarchyId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getAllAncestors(orgId, orgHierarchyId, contextInfo);
    }

    @Override
    public OrgTreeViewInfo getOrgTree(String rootOrgId, String orgHierarchyId, int maxLevels, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        checkPermission(getPermissionService(), (OrgHierarchyInfo)null, KimPermissionConstants.CAN_INVOKE_SERVICE_METHOD_READ_ORGANIZATION_HIERARCHY_DATA_PERMISSION, contextInfo);
        return getNextDecorator().getOrgTree(rootOrgId, orgHierarchyId, maxLevels, contextInfo);
    }
}

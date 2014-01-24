/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.student.r2.core.organization.service.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.common.UUIDHelper;
import org.kuali.student.common.mock.MockService;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.MetaInfo;
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
import org.kuali.student.r2.core.organization.service.util.CriteriaMatcherInMemory;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.organization.dto.OrgHierarchyInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgOrgRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPositionRestrictionInfo;
import org.kuali.student.r2.core.organization.dto.OrgTreeViewInfo;
import org.kuali.student.r2.core.organization.service.OrganizationService;



public class OrganizationServiceMockImpl implements MockService, OrganizationService
{
    /**
     * List of hash maps for each entity
     */
    // cache variable
    // The LinkedHashMap is just so the values come back in a predictable order
    private Map<String, OrgHierarchyInfo> orgHierarchyMap = new LinkedHashMap<String, OrgHierarchyInfo>();
    // cache variable
    // The LinkedHashMap is just so the values come back in a predictable order
    private Map<String, OrgInfo> orgMap = new LinkedHashMap<String, OrgInfo>();
    // cache variable
    // The LinkedHashMap is just so the values come back in a predictable order
    private Map<String, OrgOrgRelationInfo> orgOrgRelationMap = new LinkedHashMap<String, OrgOrgRelationInfo>();
    // cache variable
    // The LinkedHashMap is just so the values come back in a predictable order
    private Map<String, OrgPersonRelationInfo> orgPersonRelationMap = new LinkedHashMap<String, OrgPersonRelationInfo>();
    // cache variable
    // The LinkedHashMap is just so the values come back in a predictable order
    private Map<String, OrgPositionRestrictionInfo> orgPositionRestrictionMap = new LinkedHashMap<String, OrgPositionRestrictionInfo>();

    /**
     * clear() should clear all maps.  There should be a map for each entity/DTO.
     */
    @Override
    public void clear() {
        orgHierarchyMap.clear();
        orgMap.clear();
        orgOrgRelationMap.clear();
        orgPersonRelationMap.clear();
        orgPersonRelationMap.clear();
        orgPositionRestrictionMap.clear();
    }

    @Override
    public OrgHierarchyInfo getOrgHierarchy(String orgHierarchyId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        if (!this.orgHierarchyMap.containsKey(orgHierarchyId)) {
            throw new DoesNotExistException(orgHierarchyId);
        }
        return new OrgHierarchyInfo(this.orgHierarchyMap.get (orgHierarchyId));
    }

    @Override
    public List<OrgHierarchyInfo> getOrgHierarchiesByIds(List<String> orgHierarchyIds, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgHierarchyInfo> list = new ArrayList<OrgHierarchyInfo> ();
        for (String id: orgHierarchyIds) {
            list.add (this.getOrgHierarchy(id, contextInfo));
        }
        return list;
    }

    @Override
    public List<String> getOrgHierarchyIdsByType(String orgHierarchyTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<String> list = new ArrayList<String> ();
        for (OrgHierarchyInfo info: orgHierarchyMap.values ()) {
            if (orgHierarchyTypeKey.equals(info.getTypeKey())) {
                list.add (info.getId ());
            }
        }
        return list;
    }

    @Override
    public List<OrgHierarchyInfo> getOrgHierarchies(ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgHierarchyInfo> orgHierarchies = new ArrayList<OrgHierarchyInfo>();
        for(OrgHierarchyInfo info : orgHierarchyMap.values()) {
            orgHierarchies.add(new OrgHierarchyInfo(info));
        }

        return orgHierarchies;
    }

    @Override
    public List<String> searchForOrgHierarchyIds(QueryByCriteria criteria, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        return searchForInfoIds(criteria, orgHierarchyMap.values());
    }

    @Override
    public List<OrgHierarchyInfo> searchForOrgHierarchies(QueryByCriteria criteria, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        Collection<OrgHierarchyInfo> matches = searchForInfoObjects(criteria, orgHierarchyMap.values());

        List<OrgHierarchyInfo> matchList = new ArrayList<OrgHierarchyInfo>();
        for(OrgHierarchyInfo info : matches) {
            matchList.add(new OrgHierarchyInfo(info));
        }

        return matchList;
    }

    @Override
    public List<ValidationResultInfo> validateOrgHierarchy(String validationTypeKey, String orgHierarchyTypeKey, OrgHierarchyInfo orgHierarchyInfo, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        // validate
        return new ArrayList<ValidationResultInfo> ();
    }

    @Override
    public OrgHierarchyInfo createOrgHierarchy(String orgHierarchyTypeKey, OrgHierarchyInfo orgHierarchyInfo, ContextInfo contextInfo)
            throws DataValidationErrorException
            ,DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
            ,ReadOnlyException
    {
        if(!orgHierarchyTypeKey.equals(orgHierarchyInfo.getTypeKey())) {
            throw new InvalidParameterException(orgHierarchyTypeKey + " does not match the corresponding value in the object " + orgHierarchyInfo.getTypeKey());
        }

        //make sure that the root org exists if it is defined
        if(orgHierarchyInfo.getRootOrgId() != null) {
            getOrg(orgHierarchyInfo.getRootOrgId(), contextInfo);
        }

        OrgHierarchyInfo copy = new OrgHierarchyInfo(orgHierarchyInfo);
        if (copy.getId() == null) {
            copy.setId(UUIDHelper.genStringUUID());
        }
        copy.setTypeKey(orgHierarchyTypeKey);
        copy.setMeta(newMeta(contextInfo));
        orgHierarchyMap.put(copy.getId(), copy);
        return new OrgHierarchyInfo(copy);
    }

    @Override
    public OrgHierarchyInfo updateOrgHierarchy(String orgHierarchyId, OrgHierarchyInfo orgHierarchyInfo, ContextInfo contextInfo)
            throws DataValidationErrorException
            ,DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
            ,ReadOnlyException
            ,VersionMismatchException
    {
        // update
        if (!orgHierarchyId.equals (orgHierarchyInfo.getId())) {
            throw new ReadOnlyException("The id parameter does not match the id on the info object");
        }

        //make sure that the root org exists if it is defined
        if(orgHierarchyInfo.getRootOrgId() != null) {
            getOrg(orgHierarchyInfo.getRootOrgId(), contextInfo);
        }

        OrgHierarchyInfo old = this.getOrgHierarchy(orgHierarchyInfo.getId(), contextInfo);

        if(!old.getTypeKey().equals(orgHierarchyInfo.getTypeKey())) {
            throw new ReadOnlyException("The typekey in the updated object does not match the existing typekey");
        }

        if (!old.getMeta().getVersionInd().equals(orgHierarchyInfo.getMeta().getVersionInd())) {
            throw new VersionMismatchException(old.getMeta().getVersionInd());
        }
        OrgHierarchyInfo copy = new OrgHierarchyInfo(orgHierarchyInfo);
        copy.setMeta(updateMeta(copy.getMeta(), contextInfo));
        this.orgHierarchyMap .put(orgHierarchyInfo.getId(), copy);
        return new OrgHierarchyInfo(copy);
    }

    @Override
    public StatusInfo deleteOrgHierarchy(String orgHierarchyId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        if (this.orgHierarchyMap.remove(orgHierarchyId) == null) {
            throw new DoesNotExistException(orgHierarchyId);
        }
        return newStatus();
    }

    @Override
    public List<TypeInfo> getOrgTypes(ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        throw new OperationFailedException("not implemented because it has been deprecated");
    }

    @Override
    public OrgInfo getOrg(String orgId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        if (!this.orgMap.containsKey(orgId)) {
            throw new DoesNotExistException(orgId);
        }
        return new OrgInfo(this.orgMap.get (orgId));
    }

    @Override
    public List<OrgInfo> getOrgsByIds(List<String> orgIds, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgInfo> list = new ArrayList<OrgInfo> ();
        for (String id: orgIds) {
            list.add (this.getOrg(id, contextInfo));
        }
        return list;
    }

    @Override
    public List<String> getOrgIdsByType(String orgTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<String> list = new ArrayList<String> ();
        for (OrgInfo info: orgMap.values ()) {
            if (orgTypeKey.equals(info.getTypeKey())) {
                list.add (info.getId ());
            }
        }
        return list;
    }

    @Override
    public List<String> searchForOrgIds(QueryByCriteria criteria, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        return searchForInfoIds(criteria, orgMap.values());
    }

    @Override
    public List<OrgInfo> searchForOrgs(QueryByCriteria criteria, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        Collection<OrgInfo> matches = searchForInfoObjects(criteria, orgMap.values());

        List<OrgInfo> matchList = new ArrayList<OrgInfo>();
        for(OrgInfo info : matches) {
            matchList.add(new OrgInfo(info));
        }

        return matchList;
    }

    @Override
    public List<ValidationResultInfo> validateOrg(String validationTypeKey, String orgTypeKey, OrgInfo orgInfo, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        // validate
        return new ArrayList<ValidationResultInfo> ();
    }

    @Override
    public OrgInfo createOrg(String orgTypeKey, OrgInfo orgInfo, ContextInfo contextInfo)
            throws DataValidationErrorException
            ,DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
            ,ReadOnlyException
    {
        if(!orgTypeKey.equals(orgInfo.getTypeKey())) {
            throw new InvalidParameterException(orgTypeKey + " does not match the corresponding value in the object " + orgInfo.getTypeKey());
        }

        OrgInfo copy = new OrgInfo(orgInfo);
        if (copy.getId() == null) {
            copy.setId(UUIDHelper.genStringUUID());
        }
        copy.setTypeKey(orgTypeKey);
        copy.setMeta(newMeta(contextInfo));
        orgMap.put(copy.getId(), copy);
        return new OrgInfo(copy);
    }

    @Override
    public OrgInfo updateOrg(String orgId, OrgInfo orgInfo, ContextInfo contextInfo)
            throws DataValidationErrorException
            ,DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
            ,ReadOnlyException
            ,VersionMismatchException
    {
        if (!orgId.equals (orgInfo.getId())) {
            throw new ReadOnlyException("The id parameter does not match the id on the info object");
        }

        OrgInfo old = this.getOrg(orgInfo.getId(), contextInfo);

        if (!orgId.equals(old.getId())) {
            throw new ReadOnlyException("The id parameter does not match the id on the old object");
        }

        if(!old.getTypeKey().equals(orgInfo.getTypeKey())) {
            throw new ReadOnlyException("The typekey in the updated object does not match the existing typekey");
        }

        if (!old.getMeta().getVersionInd().equals(orgInfo.getMeta().getVersionInd())) {
            throw new VersionMismatchException(old.getMeta().getVersionInd());
        }
        OrgInfo copy = new OrgInfo(orgInfo);
        copy.setMeta(updateMeta(copy.getMeta(), contextInfo));
        this.orgMap .put(orgInfo.getId(), copy);
        return new OrgInfo(copy);
    }

    @Override
    public StatusInfo deleteOrg(String orgId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        if (this.orgMap.remove(orgId) == null) {
            throw new DoesNotExistException(orgId);
        }
        return newStatus();
    }

    @Override
    public List<TypeInfo> getOrgOrgRelationTypes(ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        throw new OperationFailedException("not implemented because deprecated");
    }

    @Override
    public List<TypeInfo> getOrgOrgRelationTypesForOrgType(String orgTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        throw new OperationFailedException("not implemented because deprecated");
    }

    @Override
    public List<TypeInfo> getOrgOrgRelationTypesForOrgHierarchy(String orgHierarchyId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        throw new OperationFailedException("not implemented because deprecated");
    }

    @Override
    public Boolean hasOrgOrgRelation(String orgId, String comparisonOrgId, String orgOrgRelationTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException {
        for(OrgOrgRelationInfo relationInfo : orgOrgRelationMap.values()) {
            if(relationInfo.getTypeKey().equals(orgOrgRelationTypeKey)) {
                if(relationInfo.getOrgId().equals(orgId) && relationInfo.getRelatedOrgId().equals(comparisonOrgId)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public OrgOrgRelationInfo getOrgOrgRelation(String orgOrgRelationId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        if (!this.orgOrgRelationMap.containsKey(orgOrgRelationId)) {
            throw new DoesNotExistException(orgOrgRelationId);
        }
        return new OrgOrgRelationInfo(this.orgOrgRelationMap.get (orgOrgRelationId));
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByIds(List<String> orgOrgRelationIds, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgOrgRelationInfo> list = new ArrayList<OrgOrgRelationInfo> ();
        for (String id: orgOrgRelationIds) {
            list.add (this.getOrgOrgRelation(id, contextInfo));
        }
        return list;
    }

    @Override
    public List<String> getOrgOrgRelationIdsByType(String orgOrgRelationTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<String> list = new ArrayList<String> ();
        for (OrgOrgRelationInfo info: orgOrgRelationMap.values ()) {
            if (orgOrgRelationTypeKey.equals(info.getTypeKey())) {
                list.add (info.getId ());
            }
        }
        return list;
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByOrg(String orgId, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgOrgRelationInfo> list = new ArrayList<OrgOrgRelationInfo> ();
        for (OrgOrgRelationInfo info: orgOrgRelationMap.values ()) {
            if (orgId.equals(info.getOrgId()) || orgId.equals(info.getRelatedOrgId())) {
                list.add (new OrgOrgRelationInfo(info));
            }
        }
        return list;
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByOrgs(String orgId, String peerOrgId, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgOrgRelationInfo> list = new ArrayList<OrgOrgRelationInfo> ();
        for (OrgOrgRelationInfo info: orgOrgRelationMap.values ()) {
            if (orgId.equals(info.getOrgId()) && peerOrgId.equals(info.getRelatedOrgId())) {
                list.add (new OrgOrgRelationInfo(info));
            }
        }
        return list;
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByTypeAndOrg(String orgId, String orgOrgRelationTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgOrgRelationInfo> list = new ArrayList<OrgOrgRelationInfo> ();
        for (OrgOrgRelationInfo info: orgOrgRelationMap.values ()) {
            if (orgId.equals(info.getOrgId())) {
                if (orgOrgRelationTypeKey.equals(info.getTypeKey())) {
                    list.add (new OrgOrgRelationInfo(info));
                }
            }
        }
        return list;
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByTypeAndRelatedOrg(String relatedOrgId, String orgOrgRelationTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgOrgRelationInfo> list = new ArrayList<OrgOrgRelationInfo> ();
        for (OrgOrgRelationInfo info: orgOrgRelationMap.values ()) {
            if (relatedOrgId.equals(info.getRelatedOrgId())) {
                if (orgOrgRelationTypeKey.equals(info.getTypeKey())) {
                    list.add (new OrgOrgRelationInfo(info));
                }
            }
        }
        return list;
    }

    @Override
    public List<String> searchForOrgOrgRelationIds(QueryByCriteria criteria, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        return searchForInfoIds(criteria, orgOrgRelationMap.values());
    }

    @Override
    public List<OrgOrgRelationInfo> searchForOrgOrgRelations(QueryByCriteria criteria, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        Collection<OrgOrgRelationInfo> matches = searchForInfoObjects(criteria, orgOrgRelationMap.values());

        List<OrgOrgRelationInfo> matchList = new ArrayList<OrgOrgRelationInfo>();
        for(OrgOrgRelationInfo info : matches) {
            matchList.add(new OrgOrgRelationInfo(info));
        }

        return matchList;
    }

    @Override
    public List<ValidationResultInfo> validateOrgOrgRelation(String validationTypeKey, String orgId, String orgPeerId, String orgOrgRelationTypeKey, OrgOrgRelationInfo orgOrgRelationInfo, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        // validate
        return new ArrayList<ValidationResultInfo> ();
    }


    @Override
    public OrgOrgRelationInfo createOrgOrgRelation(String orgId, String orgPeerId, String orgOrgRelationTypeKey, OrgOrgRelationInfo orgOrgRelationInfo, ContextInfo contextInfo)
            throws DoesNotExistException
            ,DataValidationErrorException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
            ,ReadOnlyException {
        if(!orgId.equals(orgOrgRelationInfo.getOrgId())) {
            throw new InvalidParameterException(orgId + " does not match the corresponding value in the object " + orgOrgRelationInfo.getOrgId());
        }
        if(!orgPeerId.equals(orgOrgRelationInfo.getRelatedOrgId())) {
            throw new InvalidParameterException(orgPeerId + " does not match the corresponding value in the object " + orgOrgRelationInfo.getRelatedOrgId());
        }
        if(!orgOrgRelationTypeKey.equals(orgOrgRelationInfo.getTypeKey())) {
            throw new InvalidParameterException(orgOrgRelationTypeKey + " does not match the corresponding value in the object " + orgOrgRelationInfo.getTypeKey());
        }

        //Make sure that the orgs  actually exist
        List<String> ids = new ArrayList<String>();
        ids.add(orgId);
        ids.add(orgPeerId);
        getOrgsByIds(ids, contextInfo);


        OrgOrgRelationInfo copy = new OrgOrgRelationInfo(orgOrgRelationInfo);
        if (copy.getId() == null) {
            copy.setId(UUIDHelper.genStringUUID());
        }
        copy.setTypeKey(orgOrgRelationTypeKey);
        copy.setOrgId(orgId);
        copy.setRelatedOrgId(orgPeerId);
        copy.setMeta(newMeta(contextInfo));
        orgOrgRelationMap.put(copy.getId(), copy);
        return new OrgOrgRelationInfo(copy);
    }

    @Override
    public OrgOrgRelationInfo updateOrgOrgRelation(String orgOrgRelationId, OrgOrgRelationInfo orgOrgRelationInfo, ContextInfo contextInfo)
            throws DataValidationErrorException
            ,DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
            ,ReadOnlyException
            ,VersionMismatchException
    {
        // update
        if (!orgOrgRelationId.equals (orgOrgRelationInfo.getId())) {
            throw new ReadOnlyException("The id parameter does not match the id on the info object");
        }

        OrgOrgRelationInfo old = this.getOrgOrgRelation(orgOrgRelationInfo.getId(), contextInfo);

        if(!orgOrgRelationId.equals(old.getId())) {
            throw new ReadOnlyException(orgOrgRelationId + " does not match the id in the old object " + old.getId());
        }

        if(!old.getTypeKey().equals(orgOrgRelationInfo.getTypeKey())) {
            throw new ReadOnlyException("The typekey in the updated object does not match the existing typekey");
        }

        if(!old.getOrgId().equals(orgOrgRelationInfo.getOrgId())) {
            throw new ReadOnlyException("The orgId in the updated object does not match the existing orgId");
        }

        if(!old.getRelatedOrgId().equals(orgOrgRelationInfo.getRelatedOrgId())) {
            throw new ReadOnlyException("The relatedOrgId in the updated object does not match the existing relatedOrgId");
        }

        if (!old.getMeta().getVersionInd().equals(orgOrgRelationInfo.getMeta().getVersionInd())) {
            throw new VersionMismatchException(old.getMeta().getVersionInd());
        }

        OrgOrgRelationInfo copy = new OrgOrgRelationInfo(orgOrgRelationInfo);
        copy.setMeta(updateMeta(copy.getMeta(), contextInfo));
        this.orgOrgRelationMap.put(orgOrgRelationInfo.getId(), copy);
        return new OrgOrgRelationInfo(copy);
    }

    @Override
    public StatusInfo deleteOrgOrgRelation(String orgOrgRelationId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        if (this.orgOrgRelationMap.remove(orgOrgRelationId) == null) {
            throw new DoesNotExistException(orgOrgRelationId);
        }
        return newStatus();
    }

    @Override
    public List<TypeInfo> getOrgPersonRelationTypes(ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        throw new OperationFailedException("not implemented because deprecated");
    }

    @Override
    public List<TypeInfo> getOrgPersonRelationTypesForOrgType(String orgTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        throw new OperationFailedException("not implemented because deprecated");
    }

    @Override
    public Boolean hasOrgPersonRelation(String orgId, String personId, String orgPersonRelationTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        throw new OperationFailedException ("hasOrgPersonRelation has not been implemented");
    }

    @Override
    public OrgPersonRelationInfo getOrgPersonRelation(String orgPersonRelationId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        if (!this.orgPersonRelationMap.containsKey(orgPersonRelationId)) {
            throw new DoesNotExistException(orgPersonRelationId);
        }
        return new OrgPersonRelationInfo(this.orgPersonRelationMap.get (orgPersonRelationId));
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByIds(List<String> orgPersonRelationIds, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo> ();
        for (String id: orgPersonRelationIds) {
            list.add (this.getOrgPersonRelation(id, contextInfo));
        }
        return list;
    }

    @Override
    public List<String> getOrgPersonRelationIdsByType(String orgPersonRelationTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<String> list = new ArrayList<String> ();
        for (OrgPersonRelationInfo info: orgPersonRelationMap.values ()) {
            if (orgPersonRelationTypeKey.equals(info.getTypeKey())) {
                list.add (info.getId ());
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByOrg(String orgId, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo> ();
        for (OrgPersonRelationInfo info: orgPersonRelationMap.values ()) {
            if (orgId.equals(info.getOrgId())) {
                list.add (new OrgPersonRelationInfo(info));
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByTypeAndOrg(String orgPersonRelationTypeKey, String orgId, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo> ();
        for (OrgPersonRelationInfo info: orgPersonRelationMap.values ()) {
            if (orgPersonRelationTypeKey.equals(info.getTypeKey())) {
                if (orgId.equals(info.getOrgId())) {
                    list.add (new OrgPersonRelationInfo(info));
                }
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByPerson(String personId, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo> ();
        for (OrgPersonRelationInfo info: orgPersonRelationMap.values ()) {
            if (personId.equals(info.getPersonId())) {
                list.add (new OrgPersonRelationInfo(info));
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByTypeAndPerson(String orgPersonRelationTypeKey, String personId, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo> ();
        for (OrgPersonRelationInfo info: orgPersonRelationMap.values ()) {
            if (orgPersonRelationTypeKey.equals(info.getTypeKey())) {
                if (personId.equals(info.getPersonId())) {
                    list.add (new OrgPersonRelationInfo(info));
                }
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByOrgAndPerson(String orgId, String personId, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo> ();
        for (OrgPersonRelationInfo info: orgPersonRelationMap.values ()) {
            if (orgId.equals(info.getOrgId())) {
                if (personId.equals(info.getPersonId())) {
                    list.add (new OrgPersonRelationInfo(info));
                }
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByTypeAndOrgAndPerson(String orgPersonRelationTypeKey, String orgId, String personId, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo> ();
        for (OrgPersonRelationInfo info: orgPersonRelationMap.values ()) {
            if (orgPersonRelationTypeKey.equals(info.getTypeKey())) {
                if (orgId.equals(info.getOrgId())) {
                    if (personId.equals(info.getPersonId())) {
                        list.add (new OrgPersonRelationInfo(info));
                    }
                }
            }
        }
        return list;
    }

    @Override
    public List<String> searchForOrgPersonRelationIds(QueryByCriteria criteria, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        return searchForInfoIds(criteria, orgPersonRelationMap.values());
    }

    @Override
    public List<OrgPersonRelationInfo> searchForOrgPersonRelations(QueryByCriteria criteria, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        Collection<OrgPersonRelationInfo> matches = searchForInfoObjects(criteria, orgPersonRelationMap.values());

        List<OrgPersonRelationInfo> matchList = new ArrayList<OrgPersonRelationInfo>();
        for(OrgPersonRelationInfo info : matches) {
            matchList.add(new OrgPersonRelationInfo(info));
        }

        return matchList;
    }

    @Override
    public List<ValidationResultInfo> validateOrgPersonRelation(String validationTypeKey, String orgId, String personId, String orgPersonRelationTypeKey, OrgPersonRelationInfo orgPersonRelationInfo, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        // validate
        return new ArrayList<ValidationResultInfo> ();
    }

    @Override
    public OrgPersonRelationInfo createOrgPersonRelation(String orgId, String personId, String orgPersonRelationTypeKey, OrgPersonRelationInfo orgPersonRelationInfo, ContextInfo contextInfo)
            throws DoesNotExistException
            ,DataValidationErrorException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
            ,ReadOnlyException
    {
        if(!orgId.equals(orgPersonRelationInfo.getOrgId())) {
            throw new InvalidParameterException(orgId + " does not match the corresponding value in the object " + orgPersonRelationInfo.getOrgId());
        }
        if(!personId.equals(orgPersonRelationInfo.getPersonId())) {
            throw new InvalidParameterException(personId + " does not match the corresponding value in the object " + orgPersonRelationInfo.getPersonId());
        }
        if(!orgPersonRelationTypeKey.equals(orgPersonRelationInfo.getTypeKey())) {
            throw new InvalidParameterException(orgPersonRelationTypeKey + " does not match the corresponding value in the object " + orgPersonRelationInfo.getTypeKey());
        }

        getOrg(orgId, contextInfo);

        OrgPersonRelationInfo copy = new OrgPersonRelationInfo(orgPersonRelationInfo);
        if (copy.getId() == null) {
            copy.setId(UUIDHelper.genStringUUID());
        }
        copy.setTypeKey(orgPersonRelationTypeKey);
        copy.setPersonId(personId);
        copy.setOrgId(orgId);
        copy.setMeta(newMeta(contextInfo));
        orgPersonRelationMap.put(copy.getId(), copy);
        return new OrgPersonRelationInfo(copy);
    }

    @Override
    public OrgPersonRelationInfo updateOrgPersonRelation(String orgPersonRelationId, OrgPersonRelationInfo orgPersonRelationInfo, ContextInfo contextInfo)
            throws DataValidationErrorException
            ,DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
            ,ReadOnlyException
            ,VersionMismatchException
    {
        if (!orgPersonRelationId.equals (orgPersonRelationInfo.getId())) {
            throw new ReadOnlyException ("The id parameter does not match the id on the info object");
        }

        OrgPersonRelationInfo old = this.getOrgPersonRelation(orgPersonRelationInfo.getId(), contextInfo);

        if(!orgPersonRelationId.equals(old.getId())) {
            throw new ReadOnlyException(orgPersonRelationId + " does not match the id in the old object " + old.getId());
        }



        if(!old.getTypeKey().equals(orgPersonRelationInfo.getTypeKey())) {
            throw new ReadOnlyException("The typekey in the updated object does not match the existing typekey");
        }

        if(!old.getPersonId().equals(orgPersonRelationInfo.getPersonId())) {
            throw new ReadOnlyException("The personId in the updated object does not match the existing personId");
        }
        if(!old.getOrgId().equals(orgPersonRelationInfo.getOrgId())) {
            throw new ReadOnlyException("The orgId in the updated object does not match the existing orgId");
        }

        OrgPersonRelationInfo copy = new OrgPersonRelationInfo(orgPersonRelationInfo);

        if (!old.getMeta().getVersionInd().equals(copy.getMeta().getVersionInd())) {
            throw new VersionMismatchException(old.getMeta().getVersionInd());
        }
        copy.setMeta(updateMeta(copy.getMeta(), contextInfo));
        this.orgPersonRelationMap .put(orgPersonRelationInfo.getId(), copy);
        return new OrgPersonRelationInfo(copy);
    }

    @Override
    public StatusInfo deleteOrgPersonRelation(String orgPersonRelationId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        if (this.orgPersonRelationMap.remove(orgPersonRelationId) == null) {
            throw new DoesNotExistException(orgPersonRelationId);
        }
        return newStatus();
    }

    @Override
    public OrgPositionRestrictionInfo getOrgPositionRestriction(String orgPositionRestrictionId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        if (!this.orgPositionRestrictionMap.containsKey(orgPositionRestrictionId)) {
            throw new DoesNotExistException(orgPositionRestrictionId);
        }
        return new OrgPositionRestrictionInfo(this.orgPositionRestrictionMap.get (orgPositionRestrictionId));
    }

    @Override
    public List<OrgPositionRestrictionInfo> getOrgPositionRestrictionsByIds(List<String> orgPositionRestrictionIds, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<OrgPositionRestrictionInfo> list = new ArrayList<OrgPositionRestrictionInfo> ();
        for (String id: orgPositionRestrictionIds) {
            list.add (this.getOrgPositionRestriction(id, contextInfo));
        }
        return list;
    }

    @Override
    public List<String> getOrgPositionRestrictionIdsByType(String orgPersonRelationTypeKey, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException {
        List<String> list = new ArrayList<String> ();

        for(OrgPositionRestrictionInfo info : orgPositionRestrictionMap.values()) {
            if(info.getOrgPersonRelationTypeKey().equals(orgPersonRelationTypeKey)) {
                list.add(info.getId());
            }
        }

        return list;
    }

    @Override
    public List<String> getOrgPositionRestrictionIdsByOrg(String orgId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        List<String> list = new ArrayList<String> ();
        for (OrgPositionRestrictionInfo info: orgPositionRestrictionMap.values ()) {
            if (orgId.equals(info.getOrgId())) {
                list.add (info.getId ());
            }
        }
        return list;
    }

    @Override
    public List<String> searchForOrgPositionRestrictionIds(QueryByCriteria criteria, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        return searchForInfoIds(criteria, orgPositionRestrictionMap.values());
    }

    @Override
    public List<OrgPositionRestrictionInfo> searchForOrgPositionRestrictions(QueryByCriteria criteria, ContextInfo contextInfo)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        Collection<OrgPositionRestrictionInfo> matches = searchForInfoObjects(criteria, orgPositionRestrictionMap.values());

        List<OrgPositionRestrictionInfo> matchList = new ArrayList<OrgPositionRestrictionInfo>();
        for(OrgPositionRestrictionInfo info : matches) {
            matchList.add(new OrgPositionRestrictionInfo(info));
        }

        return matchList;
    }

    @Override
    public List<ValidationResultInfo> validateOrgPositionRestriction(String validationTypeKey, String orgId, String orgPersonRelationTypeKey, OrgPositionRestrictionInfo orgPositionRestrictionInfo, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        // validate
        return new ArrayList<ValidationResultInfo> ();
    }

    @Override
    public OrgPositionRestrictionInfo createOrgPositionRestriction(String orgId, String orgPersonRelationTypeKey, OrgPositionRestrictionInfo orgPositionRestrictionInfo, ContextInfo contextInfo)
            throws AlreadyExistsException
            ,DataValidationErrorException
            ,DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
            ,ReadOnlyException {
        if(!orgId.equals(orgPositionRestrictionInfo.getOrgId())) {
            throw new InvalidParameterException(orgId + " does not match the corresponding value in the object " + orgPositionRestrictionInfo.getOrgId());
        }
        if(!orgPersonRelationTypeKey.equals(orgPositionRestrictionInfo.getOrgPersonRelationTypeKey())) {
            throw new InvalidParameterException(orgPersonRelationTypeKey + " does not match the corresponding value in the object " + orgPositionRestrictionInfo.getOrgPersonRelationTypeKey());
        }

        OrgPositionRestrictionInfo copy = new OrgPositionRestrictionInfo(orgPositionRestrictionInfo);
        if (copy.getId() == null) {
            copy.setId(UUIDHelper.genStringUUID());
        }
        copy.setOrgId(orgId);
        copy.setOrgPersonRelationTypeKey(orgPersonRelationTypeKey);
        copy.setMeta(newMeta(contextInfo));
        orgPositionRestrictionMap.put(copy.getId(), copy);
        return new OrgPositionRestrictionInfo(copy);
    }

    @Override
    public OrgPositionRestrictionInfo updateOrgPositionRestriction(String orgPositionRestrictionId, OrgPositionRestrictionInfo orgPositionRestrictionInfo, ContextInfo contextInfo)
            throws DataValidationErrorException
            ,DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
            ,ReadOnlyException
            ,VersionMismatchException
    {
        // update
        if (!orgPositionRestrictionId.equals (orgPositionRestrictionInfo.getId())) {
            throw new ReadOnlyException ("The id parameter does not match the id on the info object");
        }

        OrgPositionRestrictionInfo old = this.getOrgPositionRestriction(orgPositionRestrictionInfo.getId(), contextInfo);
        if(!orgPositionRestrictionId.equals(old.getId())) {
            throw new ReadOnlyException(orgPositionRestrictionId + " does not match the id in the old object " + old.getId());
        }

        if(!old.getOrgPersonRelationTypeKey().equals(orgPositionRestrictionInfo.getOrgPersonRelationTypeKey())) {
            throw new ReadOnlyException("The orgPersonRelationTypekey in the updated object does not match the existing orgPersonRelationTypekey");
        }

        if(!old.getOrgId().equals(orgPositionRestrictionInfo.getOrgId())) {
            throw new ReadOnlyException("The orgId in the updated object does not match the existing orgId");
        }

        if (!old.getMeta().getVersionInd().equals(orgPositionRestrictionInfo.getMeta().getVersionInd())) {
            throw new VersionMismatchException(old.getMeta().getVersionInd());
        }
        OrgPositionRestrictionInfo copy = new OrgPositionRestrictionInfo(orgPositionRestrictionInfo);
        copy.setMeta(updateMeta(copy.getMeta(), contextInfo));
        this.orgPositionRestrictionMap .put(orgPositionRestrictionInfo.getId(), copy);
        return new OrgPositionRestrictionInfo(copy);
    }

    @Override
    public StatusInfo deleteOrgPositionRestriction(String orgPositionRestrictionId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        if (this.orgPositionRestrictionMap.remove(orgPositionRestrictionId) == null) {
            throw new DoesNotExistException(orgPositionRestrictionId);
        }
        return newStatus();
    }

    @Override
    public Boolean isDescendant(String orgId, String descendantOrgId, String orgHierarchyId, ContextInfo contextInfo)
            throws InvalidParameterException
            , MissingParameterException
            , OperationFailedException
            , PermissionDeniedException {
        try {
            OrgTreeViewInfo orgTree = getOrgTree(orgId, orgHierarchyId, Integer.MAX_VALUE, contextInfo);
            if(searchTree(orgTree, descendantOrgId, new HashSet<String>())) {
                return true;
            }
        } catch (DoesNotExistException e) {
            throw new OperationFailedException("unable to determine if " + descendantOrgId + " is a descendant of " + orgId + " using hierarchy " + orgHierarchyId, e);
        }

        return false;
    }

    /*
     * Recursively search the given orgTree for the given orgId.
     */
    private boolean searchTree(OrgTreeViewInfo orgTree, String orgId, Set<String> visitedOrgs) {
        for(OrgTreeViewInfo child : orgTree.getChildren()) {
            String childOrgId = child.getOrg().getId();
            if(childOrgId.equals(orgId)) {
                return true;
            }
            //only recurse if we have not already been there
            if(visitedOrgs.add(childOrgId)) {
                if(searchTree(child, orgId, visitedOrgs)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public List<String> getAllDescendants(String orgId, String orgHierarchyId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        throw new OperationFailedException ("getAllDescendants has not been implemented because it is deprecated");
    }

    @Override
    public List<String> getAllAncestors(String orgId, String orgHierarchyId, ContextInfo contextInfo)
            throws DoesNotExistException
            ,InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException
    {
        throw new OperationFailedException ("getAllAncestors has not been implemented because it is deprecated");
    }

    @Override
    public OrgTreeViewInfo getOrgTree(String startingOrgId, String orgHierarchyId, int maxLevels, ContextInfo contextInfo)
            throws DoesNotExistException
            , InvalidParameterException
            , MissingParameterException
            , OperationFailedException
            , PermissionDeniedException {

        OrgHierarchyInfo orgHierarchyInfo = getOrgHierarchy(orgHierarchyId, contextInfo);

        OrgTreeViewInfo orgTree = new OrgTreeViewInfo();

        OrgInfo org = getOrg(startingOrgId, contextInfo);
        orgTree.setOrg(org);

        Map<String, OrgTreeViewInfo> visitedNodes = new HashMap<String, OrgTreeViewInfo>();
        if(maxLevels >= 0) {
            buildOrgTree(orgTree, maxLevels, 0, orgHierarchyInfo.getOrgOrgRelationTypes(), visitedNodes, false, contextInfo);
        }

        if(maxLevels <= 0) {
            buildOrgTree(orgTree, maxLevels, 0, orgHierarchyInfo.getOrgOrgRelationTypes(), visitedNodes, true, contextInfo);
        }

        return orgTree;
    }

    /*
    * Build the OrgTree from the given Org tree going up or down (pulling the parent/child of the given OrgTree) recursively
    */
    private void buildOrgTree(OrgTreeViewInfo orgTree, int maxLevels, int currentLevel, List<String> validRelationTypes, Map<String, OrgTreeViewInfo> visitedNodes, boolean up, ContextInfo contextInfo)
            throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException, DoesNotExistException {
        //only continue if we still need to go up/down the tree
        if (maxLevels == 0 || Math.abs(currentLevel) < Math.abs(maxLevels)) {
            visitedNodes.put(orgTree.getOrg().getId(), orgTree);

            List<OrgOrgRelationInfo> relations = pullRelations(validRelationTypes, orgTree.getOrg().getId(), up, contextInfo);
            for (OrgOrgRelationInfo relation : relations) {
                String nextOrgId = (up ? relation.getOrgId() : relation.getRelatedOrgId());
                OrgTreeViewInfo nextOrg = visitedNodes.get(nextOrgId);

                if (nextOrg == null) {
                    nextOrg = new OrgTreeViewInfo();
                    nextOrg.setOrg(getOrg(nextOrgId, contextInfo));
                }
                if(up) {
                    addToOrgTree(nextOrg, orgTree);
                } else {
                    addToOrgTree(orgTree, nextOrg);
                }
                //don't recurse on a node that we have already visited.
                if (!visitedNodes.containsKey(nextOrgId)) {
                    int nextLevel = currentLevel + (up ? -1 : 1);
                    buildOrgTree(nextOrg, maxLevels, nextLevel, validRelationTypes, visitedNodes, up, contextInfo);
                }
            }
        }
    }

    private List<OrgOrgRelationInfo> pullRelations(List<String> validRelationTypes, String orgId, boolean up, ContextInfo contextInfo) throws MissingParameterException, InvalidParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgOrgRelationInfo> relations = new ArrayList<OrgOrgRelationInfo>();
        for(String relationType : validRelationTypes) {
            if(up) {
                relations.addAll(getOrgOrgRelationsByTypeAndRelatedOrg(orgId, relationType, contextInfo));

            } else {
                relations.addAll(getOrgOrgRelationsByTypeAndOrg(orgId, relationType, contextInfo));
            }
        }

        return relations;
    }

    private void addToOrgTree(OrgTreeViewInfo parent, OrgTreeViewInfo child) {
        addToOrgTree(parent.getChildren(), child);
        addToOrgTree(child.getParents(), parent);
    }

    private void addToOrgTree(List<OrgTreeViewInfo> orgTreeList, OrgTreeViewInfo orgTreeViewInfo) {
        for(OrgTreeViewInfo currentTree : orgTreeList) {
            if(currentTree.getOrg().getId().equals(orgTreeViewInfo.getOrg().getId())) {
                return;
            }
        }
        orgTreeList.add(orgTreeViewInfo);
    }


    private StatusInfo newStatus() {
        StatusInfo status = new StatusInfo();
        status.setSuccess(Boolean.TRUE);
        return status;
    }

    private MetaInfo newMeta(ContextInfo context) {
        MetaInfo meta = new MetaInfo();
        meta.setCreateId(context.getPrincipalId());
        meta.setCreateTime(new Date());
        meta.setUpdateId(context.getPrincipalId());
        meta.setUpdateTime(meta.getCreateTime());
        meta.setVersionInd("0");
        return meta;
    }

    private MetaInfo updateMeta(MetaInfo old, ContextInfo context) {
        MetaInfo meta = new MetaInfo(old);
        meta.setUpdateId(context.getPrincipalId());
        meta.setUpdateTime(new Date());
        meta.setVersionInd((Integer.parseInt(meta.getVersionInd()) + 1) + "");
        return meta;
    }

    private <T extends HasId> List<String> searchForInfoIds(QueryByCriteria criteria, Collection<T> collection)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException {
        Collection<T> matches = searchForInfoObjects(criteria, collection);
        List<String> matchingIds = new ArrayList<String>();

        for(T info : matches) {
            matchingIds.add(info.getId());
        }
        return matchingIds;
    }

    public <T> Collection<T> searchForInfoObjects(QueryByCriteria criteria, Collection<T> collection)
            throws InvalidParameterException
            ,MissingParameterException
            ,OperationFailedException
            ,PermissionDeniedException {
        CriteriaMatcherInMemory<T> matcher = new CriteriaMatcherInMemory<T>();
        matcher.setCriteria(criteria);
        return matcher.findMatching(collection);
    }

}


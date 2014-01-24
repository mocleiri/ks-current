/**
 * Copyright 2010 The Kuali Foundation Licensed under the Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.osedu.org/licenses/ECL-2.0 Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under the License.
 */

package org.kuali.student.r2.core.organization.service.impl;

import org.apache.log4j.Logger;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.common.util.UUIDHelper;
import org.kuali.student.r2.common.criteria.CriteriaLookupService;
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

import org.kuali.student.r2.core.organization.dao.OrgDao;
import org.kuali.student.r2.core.organization.dao.OrgHierarchyDao;
import org.kuali.student.r2.core.organization.dao.OrgOrgRelationDao;
import org.kuali.student.r2.core.organization.dao.OrgPersonRelationDao;
import org.kuali.student.r2.core.organization.dao.OrgPositionRestrictionDao;
import org.kuali.student.r2.core.organization.model.OrgCodeEntity;
import org.kuali.student.r2.core.organization.model.OrgEntity;
import org.kuali.student.r2.core.organization.model.OrgHierarchyEntity;
import org.kuali.student.r2.core.organization.model.OrgOrgRelationEntity;
import org.kuali.student.r2.core.organization.model.OrgPersonRelationEntity;
import org.kuali.student.r2.core.organization.model.OrgPositionRestrictionEntity;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.class1.type.service.TypeService;
import org.kuali.student.r2.core.constants.OrganizationServiceConstants;
import org.kuali.student.r2.core.organization.dto.OrgHierarchyInfo;
import org.kuali.student.r2.core.organization.dto.OrgInfo;
import org.kuali.student.r2.core.organization.dto.OrgOrgRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPersonRelationInfo;
import org.kuali.student.r2.core.organization.dto.OrgPositionRestrictionInfo;
import org.kuali.student.r2.core.organization.dto.OrgTreeViewInfo;
import org.kuali.student.r2.core.organization.service.OrganizationService;

import org.springframework.transaction.annotation.Transactional;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@WebService(endpointInterface = "org.kuali.student.r2.core.organization.service.OrganizationService", serviceName = "OrganizationService", portName = "OrganizationService", targetNamespace = "http://student.kuali.org/wsdl/organization")
@Transactional(readOnly = true, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
public class OrganizationServiceImpl implements OrganizationService {

    private TypeService typeService;

    private OrgHierarchyDao orgHierarchyDao;
    private OrgDao orgDao;
    private OrgOrgRelationDao orgOrgRelationDao;
    private OrgPersonRelationDao orgPersonRelationDao;
    private OrgPositionRestrictionDao orgPositionRestrictionDao;

    private CriteriaLookupService orgHierarchyCriteriaLookupService;
    private CriteriaLookupService orgCriteriaLookupService;
    private CriteriaLookupService orgOrgRelationCriteriaLookupService;
    private CriteriaLookupService orgPersonRelationCriteriaLookupService;
    private CriteriaLookupService orgPositionRestrictionCriteriaLookupService;


    private final Logger logger = Logger.getLogger(OrganizationServiceImpl.class);

    public OrgHierarchyDao getOrgHierarchyDao() {
        return orgHierarchyDao;
    }

    public void setOrgHierarchyDao(OrgHierarchyDao orgHierarchyDao) {
        this.orgHierarchyDao = orgHierarchyDao;
    }

    public OrgDao getOrgDao() {
        return orgDao;
    }

    public void setOrgDao(OrgDao orgDao) {
        this.orgDao = orgDao;
    }

    public OrgOrgRelationDao getOrgOrgRelationDao() {
        return orgOrgRelationDao;
    }

    public void setOrgOrgRelationDao(OrgOrgRelationDao orgOrgRelationDao) {
        this.orgOrgRelationDao = orgOrgRelationDao;
    }

    public OrgPersonRelationDao getOrgPersonRelationDao() {
        return orgPersonRelationDao;
    }

    public void setOrgPersonRelationDao(OrgPersonRelationDao orgPresonRelationDao) {
        this.orgPersonRelationDao = orgPresonRelationDao;
    }

    public OrgPositionRestrictionDao getOrgPositionRestrictionDao() {
        return orgPositionRestrictionDao;
    }

    public void setOrgPositionRestrictionDao(OrgPositionRestrictionDao orgPositionRestrictionDao) {
        this.orgPositionRestrictionDao = orgPositionRestrictionDao;
    }

    public CriteriaLookupService getOrgHierarchyCriteriaLookupService() {
        return orgHierarchyCriteriaLookupService;
    }

    public void setOrgHierarchyCriteriaLookupService(CriteriaLookupService orgHierarchyCriteriaLookupService) {
        this.orgHierarchyCriteriaLookupService = orgHierarchyCriteriaLookupService;
    }

    public CriteriaLookupService getOrgCriteriaLookupService() {
        return orgCriteriaLookupService;
    }

    public void setOrgCriteriaLookupService(CriteriaLookupService orgCriteriaLookupService) {
        this.orgCriteriaLookupService = orgCriteriaLookupService;
    }

    public CriteriaLookupService getOrgOrgRelationCriteriaLookupService() {
        return orgOrgRelationCriteriaLookupService;
    }

    public void setOrgOrgRelationCriteriaLookupService(CriteriaLookupService orgOrgRelationCriteriaLookupService) {
        this.orgOrgRelationCriteriaLookupService = orgOrgRelationCriteriaLookupService;
    }

    public CriteriaLookupService getOrgPersonRelationCriteriaLookupService() {
        return orgPersonRelationCriteriaLookupService;
    }

    public void setOrgPersonRelationCriteriaLookupService(CriteriaLookupService orgPersonRelationCriteriaLookupService) {
        this.orgPersonRelationCriteriaLookupService = orgPersonRelationCriteriaLookupService;
    }

    public CriteriaLookupService getOrgPositionRestrictionCriteriaLookupService() {
        return orgPositionRestrictionCriteriaLookupService;
    }

    public void setOrgPositionRestrictionCriteriaLookupService(CriteriaLookupService orgPositionRestrictionCriteriaLookupService) {
        this.orgPositionRestrictionCriteriaLookupService = orgPositionRestrictionCriteriaLookupService;
    }

    public TypeService getTypeService() {
        return typeService;
    }

    public void setTypeService(TypeService typeService) {
        this.typeService = typeService;
    }

    @Override
    public OrgHierarchyInfo getOrgHierarchy(String orgHierarchyId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgHierarchyEntity entity = orgHierarchyDao.find(orgHierarchyId);
        if(entity == null) {
            throw new DoesNotExistException(orgHierarchyId);
        }
        return entity.toDto();
    }

    @Override
    public List<OrgHierarchyInfo> getOrgHierarchiesByIds(List<String> orgHierarchyIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgHierarchyEntity> entities = orgHierarchyDao.findByIds(orgHierarchyIds);
        List<OrgHierarchyInfo> result = new ArrayList<OrgHierarchyInfo>(entities.size());
        for (OrgHierarchyEntity entity : entities) {
            if (entity == null) {
                // if one of the entities from "findByIds" is returned as null, then one of the keys in the list was not found
                throw new DoesNotExistException();
            }
            result.add(entity.toDto());
        }
        return result;
    }

    @Override
    public List<String> getOrgHierarchyIdsByType(String orgHierarchyTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return orgHierarchyDao.getIdsByType(orgHierarchyTypeKey);
    }

    @Override
    public List<OrgHierarchyInfo> getOrgHierarchies(ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgHierarchyEntity> entities = orgHierarchyDao.findAll();
        List<OrgHierarchyInfo> result = new ArrayList<OrgHierarchyInfo>(entities.size());
        for (OrgHierarchyEntity entity : entities) {
            result.add(entity.toDto());
        }
        return result;
    }

    @Override
    public List<String> searchForOrgHierarchyIds(QueryByCriteria criteria, ContextInfo contextInfo)throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<String> results = new ArrayList<String>();
        GenericQueryResults<OrgHierarchyEntity> orgHierarchies = orgHierarchyCriteriaLookupService.lookup(OrgHierarchyEntity.class, criteria);
        if (null != orgHierarchies && orgHierarchies.getResults().size() > 0) {
            for (OrgHierarchyEntity orgHierarchy : orgHierarchies.getResults()) {
                results.add(orgHierarchy.getId());
            }
        }
        return results;
    }

    @Override
    public List<OrgHierarchyInfo> searchForOrgHierarchies(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgHierarchyInfo> results = new ArrayList<OrgHierarchyInfo>();
        GenericQueryResults<OrgHierarchyEntity> orgHierarchies = orgHierarchyCriteriaLookupService.lookup(OrgHierarchyEntity.class, criteria);
        if (null != orgHierarchies && orgHierarchies.getResults().size() > 0) {
            for (OrgHierarchyEntity orgHierarchy : orgHierarchies.getResults()) {
                results.add(orgHierarchy.toDto());
            }
        }
        return results;
    }

    @Override
    public List<ValidationResultInfo> validateOrgHierarchy(String validationTypeKey, String orgHierarchyTypeKey, OrgHierarchyInfo orgHierarchyInfo, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return new ArrayList<ValidationResultInfo>();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public OrgHierarchyInfo createOrgHierarchy(String orgHierarchyTypeKey, OrgHierarchyInfo orgHierarchyInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        if(!orgHierarchyTypeKey.equals(orgHierarchyInfo.getTypeKey())) {
            throw new InvalidParameterException(orgHierarchyTypeKey + " does not match the corresponding value in the object " + orgHierarchyInfo.getTypeKey());
        }

        //make sure that the root org exists if it is defined
        if(orgHierarchyInfo.getRootOrgId() != null) {
            getOrg(orgHierarchyInfo.getRootOrgId(), contextInfo);
        }

        OrgHierarchyEntity entity = new OrgHierarchyEntity(orgHierarchyInfo);
        entity.setEntityCreated(contextInfo);
        orgHierarchyDao.persist(entity);
        orgHierarchyDao.getEm().flush();
        return entity.toDto();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public OrgHierarchyInfo updateOrgHierarchy(String orgHierarchyId, OrgHierarchyInfo orgHierarchyInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException, VersionMismatchException {
        if (!orgHierarchyId.equals (orgHierarchyInfo.getId())) {
            throw new ReadOnlyException("The id parameter does not match the id on the info object");
        }

        //make sure that the root org exists if it is defined
        if(orgHierarchyInfo.getRootOrgId() != null) {
            getOrg(orgHierarchyInfo.getRootOrgId(), contextInfo);
        }

        OrgHierarchyEntity entity = orgHierarchyDao.find(orgHierarchyId);
        if(null == entity) {
            throw new DoesNotExistException(orgHierarchyId);
        }

        if(!orgHierarchyId.equals(entity.getId())) {
            throw new ReadOnlyException(orgHierarchyId + " does not match the id in the entity " + entity.getId());
        }

        if(!entity.getOrgHierarchyType().equals(orgHierarchyInfo.getTypeKey())) {
            throw new ReadOnlyException("The typekey in the updated object does not match the existing typekey");
        }

        entity.fromDto(orgHierarchyInfo);
        entity.setEntityUpdated(contextInfo);
        entity = orgHierarchyDao.merge(entity);
        orgHierarchyDao.getEm().flush();
        return entity.toDto();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public StatusInfo deleteOrgHierarchy(String orgHierarchyId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgHierarchyEntity entity = orgHierarchyDao.find(orgHierarchyId);
        if (null == entity) {
            throw new DoesNotExistException(orgHierarchyId);
        }
        orgHierarchyDao.remove(entity);
        return new StatusInfo();
    }

    @Override
    public List<TypeInfo> getOrgTypes(ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        try {
            return typeService.getTypesByRefObjectUri(OrganizationServiceConstants.REF_OBJECT_URI_ORG,contextInfo);
        } catch (DoesNotExistException e) {
            throw new OperationFailedException("Unable to pull any types for ref object URI " + OrganizationServiceConstants.REF_OBJECT_URI_ORG, e);
        }
    }

    @Override
    public OrgInfo getOrg(String orgId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgEntity entity = orgDao.find(orgId);
        if(entity == null) {
            throw new DoesNotExistException(orgId);
        }
        return entity.toDto();
    }

    @Override
    public List<OrgInfo> getOrgsByIds(List<String> orgIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgEntity> entities = orgDao.findByIds(orgIds);
        List<OrgInfo> result = new ArrayList<OrgInfo>(entities.size());
        for (OrgEntity entity : entities) {
            if (entity == null) {
                // if one of the entities from "findByIds" is returned as null, then one of the keys in the list was not found
                throw new DoesNotExistException();
            }
            result.add(entity.toDto());
        }
        return result;
    }

    @Override
    public List<String> getOrgIdsByType(String orgTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return orgDao.getIdsByType(orgTypeKey);
    }

    @Override
    public List<String> searchForOrgIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<String> results = new ArrayList<String>();
        GenericQueryResults<OrgEntity> orgs = orgCriteriaLookupService.lookup(OrgEntity.class, criteria);
        if (null != orgs && orgs.getResults().size() > 0) {
            for (OrgEntity org : orgs.getResults()) {
                results.add(org.getId());
            }
        }
        return results;
    }

    @Override
    public List<OrgInfo> searchForOrgs(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgInfo> results = new ArrayList<OrgInfo>();
        GenericQueryResults<OrgEntity> orgs = orgCriteriaLookupService.lookup(OrgEntity.class, criteria);
        if (null != orgs && orgs.getResults().size() > 0) {
            for (OrgEntity org : orgs.getResults()) {
                results.add(org.toDto());
            }
        }
        return results;
    }

    @Override
    public List<ValidationResultInfo> validateOrg(String validationTypeKey, String orgTypeKey, OrgInfo orgInfo, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return new ArrayList<ValidationResultInfo>();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public OrgInfo createOrg(String orgTypeKey, OrgInfo orgInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        if(!orgTypeKey.equals(orgInfo.getTypeKey())) {
            throw new InvalidParameterException(orgTypeKey + " does not match the corresponding value in the object " + orgInfo.getTypeKey());
        }

        OrgEntity entity = new OrgEntity(orgInfo);
        entity.setEntityCreated(contextInfo);
        if(entity.getId() == null) {
            entity.setId(UUIDHelper.genStringUUID());
        }

        for(OrgCodeEntity orgCodeEntity : entity.getOrgCodes()) {
            orgCodeEntity.setEntityCreated(contextInfo);
            orgCodeEntity.setOrgId(entity.getId());
        }

        orgDao.persist(entity);
        orgDao.getEm().flush();
        return entity.toDto();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public OrgInfo updateOrg(String orgId, OrgInfo orgInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException, VersionMismatchException {
        if(!orgId.equals(orgInfo.getId())) {
            throw new ReadOnlyException(orgId + " does not match the id in the object " + orgInfo.getId());
        }

        OrgEntity entity = orgDao.find(orgId);
        if(null == entity) {
            throw new DoesNotExistException(orgId);
        }

        if(!orgId.equals(entity.getId())) {
            throw new ReadOnlyException(orgId + " does not match the id in the entity " + entity.getId());
        }

        if(!entity.getOrgType().equals(orgInfo.getTypeKey())) {
            throw new ReadOnlyException("The typekey in the updated object does not match the existing typekey");
        }

        entity.fromDto(orgInfo);
        entity.setEntityUpdated(contextInfo);

        for(OrgCodeEntity orgCodeEntity : entity.getOrgCodes()) {
            if(orgCodeEntity.getCreateTime() == null) {
                orgCodeEntity.setEntityCreated(contextInfo);
            } else {
                orgCodeEntity.setEntityUpdated(contextInfo);
            }
        }
        entity = orgDao.merge(entity);
        orgDao.getEm().flush();
        return entity.toDto();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public StatusInfo deleteOrg(String orgId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgEntity entity = orgDao.find(orgId);
        if (null == entity) {
            throw new DoesNotExistException(orgId);
        }

        orgDao.remove(entity);
        return new StatusInfo();
    }

    @Override
    public List<TypeInfo> getOrgOrgRelationTypes(ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        try {
            return typeService.getTypesByRefObjectUri(OrganizationServiceConstants.REF_OBJECT_URI_ORG_ORG_RELATION,contextInfo);
        } catch (DoesNotExistException e) {
            throw new OperationFailedException("Unable to pull any types for ref object URI " + OrganizationServiceConstants.REF_OBJECT_URI_ORG_ORG_RELATION, e);
        }
    }

    @Override
    public List<TypeInfo> getOrgOrgRelationTypesForOrgType(String orgTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<TypeInfo> results = new ArrayList<TypeInfo>();
        try {
            List<TypeInfo> list = typeService.getAllowedTypesForType(orgTypeKey, contextInfo);
            for(TypeInfo info : list) {
                if(info.getRefObjectUri().equals(OrganizationServiceConstants.REF_OBJECT_URI_ORG_ORG_RELATION)) {
                    results.add(info);
                }
            }

        } catch (DoesNotExistException e) {
            throw new OperationFailedException("unable to OrgOrgRelationTypes for OrgType " + orgTypeKey, e);
        }
        return results;
    }

    @Override
    public List<TypeInfo> getOrgOrgRelationTypesForOrgHierarchy(String orgHierarchyId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<TypeInfo> results = new ArrayList<TypeInfo>();

        for(String id : getOrgHierarchy(orgHierarchyId, contextInfo).getOrgOrgRelationTypes()) {
            results.add(typeService.getType(id, contextInfo));
        }

        return results;
    }

    @Override
    public Boolean hasOrgOrgRelation(String orgId, String comparisonOrgId, String orgOrgRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return orgOrgRelationDao.hasOrgOrgRelation(orgId, comparisonOrgId, orgOrgRelationTypeKey);
    }

    @Override
    public OrgOrgRelationInfo getOrgOrgRelation(String orgOrgRelationId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgOrgRelationEntity entity = orgOrgRelationDao.find(orgOrgRelationId);
        if(null == entity) {
            throw new DoesNotExistException(orgOrgRelationId);
        }
        return entity.toDto();
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByIds(List<String> orgOrgRelationIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgOrgRelationEntity> entities = orgOrgRelationDao.findByIds(orgOrgRelationIds);
        List<OrgOrgRelationInfo> result = new ArrayList<OrgOrgRelationInfo>(entities.size());
        for (OrgOrgRelationEntity entity : entities) {
            if (entity == null) {
                // if one of the entities from "findByIds" is returned as null, then one of the keys in the list was not found
                throw new DoesNotExistException();
            }
            result.add(entity.toDto());
        }
        return result;
    }

    @Override
    public List<String> getOrgOrgRelationIdsByType(String orgOrgRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return orgOrgRelationDao.getIdsByType(orgOrgRelationTypeKey);
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByOrg(String orgId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgOrgRelationInfo> list = new ArrayList<OrgOrgRelationInfo>();
        List<OrgOrgRelationEntity> entities = orgOrgRelationDao.getOrgOrgRelationsByOrg(orgId);
        if(entities != null) {
            for(OrgOrgRelationEntity entity : entities) {
                list.add(entity.toDto());
            }
        }
         return list;
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByOrgs(String orgId, String peerOrgId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgOrgRelationInfo> list = new ArrayList<OrgOrgRelationInfo>();
        List<OrgOrgRelationEntity> entities = orgOrgRelationDao.getOrgOrgRelationsByOrgs(orgId, peerOrgId);
        if(entities != null) {
            for(OrgOrgRelationEntity entity : entities) {
                list.add(entity.toDto());
            }
        }
        return list;
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByTypeAndOrg(String orgId, String orgOrgRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgOrgRelationInfo> list = new ArrayList<OrgOrgRelationInfo>();
        List<OrgOrgRelationEntity> entities = orgOrgRelationDao.getOrgOrgRelationsByTypeAndOrg(orgId, orgOrgRelationTypeKey);
        if(entities != null) {
            for(OrgOrgRelationEntity entity : entities) {
                list.add(entity.toDto());
            }
        }
        return list;
    }

    @Override
    public List<OrgOrgRelationInfo> getOrgOrgRelationsByTypeAndRelatedOrg(String relatedOrgId, String orgOrgRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgOrgRelationInfo> list = new ArrayList<OrgOrgRelationInfo>();
        List<OrgOrgRelationEntity> entities = orgOrgRelationDao.getOrgOrgRelationsByTypeAndRelatedOrg(relatedOrgId, orgOrgRelationTypeKey);
        if(entities != null) {
            for(OrgOrgRelationEntity entity : entities) {
                list.add(entity.toDto());
            }
        }
        return list;
    }

    @Override
    public List<String> searchForOrgOrgRelationIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<String> results = new ArrayList<String>();
        GenericQueryResults<OrgOrgRelationEntity> orgOrgRelations = orgOrgRelationCriteriaLookupService.lookup(OrgOrgRelationEntity.class, criteria);
        if (null != orgOrgRelations && orgOrgRelations.getResults().size() > 0) {
            for (OrgOrgRelationEntity orgOrgRelation : orgOrgRelations.getResults()) {
                results.add(orgOrgRelation.getId());
            }
        }
        return results;
    }

    @Override
    public List<OrgOrgRelationInfo> searchForOrgOrgRelations(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgOrgRelationInfo> results = new ArrayList<OrgOrgRelationInfo>();
        GenericQueryResults<OrgOrgRelationEntity> orgOrgRelations = orgOrgRelationCriteriaLookupService.lookup(OrgOrgRelationEntity.class, criteria);
        if (null != orgOrgRelations && orgOrgRelations.getResults().size() > 0) {
            for (OrgOrgRelationEntity orgOrgRelation : orgOrgRelations.getResults()) {
                results.add(orgOrgRelation.toDto());
            }
        }
        return results;
    }

    @Override
    public List<ValidationResultInfo> validateOrgOrgRelation(String validationTypeKey, String orgId, String orgPeerId, String orgOrgRelationTypeKey, OrgOrgRelationInfo orgOrgRelationInfo, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return new ArrayList<ValidationResultInfo>();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public OrgOrgRelationInfo createOrgOrgRelation(String orgId, String orgPeerId, String orgOrgRelationTypeKey, OrgOrgRelationInfo orgOrgRelationInfo, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
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

        OrgOrgRelationEntity entity = new OrgOrgRelationEntity(orgOrgRelationInfo);
        entity.setEntityCreated(contextInfo);

        orgOrgRelationDao.persist(entity);
        orgOrgRelationDao.getEm().flush();
        return entity.toDto();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public OrgOrgRelationInfo updateOrgOrgRelation(String orgOrgRelationId, OrgOrgRelationInfo orgOrgRelationInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException, VersionMismatchException {
        if(!orgOrgRelationId.equals(orgOrgRelationInfo.getId())) {
            throw new ReadOnlyException(orgOrgRelationId + " does not match the id in the object " + orgOrgRelationInfo.getId());
        }
        OrgOrgRelationEntity entity = orgOrgRelationDao.find(orgOrgRelationId);
        if(null == entity) {
            throw new DoesNotExistException(orgOrgRelationId);
        }

        if(!orgOrgRelationId.equals(entity.getId())) {
            throw new ReadOnlyException(orgOrgRelationId + " does not match the id in the entity " + entity.getId());
        }

        if(!entity.getOrgOrgRelationType().equals(orgOrgRelationInfo.getTypeKey())) {
            throw new ReadOnlyException("The typekey in the updated object does not match the existing typekey");
        }

        if(!entity.getOrgId().equals(orgOrgRelationInfo.getOrgId())) {
            throw new ReadOnlyException("The orgId in the updated object does not match the existing orgId");
        }

        if(!entity.getRelatedOrgId().equals(orgOrgRelationInfo.getRelatedOrgId())) {
            throw new ReadOnlyException("The relatedOrgId in the updated object does not match the existing relatedOrgId");
        }

        entity.fromDto(orgOrgRelationInfo);
        entity.setEntityUpdated(contextInfo);
        entity = orgOrgRelationDao.merge(entity);
        orgOrgRelationDao.getEm().flush();
        return entity.toDto();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public StatusInfo deleteOrgOrgRelation(String orgOrgRelationId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgOrgRelationEntity entity = orgOrgRelationDao.find(orgOrgRelationId);
        if (null == entity) {
            throw new DoesNotExistException(orgOrgRelationId);
        }
        orgOrgRelationDao.remove(entity);
        StatusInfo status = new StatusInfo();
        status.setSuccess(Boolean.TRUE);
        return status;
    }

    @Override
    public List<TypeInfo> getOrgPersonRelationTypes(ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        try {
            return typeService.getTypesByRefObjectUri(OrganizationServiceConstants.REF_OBJECT_URI_ORG_PERSON_RELATION,contextInfo);
        } catch (DoesNotExistException e) {
            throw new OperationFailedException("Unable to pull any types for ref object URI " + OrganizationServiceConstants.REF_OBJECT_URI_ORG_PERSON_RELATION, e);
        }
    }

    @Override
    public List<TypeInfo> getOrgPersonRelationTypesForOrgType(String orgTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<TypeInfo> results = new ArrayList<TypeInfo>();
        try {
            List<TypeInfo> list = typeService.getAllowedTypesForType(orgTypeKey, contextInfo);
            for(TypeInfo info : list) {
                if(info.getRefObjectUri().equals(OrganizationServiceConstants.REF_OBJECT_URI_ORG_PERSON_RELATION)) {
                    results.add(info);
                }
            }

        } catch (DoesNotExistException e) {
            throw new OperationFailedException("unable to OrgPersonRelationTypes for OrgType " + orgTypeKey, e);
        }
        return results;
    }

    @Override
    public Boolean hasOrgPersonRelation(String orgId, String personId, String orgPersonRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return orgPersonRelationDao.hasOrgPersonRelation(orgId, personId, orgPersonRelationTypeKey);
    }

    @Override
    public OrgPersonRelationInfo getOrgPersonRelation(String orgPersonRelationId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgPersonRelationEntity entity = orgPersonRelationDao.find(orgPersonRelationId);
        if(null == entity) {
            throw new DoesNotExistException(orgPersonRelationId);
        }
        return entity.toDto();
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByIds(List<String> orgPersonRelationIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgPersonRelationEntity> entities = orgPersonRelationDao.findByIds(orgPersonRelationIds);
        List<OrgPersonRelationInfo> result = new ArrayList<OrgPersonRelationInfo>(entities.size());
        for (OrgPersonRelationEntity entity : entities) {
            result.add(entity.toDto());
        }
        return result;
    }

    @Override
    public List<String> getOrgPersonRelationIdsByType(String orgPersonRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return orgPersonRelationDao.getIdsByType(orgPersonRelationTypeKey);
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByOrg(String orgId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo>();
        List<OrgPersonRelationEntity> entities = orgPersonRelationDao.getOrgPersonRelationsByOrg(orgId);
        if(entities != null) {
            for(OrgPersonRelationEntity entity : entities) {
                list.add(entity.toDto());
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByTypeAndOrg(String orgPersonRelationTypeKey, String orgId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo>();
        List<OrgPersonRelationEntity> entities = orgPersonRelationDao.getOrgPersonRelationsByTypeAndOrg(orgPersonRelationTypeKey, orgId);
        if(entities != null) {
            for(OrgPersonRelationEntity entity : entities) {
                list.add(entity.toDto());
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByPerson(String personId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo>();
        List<OrgPersonRelationEntity> entities = orgPersonRelationDao.getOrgPersonRelationsByPerson(personId);
        if(entities != null) {
            for(OrgPersonRelationEntity entity : entities) {
                list.add(entity.toDto());
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByTypeAndPerson(String orgPersonRelationTypeKey, String personId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo>();
        List<OrgPersonRelationEntity> entities = orgPersonRelationDao.getOrgPersonRelationsByTypeAndPerson(orgPersonRelationTypeKey, personId);
        if(entities != null) {
            for(OrgPersonRelationEntity entity : entities) {
                list.add(entity.toDto());
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByOrgAndPerson(String orgId, String personId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo>();
        List<OrgPersonRelationEntity> entities = orgPersonRelationDao.getOrgPersonRelationsByOrgAndPerson(orgId, personId);
        if(entities != null) {
            for(OrgPersonRelationEntity entity : entities) {
                list.add(entity.toDto());
            }
        }
        return list;
    }

    @Override
    public List<OrgPersonRelationInfo> getOrgPersonRelationsByTypeAndOrgAndPerson(String orgPersonRelationTypeKey, String orgId, String personId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgPersonRelationInfo> list = new ArrayList<OrgPersonRelationInfo>();
        List<OrgPersonRelationEntity> entities = orgPersonRelationDao.getOrgPersonRelationsByTypeAndOrgAndPerson(orgPersonRelationTypeKey, orgId, personId);
        if(entities != null) {
            for(OrgPersonRelationEntity entity : entities) {
                list.add(entity.toDto());
            }
        }
        return list;

    }

    @Override
    public List<String> searchForOrgPersonRelationIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<String> results = new ArrayList<String>();
        GenericQueryResults<OrgPersonRelationEntity> orgPersonRelations = orgPersonRelationCriteriaLookupService.lookup(OrgPersonRelationEntity.class, criteria);
        if (null != orgPersonRelations && orgPersonRelations.getResults().size() > 0) {
            for (OrgPersonRelationEntity orgPersonRelation : orgPersonRelations.getResults()) {
                results.add(orgPersonRelation.getId());
            }
        }
        return results;
    }

    @Override
    public List<OrgPersonRelationInfo> searchForOrgPersonRelations(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgPersonRelationInfo> results = new ArrayList<OrgPersonRelationInfo>();
        GenericQueryResults<OrgPersonRelationEntity> orgPersonRelations = orgPersonRelationCriteriaLookupService.lookup(OrgPersonRelationEntity.class, criteria);
        if (null != orgPersonRelations && orgPersonRelations.getResults().size() > 0) {
            for (OrgPersonRelationEntity orgPersonRelation : orgPersonRelations.getResults()) {
                results.add(orgPersonRelation.toDto());
            }
        }
        return results;
    }

    @Override
    public List<ValidationResultInfo> validateOrgPersonRelation(String validationTypeKey, String orgId, String personId, String orgPersonRelationTypeKey, OrgPersonRelationInfo orgPersonRelationInfo, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return new ArrayList<ValidationResultInfo>();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public OrgPersonRelationInfo createOrgPersonRelation(String orgId, String personId, String orgPersonRelationTypeKey, OrgPersonRelationInfo orgPersonRelationInfo, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
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

        OrgPersonRelationEntity entity = new OrgPersonRelationEntity(orgPersonRelationInfo);
        entity.setEntityCreated(contextInfo);
        orgPersonRelationDao.persist(entity);
        orgPersonRelationDao.getEm().flush();
        return entity.toDto();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public OrgPersonRelationInfo updateOrgPersonRelation(String orgPersonRelationId, OrgPersonRelationInfo orgPersonRelationInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException, VersionMismatchException {
        if(!orgPersonRelationId.equals(orgPersonRelationInfo.getId())) {
            throw new ReadOnlyException(orgPersonRelationId + " does not match the id in the object " + orgPersonRelationInfo.getId());
        }
        OrgPersonRelationEntity entity = orgPersonRelationDao.find(orgPersonRelationId);
        if(null == entity) {
            throw new DoesNotExistException(orgPersonRelationId);
        }

        if(!orgPersonRelationId.equals(entity.getId())) {
            throw new ReadOnlyException(orgPersonRelationId + " does not match the id in the entity " + entity.getId());
        }
        if(!entity.getPersonId().equals(orgPersonRelationInfo.getPersonId())) {
            throw new ReadOnlyException(orgPersonRelationInfo.getPersonId() + " does not match the person id in the entity " + entity.getPersonId());
        }
        if(!entity.getOrgPersonRelationType().equals(orgPersonRelationInfo.getTypeKey())) {
            throw new ReadOnlyException(orgPersonRelationInfo.getTypeKey() + " does not match the typeKey in the entity " + entity.getOrgPersonRelationType());
        }

        entity.fromDto(orgPersonRelationInfo);
        entity.setEntityUpdated(contextInfo);
        entity = orgPersonRelationDao.merge(entity);
        orgPersonRelationDao.getEm().flush();
        return entity.toDto();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public StatusInfo deleteOrgPersonRelation(String orgPersonRelationId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgPersonRelationEntity entity = orgPersonRelationDao.find(orgPersonRelationId);
        if (null == entity) {
            throw new DoesNotExistException(orgPersonRelationId);
        }
        orgPersonRelationDao.remove(entity);
        return new StatusInfo();
    }

    @Override
    public OrgPositionRestrictionInfo getOrgPositionRestriction(String orgPositionRestrictionId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgPositionRestrictionEntity entity = orgPositionRestrictionDao.find(orgPositionRestrictionId);
        if(null == entity) {
            throw new DoesNotExistException(orgPositionRestrictionId);
        }
        return entity.toDto();
    }

    @Override
    public List<OrgPositionRestrictionInfo> getOrgPositionRestrictionsByIds(List<String> orgPositionRestrictionIds, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgPositionRestrictionEntity> entities = orgPositionRestrictionDao.findByIds(orgPositionRestrictionIds);
        List<OrgPositionRestrictionInfo> result = new ArrayList<OrgPositionRestrictionInfo>(entities.size());
        for (OrgPositionRestrictionEntity entity : entities) {
            if (entity == null) {
                // if one of the entities from "findByIds" is returned as null, then one of the keys in the list was not found
                throw new DoesNotExistException("One of the ids given is invalid and does not correspond to a valid OrgPositionRestriction");
            }
            result.add(entity.toDto());
        }
        return result;
    }

    @Override
    public List<String> getOrgPositionRestrictionIdsByType(String orgPersonRelationTypeKey, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return orgPositionRestrictionDao.getOrgPositionRestrictionIdsByType(orgPersonRelationTypeKey);
    }

    @Override
    public List<String> getOrgPositionRestrictionIdsByOrg(String orgId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return orgPositionRestrictionDao.getOrgPositionRestrictionIdsByOrg(orgId);
    }

    @Override
    public List<String> searchForOrgPositionRestrictionIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<String> results = new ArrayList<String>();
        GenericQueryResults<OrgPositionRestrictionEntity> orgPositionRestrictions = orgPositionRestrictionCriteriaLookupService.lookup(OrgPositionRestrictionEntity.class, criteria);
        if (null != orgPositionRestrictions && orgPositionRestrictions.getResults().size() > 0) {
            for (OrgPositionRestrictionEntity orgPositionRestriction : orgPositionRestrictions.getResults()) {
                results.add(orgPositionRestriction.getId());
            }
        }
        return results;
    }

    @Override
    public List<OrgPositionRestrictionInfo> searchForOrgPositionRestrictions(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        List<OrgPositionRestrictionInfo> results = new ArrayList<OrgPositionRestrictionInfo>();
        GenericQueryResults<OrgPositionRestrictionEntity> orgPositionRestrictions = orgPositionRestrictionCriteriaLookupService.lookup(OrgPositionRestrictionEntity.class, criteria);
        if (null != orgPositionRestrictions && orgPositionRestrictions.getResults().size() > 0) {
            for (OrgPositionRestrictionEntity orgPositionRestriction : orgPositionRestrictions.getResults()) {
                results.add(orgPositionRestriction.toDto());
            }
        }
        return results;
    }

    @Override
    public List<ValidationResultInfo> validateOrgPositionRestriction(String validationTypeKey, String orgId, String orgPersonRelationTypeKey, OrgPositionRestrictionInfo orgPositionRestrictionInfo, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        return new ArrayList<ValidationResultInfo>();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public OrgPositionRestrictionInfo createOrgPositionRestriction(String orgId, String orgPersonRelationTypeKey, OrgPositionRestrictionInfo orgPositionRestrictionInfo, ContextInfo contextInfo) throws AlreadyExistsException, DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        if(!orgId.equals(orgPositionRestrictionInfo.getOrgId())) {
            throw new InvalidParameterException(orgId + " does not match the corresponding value in the object " + orgPositionRestrictionInfo.getOrgId());
        }
        if(!orgPersonRelationTypeKey.equals(orgPositionRestrictionInfo.getOrgPersonRelationTypeKey())) {
            throw new InvalidParameterException(orgPersonRelationTypeKey + " does not match the corresponding value in the object " + orgPositionRestrictionInfo.getOrgPersonRelationTypeKey());
        }

        getOrg(orgId, contextInfo);

        OrgPositionRestrictionEntity entity = new OrgPositionRestrictionEntity(orgPositionRestrictionInfo);
        entity.setEntityCreated(contextInfo);
        orgPositionRestrictionDao.persist(entity);
        orgPositionRestrictionDao.getEm().flush();
        return entity.toDto();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public OrgPositionRestrictionInfo updateOrgPositionRestriction(String orgPositionRestrictionId, OrgPositionRestrictionInfo orgPositionRestrictionInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException, VersionMismatchException {
        if(!orgPositionRestrictionId.equals(orgPositionRestrictionInfo.getId())) {
            throw new InvalidParameterException(orgPositionRestrictionId + " does not match the id in the object " + orgPositionRestrictionInfo.getId());
        }
        OrgPositionRestrictionEntity entity = orgPositionRestrictionDao.find(orgPositionRestrictionId);
        if(null == entity) {
            throw new DoesNotExistException(orgPositionRestrictionId);
        }

        if(!orgPositionRestrictionId.equals(entity.getId())) {
            throw new ReadOnlyException(orgPositionRestrictionId + " does not match the id in the entity " + entity.getId());
        }

        if(!entity.getOrgPersonRelationType().equals(orgPositionRestrictionInfo.getOrgPersonRelationTypeKey())) {
            throw new ReadOnlyException(orgPositionRestrictionInfo.getOrgPersonRelationTypeKey() + " does not match the typeKey in the entity " + entity.getOrgPersonRelationType());
        }

        entity.fromDto(orgPositionRestrictionInfo);
        entity.setEntityUpdated(contextInfo);
        entity = orgPositionRestrictionDao.merge(entity);
        orgPositionRestrictionDao.getEm().flush();
        return entity.toDto();
    }

    @Override
    @Transactional(readOnly = false, noRollbackFor = {DoesNotExistException.class}, rollbackFor = {Throwable.class})
    public StatusInfo deleteOrgPositionRestriction(String orgPositionRestrictionId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgPositionRestrictionEntity entity = orgPositionRestrictionDao.find(orgPositionRestrictionId);
        if (null == entity) {
            throw new DoesNotExistException(orgPositionRestrictionId);
        }
        orgPositionRestrictionDao.remove(entity);
        return new StatusInfo();
    }

    @Override
    public Boolean isDescendant(String orgId, String descendantOrgId, String orgHierarchyId, ContextInfo contextInfo) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
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
    public List<String> getAllDescendants(String orgId, String orgHierarchyId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        Set<String> ids = new HashSet<String>();
        OrgTreeViewInfo descendantTree = getOrgTree(orgId, orgHierarchyId, Integer.MAX_VALUE, contextInfo);

        addDescendantTree(descendantTree, ids);
        ids.remove(orgId);

        return new ArrayList<String>(ids);
    }

    private void addDescendantTree(OrgTreeViewInfo orgTree, Set<String> ids) {
        for(OrgTreeViewInfo child : orgTree.getChildren()) {
            String childOrgId = child.getOrg().getId();
            //only recurse if we have not already been there
            if(ids.add(childOrgId)) {
                addDescendantTree(child, ids);
            }
        }
    }

    @Override
    public List<String> getAllAncestors(String orgId, String orgHierarchyId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        Set<String> ids = new HashSet<String>();
        OrgTreeViewInfo ancestorTree = getOrgTree(orgId, orgHierarchyId, Integer.MIN_VALUE, contextInfo);

        addAncestorsTree(ancestorTree, ids);
        ids.remove(orgId);

        return new ArrayList<String>(ids);
    }

    private void addAncestorsTree(OrgTreeViewInfo orgTree, Set<String> ids) {
        for(OrgTreeViewInfo parent : orgTree.getParents()) {
            String parentOrgId = parent.getOrg().getId();
            //only recurse if we have not already been there
            if(ids.add(parentOrgId)) {
                addAncestorsTree(parent, ids);
            }
        }
    }

    @Override
    public OrgTreeViewInfo getOrgTree(String rootOrgId, String orgHierarchyId, int maxLevels, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        OrgHierarchyInfo orgHierarchyInfo = getOrgHierarchy(orgHierarchyId, contextInfo);

        OrgTreeViewInfo orgTree = new OrgTreeViewInfo();

        OrgInfo org = getOrg(rootOrgId, contextInfo);
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
        if (maxLevels == 0 || (maxLevels >= 0 && currentLevel < maxLevels) || (maxLevels < 0 && currentLevel > maxLevels)) {
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
}

/*
 * 
 */
package org.kuali.student.service.remote.impl;

import java.util.Date;
import java.util.List;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.dto.ValidationResultInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.CircularRelationshipException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DependentObjectsExistException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.IllegalVersionSequencingException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.ReadOnlyException;
import org.kuali.student.r2.common.exceptions.UnsupportedActionException;
import org.kuali.student.r2.common.exceptions.VersionMismatchException;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;
import org.kuali.student.r2.core.versionmanagement.dto.VersionDisplayInfo;
import org.kuali.student.r2.lum.clu.dto.CluCluRelationInfo;
import org.kuali.student.r2.lum.clu.dto.CluInfo;
import org.kuali.student.r2.lum.clu.dto.CluLoRelationInfo;
import org.kuali.student.r2.lum.clu.dto.CluPublicationInfo;
import org.kuali.student.r2.lum.clu.dto.CluResultInfo;
import org.kuali.student.r2.lum.clu.dto.CluSetInfo;
import org.kuali.student.r2.lum.clu.dto.CluSetTreeViewInfo;
import org.kuali.student.r2.lum.clu.service.CluService;

public class CluServiceDecorator implements CluService {

    private CluService nextDecorator;

    public CluService getNextDecorator() throws OperationFailedException {
        if (null == nextDecorator) {
            throw new OperationFailedException("Misconfigured application: nextDecorator is null");
        }
        return nextDecorator;
    }

    // NEEDED because the version in LUM-API does not have a setter!
    public void setNextDecorator(CluService nextDecorator) {
        this.nextDecorator = nextDecorator;
    }

    @Override
    public List<TypeInfo> getDeliveryMethodTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getDeliveryMethodTypes(contextInfo);
    }

    @Override
    public TypeInfo getDeliveryMethodType(String deliveryMethodTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getDeliveryMethodType(deliveryMethodTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getInstructionalFormatTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getInstructionalFormatTypes(contextInfo);
    }

    @Override
    public TypeInfo getInstructionalFormatType(String instructionalFormatTypeKey, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getInstructionalFormatType(instructionalFormatTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getLuTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getLuTypes(contextInfo);
    }

    @Override
    public TypeInfo getLuType(String luTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getLuType(luTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getLuCodeTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getLuCodeTypes(contextInfo);
    }

    @Override
    public TypeInfo getLuCodeType(String luCodeTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getLuCodeType(luCodeTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getCluCluRelationTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluCluRelationTypes(contextInfo);
    }

    @Override
    public TypeInfo getLuLuRelationType(String cluCluRelationTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getLuLuRelationType(cluCluRelationTypeKey, contextInfo);
    }

    @Override
    public List<String> getAllowedLuLuRelationTypesForLuType(String luTypeKey, String relatedLuTypeKey, ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getAllowedLuLuRelationTypesForLuType(luTypeKey, relatedLuTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getLuPublicationTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getLuPublicationTypes(contextInfo);
    }

    @Override
    public TypeInfo getLuPublicationType(String luPublicationTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getLuPublicationType(luPublicationTypeKey, contextInfo);
    }

    @Override
    public List<String> getLuPublicationTypesForLuType(String luTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getLuPublicationTypesForLuType(luTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getCluResultTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluResultTypes(contextInfo);
    }

    @Override
    public TypeInfo getCluResultType(String cluResultTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluResultType(cluResultTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getCluResultTypesForLuType(String luTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluResultTypesForLuType(luTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getResultUsageTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getResultUsageTypes(contextInfo);
    }

    @Override
    public TypeInfo getResultUsageType(String resultUsageTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getResultUsageType(resultUsageTypeKey, contextInfo);
    }

    @Override
    public List<String> getAllowedResultUsageTypesForLuType(String luTypeKey, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getAllowedResultUsageTypesForLuType(luTypeKey, contextInfo);
    }

    @Override
    public List<String> getAllowedResultComponentTypesForResultUsageType(String resultUsageTypeKey, ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getAllowedResultComponentTypesForResultUsageType(resultUsageTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getCluLoRelationTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluLoRelationTypes(contextInfo);
    }

    @Override
    public TypeInfo getCluLoRelationType(String cluLoRelationTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluLoRelationType(cluLoRelationTypeKey, contextInfo);
    }

    @Override
    public List<String> getAllowedCluLoRelationTypesForLuType(String luTypeKey, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getAllowedCluLoRelationTypesForLuType(luTypeKey, contextInfo);
    }

    @Override
    public List<TypeInfo> getCluSetTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluSetTypes(contextInfo);
    }

    @Override
    public TypeInfo getCluSetType(String cluSetTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluSetType(cluSetTypeKey, contextInfo);
    }

    @Override
    public CluInfo getClu(String cluId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getClu(cluId, contextInfo);
    }

    @Override
    public List<CluInfo> getClusByIds(List<String> cluIds, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getClusByIds(cluIds, contextInfo);
    }

    @Override
    public List<CluInfo> getClusByLuType(String luTypeKey, String luState, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getClusByLuType(luTypeKey, luState, contextInfo);
    }

    @Override
    public List<String> getCluIdsByLuType(String luTypeKey, String luState, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluIdsByLuType(luTypeKey, luState, contextInfo);
    }

    @Override
    public List<String> getAllowedCluCluRelationTypesByClu(String cluId, String relatedCluId, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getAllowedCluCluRelationTypesByClu(cluId, relatedCluId, contextInfo);
    }

    @Override
    public List<CluInfo> getClusByRelatedCluAndRelationType(String relatedCluId, String cluCLuRelationTypeKey,
            ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getClusByRelatedCluAndRelationType(relatedCluId, cluCLuRelationTypeKey, contextInfo);
    }

    @Override
    public List<String> getCluIdsByRelatedCluAndRelationType(String relatedCluId, String cluCluRelationTypeKey,
            ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluIdsByRelatedCluAndRelationType(relatedCluId, cluCluRelationTypeKey, contextInfo);
    }

    @Override
    public List<CluInfo> getRelatedClusByCluAndRelationType(String cluId, String cluCluRelationTypeKey, ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getRelatedClusByCluAndRelationType(cluId, cluCluRelationTypeKey, contextInfo);
    }

    @Override
    public List<String> getRelatedCluIdsByCluAndRelationType(String cluId, String cluCluRelationTypeKey, ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getRelatedCluIdsByCluAndRelationType(cluId, cluCluRelationTypeKey, contextInfo);
    }

    @Override
    public CluCluRelationInfo getCluCluRelation(String cluCluRelationId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluCluRelation(cluCluRelationId, contextInfo);
    }

    @Override
    public List<CluCluRelationInfo> getCluCluRelationsByClu(String cluId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluCluRelationsByClu(cluId, contextInfo);
    }

    @Override
    public List<CluPublicationInfo> getCluPublicationsByClu(String cluId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluPublicationsByClu(cluId, contextInfo);
    }

    @Override
    public List<CluPublicationInfo> getCluPublicationsByType(String luPublicationTypeKey, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluPublicationsByType(luPublicationTypeKey, contextInfo);
    }

    @Override
    public CluPublicationInfo getCluPublication(String cluPublicationId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluPublication(cluPublicationId, contextInfo);
    }

    @Override
    public CluResultInfo getCluResult(String cluResultId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluResult(cluResultId, contextInfo);
    }

    @Override
    public List<CluResultInfo> getCluResultByClu(String cluId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getCluResultByClu(cluId, contextInfo);
    }

    @Override
    public List<CluResultInfo> getCluResultsByClus(List<String> cluIds, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getCluResultsByClus(cluIds, contextInfo);
    }

    @Override
    public List<String> getCluIdsByResultUsageType(String resultUsageTypeKey, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getCluIdsByResultUsageType(resultUsageTypeKey, contextInfo);
    }

    @Override
    public List<String> getCluIdsByResultComponent(String resultComponentId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getCluIdsByResultComponent(resultComponentId, contextInfo);
    }

    @Override
    public CluLoRelationInfo getCluLoRelation(String cluLoRelationId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluLoRelation(cluLoRelationId, contextInfo);
    }

    @Override
    public List<CluLoRelationInfo> getCluLoRelationsByClu(String cluId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluLoRelationsByClu(cluId, contextInfo);
    }

    @Override
    public List<CluLoRelationInfo> getCluLoRelationsByLo(String loId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluLoRelationsByLo(loId, contextInfo);
    }

    @Override
    public List<String> getResourceRequirementsForClu(String cluId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getResourceRequirementsForClu(cluId, contextInfo);
    }

    @Override
    public CluSetInfo getCluSet(String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluSet(cluSetId, contextInfo);
    }

    @Override
    public CluSetTreeViewInfo getCluSetTreeView(String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluSetTreeView(cluSetId, contextInfo);
    }

    @Override
    public List<CluSetInfo> getCluSetsByIds(List<String> cluSetIds, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluSetsByIds(cluSetIds, contextInfo);
    }

    @Override
    public List<String> getCluSetIdsFromCluSet(String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluSetIdsFromCluSet(cluSetId, contextInfo);
    }

    @Override
    public Boolean isCluSetDynamic(String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().isCluSetDynamic(cluSetId, contextInfo);
    }

    @Override
    public List<CluInfo> getClusFromCluSet(String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getClusFromCluSet(cluSetId, contextInfo);
    }

    @Override
    public List<String> getCluIdsFromCluSet(String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCluIdsFromCluSet(cluSetId, contextInfo);
    }

    @Override
    public List<CluInfo> getAllClusInCluSet(String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getAllClusInCluSet(cluSetId, contextInfo);
    }

    @Override
    public List<String> getAllCluIdsInCluSet(String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getAllCluIdsInCluSet(cluSetId, contextInfo);
    }

    @Override
    public Boolean isCluInCluSet(String cluId, String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().isCluInCluSet(cluId, cluSetId, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateClu(String validationTypeKey, CluInfo cluInfo, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().validateClu(validationTypeKey, cluInfo, contextInfo);
    }

    @Override
    public CluInfo createClu(String luTypeKey, CluInfo cluInfo, ContextInfo contextInfo) throws DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException {
        return getNextDecorator().createClu(luTypeKey, cluInfo, contextInfo);
    }

    @Override
    public CluInfo updateClu(String cluId, CluInfo cluInfo, ContextInfo contextInfo) throws DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException,
            VersionMismatchException {
        return getNextDecorator().updateClu(cluId, cluInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteClu(String cluId, ContextInfo contextInfo) throws DependentObjectsExistException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().deleteClu(cluId, contextInfo);
    }

    @Override
    public CluInfo createNewCluVersion(String cluId, String versionComment, ContextInfo contextInfo) throws
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException {
        return getNextDecorator().createNewCluVersion(cluId, versionComment, contextInfo);
    }

    @Override
    public StatusInfo setCurrentCluVersion(String cluVersionId, Date currentVersionStart, ContextInfo contextInfo) throws
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            IllegalVersionSequencingException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().setCurrentCluVersion(cluVersionId, currentVersionStart, contextInfo);
    }

    @Override
    public CluInfo updateCluState(String cluId, String luState, ContextInfo contextInfo) throws DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException,
            VersionMismatchException {
        return getNextDecorator().updateCluState(cluId, luState, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateCluCluRelation(String validationTypeKey, String cluId, String relatedCluId,
            String cluCluRelationTypeKey, CluCluRelationInfo cluCluRelationInfo, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().validateCluCluRelation(validationTypeKey, cluId, relatedCluId, cluCluRelationTypeKey,
                cluCluRelationInfo, contextInfo);
    }

    @Override
    public CluCluRelationInfo createCluCluRelation(String cluId, String relatedCluId, String cluCluRelationTypeKey,
            CluCluRelationInfo cluCluRelationInfo, ContextInfo contextInfo) throws CircularRelationshipException,
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException {
        return getNextDecorator().createCluCluRelation(cluId, relatedCluId, cluCluRelationTypeKey, cluCluRelationInfo, contextInfo);
    }

    @Override
    public CluCluRelationInfo updateCluCluRelation(String cluCluRelationId, CluCluRelationInfo cluCluRelationInfo,
            ContextInfo contextInfo) throws DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException,
            VersionMismatchException {
        return getNextDecorator().updateCluCluRelation(cluCluRelationId, cluCluRelationInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteCluCluRelation(String cluCluRelationId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().deleteCluCluRelation(cluCluRelationId, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateCluPublication(String validationTypeKey, String cluId, String luPublicationTypeKey,
            CluPublicationInfo cluPublicationInfo, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().validateCluPublication(validationTypeKey, cluId, luPublicationTypeKey, cluPublicationInfo,
                contextInfo);
    }

    @Override
    public CluPublicationInfo createCluPublication(String cluId, String luPublicationTypeKey,
            CluPublicationInfo cluPublicationInfo, ContextInfo contextInfo) throws DataValidationErrorException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException {
        return getNextDecorator().createCluPublication(cluId, luPublicationTypeKey, cluPublicationInfo, contextInfo);
    }

    @Override
    public CluPublicationInfo updateCluPublication(String cluPublicationId, CluPublicationInfo cluPublicationInfo,
            ContextInfo contextInfo) throws DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException,
            VersionMismatchException {
        return getNextDecorator().updateCluPublication(cluPublicationId, cluPublicationInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteCluPublication(String cluPublicationId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            DependentObjectsExistException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().deleteCluPublication(cluPublicationId, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateCluResult(String validationTypeKey, String cluId, String cluResultTypeKey,
            CluResultInfo cluResultInfo, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().validateCluResult(validationTypeKey, cluId, cluResultTypeKey, cluResultInfo, contextInfo);
    }

    @Override
    public CluResultInfo createCluResult(String cluId, String cluResultTypeKey, CluResultInfo cluResultInfo,
            ContextInfo contextInfo) throws DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException {
        return getNextDecorator().createCluResult(cluId, cluResultTypeKey, cluResultInfo, contextInfo);
    }

    @Override
    public CluResultInfo updateCluResult(String cluResultId, CluResultInfo cluResultInfo, ContextInfo contextInfo) throws
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException,
            VersionMismatchException {
        return getNextDecorator().updateCluResult(cluResultId, cluResultInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteCluResult(String cluResultId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            DependentObjectsExistException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().deleteCluResult(cluResultId, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateCluLoRelation(String validationTypeKey, String cluId, String loId,
            String cluLoRelationTypeKey, CluLoRelationInfo cluLoRelationInfo, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().validateCluLoRelation(validationTypeKey, cluId, loId, cluLoRelationTypeKey, cluLoRelationInfo,
                contextInfo);
    }

    @Override
    public CluLoRelationInfo createCluLoRelation(String cluId, String loId, String cluLoRelationTypeKey,
            CluLoRelationInfo cluLoRelationInfo, ContextInfo contextInfo) throws DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException {
        return getNextDecorator().createCluLoRelation(cluId, loId, cluLoRelationTypeKey, cluLoRelationInfo, contextInfo);
    }

    @Override
    public CluLoRelationInfo updateCluLoRelation(String cluLoRelationId, CluLoRelationInfo cluLoRelationInfo,
            ContextInfo contextInfo) throws DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException,
            VersionMismatchException {
        return getNextDecorator().updateCluLoRelation(cluLoRelationId, cluLoRelationInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteCluLoRelation(String cluLoRelationId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().deleteCluLoRelation(cluLoRelationId, contextInfo);
    }

    @Override
    public StatusInfo addCluResourceRequirement(String resourceTypeKey, String cluId, ContextInfo contextInfo) throws
            AlreadyExistsException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().addCluResourceRequirement(resourceTypeKey, cluId, contextInfo);
    }

    @Override
    public StatusInfo removeCluResourceRequirement(String resourceTypeKey, String cluId, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().removeCluResourceRequirement(resourceTypeKey, cluId, contextInfo);
    }

    @Override
    public List<ValidationResultInfo> validateCluSet(String validationTypeKey, String cluSetTypeKey, CluSetInfo cluSetInfo,
            ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().validateCluSet(validationTypeKey, cluSetTypeKey, cluSetInfo, contextInfo);
    }

    @Override
    public CluSetInfo createCluSet(String cluSetTypeKey, CluSetInfo cluSetInfo, ContextInfo contextInfo) throws
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException,
            UnsupportedActionException {
        return getNextDecorator().createCluSet(cluSetTypeKey, cluSetInfo, contextInfo);
    }

    @Override
    public CluSetInfo updateCluSet(String cluSetId, CluSetInfo cluSetInfo, ContextInfo contextInfo) throws
            CircularRelationshipException,
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            ReadOnlyException,
            UnsupportedActionException,
            VersionMismatchException {
        return getNextDecorator().updateCluSet(cluSetId, cluSetInfo, contextInfo);
    }

    @Override
    public StatusInfo deleteCluSet(String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().deleteCluSet(cluSetId, contextInfo);
    }

    @Override
    public StatusInfo addCluSetToCluSet(String cluSetId, String addedCluSetId, ContextInfo contextInfo) throws
            CircularRelationshipException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            UnsupportedActionException {
        return getNextDecorator().addCluSetToCluSet(cluSetId, addedCluSetId, contextInfo);
    }

    @Override
    public StatusInfo addCluSetsToCluSet(String cluSetId, List<String> addedCluSetIds, ContextInfo contextInfo) throws
            CircularRelationshipException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            UnsupportedActionException {
        return getNextDecorator().addCluSetsToCluSet(cluSetId, addedCluSetIds, contextInfo);
    }

    @Override
    public StatusInfo removeCluSetFromCluSet(String cluSetId, String removedCluSetId, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            UnsupportedActionException {
        return getNextDecorator().removeCluSetFromCluSet(cluSetId, removedCluSetId, contextInfo);
    }

    @Override
    public StatusInfo addCluToCluSet(String cluId, String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            UnsupportedActionException {
        return getNextDecorator().addCluToCluSet(cluId, cluSetId, contextInfo);
    }

    @Override
    public StatusInfo addClusToCluSet(List<String> cluSetIds, String cluSetId, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            UnsupportedActionException {
        return getNextDecorator().addClusToCluSet(cluSetIds, cluSetId, contextInfo);
    }

    @Override
    public StatusInfo removeCluFromCluSet(String cluId, String cluSetId, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            UnsupportedActionException {
        return getNextDecorator().removeCluFromCluSet(cluId, cluSetId, contextInfo);
    }

    @Override
    public List<CluInfo> searchForClus(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().searchForClus(criteria, contextInfo);
    }

    @Override
    public List<String> searchForCluIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().searchForCluIds(criteria, contextInfo);
    }

    @Override
    public List<CluCluRelationInfo> searchForCluCluRelations(QueryByCriteria criteria, ContextInfo contextInfo) throws
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().searchForCluCluRelations(criteria, contextInfo);
    }

    @Override
    public List<String> searchForCluCluRelationIds(QueryByCriteria criteria, ContextInfo contextInfo) throws
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().searchForCluCluRelationIds(criteria, contextInfo);
    }

    @Override
    public List<CluLoRelationInfo> searchForCluLoRelations(QueryByCriteria criteria, ContextInfo contextInfo) throws
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().searchForCluLoRelations(criteria, contextInfo);
    }

    @Override
    public List<String> searchForCluLoRelationIds(QueryByCriteria criteria, ContextInfo contextInfo) throws
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().searchForCluLoRelationIds(criteria, contextInfo);
    }

    @Override
    public List<CluPublicationInfo> searchForCluPublications(QueryByCriteria criteria, ContextInfo contextInfo) throws
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().searchForCluPublications(criteria, contextInfo);
    }

    @Override
    public List<String> searchForCluPublicationIds(QueryByCriteria criteria, ContextInfo contextInfo) throws
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().searchForCluPublicationIds(criteria, contextInfo);
    }

    @Override
    public List<CluResultInfo> searchForCluResults(QueryByCriteria criteria, ContextInfo contextInfo) throws
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().searchForCluResults(criteria, contextInfo);
    }

    @Override
    public List<String> searchForCluResultIds(QueryByCriteria criteria, ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().searchForCluResultIds(criteria, contextInfo);
    }

    @Override
    public List<VersionDisplayInfo> getVersions(String refObjectUri, String refObjectId, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getVersions(refObjectUri, refObjectId, contextInfo);
    }

    @Override
    public VersionDisplayInfo getFirstVersion(String refObjectUri, String refObjectId, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getFirstVersion(refObjectUri, refObjectId, contextInfo);
    }

    @Override
    public VersionDisplayInfo getLatestVersion(String refObjectUri, String refObjectId, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getLatestVersion(refObjectUri, refObjectId, contextInfo);
    }

    @Override
    public VersionDisplayInfo getCurrentVersion(String refObjectUri, String refObjectId, ContextInfo contextInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCurrentVersion(refObjectUri, refObjectId, contextInfo);
    }

    @Override
    public VersionDisplayInfo getVersionBySequenceNumber(String refObjectUri, String refObjectId, Long sequence,
            ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getVersionBySequenceNumber(refObjectUri, refObjectId, sequence, contextInfo);
    }

    @Override
    public VersionDisplayInfo getCurrentVersionOnDate(String refObjectUri, String refObjectId, Date date, ContextInfo contextInfo)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getCurrentVersionOnDate(refObjectUri, refObjectId, date, contextInfo);
    }

    @Override
    public List<VersionDisplayInfo> getVersionsInDateRange(String refObjectUri, String refObjectId, Date from, Date to,
            ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().getVersionsInDateRange(refObjectUri, refObjectId, from, to, contextInfo);
    }

    @Override
    public List<TypeInfo> getSearchTypes(ContextInfo contextInfo) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getSearchTypes(contextInfo);
    }

    @Override
    public TypeInfo getSearchType(String searchTypeKey, ContextInfo contextInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getSearchType(searchTypeKey, contextInfo);
    }

    @Override
    public SearchResultInfo search(SearchRequestInfo searchRequestInfo, ContextInfo contextInfo) throws MissingParameterException,
            InvalidParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().search(searchRequestInfo, contextInfo);
    }

    

}

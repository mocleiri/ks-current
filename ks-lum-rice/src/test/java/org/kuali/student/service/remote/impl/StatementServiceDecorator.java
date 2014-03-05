/*
 * 
 */
package org.kuali.student.service.remote.impl;

import org.kuali.student.r1.common.dictionary.dto.ObjectStructureDefinition;
import org.kuali.student.r1.core.statement.dto.StatementTreeViewInfo;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kuali.student.r1.core.statement.dto.NlUsageTypeInfo;
import org.kuali.student.r1.core.statement.dto.RefStatementRelationInfo;
import org.kuali.student.r1.core.statement.dto.RefStatementRelationTypeInfo;
import org.kuali.student.r1.core.statement.dto.ReqComponentInfo;
import org.kuali.student.r1.core.statement.dto.ReqComponentTypeInfo;
import org.kuali.student.r1.core.statement.dto.StatementInfo;
import org.kuali.student.r1.core.statement.dto.StatementTypeInfo;
import org.kuali.student.r1.core.statement.service.StatementService;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.ValidationResultInfo;
import org.kuali.student.r2.common.exceptions.AlreadyExistsException;
import org.kuali.student.r2.common.exceptions.CircularReferenceException;
import org.kuali.student.r2.common.exceptions.DataValidationErrorException;
import org.kuali.student.r2.common.exceptions.DoesNotExistException;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;
import org.kuali.student.r2.common.exceptions.VersionMismatchException;
import org.kuali.student.r2.core.class1.type.dto.TypeInfo;
import org.kuali.student.r2.core.search.dto.SearchRequestInfo;
import org.kuali.student.r2.core.search.dto.SearchResultInfo;

public class StatementServiceDecorator implements StatementService {

    private StatementService nextDecorator;

    public StatementService getNextDecorator() throws OperationFailedException {
        if (null == nextDecorator) {
            throw new OperationFailedException("Misconfigured application: nextDecorator is null");
        }
        return nextDecorator;
    }

    // NEEDED because the version in LUM-API does not have a setter!
    public void setNextDecorator(StatementService nextDecorator) {
        this.nextDecorator = nextDecorator;
    }

    @Override
    public List<String> getRefObjectTypes() throws OperationFailedException {
        return getNextDecorator().getRefObjectTypes();
    }

    @Override
    public List<String> getRefObjectSubTypes(String objectTypeKey) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getRefObjectSubTypes(objectTypeKey);
    }

    @Override
    public List<NlUsageTypeInfo> getNlUsageTypes() throws OperationFailedException {
        return getNextDecorator().getNlUsageTypes();
    }

    @Override
    public NlUsageTypeInfo getNlUsageType(String nlUsageTypeKey) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getNlUsageType(nlUsageTypeKey);
    }

    @Override
    public RefStatementRelationInfo createRefStatementRelation(RefStatementRelationInfo refStatementRelationInfo) throws
            AlreadyExistsException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().createRefStatementRelation(refStatementRelationInfo);
    }

    @Override
    public RefStatementRelationInfo updateRefStatementRelation(String refStatementRelationId,
            RefStatementRelationInfo refStatementRelationInfo) throws DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            VersionMismatchException {
        return getNextDecorator().updateRefStatementRelation(refStatementRelationId, refStatementRelationInfo);
    }

    @Override
    public org.kuali.student.r1.common.dto.StatusInfo deleteRefStatementRelation(String refStatementRelationId) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().deleteRefStatementRelation(refStatementRelationId);
    }

    @Override
    public List<ValidationResultInfo> validateRefStatementRelation(String validationType,
            RefStatementRelationInfo refStatementRelationInfo) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().validateRefStatementRelation(validationType, refStatementRelationInfo);
    }

    @Override
    public RefStatementRelationInfo getRefStatementRelation(String refStatementRelationId) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getRefStatementRelation(refStatementRelationId);
    }

    @Override
    public List<RefStatementRelationInfo> getRefStatementRelationsByRef(String refObjectTypeKey, String refObjectId) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getRefStatementRelationsByRef(refObjectTypeKey, refObjectId);
    }

    @Override
    public List<RefStatementRelationInfo> getRefStatementRelationsByStatement(String statementId) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getRefStatementRelationsByStatement(statementId);
    }

    @Override
    public String getNaturalLanguageForStatement(String statementId, String nlUsageTypeKey, String language) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getNaturalLanguageForStatement(statementId, nlUsageTypeKey, language);
    }

    @Override
    public String getNaturalLanguageForRefStatementRelation(String refStatementRelationId, String nlUsageTypeKey, String language)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getNaturalLanguageForRefStatementRelation(refStatementRelationId, nlUsageTypeKey, language);
    }

    @Override
    public String getNaturalLanguageForReqComponent(String reqComponentId, String nlUsageTypeKey, String language) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getNaturalLanguageForReqComponent(reqComponentId, nlUsageTypeKey, language);
    }

    @Override
    public String translateStatementTreeViewToNL(StatementTreeViewInfo statementTreeViewInfo, String nlUsageTypeKey,
            String language) throws InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().translateStatementTreeViewToNL(statementTreeViewInfo, nlUsageTypeKey, language);
    }

    @Override
    public String translateReqComponentToNL(ReqComponentInfo reqComponentInfo, String nlUsageTypeKey, String language) throws
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().translateReqComponentToNL(reqComponentInfo, nlUsageTypeKey, language);
    }

    @Override
    public List<ValidationResultInfo> validateReqComponent(String validationType, ReqComponentInfo reqComponentInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().validateReqComponent(validationType, reqComponentInfo);
    }

    @Override
    public List<ValidationResultInfo> validateStatement(String validationType, StatementInfo statementInfo) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().validateStatement(validationType, statementInfo);
    }

    @Override
    public StatementInfo getStatement(String statementId) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getStatement(statementId);
    }

    @Override
    public List<StatementInfo> getStatementsByType(String statementTypeKey) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getStatementsByType(statementTypeKey);
    }

    @Override
    public ReqComponentInfo getReqComponent(String reqComponentId) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getReqComponent(reqComponentId);
    }

    @Override
    public List<ReqComponentInfo> getReqComponentsByType(String reqComponentTypeKey) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getReqComponentsByType(reqComponentTypeKey);
    }

    @Override
    public List<StatementInfo> getStatementsUsingReqComponent(String reqComponentId) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getStatementsUsingReqComponent(reqComponentId);
    }

    @Override
    public List<StatementInfo> getStatementsUsingStatement(String statementId) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getStatementsUsingStatement(statementId);
    }

    @Override
    public ReqComponentInfo createReqComponent(String reqComponentType, ReqComponentInfo reqComponentInfo) throws
            AlreadyExistsException,
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().createReqComponent(reqComponentType, reqComponentInfo);
    }

    @Override
    public org.kuali.student.r1.common.dto.StatusInfo deleteReqComponent(String reqComponentId) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().deleteReqComponent(reqComponentId);
    }

    @Override
    public StatementInfo createStatement(String statementType, StatementInfo statementInfo) throws AlreadyExistsException,
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().createStatement(statementType, statementInfo);
    }

    @Override
    public StatementInfo updateStatement(String statementId, StatementInfo statementInfo) throws CircularReferenceException,
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            VersionMismatchException {
        return getNextDecorator().updateStatement(statementId, statementInfo);
    }

    @Override
    public org.kuali.student.r1.common.dto.StatusInfo deleteStatement(String statementId) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().deleteStatement(statementId);
    }

    @Override
    public StatementTypeInfo getStatementType(String statementTypeKey) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getStatementType(statementTypeKey);
    }

    @Override
    public List<StatementTypeInfo> getStatementTypes() throws OperationFailedException {
        return getNextDecorator().getStatementTypes();
    }

    @Override
    public List<String> getStatementTypesForStatementType(String statementTypeKey) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getStatementTypesForStatementType(statementTypeKey);
    }

    @Override
    public List<ReqComponentTypeInfo> getReqComponentTypes() throws OperationFailedException {
        return getNextDecorator().getReqComponentTypes();
    }

    @Override
    public ReqComponentTypeInfo getReqComponentType(String reqComponentTypeKey) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getReqComponentType(reqComponentTypeKey);
    }

    @Override
    public List<ReqComponentTypeInfo> getReqComponentTypesForStatementType(String statementTypeKey) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getReqComponentTypesForStatementType(statementTypeKey);
    }

    @Override
    public List<RefStatementRelationTypeInfo> getRefStatementRelationTypes() throws OperationFailedException {
        return getNextDecorator().getRefStatementRelationTypes();
    }

    @Override
    public RefStatementRelationTypeInfo getRefStatementRelationType(String refStatementRelationTypeKey) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getRefStatementRelationType(refStatementRelationTypeKey);
    }

    @Override
    public List<String> getStatementTypesForRefStatementRelationType(String refStatementRelationTypeKey) throws
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getStatementTypesForRefStatementRelationType(refStatementRelationTypeKey);
    }

    @Override
    public List<String> getRefStatementRelationTypesForRefObjectSubType(String refSubTypeKey) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getRefStatementRelationTypesForRefObjectSubType(refSubTypeKey);
    }

    @Override
    public ReqComponentInfo updateReqComponent(String reqComponentId, ReqComponentInfo reqComponentInfo) throws
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            VersionMismatchException {
        return getNextDecorator().updateReqComponent(reqComponentId, reqComponentInfo);
    }

    @Override
    public StatementTreeViewInfo getStatementTreeView(String statementId) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getStatementTreeView(statementId);
    }

    @Override
    public StatementTreeViewInfo getStatementTreeViewForNlUsageType(String statementId, String nlUsageTypeKey, String language)
            throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException {
        return getNextDecorator().getStatementTreeViewForNlUsageType(statementId, nlUsageTypeKey, language);
    }

    @Override
    public StatementTreeViewInfo updateStatementTreeView(String statementId, StatementTreeViewInfo statementTreeViewInfo) throws
            CircularReferenceException,
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException,
            VersionMismatchException {
        return getNextDecorator().updateStatementTreeView(statementId, statementTreeViewInfo);
    }

    @Override
    public StatementTreeViewInfo createStatementTreeView(StatementTreeViewInfo statementTreeViewInfo) throws
            CircularReferenceException,
            AlreadyExistsException,
            DataValidationErrorException,
            DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().createStatementTreeView(statementTreeViewInfo);
    }

    @Override
    public org.kuali.student.r1.common.dto.StatusInfo deleteStatementTreeView(String statementId) throws DoesNotExistException,
            InvalidParameterException,
            MissingParameterException,
            OperationFailedException,
            PermissionDeniedException {
        return getNextDecorator().deleteStatementTreeView(statementId);
    }

    

    @Override
    public List<String> getObjectTypes() {
        try {
            return getNextDecorator().getObjectTypes();
        } catch (OperationFailedException ex) {
            throw new RuntimeException (ex);
        }
    }

    @Override
    public ObjectStructureDefinition getObjectStructure(String objectTypeKey) {
        try {
            return getNextDecorator().getObjectStructure(objectTypeKey);
        } catch (OperationFailedException ex) {
            throw new RuntimeException (ex);
        }
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

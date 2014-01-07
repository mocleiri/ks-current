package com.sigmasys.kuali.ksa.service.atp;

import com.sigmasys.kuali.ksa.service.UserSessionManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.exceptions.*;
import org.kuali.student.r2.core.atp.dto.AtpAtpRelationInfo;
import org.kuali.student.r2.core.atp.dto.AtpInfo;
import org.kuali.student.r2.core.atp.dto.MilestoneInfo;
import org.kuali.student.r2.core.class1.atp.service.impl.AtpServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * KSA-specific AtpService implementation.
 *
 * @author Michael Ivanov
 */
@Transactional
public class AtpServiceDecorator extends AtpServiceImpl implements AtpService {

    private static final Log logger = LogFactory.getLog(AtpServiceDecorator.class);


    @Autowired
    private UserSessionManager userSessionManager;


    /**
     * Returns the default ATP ContextInfo object.
     *
     * @return ContextInfo instance
     */
    @Override
    @Transactional(readOnly = true)
    public ContextInfo getAtpContextInfo() {
        IdentityService identityService = KimApiServiceLocator.getIdentityService();
        Principal principal = identityService.getPrincipalByPrincipalName(userSessionManager.getUserId());
        ContextInfo contextInfo = new ContextInfo();
        contextInfo.setAuthenticatedPrincipalId(principal.getPrincipalId());
        contextInfo.setPrincipalId(principal.getPrincipalId());
        contextInfo.setCurrentDate(new Date());
        return contextInfo;
    }

    /**
     * Checks with the ATP service whether the given ATP ID exists in the system.
     *
     * @param atpId ATP ID to check
     * @return true if the ATP ID exists, false - otherwise
     */
    @Override
    @Transactional(readOnly = true, noRollbackFor = DoesNotExistException.class)
    public boolean atpExists(String atpId) {
        // Checking if the ATP ID exists using AtpService
        boolean atpExists;
        try {
            atpExists = getAtp(atpId, getAtpContextInfo()) != null;
        } catch (DoesNotExistException e) {
            logger.error(e.getMessage(), e);
            atpExists = false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return atpExists;
    }


    @Override
    public AtpInfo updateAtp(String atpId, AtpInfo atpInfo, ContextInfo context) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, VersionMismatchException {
        atpInfo = super.updateAtp(atpId, atpInfo, context);
        getAtpDao().getEm().flush();
        return atpInfo;
    }

    @Override
    public AtpInfo createAtp(String atpTypeKey, AtpInfo atpInfo, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        atpInfo = super.createAtp(atpTypeKey, atpInfo, contextInfo);
        getAtpDao().getEm().flush();
        return atpInfo;
    }

    @Override
    public StatusInfo deleteAtp(String atpId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        StatusInfo statusInfo = super.deleteAtp(atpId, contextInfo);
        getAtpDao().getEm().flush();
        return statusInfo;
    }

    @Override
    public MilestoneInfo createMilestone(String milestoneTypeKey, MilestoneInfo milestoneInfo, ContextInfo contextInfo) throws DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        milestoneInfo = super.createMilestone(milestoneTypeKey, milestoneInfo, contextInfo);
        getMilestoneDao().getEm().flush();
        return milestoneInfo;
    }

    @Override
    public StatusInfo addMilestoneToAtp(String milestoneId, String atpId, ContextInfo contextInfo) throws AlreadyExistsException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        StatusInfo statusInfo = super.addMilestoneToAtp(milestoneId, atpId, contextInfo);
        getAtpMilestoneRelationDao().getEm().flush();
        return statusInfo;
    }

    @Override
    public StatusInfo removeMilestoneFromAtp(String milestoneId, String atpId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        StatusInfo statusInfo = super.removeMilestoneFromAtp(milestoneId, atpId, contextInfo);
        getAtpMilestoneRelationDao().getEm().flush();
        return statusInfo;
    }

    @Override
    public StatusInfo deleteMilestone(String milestoneId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        StatusInfo statusInfo = super.deleteMilestone(milestoneId, contextInfo);
        getMilestoneDao().getEm().flush();
        return statusInfo;
    }

    @Override
    public MilestoneInfo updateMilestone(String milestoneId, MilestoneInfo milestoneInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, VersionMismatchException {
        milestoneInfo = super.updateMilestone(milestoneId, milestoneInfo, contextInfo);
        getMilestoneDao().getEm().flush();
        return milestoneInfo;
    }

    @Override
    public AtpAtpRelationInfo createAtpAtpRelation(String atpId, String relatedAtpId, String atpAtpRelationTypeKey, AtpAtpRelationInfo atpAtpRelationInfo, ContextInfo contextInfo) throws DoesNotExistException, DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, ReadOnlyException {
        atpAtpRelationInfo = super.createAtpAtpRelation(atpId, relatedAtpId, atpAtpRelationTypeKey, atpAtpRelationInfo, contextInfo);
        getAtpRelDao().getEm().flush();
        return atpAtpRelationInfo;
    }

    @Override
    public AtpAtpRelationInfo updateAtpAtpRelation(String atpAtpRelationId, AtpAtpRelationInfo atpAtpRelationInfo, ContextInfo contextInfo) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, VersionMismatchException {
        atpAtpRelationInfo = super.updateAtpAtpRelation(atpAtpRelationId, atpAtpRelationInfo, contextInfo);
        getAtpRelDao().getEm().flush();
        return atpAtpRelationInfo;
    }

    @Override
    public StatusInfo deleteAtpAtpRelation(String atpAtpRelationId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        StatusInfo statusInfo = super.deleteAtpAtpRelation(atpAtpRelationId, contextInfo);
        getAtpRelDao().getEm().flush();
        return statusInfo;
    }
}

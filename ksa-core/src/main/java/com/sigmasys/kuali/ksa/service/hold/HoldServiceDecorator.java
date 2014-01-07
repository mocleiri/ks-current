package com.sigmasys.kuali.ksa.service.hold;

import com.sigmasys.kuali.ksa.service.UserSessionManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.student.enrollment.class1.hold.service.impl.HoldServiceImpl;
import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.common.dto.RichTextInfo;
import org.kuali.student.r2.common.dto.StatusInfo;
import org.kuali.student.r2.common.exceptions.*;
import org.kuali.student.r2.core.constants.HoldServiceConstants;
import org.kuali.student.r2.core.hold.dto.AppliedHoldInfo;
import org.kuali.student.r2.core.hold.dto.HoldIssueInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * KSA-specific HoldService implementation.
 *
 * @author Michael Ivanov
 */
@Transactional
public class HoldServiceDecorator extends HoldServiceImpl implements HoldService {

    private static final Log logger = LogFactory.getLog(HoldServiceDecorator.class);

    @Autowired
    private UserSessionManager userSessionManager;


    /**
     * Returns the default Hold ContextInfo object.
     *
     * @return ContextInfo instance
     */
    @Override
    @Transactional(readOnly = true)
    public ContextInfo getHoldContextInfo() {
        IdentityService identityService = KimApiServiceLocator.getIdentityService();
        Principal principal = identityService.getPrincipalByPrincipalName(userSessionManager.getUserId());
        ContextInfo contextInfo = new ContextInfo();
        contextInfo.setAuthenticatedPrincipalId(principal.getPrincipalId());
        contextInfo.setPrincipalId(principal.getPrincipalId());
        contextInfo.setCurrentDate(new Date());
        return contextInfo;
    }


    @Override
    public AppliedHoldInfo createAppliedHold(String personId, String issueId, String holdTypeKey, AppliedHoldInfo holdInfo, ContextInfo context) throws DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        AppliedHoldInfo appliedHoldInfo = super.createAppliedHold(personId, issueId, holdTypeKey, holdInfo, context);
        getAppliedHoldDao().getEm().flush();
        return appliedHoldInfo;
    }

    @Override
    public AppliedHoldInfo updateAppliedHold(String holdId, AppliedHoldInfo holdInfo, ContextInfo context) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, VersionMismatchException {
        AppliedHoldInfo appliedHoldInfo = super.updateAppliedHold(holdId, holdInfo, context);
        getAppliedHoldDao().getEm().flush();
        return appliedHoldInfo;
    }

    @Override
    public AppliedHoldInfo releaseAppliedHold(String holdId, ContextInfo context) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        AppliedHoldInfo appliedHoldInfo = super.releaseAppliedHold(holdId, context);
        getAppliedHoldDao().getEm().flush();
        return appliedHoldInfo;
    }

    @Override
    public StatusInfo deleteAppliedHold(String holdId, ContextInfo context) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        StatusInfo statusInfo = super.deleteAppliedHold(holdId, context);
        getAppliedHoldDao().getEm().flush();
        return statusInfo;
    }

    @Override
    public HoldIssueInfo createHoldIssue(String issueTypeKey, HoldIssueInfo issueInfo, ContextInfo context) throws DataValidationErrorException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException {
        HoldIssueInfo holdIssueInfo = super.createHoldIssue(issueTypeKey, issueInfo, context);
        getHoldIssueDao().getEm().flush();
        return holdIssueInfo;
    }

    @Override
    public HoldIssueInfo updateHoldIssue(String issueId, HoldIssueInfo issueInfo, ContextInfo context) throws DataValidationErrorException, DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, VersionMismatchException {
        HoldIssueInfo holdIssueInfo = super.updateHoldIssue(issueId, issueInfo, context);
        getHoldIssueDao().getEm().flush();
        return holdIssueInfo;
    }

    @Override
    public StatusInfo deleteHoldIssue(String issueId, ContextInfo contextInfo) throws DoesNotExistException, InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException, DependentObjectsExistException {
        StatusInfo statusInfo = super.deleteHoldIssue(issueId, contextInfo);
        getHoldIssueDao().getEm().flush();
        return statusInfo;
    }

    /**
     * Retrieves HoldIssueInfo objects by User ID and ContextInfo.
     *
     * @param userId User ID
     * @return list of HoldIssueInfo instances
     */
    @Override
    @Transactional(readOnly = true)
    public List<HoldIssueInfo> getHoldIssuesByUserId(String userId) {

        final ContextInfo contextInfo = getHoldContextInfo();

        try {

            List<AppliedHoldInfo> appliedHolds = getAppliedHoldsByPerson(userId, contextInfo);

            if (CollectionUtils.isNotEmpty(appliedHolds)) {

                Set<String> holdIssueIds = new HashSet<String>();

                for (AppliedHoldInfo appliedHold : appliedHolds) {
                    holdIssueIds.add(appliedHold.getHoldIssueId());
                }

                return getHoldIssuesByIds(new ArrayList<String>(holdIssueIds), contextInfo);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

        return Collections.emptyList();
    }

    /**
     * Retrieves HoldIssue names by User ID and ContextInfo.
     *
     * @param userId User ID
     * @return list of HoldIssue names
     */
    @Override
    @Transactional(readOnly = true)
    public List<String> getHoldIssueNamesByUserId(String userId) {

        List<HoldIssueInfo> holdIssues = getHoldIssuesByUserId(userId);

        List<String> holdIssueNames = new ArrayList<String>(holdIssues.size());

        for (HoldIssueInfo holdIssue : holdIssues) {
            holdIssueNames.add(holdIssue.getName());
        }

        return holdIssueNames;
    }

    /**
     * Creates and persists a new AppliedHoldInfo entity based on the given parameters.
     *
     * @param userId          User ID
     * @param holdIssueType   Hold Issue type key
     * @param holdIssueName   Hold Issue name
     * @param holdName        Applied Hold name
     * @param holdDescription Applied Hold description
     * @param effectiveDate   Effective Date
     * @param expirationDate  Expiration Date
     * @return AppliedHoldInfo instance
     */
    @Override
    public AppliedHoldInfo createAppliedHold(String userId,
                                             String holdIssueType,
                                             String holdIssueName,
                                             String holdName,
                                             String holdDescription,
                                             Date effectiveDate,
                                             Date expirationDate) {

        final ContextInfo contextInfo = getHoldContextInfo();

        try {

            List<String> holdIssueIds = getHoldIssueIdsByType(holdIssueType, contextInfo);
            if (CollectionUtils.isEmpty(holdIssueIds)) {
                String errMsg = "Cannot find HoldIssue IDs for '" + holdIssueType + "' hold issue type";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            List<HoldIssueInfo> holdIssueInfos = getHoldIssuesByIds(holdIssueIds, contextInfo);

            HoldIssueInfo holdIssueInfo = null;
            for (HoldIssueInfo holdIssue : holdIssueInfos) {
                if (holdIssue.getName().equalsIgnoreCase(holdIssueName)) {
                    holdIssueInfo = holdIssue;
                    break;
                }
            }

            if (holdIssueInfo == null) {
                String errMsg = "Cannot find HoldIssueInfo for '" + holdIssueType + "' type and '" + holdIssueName + "' name";
                logger.error(errMsg);
                throw new IllegalStateException(errMsg);
            }

            AppliedHoldInfo hold = new AppliedHoldInfo();

            hold.setHoldIssueId(holdIssueInfo.getId());
            hold.setPersonId(userId);

            hold.setTypeKey(HoldServiceConstants.STUDENT_HOLD_TYPE_KEY);
            hold.setStateKey(HoldServiceConstants.HOLD_ACTIVE_STATE_KEY);

            hold.setName(holdName);
            hold.setDescr(new RichTextInfo(holdDescription, holdDescription));

            hold.setEffectiveDate(effectiveDate);
            hold.setReleasedDate(expirationDate);

            return createAppliedHold(userId, holdIssueInfo.getId(), hold.getTypeKey(), hold, contextInfo);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }

    }

}

package com.sigmasys.kuali.ksa.service.hold;

import org.kuali.student.r2.common.dto.ContextInfo;
import org.kuali.student.r2.core.hold.dto.AppliedHoldInfo;
import org.kuali.student.r2.core.hold.dto.HoldIssueInfo;

import java.util.Date;
import java.util.List;

/**
 * KSA-specific HoldService interface inherited from KS's HoldService
 *
 * @author Michael Ivanov
 */
public interface HoldService extends org.kuali.student.r2.core.hold.service.HoldService {

    /**
     * Returns the default Hold ContextInfo object.
     *
     * @return ContextInfo instance
     */
    ContextInfo getHoldContextInfo();

    /**
     * Retrieves HoldIssueInfo objects by User ID and ContextInfo.
     *
     * @param userId User ID
     * @return list of HoldIssueInfo instances
     */
    List<HoldIssueInfo> getHoldIssuesByUserId(String userId);

    /**
     * Retrieves HoldIssue names by User ID and ContextInfo.
     *
     * @param userId User ID
     * @return list of HoldIssue names
     */
    List<String> getHoldIssueNamesByUserId(String userId);

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
    AppliedHoldInfo createAppliedHold(String userId,
                                      String holdIssueType,
                                      String holdIssueName,
                                      String holdName,
                                      String holdDescription,
                                      Date effectiveDate,
                                      Date expirationDate);

}

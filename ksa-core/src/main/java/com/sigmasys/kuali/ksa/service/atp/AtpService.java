package com.sigmasys.kuali.ksa.service.atp;


import org.kuali.student.r2.common.dto.ContextInfo;

/**
 * KSA-specific AtpService interface inherited from KS's AtpService
 *
 * @author Michael Ivanov
 */
public interface AtpService extends org.kuali.student.r2.core.atp.service.AtpService {


    /**
     * Returns the default ATP ContextInfo object.
     *
     * @return ContextInfo instance
     */
    ContextInfo getAtpContextInfo();


    /**
     * Checks with the ATP service whether the given ATP ID exists in the system.
     *
     * @param atpId ATP ID to check
     * @return true if the ATP ID exists, false - otherwise
     */
    boolean atpExists(String atpId);

}

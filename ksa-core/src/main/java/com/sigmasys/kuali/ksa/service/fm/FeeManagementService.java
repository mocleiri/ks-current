package com.sigmasys.kuali.ksa.service.fm;

import com.sigmasys.kuali.ksa.annotation.Url;
import com.sigmasys.kuali.ksa.model.Constants;
import com.sigmasys.kuali.ksa.model.fm.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * FeeManagementService.
 * <p/>
 * This interface describes the new FeeManagement Service defined in the "Process Diagrams" document.
 *
 * @author Sergey Godunov
 */
@Url(FeeManagementService.SERVICE_URL)
@WebService(serviceName = FeeManagementService.SERVICE_NAME, portName = FeeManagementService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
public interface FeeManagementService {

    String SERVICE_URL = "feeManagement.webservice";
    String SERVICE_NAME = "FeeManagementService";
    String PORT_NAME = SERVICE_NAME + "Port";

    /**
     * Reconciles a FeeManagement session.
     * Session reconciliation takes former sessions and ensures that charges that need to be removed are
     * accurately recorded in the new session.
     *
     * @param feeManagementSessionId FeeManagementSession ID
     */
    void reconcileSession(Long feeManagementSessionId);

    /**
     * Creates charges on the current Fee Management session.
     * Creates reversals and cancellations and also rate transactions.
     *
     * @param feeManagementSessionId FeeManagementSession ID
     */
    void chargeSession(Long feeManagementSessionId);

    /**
     * Returns all FM Manifests associated with an FM Session with the given ID.
     *
     * @param feeManagementSessionId An FM Session ID.
     * @return All associated FM Manifests.
     */
    List<FeeManagementManifest> getManifests(Long feeManagementSessionId);

    /**
     * Returns all FM Manifests of given types associated with an FM Session with the given ID.
     *
     * @param feeManagementSessionId An FM Session ID.
     * @param manifestTypes          Types of FM Manifests to retrieve.
     * @return All associated FM Manifests of given types.
     */
    @WebMethod(exclude = true)
    List<FeeManagementManifest> getManifests(Long feeManagementSessionId, FeeManagementManifestType... manifestTypes);

    /**
     * Queues a FeeManagementSession.
     *
     * @param feeManagementSessionId ID of an FM Session to queue.
     */
    void addFeeManagementSessionToQueue(Long feeManagementSessionId);

    /**
     * Dequeues a FeeManagementSession.
     *
     * @param feeManagementSessionId ID of an FM Session to dequeue.
     */
    void removeFeeManagementSessionFromQueue(Long feeManagementSessionId);

    /**
     * This method kicks off the workflow for assessing fees by creating a fee management session and then
     * queuing it up for later execution.
     *
     * @param feeManagementTermRecord FM Term Record for FM Session creation.
     * @return FeeManagementSession instance
     */
    FeeManagementSession queueFeeManagementSession(FeeManagementTermRecord feeManagementTermRecord);

    /**
     * Accesses an FM Session and invokes the Rules Engine to create a Manifest.
     *
     * @param feeManagementSessionId ID of an FM session to process.
     * @return FeeManagementSession instance
     */
    FeeManagementSession processFeeManagementSession(Long feeManagementSessionId);

    /**
     * Creates a new FM Session using the given FM TermRecord.
     *
     * @param feeManagementTermRecord FM TermRecord.
     * @return The newly generated FM Session.
     */
    FeeManagementSession createFeeManagementSession(FeeManagementTermRecord feeManagementTermRecord);

    /**
     * Creates a report info from an FM Session. Documentation to this method reads:
     * In order to report back a simulated session, we can use this method to create a simplified version of the
     * manifest. This can be sent back to an external system.
     * NOTE: FeeManagementReportInfo is a non-persistent object.
     *
     * @param feeManagementSessionId ID of an FM Session for which to create a report.
     * @return The newly created report object.
     */
    FeeManagementReportInfo createFeeManagementReport(Long feeManagementSessionId);

    /**
     * Creates a new FM Session and actually charges it.
     *
     * @param feeManagementTermRecord A transient FM data holder.
     * @return FeeManagementSession instance
     */
    FeeManagementSession assessRealTimeFeeManagement(FeeManagementTermRecord feeManagementTermRecord);

    /**
     * Simulates Real Time FeeManagement. Doesn't actually charge the FM Session, but rather creates
     * a report displaying what would have happened if the FM Session had been charged.
     *
     * @param feeManagementTermRecord A transient FM data holder.
     * @return A report with Fee Management assessment.
     */
    FeeManagementReportInfo simulateRealTimeFeeManagement(FeeManagementTermRecord feeManagementTermRecord);


    /**
     * Creates and persists a new instance of FeeManagementManifest class for the given parameters.
     *
     * @param manifestType      FeeManagementManifestType enum value
     * @param manifestStatus    FeeManagementManifestStatus enum value
     * @param transactionTypeId Transaction type ID
     * @param rateId            Rate ID
     * @param internalChargeId  Internal Charge ID
     * @param registrationId    Registration ID
     * @param offeringId        Offering ID
     * @param effectiveDate     Effective Date
     * @param recognitionDate   Recognition Date
     * @param amount            Manifest amount
     * @param isSessionCurrent  Indicates if the FM session should be current or not.
     * @return FeeManagementManifest instance
     */
    FeeManagementManifest createFeeManagementManifest(FeeManagementManifestType manifestType,
                                                      FeeManagementManifestStatus manifestStatus,
                                                      String transactionTypeId,
                                                      Long rateId,
                                                      String internalChargeId,
                                                      String registrationId,
                                                      String offeringId,
                                                      Date effectiveDate,
                                                      Date recognitionDate,
                                                      BigDecimal amount,
                                                      boolean isSessionCurrent);

    /**
     * Returns FeeManagementSession instance by ID from the persistent store.
     *
     * @param sessionId FeeManagementSession ID
     * @return FeeManagementSession instance
     */
    FeeManagementSession getFeeManagementSession(Long sessionId);

}

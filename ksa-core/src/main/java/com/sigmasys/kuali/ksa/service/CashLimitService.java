package com.sigmasys.kuali.ksa.service;

import com.sigmasys.kuali.ksa.annotation.Url;
import com.sigmasys.kuali.ksa.model.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Cash limit service API.
 * <p/>
 *
 * @author Michael Ivanov
 */
@Url(CashLimitService.SERVICE_URL)
@WebService(serviceName = CashLimitService.SERVICE_NAME, portName = CashLimitService.PORT_NAME,
        targetNamespace = Constants.WS_NAMESPACE)
public interface CashLimitService {

    String SERVICE_URL = "cashLimit.webservice";
    String SERVICE_NAME = "CashLimitService";
    String PORT_NAME = SERVICE_NAME + "Port";

    /**
     * Creates a new cash limit parameter for the given parameters.
     *
     * @param code          Parameter code
     * @param name          Parameter name
     * @param description   Parameter description
     * @param tag           Parameter tag
     * @param lowerLimit    Lower limit
     * @param upperLimit    Upper limit
     * @param isActive      Indicates whether the new parameter should be active upon creation
     * @param authorityName Authority name
     * @param xmlElement    XML element name
     * @return a new instance of CashLimitParameter
     */
    CashLimitParameter createCashLimitParameter(String code, String name, String description, Tag tag,
                                                BigDecimal lowerLimit, BigDecimal upperLimit, boolean isActive,
                                                String authorityName, String xmlElement);

    /**
     * Persists the given cash limit parameter in the persistent store
     *
     * @param cashLimitParameter CashLimitParameter instance
     * @return Cash limit parameter ID
     */
    Long persistCashLimitParameter(CashLimitParameter cashLimitParameter);

    /**
     * This method will return true if there has been a cash limit event. A <code>CashLimitEvent</code> object will be
     * created and persisted and an email will be sent to the appropriate administrator to allow them to check
     * up on the event. A further step will be required to actually register the event (by producing an 8300
     * form to be filed with the IRS).
     *
     * @param userId Account ID
     * @return true if there has been a cash limit event, otherwise false
     */
    boolean checkCashLimit(String userId);

    /**
     * Retrieves all CashLimitEvent objects for the given list of Account IDs and CashLimitEvent statuses
     * and where the "Event Date" falls in the specified date range.
     *
     * @param userIds  Account IDs
     * @param dateFrom Beginning of the date range for "Event Date" search.
     * @param dateTo   End of the date range for "Event Date" search.
     * @param statuses CashLimitEvent statuses
     * @return a list of cash limit events
     */
    @WebMethod(exclude = true)
    List<CashLimitEvent> getCashLimitEvents(List<String> userIds, Date dateFrom, Date dateTo, CashLimitEventStatus... statuses);

    /**
     * Retrieves all CashLimitEvent objects for the given list of Account IDs and CashLimitEvent statuses.
     *
     * @param userIds  Account IDs
     * @param statuses CashLimitEvent statuses
     * @return a list of cash limit events
     */
    @WebMethod(exclude = true)
    List<CashLimitEvent> getCashLimitEvents(List<String> userIds, CashLimitEventStatus... statuses);

    /**
     * Retrieves all CashLimitEvent objects for the given list of Account IDs.
     *
     * @param userIds Account IDs
     * @return a list of cash limit events
     */
    List<CashLimitEvent> getCashLimitEvents(List<String> userIds);

    /**
     * Returns CashLimitEvent object by ID.
     *
     * @param id Cash limit event ID
     * @return CashLimitEvent instance
     */
    CashLimitEvent getCashLimitEvent(Long id);

    /**
     * Retrieves cash limit parameters.
     *
     * @param activeOnly if true only active parameters will be retrieved.
     * @return a list of cash limit parameters
     */
    List<CashLimitParameter> getCashLimitParameters(boolean activeOnly);

    /**
     * Checks if the cash limit parameter exists by code.
     *
     * @param code CashLimitParameter's code
     * @return true if it exists, false - otherwise
     */
    boolean cashLimitParameterExists(String code);

    /**
     * Returns the cash limit parameter by code.
     *
     * @param code CashLimitParameter's code
     * @return CashLimitParameter instance
     */
    CashLimitParameter getCashLimitParameterByCode(String code);

    /**
     * Returns the cash limit parameter by ID.
     *
     * @param id CashLimitParameter's ID
     * @return CashLimitParameter instance
     */
    CashLimitParameter getCashLimitParameter(Long id);

    /**
     * Completes a CashLimitEvent with the given ID.
     * Only "Queued" CashLimitEvents can be Completed.
     * Optionally, generates an IRS form 8300.
     *
     * @param id               ID of a CashLimitEvent to complete.
     * @param generateForm8300 Whether to generate an IRS form 8300.
     * @return Completed CashLimitEvent
     */
    CashLimitEvent completeCashLimitEvent(Long id, boolean generateForm8300);

    /**
     * Ignores a CashLimitEvent with the given ID.
     * Only "Queued" CashLimitEvents can be Ignored.
     * Removes the stored IRS form 8300.
     *
     * @param id ID of a CashLimitEvent to ignore.
     * @return Ignored CashLimitEvent
     */
    CashLimitEvent ignoreCashLimitEvent(Long id);

    /**
     * Queues a CashLimitEvent with the given ID.
     * Both "Ignored" and "Completed" CashLimitEvents can be queued.
     * Removes the stored IRS form 8300.
     *
     * @param id ID of a CashLimitEvent to enqueue.
     * @return Queued CashLimitEvent
     */
    CashLimitEvent queueCashLimitEvent(Long id);
}

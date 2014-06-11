package org.kuali.student.enrollment.registration.client.service;


import org.kuali.student.enrollment.registration.client.service.dto.CourseSearchResult;
import org.kuali.student.enrollment.registration.client.service.dto.RegGroupSearchResult;
import org.kuali.student.r2.common.exceptions.InvalidParameterException;
import org.kuali.student.r2.common.exceptions.MissingParameterException;
import org.kuali.student.r2.common.exceptions.OperationFailedException;
import org.kuali.student.r2.common.exceptions.PermissionDeniedException;

import java.util.List;

/**
 * Note that common parameters used by this object's methods have the following meaning:
 * - termId: the atp.id field; ie: kuali.atp.2012Spring
 * - termCode: the atp.code field; ie: 201201 = Spring 2012
 * - courseCode: the lui.code field; ie: CHEM237
 * - courseOfferingId: the lui.id field; ie: 443be82c-41e0-4e4f-bc09-c1cf086264df
 * - activityOfferingId: ie: 44cb726b-01eb-4d86-9517-b45bef99c120
 * - regGroupName: ie: 1001
 * <p/>
 * In general, methods should only return a single specific entity when it is uniquely-identified by a unique identifier;
 * all other methods should return a list, even if the list contains only a single entity.
 * <p/>
 * In the specific-entity case, the URI should indicate it will return a single entity (ie: /courseoffering) and should
 * require the entity's unique identifier via @PathParam (ie: /courseoffering/{uid}).
 * <p/>
 * In the bulk-entity case, the URI should be pluralized (ie: /courseofferings) and arguments should be optional via
 * the @QueryParam (ie: /courseofferings?termCode=201208&courseCode=ENGL101).  This is true even in the case where a
 * specific combination of arguments will necessarily always return only a single entity in the results
 * (ie: /reggroups?termCode=201208&courseCode=ENGL250&regGroupName=1001)
 * <p/>
 * <p/>
 * It should be noted that this service is too broad in TYPE:
 * It deals with all types we offer (ie: CourseOffering, Activities, etc); it should be refactored into a
 * small set of sub-services ("Resources") where each Resource deals with a specific type
 * (ie: CourseOfferingResource, ActivitiesResource, etc) and collectively combined under a single
 * "Composite Service".
 */

public interface ScheduleOfClassesService {


/** COURSE OFFERINGS **/

    /**
     * Java Helper method.
     *
     * @param termId     required
     * @param courseCode required
     * @return Returns a list of course offerings
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws PermissionDeniedException
     * @throws OperationFailedException
     */
    List<CourseSearchResult> searchForCourseOfferingsByTermIdAndCourse(String termId, String courseCode) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException;

    /**
     * Java Helper method.
     *
     * @param termCode   required
     * @param courseCode required
     * @return Returns a list of course offerings
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws PermissionDeniedException
     * @throws OperationFailedException
     */
    List<CourseSearchResult> searchForCourseOfferingsByTermCodeAndCourse(String termCode, String courseCode) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException;

/** REGISTRATION GROUPS **/

    /**
     * Java Helper method.
     *
     * @param termId not required
     * @param termCode     required
     * @param courseCode   required
     * @param regGroupCode required
     * @return Returns a single registration group
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws OperationFailedException
     * @throws PermissionDeniedException
     */
    RegGroupSearchResult searchForRegistrationGroupByTermAndCourseAndRegGroup(String termId, String termCode, String courseCode, String regGroupCode) throws InvalidParameterException, MissingParameterException, OperationFailedException, PermissionDeniedException;

/** TERMS **/

    /**
     * Java Helper method.
     *
     * @param termCode required
     * @return Returns a term's id
     * @throws InvalidParameterException
     * @throws MissingParameterException
     * @throws PermissionDeniedException
     * @throws OperationFailedException
     */
    String getTermIdByTermCode(String termCode) throws InvalidParameterException, MissingParameterException, PermissionDeniedException, OperationFailedException;
}

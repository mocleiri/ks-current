package org.kuali.student.enrollment.registration.client.service;

import org.kuali.student.enrollment.courseregistration.dto.RegistrationResponseInfo;
import org.kuali.student.enrollment.registration.client.service.dto.StudentScheduleCourseResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * This class performance registration functions for a particular user. Unlike the ScheduleOfClasses service, each
 * method requires that the principal user information be populated in the context.
 */
@Path("/")
public interface CourseRegistrationClientService {

    /**
     * This is the "one click" registration method. It will first create a registration request then submit that
     * request to the registration engine.
     *
     * @param userId user id of the person you want to register in a course. This is for POC testing only and needs to be removed post POC for secuirty
     * @param termCode
     * @param courseCode
     * @param regGroupName
     * @param regGroupId  optional, but the term, course, and reg group name are not checked if you supply the id
     * @return The response should be instant and give a handle to the registrationRequestId. The registration engine is
     * ansynchonous so the client will need to poll the system for status updates.
     * @throws Exception
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/registerreggroup")
    public RegistrationResponseInfo registerForRegistrationGroupByTermCodeAndCourseCodeAndRegGroupName(@QueryParam("userId") String userId,
                                                                                                       @QueryParam("termCode") String termCode,
                                                                                                       @QueryParam("courseCode") String courseCode,
                                                                                                       @QueryParam("regGroupName") String regGroupName,
                                                                                                       @QueryParam("regGroupId") String regGroupId) throws Exception;

    /**
     * Returns statistics for the registration engine.
     *
     * @return JSON representing various statistics as a JSON-map of elements (ie: entities) where each element contains
     *          an array of key-value pairs.
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/stats/regengine")
    public Response getRegEngineStats();

    /** SEARCH for STUDENT REGISTRATION INFO
     * @param personId
     * @param termCode
     * @return StudentScheduleCourseResult
    **/
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/personschedule")
    public List<StudentScheduleCourseResult> searchForScheduleByPersonAndTerm(@QueryParam("person") String personId,
                                                                              @QueryParam("termCode") String termCode) throws Exception;

    /**
     * Clears the overall registration engine stats.
     * It will not clear the MQ stats plugin.
     *
     * @return
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/stats/regengine/clear")
    public Response clearRegEngineStats();

    /**
     * Deletes all LPRs for a person
     * Returns an empty list(should it return something else?)
     * @param personId
     * @return
     * @throws Exception
     */

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/clearpersonlprs")
    public List<StudentScheduleCourseResult> clearLPRsByPerson( @QueryParam("person") String personId) throws Exception;

}

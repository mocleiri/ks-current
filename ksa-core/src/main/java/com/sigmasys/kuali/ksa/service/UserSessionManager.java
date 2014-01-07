package com.sigmasys.kuali.ksa.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Set;

public interface UserSessionManager {


    /**
     * Creates a new HTTP session
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @param userId   the User ID
     * @return a new HTTPSession
     */
    HttpSession createSession(HttpServletRequest request, HttpServletResponse response, String userId);

    /**
     * Destroys the current HTTP session
     *
     * @param request the HTTP request
     */
    void destroySession(HttpServletRequest request);

    /**
     * Checks if the user session is valid.
     *
     * @param request the HTTP request
     * @return true if the session is valid.
     */
    boolean isSessionValid(HttpServletRequest request);

    /**
     * Returns the user ID. This method should be called after session creation and validation.
     *
     * @param request HttpServletRequest instance
     * @return the user ID
     */
    String getUserId(HttpServletRequest request);

    /**
     * Returns the user ID for the current LocalThread HttpServletRequest object.
     *
     * @return the user ID
     */
    String getUserId();

    /**
     * Returns a set of user permissions. This method should be called after session creation and validation.
     *
     * @param request HttpServletRequest instance
     * @return a set of user permission names.
     */
    Set<String> getUserPermissions(HttpServletRequest request);

    /**
     * Returns a set of user permissions for the current user.
     *
     * @return a set of user permission names.
     */
    Set<String> getUserPermissions();

}


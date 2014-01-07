package com.sigmasys.kuali.ksa.krad.controller;

import com.sigmasys.kuali.ksa.service.UserSessionManager;
import com.sigmasys.kuali.ksa.servlet.CoreFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * LogoutController
 *
 * @author Michael Ivanov
 */
@Controller
@RequestMapping(value = LogoutController.LOGOUT_SERVICE_URL)
public class LogoutController {

    private static final Log logger = LogFactory.getLog(LogoutController.class);

    public static final String LOGOUT_SERVICE_URL = "logout.service";

    @Autowired
    private UserSessionManager userSessionManager;


    /**
     * Destroys the current HTTP session and forwards the request to the login page.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {

        if (userSessionManager.isSessionValid(request)) {

            String userId = userSessionManager.getUserId(request);

            String loginUrl = (String) request.getSession(false).getAttribute(CoreFilter.LOGIN_URL_SESSION_KEY);

            userSessionManager.destroySession(request);

            logger.info("User " + userId + " has been logged out");

            request.setAttribute("errorMessage", "You have been successfully logged out");

            request.getRequestDispatcher(loginUrl).forward(request, response);
        }

        return null;
    }

    /**
     * Forwards the request to the KSA base URL.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @return ModelAndView
     */
    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView forward(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getRequestDispatcher("/").forward(request, response);
        return null;
    }


}

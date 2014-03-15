package com.sigmasys.kuali.ksa.servlet;

import com.sigmasys.kuali.ksa.config.ConfigService;
import com.sigmasys.kuali.ksa.krad.controller.LogoutController;
import com.sigmasys.kuali.ksa.service.AccountService;
import com.sigmasys.kuali.ksa.service.UserSessionManager;
import com.sigmasys.kuali.ksa.util.ContextUtils;
import com.sigmasys.kuali.ksa.util.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Main KSA Filter
 *
 * @author Michael Ivanov
 */
public class CoreFilter implements Filter {

    private static final Log logger = LogFactory.getLog(CoreFilter.class);

    public static final String REDIRECT_URL_PARAM_NAME = "redirectUrl";
    public static final String TRUSTED_URLS_PARAM_NAME = "trustedUrls";
    public static final String LOGIN_URL_SESSION_KEY = "_loginUrl";

    // Error messages
    private static final String COMMON_ERROR_MSG = "Error occurred";
    private static final String INVALID_CREDENTIALS_MSG = "Invalid user name or password";

    // Used for trusted URLs
    private static final String DEFAULT_USER_ID = "admin";

    private static final String DEFAULT_LOGIN_URL = "/WEB-INF/jsp/login.jsp";

    private final Set<String> trustedUrls = new HashSet<String>();

    private final Object lock = new Object();

    private boolean isInitialized;

    private String loginUrl;

    @Override
    public void init(FilterConfig config) throws ServletException {
        loginUrl = config.getInitParameter("loginUrl");
        if (loginUrl == null) {
            loginUrl = DEFAULT_LOGIN_URL;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void initTrustedUrls() {
        ConfigService configService = ContextUtils.getBean(ConfigService.class);
        String trustedUrls = configService.getParameter(TRUSTED_URLS_PARAM_NAME);
        if (StringUtils.isNotBlank(trustedUrls)) {
            String[] patterns = trustedUrls.split(",");
            for (String pattern : patterns) {
                pattern = pattern.trim();
                if (!pattern.isEmpty()) {
                    this.trustedUrls.add(pattern.toLowerCase());
                }
            }
        }
    }

    /**
     * Checks whether the URL is trusted so the webapp can proceed with request handling
     * without user authentication.
     *
     * @param request HttpServletRequest
     * @return true if trusted URL
     */
    private boolean isTrustedUrl(HttpServletRequest request) {
        String url = request.getRequestURI().toLowerCase();
        for (String trustedUrl : trustedUrls) {
            if (url.contains(trustedUrl)) {
                return true;
            }
        }
        return false;
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

        try {

            // Expose thread local response to the entire application
            RequestUtils.setThreadResponse(response);

            if (initializeSession(request, response)) {
                chain.doFilter(request, response);
            }

        } catch (Throwable t) {
            logger.error("Error occurred while serving HTTP request: " + t.getMessage(), t);
        } finally {
            RequestUtils.setThreadResponse(null);
        }
    }

    /**
     * Initializes HTTP KSA/KRAD session and related resources.
     *
     * @param request  HTTP request
     * @param response HTTP response
     * @return "true" if the filter chaining should proceed, "false" - otherwise
     * @throws ServletException
     * @throws IOException
     */
    private boolean initializeSession(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final UserSession kradSession = KRADUtils.getUserSessionFromRequest(request);

        final UserSessionManager sessionManager = ContextUtils.getBean(UserSessionManager.class);

        // Initializing services if necessary
        synchronized (lock) {
            if (!isInitialized) {
                initTrustedUrls();
                isInitialized = true;
            }
        }

        request.setAttribute("showPasswordField", true);

        if (kradSession == null || !sessionManager.isSessionValid(request)) {

            String queryString = request.getQueryString();

            final String redirectUrl = request.getRequestURI() + (queryString != null ? "?" + queryString : "");

            if (!redirectUrl.contains(LogoutController.LOGOUT_SERVICE_URL)) {
                request.setAttribute(REDIRECT_URL_PARAM_NAME, redirectUrl);
            }

            final String userId = request.getParameter("ksa_userId");
            final String password = request.getParameter("ksa_password");

            if (userId != null) {

                // Very simple password checking. Nothing hashed or encrypted.
                final IdentityService identityService = KimApiServiceLocator.getIdentityService();
                if (identityService == null) {
                    String errMsg = "IdentityService cannot be null";
                    logger.error(errMsg);
                    invalidateSession(request, response, errMsg);
                    return false;
                }

                Principal principal;

                try {
                    principal = isTrustedUrl(request) ?
                            identityService.getPrincipalByPrincipalName(DEFAULT_USER_ID) :
                            identityService.getPrincipalByPrincipalNameAndPassword(userId, password);
                } catch (RiceIllegalArgumentException re) {
                    logger.error(re.getMessage(), re);
                    invalidateSession(request, response, re.getMessage());
                    return false;
                }

                if (principal != null) {

                    // Creating HTTP session
                    final HttpSession session = sessionManager.createSession(request, response, userId);

                    // wrap the request with the remote user
                    // UserLoginFilter and WebAuthenticationService will create the session
                    request = new HttpServletRequestWrapper(request) {
                        @Override
                        public String getRemoteUser() {
                            return userId;
                        }
                    };

                    // Creating KRAD UserSession and put it as an attribute into HTTP session
                    UserSession userSession = new UserSession(userId) {

                        @Override
                        public String getKualiSessionId() {
                            return session.getId();
                        }

                        @Override
                        protected void initPerson(String principalName) {
                            try {
                                final PersonService personService = KimApiServiceLocator.getPersonService();
                                Field field = UserSession.class.getDeclaredField("person");
                                field.setAccessible(true);
                                field.set(this, personService.getPersonByPrincipalName(userId));
                            } catch (Exception e) {
                                logger.error(e.getMessage(), e);
                                throw new IllegalStateException(e.getMessage(), e);
                            }
                        }
                    };

                    session.setAttribute(KRADConstants.USER_SESSION_KEY, userSession);
                    session.setAttribute(LOGIN_URL_SESSION_KEY, loginUrl);

                    // If the KSA account does not exist the following method is supposed to create it
                    // from the corresponding KIM Person object
                    ContextUtils.getBean(AccountService.class).getOrCreateAccount(userId);

                    // If the request has redirectUrl parameter ->
                    // redirect to the location specified by redirectUrl
                    String targetUrl = request.getParameter(REDIRECT_URL_PARAM_NAME);
                    if (targetUrl != null && !targetUrl.trim().isEmpty()) {
                        response.sendRedirect(targetUrl);
                        return false;
                    }

                } else {

                    if (isInitialized) {
                        invalidateSession(request, response, INVALID_CREDENTIALS_MSG);
                    }

                    return false;
                }

            } else {

                // If this is an ajax request, don't send the login form, send a 403 that the krad.initialize.js set up to catch.
                String requestedWith = request.getHeader("X-Requested-With");
                if ("XMLHttpRequest".equals(requestedWith)) {
                    response.resetBuffer();
                    response.sendError(403, request.getContextPath());
                    return false;
                }


                // No session has been established and this is not a login form submission,
                // so forward to login page
                request.getRequestDispatcher(loginUrl).forward(request, response);
                return false;
            }

        } else {

            request = new HttpServletRequestWrapper(request) {
                @Override
                public String getRemoteUser() {
                    return kradSession.getPrincipalName();
                }
            };
        }

        logger.info("USER-REQUEST-INFO: user = '" + sessionManager.getUserId(request) +
                "' requested URI = '" + request.getRequestURI() + "'");

        return true;
    }

    /**
     * Handles and invalid login attempt.
     *
     * @param request  the incoming request
     * @param response the outgoing response
     * @throws ServletException if unable to handle the invalid login
     * @throws IOException      if unable to handle the invalid login
     */
    private void invalidateSession(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws ServletException, IOException {

        final UserSessionManager sessionManager = ContextUtils.getBean(UserSessionManager.class);

        if (sessionManager != null) {
            sessionManager.destroySession(request);
        }

        if (StringUtils.isNotBlank(errorMessage)) {
            errorMessage = errorMessage.substring(0, 1).toUpperCase() + (errorMessage.length() > 1 ? errorMessage.substring(1) : "");
        } else {
            errorMessage = COMMON_ERROR_MSG;
        }

        request.setAttribute("errorMessage", errorMessage);
        request.getRequestDispatcher(loginUrl).forward(request, response);
    }

    @Override
    public void destroy() {
        loginUrl = null;
        trustedUrls.clear();
    }
}

package edu.uw.kuali.student.web;

import org.kuali.rice.krad.web.filter.UserLoginFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.Override;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: hemanthg
 * Date: 2/21/13
 * Time: 11:35 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyPlanUserLoginFilter extends UserLoginFilter {

    /*Filter which sees if the request session is expired.
     * if so redirects to session expired page */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest hReq = (HttpServletRequest) req;
        HttpServletResponse hResp = (HttpServletResponse) resp;

        /*If no user present and attribute lastSesionId is not present
        * Redirect to the session expired page*/
        if (hReq.getRemoteUser() == null && hReq.getSession().getAttribute("lastSessionId") == null) {
            hResp.sendRedirect("/student/myplan/sessionExpired");
        }
        /*If  user present and attribute lastSessionId is  present  and the lsrSessionId is same as the currentSessionId
         * Check if the currentTime is after expireTime attribute if so then redirect to session expiredPage
         * Otherwise set the lastSessionId and the expireTime and proceed further*/
        else if (hReq.getRemoteUser() != null && hReq.getSession().getAttribute("lastSessionId") != null && hReq.getSession().getId().equals(hReq.getSession().getAttribute("lastSessionId"))) {
            Date currentTime = new Date(System.currentTimeMillis());
            Date expiredTime = new Date((Long) hReq.getSession().getAttribute("expireTime"));
            if (currentTime.after(expiredTime)) {
                hResp.sendRedirect("/student/myplan/sessionExpired");
            } else {
                hReq.getSession().setAttribute("lastSessionId", hReq.getSession().getId());
                hReq.getSession().setAttribute("expireTime", System.currentTimeMillis() + (hReq.getSession().getMaxInactiveInterval() * 1000));
                super.doFilter(req, resp, chain);
            }


        }
        /*In any case other than the above two set the lastSessionId and the expireTime and proceed further*/
        else {
            hReq.getSession().setAttribute("lastSessionId", hReq.getSession().getId());
            hReq.getSession().setAttribute("expireTime", System.currentTimeMillis() + (hReq.getSession().getMaxInactiveInterval() * 1000));
            super.doFilter(req, resp, chain);
        }

    }

}

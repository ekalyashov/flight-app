package cselp.util;


import cselp.Const;
import cselp.bean.ISessionData;
import cselp.bean.LdapUser;
import cselp.bean.SessionData;
import cselp.domain.local.Person;
import cselp.dto.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utility class, contains web helper methods.
 */
public class WebUtil {
    private static final Log log = LogFactory.getLog(WebUtil.class);

    private WebUtil() {
    }

    private static HttpServletRequest getHttpRequest() {
        RequestAttributes attr = RequestContextHolder.getRequestAttributes();
        if (attr instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) attr).getRequest();
        }
        return null;
    }

    private static HttpSession getHttpSession(boolean create) {
        HttpServletRequest request = getHttpRequest();
        if (request != null) {
            return request.getSession(create);
        }
        else {
            return null;
        }
    }

    /**
     * method to suppress IllegalStateException if session invalidated;
     * @param session HttpSession
     * @param name attribute name
     * @return attribute or null
     */
    private static Object getSessionAttribute(HttpSession session, String name) {
        Object res = null;
        if (session != null) {
            try {
                res = session.getAttribute(name);
            } catch (Exception ex) {
                log.error("getSessionAttribute error", ex);
            }
        }
        return res;
    }

    private static ISessionData getSessionData() {
        return getSessionData(getHttpSession(false));
    }

    private static ISessionData getSessionData(HttpSession session) {
        try {
            if (session == null) {
                log.warn("HttpSession undefined");
                return null;
            }
            ISessionData sessionData = (ISessionData) getSessionAttribute(session, Const.USER_SESSION_DATA);
            if (sessionData == null) {
                sessionData = new SessionData();
                session.setAttribute(Const.USER_SESSION_DATA, sessionData);
            }
            return sessionData;
        } catch (Exception ex) {
            log.error("getSessionData error", ex);
            return null;
        }
    }

    /**
     * Fills current user attributes and stores in http session.
     * @param ldapUser ldap user data
     * @param person person data
     * @param user user data
     */
    public static void setCurrentUser(LdapUser ldapUser, Person person, User user) {
        HttpSession session = getHttpSession(true);
        if (session != null) {
            ISessionData sessionData = (ISessionData)getSessionAttribute(session, Const.USER_SESSION_DATA);
            if (sessionData == null) {
                sessionData = new SessionData();
            }
            sessionData.setLdapUser(ldapUser);
            sessionData.setPerson(person);
            sessionData.setUser(user);
            session.setAttribute(Const.USER_SESSION_DATA, sessionData);
        }
    }

    /**
     * Returns current user data.
     * @return User object for current user
     */
    public static User getCurrentUser() {
        ISessionData sessionData = getSessionData();
        if (sessionData != null) {
            return sessionData.getUser();
        }
        return null;
    }

    /**
     * Returns current person data.
     * @return Person object for current user
     */
    public static Person getCurrentPerson() {
        ISessionData sessionData = getSessionData();
        if (sessionData != null) {
            return sessionData.getPerson();
        }
        return null;
    }

    /**
     * Checks that successful logon occurred.
     * @return true if valid session data placed into current http session
     */
    public static boolean isLogged() {
        ISessionData sessionData = getSessionData();
        if (sessionData == null) {
            return false;
        }
        else {
            return sessionData.getLdapUser() != null;
        }
    }

    /**
     * Invalidates current http session
     */
    public static void closeSession() {
        HttpSession session = getHttpSession(false);
        if (session != null) {
            try {
                session.invalidate();
            }
            catch (IllegalStateException e) {
                //suppress exception
            }
        }
    }

}

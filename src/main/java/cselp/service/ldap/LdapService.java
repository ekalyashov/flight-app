package cselp.service.ldap;


import cselp.bean.LdapUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.*;
import java.util.*;

/**
 * Implements service for access to LDAP server (Active Directory).
 * Provides users search, user authorization etc.
 */
public class LdapService implements ILdapService {
    private static final Log log = LogFactory.getLog(LdapService.class);
    public static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    public static final String SECURITY_AUTHENTICATION_MD5 = "DIGEST-MD5"; // Use DIGEST-MD5, it works with Windows.
    // DIGEST-MD5 failed for RA Active Directory, use simple
    public static final String SECURITY_AUTHENTICATION_SIMPLE = "simple";
    public static final int RESULT_LIMIT = 50;

    private String securityPrincipal;
    private String securityCredentials;
    private String rootContext;
    private String providerUrl;
    private String principalAttributeName;

    private Properties appConfig;

    public void setAppProperties(Properties appProperties) {
        appConfig = appProperties;
    }

    public void init() {
        securityPrincipal = appConfig.getProperty("ldap.security.principal");
        securityCredentials = appConfig.getProperty("ldap.security.credentials");
        rootContext = appConfig.getProperty("ldap.root.context");
        providerUrl = appConfig.getProperty("ldap.provider.url");
        principalAttributeName = appConfig.getProperty("ldap.principal.attribute.name");
    }

    /**
     * Returns Active directory users, found under predefined root context.
     * Result size limits to RESULT_LIMIT constant.
     * @return string composed from users CN
     * @throws NamingException if any
     */
    @Override
    public String getUsers() throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION_SIMPLE);
        env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
        DirContext context = new InitialDirContext(env);
        SearchControls ctrl = new SearchControls();
        ctrl.setCountLimit(RESULT_LIMIT);  // This is a good idea, limits result size.
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
        //ctrl.setReturningAttributes(new String [] {"cn", "mail"});
        ctrl.setReturningAttributes(null);
        NamingEnumeration res = context.search(rootContext, "(objectClass=user)", ctrl);
        StringBuilder out = new StringBuilder();
        int count = 0;
        try {
            while (res.hasMore()) {
                SearchResult result = (SearchResult) res.next();
                count++;
                if (log.isDebugEnabled()) {
                    log.debug("result : " + result.getName());
                }
                Attributes attrs = result.getAttributes();
                if (attrs != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Attributes : " + attrs);
                    }
                    Attribute attr = attrs.get("cn");
                    if (attr != null) {
                        NamingEnumeration values = attr.getAll();
                        while (values.hasMore()) {
                            out.append(values.next()).append(',');
                        }
                    } else {
                        out.append("attr cn is null");
                    }
                    out.append(';');
                } else out.append("no attributes;");
            }
        } catch (SizeLimitExceededException sle) {
            log.warn("SizeLimitExceededException " + sle.getLocalizedMessage() +
                    "\nExpected " + RESULT_LIMIT + ", received " + count);
        } catch (NamingException ne) {
            log.error("error in findUsers", ne);
        } finally {
            res.close();
        }
        return out.toString();
    }

    /**
     * Try to authorize specified user: searches user using specified LDAP attribute -
     * f.e. sAMAccountName or userPrincipalName,
     * then try to login this user with user's DN and specified password.
     * Direct usage of login as SECURITY_PRINCIPAL is impossibly,
     * OpenSky uses attribute 'sAMAccountName' as login. sAMAccountName contains tabNo of user.
     * @param login user search attribute - sAMAccountName or userPrincipalName (defined in application properties)
     * @param password user password
     * @return authorized LdapUser or null
     * @throws NamingException if any
     */
    @Override
    public LdapUser tryLogin(String login, String password) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_AUTHENTICATION, SECURITY_AUTHENTICATION_SIMPLE);
        env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
        DirContext context = new InitialDirContext(env);
        //search users with specified principalAttributeName
        SearchControls ctrl = new SearchControls();
        ctrl.setCountLimit(RESULT_LIMIT);  // This is a good idea, limits result size.
        ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
        ctrl.setReturningAttributes(new String [] {"cn", "givenName", "sn", "middleName",
                "userPrincipalName", "distinguishedName", "mail"});
        String filter = "(objectClass=user)";
        filter = "(&" + filter + "(" + principalAttributeName + "=" + login + "))";
        NamingEnumeration res = context.search(rootContext, filter, ctrl);
        List<LdapUser> users = new ArrayList<>();
        int count = 0;
        try {
            while (res.hasMore()) {
                SearchResult result = (SearchResult) res.next();
                count++;
                if (log.isTraceEnabled()) {
                    log.trace("result : " + result.getName());
                }
                Attributes attrs = result.getAttributes();
                if (attrs != null) {
                    if (log.isTraceEnabled()) {
                        log.trace("Attributes : " + attrs);
                    }
                    LdapUser user = new LdapUser();
                    user.setUserName(getAttribute(attrs, "cn"));
                    user.setFirstName(getAttribute(attrs, "givenName"));
                    user.setLastName(getAttribute(attrs, "sn"));
                    user.setMiddleName(getAttribute(attrs, "middleName"));
                    user.setLogonName(getAttribute(attrs, "userPrincipalName"));
                    user.setDn(getAttribute(attrs, "distinguishedName"));
                    users.add(user);
                }
            }
        } catch (SizeLimitExceededException sle) {
            log.warn("SizeLimitExceededException " + sle.getLocalizedMessage() +
                    "\nExpected " + RESULT_LIMIT + ", received " + count);
        } catch (NamingException ne) {
            log.error("error in findUsers", ne);
        } finally {
            res.close();
        }
        if (users.size() > 1) {
            log.warn("Found " + users.size() + " users with login " + login);
        } else if (users.size() == 0) {
            log.warn("No users found with login " + login);
            return null;
        }
        //try to login
        for (LdapUser u : users) {
            try {
                env.put(Context.SECURITY_PRINCIPAL, u.getDn());
                env.put(Context.SECURITY_CREDENTIALS, password);
                DirContext ctx = new InitialDirContext(env);
                //exception if credentials invalid?
                if (log.isDebugEnabled()) {
                    log.debug("Login " + login + " for user " + u.getDn() + " : success");
                }
                return u;
            } catch (NamingException ne) {
                log.warn("Login " + login + " for user " + u.getDn() + " : error", ne);
            }
        }
        return null;
    }

    private String getAttribute(Attributes attrs, String name) throws NamingException {
        Attribute attr = attrs.get(name);
        if (attr != null) {
            NamingEnumeration values = attr.getAll();
            if (values.hasMore()) {
                return String.valueOf(values.next());
            }
        }
        return null;
    }

    /**
     * Authorize user using specified login or password.
     * Some LDAP attributes can be used as login parameter:
     *   Down-Level Logon Name, i.e. NetBIOSDomainName\\sAMAccountName (e.g. domain\\username)
     *   userPrincipalName
     *   distinguishedName
     *   canonical name
     *   objectSid
     *   objectGUID
     * @param login specified login
     * @param password specified password
     * @return status of operation
     * @throws NamingException if any
     */
    @Override
    public String login(String login, String password) throws NamingException {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
        env.put(Context.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, login);
        env.put(Context.SECURITY_CREDENTIALS, password);
        try {
            DirContext context = new InitialDirContext(env);
            String msg = "Login " + login + " for user " + login + " : success";
            log.debug(msg);
            return msg;
        } catch (NamingException ne) {
            String msg = "Login " + login + " for user " + login + " : error";
            log.warn(msg, ne);
            return msg + ", " + ne.getLocalizedMessage();
        }
    }
}

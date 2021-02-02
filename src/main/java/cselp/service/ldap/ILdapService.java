package cselp.service.ldap;


import cselp.bean.LdapUser;

import javax.naming.NamingException;

/**
 * Interface for access to LDAP server (Active Directory).
 * Provides users search, user authorization etc.
 */
public interface ILdapService {

    /**
     * Returns Active directory users, found under predefined root context.
     * Result size limits to RESULT_LIMIT constant.
     * @return string composed from users CN
     * @throws NamingException if any
     */
    String getUsers() throws NamingException;

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
    LdapUser tryLogin(String login, String password) throws NamingException;

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
    String login(String login, String password) throws NamingException;
}

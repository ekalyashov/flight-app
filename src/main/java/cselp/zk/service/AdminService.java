package cselp.zk.service;


import cselp.bean.LdapUser;
import cselp.bean.LegSearchFlags;
import cselp.domain.external.FlightWrapper;
import cselp.domain.local.Leg;
import cselp.exception.RAException;
import cselp.service.external.IFlightService;
import cselp.service.local.ILFlightService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.SizeLimitExceededException;
import javax.naming.directory.*;
import java.util.*;

@Service("adminService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.INTERFACES)
public class AdminService implements IAdminService {
    private static final Log log = LogFactory.getLog(AdminService.class);

    public static final String INITIAL_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
    public static final String SECURITY_AUTHENTICATION_SIMPLE = "simple";
    public static final int RESULT_LIMIT = 50;

    @Resource(name = "flightService")
    private IFlightService flightService;

    @Resource(name = "lFlightService")
    private ILFlightService lFlightService;

    @Resource(name = "appConfig")
    private Properties appConfig;

    @Override
    public List<LdapUser> findUsers(String userName) throws NamingException {
        String securityPrincipal = appConfig.getProperty("ldap.security.principal");
        String securityCredentials = appConfig.getProperty("ldap.security.credentials");
        String rootContext = appConfig.getProperty("ldap.root.context");
        String providerUrl = appConfig.getProperty("ldap.provider.url");
        //String principalAttributeName = appConfig.getProperty("ldap.principal.attribute.name");
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
        String filter = "(objectClass=user)";
        if (StringUtils.isNotBlank(userName)) {
            filter = "(&" + filter  +"(cn="+userName + "*))";
        }
        NamingEnumeration res = context.search(rootContext, filter, ctrl);
        List<LdapUser> out = new ArrayList<>();
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
                    } else if (log.isDebugEnabled()) {
                        log.debug("Found " + getAttribute(attrs, "distinguishedName"));
                    }
                    LdapUser user = new LdapUser();
                    user.setUserName(getAttribute(attrs, "cn"));
                    user.setFirstName(getAttribute(attrs, "givenName"));
                    user.setLastName(getAttribute(attrs, "sn"));
                    user.setMiddleName(getAttribute(attrs, "middleName"));
                    user.setLogonName(getAttribute(attrs, "userPrincipalName"));
                    user.setDn(getAttribute(attrs, "distinguishedName"));
                    out.add(user);
                }
            }
        }
        catch (SizeLimitExceededException sle) {
            log.warn("SizeLimitExceededException " + sle.getLocalizedMessage() +
                    "\nExpected " + RESULT_LIMIT + ", received " + count);
        }
        catch (NamingException ne) {
            log.error("error in findUsers", ne);
        }
        finally {
            res.close();
        }
        return out;
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

    @Override
    public List<Leg> findUnlinkedLegs(Date from, Date to) throws RAException {
        return lFlightService.findUnlinkedLegs(from, to);
    }

    @Override
    public List<FlightWrapper> findSimilarFlights(LegSearchFlags cond, Long delta) throws RAException {
        Map<String, List<String>> regNumMap = lFlightService.getAircraftRegNumMap();
        Leg leg = lFlightService.getLeg(cond.getId());
        if (!cond.isSearchTailNo()) {
            leg.setTailNum(null);
        }
        if (!cond.isSearchOrigin()) {
            leg.setOrigin(null);
            leg.setOriginIata(null);
            leg.setOriginIcao(null);
        }
        if (!cond.isSearchDestination()) {
            leg.setDestination(null);
            leg.setDestinationIata(null);
            leg.setDestinationIcao(null);
        }
        if (!cond.isSearchDeparture()) {
            leg.setDepartureFact(null);
            leg.setTakeOff(null);
        }
        if (!cond.isSearchArrival()) {
            leg.setArrivalFact(null);
            leg.setTouchDown(null);
        }
        return flightService.findSimilarFlight(leg, regNumMap, delta);
    }
}

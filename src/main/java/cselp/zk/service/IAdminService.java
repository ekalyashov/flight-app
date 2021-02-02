package cselp.zk.service;



import cselp.bean.LdapUser;
import cselp.bean.LegSearchFlags;
import cselp.domain.external.FlightWrapper;
import cselp.domain.local.Leg;
import cselp.exception.RAException;

import javax.naming.NamingException;
import java.util.Date;
import java.util.List;

public interface IAdminService {
    List<LdapUser> findUsers(String userName) throws NamingException;

    List<Leg> findUnlinkedLegs(Date from, Date to) throws RAException;

    List<FlightWrapper> findSimilarFlights(LegSearchFlags cond, Long delta) throws RAException;
}

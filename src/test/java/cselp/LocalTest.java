package cselp;

import cselp.bean.PersonSearchCondition;
import cselp.domain.external.EventPrimaryParameter;
import cselp.domain.external.EventType;
import cselp.domain.external.Phase;
import cselp.domain.local.*;
import cselp.domain.opensky.*;
import cselp.exception.RAException;
import cselp.service.IDataSyncService;
import cselp.service.local.ILFlightService;
import cselp.util.DataUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Timestamp;
import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@TransactionConfiguration(transactionManager = "localTransactionManager", defaultRollback = true)
@ContextConfiguration("file:src/main/webapp/WEB-INF/config/spring/context-test.xml")
public class LocalTest extends AbstractTransactionalJUnit4SpringContextTests {

    public static final String LO_1 = "\u041B\u041E 1";

    @Resource(name = "lFlightService")
    private ILFlightService lFlightService;

    @Resource(name = "dataSyncService")
    private IDataSyncService dataSyncService;

    @Override
    @Resource(name = "localDataSource")
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Rollback(true)
    @Test
    public void testPersons() throws RAException {
        Person p = createPerson("123456789", "test name", 0.0, 0.0, 0.0);
        lFlightService.flushSession();
        lFlightService.clearSession();
        Person p2 = new Person();
        p2.setTabNum("123456789");
        p2.setFullName("test name");
        PersonMinimum pm2 = new PersonMinimum();
        pm2.setLandingVisHorizontal(1.0);
        pm2.setLandingVisVertical(1.0);
        pm2.setTakeOffVisHorizontal(0.0);
        p2.setPersonMinimum(pm2);
        List<String> ignoreProperties = Arrays.asList("id", "personId");
        List<Class> nestedClasses = new ArrayList<>();
        nestedClasses.add(Person.class);
        nestedClasses.add(PersonMinimum.class);
        Map<String, String> diffProps = DataUtil.getChangedProperties(p, p2, ignoreProperties, nestedClasses);
        for (Map.Entry e : diffProps.entrySet()) {
            System.out.println("===difference " + e.getKey() + " : " + e.getValue());
        }
        boolean changed = DataUtil.compareAndUpdate(p, p2);
        System.out.println("changed = " + changed);
        if (changed) {
            try {
                p = lFlightService.updatePerson(p);
                lFlightService.flushSession();
                lFlightService.clearSession();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            diffProps = DataUtil.getChangedProperties(p, p2, ignoreProperties, nestedClasses);
            System.out.println("diffProps size = " + diffProps.size());
            for (Map.Entry e : diffProps.entrySet()) {
                System.out.println("===updated difference " + e.getKey() + " : " + e.getValue());
            }
            assertEquals("Properties difference not 0", diffProps.size(), 0);
        }
    }

    private Person createPerson(String tabNum, String fullName, double landingVisHorizontal,
                                double landingVisVertical, double takeOffVisHorizontal)
            throws RAException {
        Person p = new Person();
        p.setTabNum(tabNum);
        p.setFullName(fullName);
        PersonMinimum pm = new PersonMinimum();
        pm.setLandingVisHorizontal(landingVisHorizontal);
        pm.setLandingVisVertical(landingVisVertical);
        pm.setTakeOffVisHorizontal(takeOffVisHorizontal);
        p.setPersonMinimum(pm);
        p = lFlightService.createPerson(p);
        return p;
    }

    @Rollback(true)
    @Test
    public void testPersonSearch() throws RAException {
        Division d = lFlightService.getDivisionFull(LO_1);
        assertNotNull("Division " + LO_1 + " is null", d);
        Squadron s = d.getSquadrons().get(0);
        assertNotNull("Squadron 0 is null", s);
        PersonSearchCondition cond = new PersonSearchCondition();
        cond.setSquadronId(s.getId());
        List<Person> persons = lFlightService.findPersons(cond);
        assertTrue("persons search for " + s.getId() + " squadron invalid", persons.size() > 0);
        cond = new PersonSearchCondition();
        cond.setDivisionId(d.getId());
        persons = lFlightService.findPersons(cond);
        assertTrue("persons search for " + d.getId() + " division invalid", persons.size() > 0);
        cond = new PersonSearchCondition();
        cond.setLastName("KO");
        persons = lFlightService.findPersons(cond);
        assertTrue("persons search for last name invalid", persons.size() > 0);
        String commander = "\u041A\u043E\u043C\u0430\u043D\u0434\u0438\u0440";
        cond = new PersonSearchCondition();
        cond.setDivisionRole(commander);
        persons = lFlightService.findPersons(cond);
        assertTrue("persons search for DivisionRole invalid", persons.size() > 0);
    }

    @Rollback(true)
    @Test
    public void testDivision() throws RAException {
        String dName = "test";
        String sName = "testSquadron";
        Division d = lFlightService.createDivision(dName);
        assertNotNull("New Division is null", d);
        Squadron s = lFlightService.createSquadron(d.getId(), sName);
        assertNotNull("New Squadron is null", s);
        d = lFlightService.getDivision(dName);
        assertNotNull("Division is null", d);
        assertEquals("Division name invalid", dName, d.getName());
        s = lFlightService.getSquadron(d.getId(), sName);
        assertNotNull("Squadron is null", s);
        assertEquals("Squadron name invalid", sName, s.getName());
    }

    @Rollback(true)
    @Test
    public void testTrip() throws RAException {
        Long flightId = 123L;
        String carrier = "FV";
        String flightNum = "123";
        Timestamp flightDate = new Timestamp(System.currentTimeMillis());
        Timestamp actualDate = new Timestamp(System.currentTimeMillis());
        String tailNum = "AA-AAA";
        String origin = "ICAO";
        String destination = "IATA";
        String flightKind = "Knd";
        List<Trip> trips = lFlightService.findTrips(carrier, flightNum, flightDate, actualDate,
                tailNum, origin, destination, flightKind);
        assertEquals("Found nonexistent trip", 0, trips.size());
        Trip t = new Trip(carrier, flightNum, flightDate, actualDate, tailNum, origin, destination, flightKind);
        Leg l = new Leg();
        l.setTailNum(tailNum);
        l.setDeparturePlan(flightDate);
        l.setOrigin(origin);
        l.setDestination(destination);
        //l.setFlightId(flightId);
        t.getLegs().add(l);
        //add crew
        Division d = lFlightService.getDivisionFull(LO_1);
        Squadron s = d.getSquadrons().get(0);
        Person p = createPerson("@123", "full name", 0, 0, 0);
        CrewMember cm = new CrewMember();
        cm.setFlightRole("test");
        cm.setPersonId(p.getId());
        Crew c = new Crew();
        c.getMembers().add(cm);
        c.setSquadronId(s.getId());
        l.getCrews().add(c);
        t = lFlightService.createTrip(t);
        lFlightService.clearSession();
        assertNotNull("Trip is null", t);
        assertNotNull("Leg is null", t.getLegs().get(0));
        trips = lFlightService.findTrips(carrier, flightNum, flightDate, actualDate,
                tailNum, origin, destination, flightKind);
        assertEquals("Found trip result invalid", 1, trips.size());
        Trip trip = trips.get(0);
        Leg leg1 = trip.getLegs().get(0);
        assertEquals("Leg TailNum values", l.getTailNum(), leg1.getTailNum());
        assertEquals("Crew squadron", l.getCrews().get(0).getSquadronId(), leg1.getCrews().get(0).getSquadronId());
        leg1.setFlightId(flightId);
        List<EventPrimaryParameter> evParameters = new ArrayList<>();
        Map<Pair<Long, Short>, EventTypeScore> etsMap = lFlightService.getEventTypeScoresMap();
        Map<Long, EventType> eventTypeMap = new HashMap<Long, EventType>() {{
            put(1L, new EventType(1L,1L,1006L,"Speed Above VMO","Speed Above Velocity Maximum Operation"));
            put(1L, new EventType(2L,1L,1018L,"Exceedance of Flaps/Slats Limit Speed in Approach","Exceedance of Flaps/Slats limit Speed in Approach"));
        }};
        Map<Short, Phase> phaseMap = new HashMap<Short, Phase>() {{
            put((short)0, new Phase((short)0, "PRF","PREFLIGHT"));
            put((short)1, new Phase((short)1, "ESR","ENGINE START"));
        }};
        leg1 = lFlightService.updateLegAndScores(leg1, evParameters, etsMap, eventTypeMap, phaseMap);
        assertEquals("Leg sync result invalid", flightId, leg1.getFlightId());
    }

    @Rollback(true)
    @Test
    public void testParseFlights() throws RAException, IOException {
        org.springframework.core.io.Resource testFile =
                applicationContext.getResource("file:src/test/resources/OpenSky_test.xml");
        assertTrue("Existence of OpenSky_test.xml file", testFile.exists());
        Properties appConfig = (Properties)applicationContext.getBean("appConfig");
        String fileEncoding = appConfig.getProperty("load.flights.file.encoding", "windows-1251");
        String xml = FileUtils.readFileToString(testFile.getFile(), fileEncoding);
        System.out.println("===xml.length() = " + xml.length() + ", fileEncoding=" + fileEncoding);
        assertTrue("OpenSky_test.xml length", xml.length() > 0);
        String res = dataSyncService.parseFlights(xml, testFile.getFile().getName());
        System.out.println("===" + res);
        assertTrue("parseFlights result.", res.length() > 0);
    }

    @Rollback(true)
    @Test
    public void testEmptyMinimums() throws RAException, IOException, JAXBException {
        org.springframework.core.io.Resource testFile =
                applicationContext.getResource("file:src/test/resources/OpenSky_test2.xml");
        assertTrue("Existence of OpenSky_test.xml file", testFile.exists());
        Properties appConfig = (Properties) applicationContext.getBean("appConfig");
        String fileEncoding = appConfig.getProperty("load.flights.file.encoding", "windows-1251");
        String xml = FileUtils.readFileToString(testFile.getFile(), fileEncoding);
        System.out.println("===xml.length() = " + xml.length() + ", fileEncoding=" + fileEncoding);
        assertTrue("OpenSky_test.xml length", xml.length() > 0);
        JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<PERFORMEDFLIGHTSType> unmarshalledObject =
                (JAXBElement<PERFORMEDFLIGHTSType>)unmarshaller.unmarshal(new StringReader(xml));
        PERFORMEDFLIGHTSType flightsRoot = unmarshalledObject.getValue();
        List<FLIGHTType> flights = flightsRoot.getFLIGHT();
        FLIGHTType ft = flights.get(0);
        LEGType leg = ft.getLEG().get(0);
        CREWTASKType ct = leg.getCREWTASK().get(0);
        MEMBERSType m = ct.getMEMBERS().get(0);
        MINIMUMSType min = m.getMEMBER().getMINIMUMS();
        assertTrue("All minimums should be null", min.getLANDINGVISHORIZONTAL() == null &&
                min.getLANDINGVISVERTICAL() == null && min.getTAKEOFFVISHORIZONTAL() == null);
    }
}

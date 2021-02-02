package cselp.util;


import cselp.Const;
import cselp.bean.ErrorMessage;
import cselp.domain.external.*;
import cselp.domain.external.Process;
import cselp.domain.local.*;
import cselp.domain.opensky.*;
import cselp.exception.RAException;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Utility methods to transfer data from one domain to other.
 */
public class DataUtil {
    private static final Log log = LogFactory.getLog(DataUtil.class);

    public static final ThreadLocal<DateFormat> df = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd.MM.yyyy HH:mm");
        }
    };

    public static final ThreadLocal<DateFormat> dfs = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        }
    };

    public static final ThreadLocal<DateFormat> inputDf = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd-MM-yyyy");
        }
    };

    public static final String[] datePatterns =
            {"dd-MM-yyyy", "dd-MM-yyyy HH:mm", "dd.MM.yyyy", "dd.MM.yyyy HH:mm"};

    public static final int JSON_MAX_SIZE = 1024;

    /**
     * Creates new Person object and fills ift from supplied MEMBERSType bean.
     * @param person MEMBERSType object
     * @param divSqIds map of division and squadron identifiers
     * @return new Person object
     */
    public static Person newPerson(MEMBERSType person, Map<String, Map<String, Long>> divSqIds) {
        Person p = new Person();
        return setPerson(p, person, divSqIds);
    }

    private static Person setPerson(Person p, MEMBERSType person, Map<String, Map<String, Long>> divSqIds) {
        MEMBERType m = person.getMEMBER();
        p.setTabNum(StringUtils.trim(m.getNO()));
        p.setFirstName(StringUtils.trim(m.getFIRSTNAME()));
        p.setLastName(StringUtils.trim(m.getLASTNAME()));
        p.setFullName(StringUtils.trim(m.getFULLNAME()));
        p.setGender(StringUtils.trim(m.getGENDER()));
        p.setBirthDate(m.getDATEOFBIRTH().toGregorianCalendar().getTime());
        p.setDivisionRole(StringUtils.trim(person.getROLE().getDIVISIONROLE()));
        p.setDivisionRoleId(person.getROLE().getDIVISIONROLEID());
        p.setAppRoleId(Const.AppRoles.PILOT);
        PersonMinimum pm = new PersonMinimum();
        pm.setLandingVisVertical(m.getMINIMUMS().getLANDINGVISVERTICAL());
        pm.setLandingVisHorizontal(m.getMINIMUMS().getLANDINGVISHORIZONTAL());
        pm.setTakeOffVisHorizontal(m.getMINIMUMS().getTAKEOFFVISHORIZONTAL());
        p.setPersonMinimum(pm);
        p.setSquadronId(getSquadronId(m.getDIVISION(), divSqIds));
        return p;
    }

    private static Long getSquadronId(DIVISIONType div, Map<String, Map<String, Long>> divSqIds) {
        if (div == null) {
            return null;
        }
        //division name
        Map<String, Long> sqIds = divSqIds.get(div.getNAME());
        if (sqIds != null) {
            //squadron name
            return sqIds.get(div.getNO());
        }
        else {
            return null;
        }
    }

    /**
     * Creates new Trip object and fills ift from supplied FLIGHTType bean.
     * @param flight FLIGHTType object
     * @param personMap map of Person identifiers
     * @param divSqIds map of division and squadron identifiers
     * @return new Trip object
     */
    public static Trip newTrip(FLIGHTType flight, Map<String, Person> personMap,
                               Map<String, Map<String, Long>> divSqIds) {
        Trip trip = new Trip();
        trip.setCarrier(StringUtils.trim(flight.getCARRIER()));
        trip.setFlightNum(StringUtils.trim(flight.getFLIGHTNO()));
        trip.setFlightDate(toTimestamp(flight.getFLIGHTDATE()));
        trip.setActualDate(toTimestamp(flight.getACTUALDATE()));
        trip.setTailNum(StringUtils.trim(flight.getTAILNO()));
        trip.setOrigin(StringUtils.trim(flight.getORIGIN()));
        trip.setDestination(StringUtils.trim(flight.getDESTINATION()));
        trip.setFlightKind(StringUtils.trim(flight.getFLTKIND()));
        trip.getLegs().addAll(newLegs(flight.getLEG(), personMap, divSqIds));
        return trip;
    }

    private static List<Leg> newLegs(Collection<LEGType> legs, Map<String, Person> personMap,
                                    Map<String, Map<String, Long>> divSqIds) {
        List<Leg> res = new ArrayList<>();
        for (LEGType l : legs) {
            Leg leg = new Leg();
            leg.setTailNum(StringUtils.trim(l.getTAILNO()));
            leg.setOrigin(StringUtils.trim(l.getORIGIN()));
            leg.setOriginIcao(getICAOCode(l.getORIGIN()));
            leg.setOriginIata(getIATACode(l.getORIGIN()));
            leg.setDestination(StringUtils.trim(l.getDESTINATION()));
            leg.setDestinationIcao(getICAOCode(l.getDESTINATION()));
            leg.setDestinationIata(getIATACode(l.getDESTINATION()));
            leg.setDeparturePlan(toTimestamp(l.getDEPARTUREPLAN()));
            leg.setDepartureFact(toTimestamp(l.getDEPARTUREFACT()));
            leg.setArrivalPlan(toTimestamp(l.getARRIVALPLAN()));
            leg.setArrivalFact(toTimestamp(l.getARRIVALFACT()));
            leg.setTakeOff(toTimestamp(l.getTAKEOFF()));
            leg.setTouchDown(toTimestamp(l.getTOUCHDOWN()));
            leg.getCrews().addAll(newCrews(l.getCREWTASK(), personMap, divSqIds));
            res.add(leg);
        }
        return res;
    }

    private static List<Crew> newCrews(List<CREWTASKType> crewTasks, Map<String, Person> personMap,
                                      Map<String, Map<String, Long>> divSqIds) {
        List<Crew> res = new ArrayList<>();
        for (CREWTASKType crewTask : crewTasks) {
            Crew crew = new Crew();
            crew.setTaskNum(StringUtils.trim(crewTask.getNO()));
            crew.setTaskDate(toTimestamp(crewTask.getDATE()));
            crew.setType(StringUtils.trim(crewTask.getTYPE()));
            crew.setMemberCount(crewTask.getMEMBERNR());
            crew.setSquadronId(getSquadronId(crewTask.getDIVISION(), divSqIds));
            crew.getMembers().addAll(newCrewMembers(crewTask, personMap));
            //todo check if SquadronId is null and ignore such crew?
            res.add(crew);
        }
        return res;
    }

    private static List<CrewMember> newCrewMembers(CREWTASKType crewTask, Map<String, Person> personMap) {
        List<CrewMember> res = new ArrayList<>();
        for (MEMBERSType mt : crewTask.getMEMBERS()) {
            CrewMember cm = new CrewMember();
            String tabNum = null;
            if (mt.getMEMBER() != null) {
                tabNum = StringUtils.trim(mt.getMEMBER().getNO());
                Person p = personMap.get(tabNum);
                if (p != null) {
                    cm.setPersonId(p.getId());
                }
            }
            if (mt.getROLE() != null) {
                cm.setFlightRole(StringUtils.trim(mt.getROLE().getFLIGHTROLE()));
            }
            if (cm.getPersonId() != null) {
                res.add(cm);
            }
            else {
                log.warn("CrewMember '" + tabNum + "' from crewTask " +
                        crewTask.getNO() + " has not corresponding person");
            }
        }
        return res;
    }

    public static Map<String, Pair<Leg, AirportCode>> getAirportCodes(List<Leg> legs) {
        Map<String, Pair<Leg, AirportCode>> codesMap = new HashMap<>();
        for (Leg leg : legs) {
            AirportCode code = getAirportCode(leg.getOrigin());
            if (code != null) {
                codesMap.put(code.getCodes(), new ImmutablePair<>(leg, code));
            }
            code = getAirportCode(leg.getDestination());
            if (code != null) {
                codesMap.put(code.getCodes(), new ImmutablePair<>(leg, code));
            }
        }
        return codesMap;
    }

    public static AirportCode getAirportCode(String codeString) {
        String[] codes = codeString.split(",");
        if (codes.length >= 1) {
            AirportCode res = new AirportCode();
            res.setIataCode(StringUtils.trimToNull(codes[0]));
            if (codes.length >= 2) {
                res.setCrtCode(StringUtils.trimToNull(codes[1]));
            }
            if (codes.length >= 3) {
                res.setIcaoCode(StringUtils.trimToNull(codes[2]));
            }
            if (codes.length >= 4) {
                res.setIcaoCyrillicCode(StringUtils.trimToNull(codes[3]));
            }
            if (codes.length >= 5) {
                try {
                    res.setAptZone((long)(Double.parseDouble(StringUtils.trim(codes[4])) * 60));
                }
                catch (Exception e) {
                    //suppress exception
                    log.error("AirportCode, invalid timezone shift : " + codes[4]);
                }
            }
            return res;
        }
        else return null;
    }

    private static String getICAOCode(String codeString) {
        String[] codes = codeString.split(",");
        if (codes.length >= 3) {
            return StringUtils.trimToNull(codes[2]);
        }
        else return null;
    }

    private static String getIATACode(String codeString) {
        String[] codes = codeString.split(",");
        if (codes.length >= 1) {
            return StringUtils.trimToNull(codes[0]);
        }
        else return null;
    }

    public static int getAirportZoneOffset(AirportCode code) {
        return (code == null || code.getAptZone() == null) ? 0 : code.getAptZone().intValue();
    }

    public static String getStringAirportCode(AirportCode code) {
        if (code == null) {
            return null;
        }
        String res = code.getIcaoCode();
        if (res == null) {
            res = code.getIataCode();
        }
        if (res == null) {
            res = code.getCodes();
        }
        return res;
    }

    public static String getStringAirportCode(String code) {
        String res = getICAOCode(code);
        if (res == null) {
            res = getIATACode(code);
        }
        if (res == null) {
            res = code;
        }
        return res;
    }

    public static Date toDate(String date) {
        try {
            Date res = DateUtils.parseDate(date, datePatterns);
            return res;
        } catch (Exception e) {
            log.error("Error parsing date " + date, e);
            return null;
        }
    }

    public static Timestamp toTimestamp(String date) {
        try {
            //Date res = df.get().parse(StringUtils.trim(date));
            Date res = DateUtils.parseDate(date, datePatterns);
            return new Timestamp(res.getTime());
        } catch (Exception e) {
            log.error("Error parsing date " + date, e);
            return null;
        }
    }

    public static boolean equals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        return object1.equals(object2);
    }

    /**
     * Returns true if objects are equals or source is null (value undefined and we should not change destination)
     * @param dest destination object
     * @param src source object
     * @return true if objects are equals or source is null, false otherwise
     */
    public static boolean equalsOrNull(Object dest, Object src) {
        if (src == dest) {
            return true;
        }
        if (src == null) {
            return true;
        }
        return src.equals(dest);
    }

    /**
     * Compares target and source and updates different target fields from source.
     * @param target Person object
     * @param source Person object
     * @return true if target was updated
     */
    public static boolean compareAndUpdate(Person target, Person source) {
        boolean res = false;
        if (!equals(target.getSquadronId(), source.getSquadronId())) {
             target.setSquadronId(source.getSquadronId());
            res = true;
        }
        if (!equals(target.getTabNum(), source.getTabNum())) {
            target.setTabNum(source.getTabNum());
            res = true;
        }
        if (!equals(target.getFirstName(), source.getFirstName())) {
            target.setFirstName(source.getFirstName());
            res = true;
        }
        if (!equals(target.getLastName(), source.getLastName())) {
            target.setLastName(source.getLastName());
            res = true;
        }
        if (!equals(target.getFullName(), source.getFullName())) {
            target.setFullName(source.getFullName());
            res = true;
        }
        if (!equals(target.getGender(), source.getGender())) {
            target.setGender(source.getGender());
            res = true;
        }
        if (!equals(target.getBirthDate(), source.getBirthDate())) {
            target.setBirthDate(source.getBirthDate());
            res = true;
        }
        if (!equals(target.getDivisionRole(), source.getDivisionRole())) {
            target.setDivisionRole(source.getDivisionRole());
            res = true;
        }
        if (!equals(target.getDivisionRoleId(), source.getDivisionRoleId())) {
            target.setDivisionRoleId(source.getDivisionRoleId());
            res = true;
        }
        if (!equals(target.getAppRoleId(), source.getAppRoleId())) {
            target.setAppRoleId(source.getAppRoleId());
            res = true;
        }
        if (source.getPersonMinimum() != null) {
            if (target.getPersonMinimum() != null) {
                PersonMinimum sPm = source.getPersonMinimum();
                PersonMinimum tpm = target.getPersonMinimum();
                if (!equalsOrNull(tpm.getLandingVisHorizontal(), sPm.getLandingVisHorizontal())) {
                    tpm.setLandingVisHorizontal(sPm.getLandingVisHorizontal());
                    res = true;
                }
                if (!equalsOrNull(tpm.getLandingVisVertical(), sPm.getLandingVisVertical())) {
                    tpm.setLandingVisVertical(sPm.getLandingVisVertical());
                    res = true;
                }
                if (!equalsOrNull(tpm.getTakeOffVisHorizontal(), sPm.getTakeOffVisHorizontal())) {
                    tpm.setTakeOffVisHorizontal(sPm.getTakeOffVisHorizontal());
                    res = true;
                }
            }
            else {
                target.setPersonMinimum(source.getPersonMinimum());
                res = true;
            }
        }
        else if (target.getPersonMinimum() != null) {
            target.setPersonMinimum(source.getPersonMinimum());
            res = true;
        }
        return res;
    }

    public static boolean setChanged(XpsSnapshot target, XpsSnapshot source) {
        boolean res = false;
        if (!equals(target.getValue(), source.getValue())) {
            target.setValue(source.getValue());
            res = true;
        }
        if (!equals(target.getValueMinus1(), source.getValueMinus1())) {
            target.setValueMinus1(source.getValueMinus1());
            res = true;
        }
        if (!equals(target.getValueMinus2(), source.getValueMinus2())) {
            target.setValueMinus2(source.getValueMinus2());
            res = true;
        }
        if (!equals(target.getValueMinus3(), source.getValueMinus3())) {
            target.setValueMinus3(source.getValueMinus3());
            res = true;
        }
        if (!equals(target.getValuePlus1(), source.getValuePlus1())) {
            target.setValuePlus1(source.getValuePlus1());
            res = true;
        }
        if (!equals(target.getValuePlus2(), source.getValuePlus2())) {
            target.setValuePlus2(source.getValuePlus2());
            res = true;
        }
        if (!equals(target.getValuePlus3(), source.getValuePlus3())) {
            target.setValuePlus3(source.getValuePlus3());
            res = true;
        }
        if (!equals(target.getValidity(), source.getValidity())) {
            target.setValidity(source.getValidity());
            res = true;
        }
        return res;
    }

    public static boolean setChanged(Process target, Process source) {
        boolean res = false;
        if (!equals(target.getRecorderTypeId(), source.getRecorderTypeId())) {
            target.setRecorderTypeId(source.getRecorderTypeId());
            res = true;
        }
        if (!equals(target.getCartridgeId(), source.getCartridgeId())) {
            target.setCartridgeId(source.getCartridgeId());
            res = true;
        }
        if (!equals(target.getFapConfId(), source.getFapConfId())) {
            target.setFapConfId(source.getFapConfId());
            res = true;
        }
        if (!equals(target.getFileName(), source.getFileName())) {
            target.setFileName(source.getFileName());
            res = true;
        }
        if (!equals(target.getDate(), source.getDate())) {
            target.setDate(source.getDate());
            res = true;
        }
        if (!equals(target.getCartridgeDateIn(), source.getCartridgeDateIn())) {
            target.setCartridgeDateIn(source.getCartridgeDateIn());
            res = true;
        }
        if (!equals(target.getCartridgeDateOut(), source.getCartridgeDateOut())) {
            target.setCartridgeDateOut(source.getCartridgeDateOut());
            res = true;
        }
        if (!equals(target.getFlightLegsCount(), source.getFlightLegsCount())) {
            target.setFlightLegsCount(source.getFlightLegsCount());
            res = true;
        }
        if (!equals(target.getFramesCount(), source.getFramesCount())) {
            target.setFramesCount(source.getFramesCount());
            res = true;
        }
        if (!equals(target.getBadFramesCount(), source.getBadFramesCount())) {
            target.setBadFramesCount(source.getBadFramesCount());
            res = true;
        }
        if (!equals(target.getSubFramesCount(), source.getSubFramesCount())) {
            target.setSubFramesCount(source.getSubFramesCount());
            res = true;
        }
        if (!equals(target.getBadSubFramesCount(), source.getBadSubFramesCount())) {
            target.setBadSubFramesCount(source.getBadSubFramesCount());
            res = true;
        }
        if (!equals(target.getTranscriptionSpeed(), source.getTranscriptionSpeed())) {
            target.setTranscriptionSpeed(source.getTranscriptionSpeed());
            res = true;
        }
        if (!equals(target.getErrorRate(), source.getErrorRate())) {
            target.setErrorRate(source.getErrorRate());
            res = true;
        }
        if (!equals(target.getArchiveFileName(), source.getArchiveFileName())) {
            target.setArchiveFileName(source.getArchiveFileName());
            res = true;
        }
        if (!equals(target.getFapStartDate(), source.getFapStartDate())) {
            target.setFapStartDate(source.getFapStartDate());
            res = true;
        }
        if (!equals(target.getFapEndDate(), source.getFapEndDate())) {
            target.setFapEndDate(source.getFapEndDate());
            res = true;
        }
        if (!equals(target.getFapFramesCount(), source.getFapFramesCount())) {
            target.setFapFramesCount(source.getFapFramesCount());
            res = true;
        }
        if (!equals(target.getFapRejectedFramesCount(), source.getFapRejectedFramesCount())) {
            target.setFapRejectedFramesCount(source.getFapRejectedFramesCount());
            res = true;
        }
        if (!equals(target.getComments(), source.getComments())) {
            target.setComments(source.getComments());
            res = true;
        }
        if (!equals(target.getDataDirectory(), source.getDataDirectory())) {
            target.setDataDirectory(source.getDataDirectory());
            res = true;
        }
        if (!equals(target.getAircraftId(), source.getAircraftId())) {
            target.setAircraftId(source.getAircraftId());
            res = true;
        }
        if (!equals(target.getLogin(), source.getLogin())) {
            target.setLogin(source.getLogin());
            res = true;
        }
        return res;
    }

    //todo do not use reflection for massive updates
    public static boolean setChanged(Parameter target, Parameter source) {
        List<String> ignoreProperties = Arrays.asList("id");
        try {
            return setChangedProperties(target, source, ignoreProperties);
        }
        catch (RAException e) {
            log.error("setChanged error", e);
            return false;
        }
    }

    public static <T, K extends T> boolean setChangedProperties(T target, K source , List<String> ignoreProperties)
            throws RAException {
        boolean res = false;
        Class clazz = target.getClass();
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(clazz);
        for (PropertyDescriptor targetPd : targetPds) {

            PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
            if (sourcePd != null) {
                Method readMethod = sourcePd.getReadMethod();
                Method targetReadMethod = targetPd.getReadMethod();
                if (readMethod != null && targetReadMethod != null) {
                    try {
                        if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                            readMethod.setAccessible(true);
                        }
                        if (!Modifier.isPublic(targetReadMethod.getDeclaringClass().getModifiers())) {
                            targetReadMethod.setAccessible(true);
                        }
                        Object sourceValue = readMethod.invoke(source);
                        Object targetValue = targetReadMethod.invoke(target);
                        if (!equals(targetValue, sourceValue)) {
                            Method writeMethod = targetPd.getWriteMethod();
                            if (writeMethod != null &&
                                    (ignoreProperties == null || (!ignoreProperties.contains(targetPd.getName())))) {
                                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                                    writeMethod.setAccessible(true);
                                }
                                writeMethod.invoke(target, sourceValue);
                                res = true;
                            }
                        }
                    } catch (Throwable ex) {
                        throw new RAException(
                                "Could not copy property '" + targetPd.getName() + "' from source to target", ex);
                    }
                }
            }
        }
        return res;
    }

    /**
     * Compares two beans and returns differences of its properties : map of (property name : values difference).
     * property name can be nested, if nestedClasses list contains property class : "field1.field2"
     * values difference format : old value + " -> " + new value;
     * @param oldVersion old bean
     * @param newVersion new bean
     * @param ignoreProperties property names, that will be excluded from comparison
     * @param nestedClasses list of classes. if nestedClasses contains bean property class,
     *                      this object inner properties will be compared
     * @param <T> compared beans type
     * @return map of property names -> value differences
     */
    public static <T> Map<String, String> getChangedProperties(T oldVersion, T newVersion,
                                                                            List<String> ignoreProperties,
                                                                            List<Class> nestedClasses) {
        Map<String, String> res = new HashMap<>();
        BeanMap bm1 = new BeanMap(oldVersion);
        BeanMap bm2 = new BeanMap(newVersion);
        for (Map.Entry e : bm1.entrySet()) {
            Object key = e.getKey();
            if (ignoreProperties != null && ignoreProperties.contains(key)) {
                continue;
            }
            Object old = e.getValue();
            Object newObj = bm2.get(key);
            Map<String, String> inner = null;
            if (nestedClasses != null) {
                for (Class clazz : nestedClasses) {
                    if (old != null && newObj != null &&
                            clazz.isInstance(old) && clazz.isInstance(newObj)) {
                        inner = getChangedProperties(old, newObj, ignoreProperties, nestedClasses);
                        break;
                    }
                }
            }
            if (inner != null) {
                //add properties
                for (Map.Entry<String, String> entry : inner.entrySet()) {
                    res.put(String.valueOf(key) + "." + entry.getKey(), entry.getValue());
                }
            }
            else {
                if (!equals(old, newObj)) {
                    res.put(String.valueOf(key), String.valueOf(old) + " -> "+ newObj);
                }
            }
        }
        return res;
    }

    /**
     * Validates leg, creates message if validation errors occurred.
     * @param leg Leg object
     * @return ErrorMessage if errors occurred, null otherwise
     */
    public static ErrorMessage validateLeg(Leg leg) {
        List<String> messages = new ArrayList<>();
        if (leg == null) {
            ErrorMessage res = new ErrorMessage("Leg validation error: leg is null");
            res.getMessages().add("LEG");
            return res;
        }
        if (leg.getDepartureFact() == null) {
            messages.add("DEPARTURE_FACT");
        }
        if (leg.getTakeOff() == null) {
            messages.add("TAKE_OFF");
        }
        if (leg.getArrivalFact() == null) {
            messages.add("ARRIVAL_FACT");
        }
        if (leg.getTouchDown() == null) {
            messages.add("TOUCH_DOWN");
        }
        if (leg.getTailNum() == null) {
            messages.add("TAIL_NO");
        }
        if (!messages.isEmpty()) {
            ErrorMessage res = new ErrorMessage("Leg validation error, values is not defined");
            res.getMessages().addAll(messages);
            return res;
        }
        return null;
    }

    /**
     * Validates leg, creates message if validation errors occurred.
     * In case of only touchDown field invalid, fix this field.
     * @param originalLeg Leg object
     * @param touchDownFixShift value to fix touchDown field
     * @return tuple (ErrorMessage, Leg). If leg was fixed, ErrorMessage value is null, only fixed leg returned
     */
    public static Pair<ErrorMessage, Leg> validateAndFixLeg(Leg originalLeg, int touchDownFixShift) {
        MutablePair<ErrorMessage, Leg> res = new MutablePair<>();
        Leg leg = SerializationUtils.clone(originalLeg);
        List<String> messages = new ArrayList<>();
        if (leg == null) {
            res.setLeft(new ErrorMessage("Leg validation error: leg is null"));
            return res;
        }
        if (leg.getDepartureFact() == null) {
            messages.add("DEPARTURE_FACT");
        }
        if (leg.getTakeOff() == null) {
            messages.add("TAKE_OFF");
        }
        if (leg.getArrivalFact() == null) {
            messages.add("ARRIVAL_FACT");
        }
        if (leg.getTouchDown() == null) {
            if (leg.getArrivalFact() != null) {
                Calendar cld = Calendar.getInstance();
                cld.setTime(leg.getArrivalFact());
                cld.add(Calendar.MINUTE, -touchDownFixShift);
                leg.setTouchDown(new Timestamp(cld.getTimeInMillis()));
            }
            else {
                messages.add("TOUCH_DOWN");
            }
        }
        if (leg.getTailNum() == null) {
            messages.add("TAIL_NO");
        }
        if (!messages.isEmpty()) {
            ErrorMessage msg = new ErrorMessage("Leg id " + leg.getId() + ": validation errors:\n");
            msg.getMessages().addAll(messages);
            res.setLeft(msg);
        }
        res.setRight(leg);
        return res;
    }

    public static LogMessage createLogMessage(String classifier, String errorCode, Level level, Map properties) {
        LogMessage res = new LogMessage(classifier, errorCode, level.toString(),
                new Timestamp(System.currentTimeMillis()));
        JSONObject json = new JSONObject(properties);
        String props = JSONUtil.jsonToString(json, JSON_MAX_SIZE);
        res.setProperties(props);
        res.setSource("cselp");
        return res;
    }

    public static LogMessage createLogMessage(String classifier, String errorCode, Level level, long time,
                                              Map properties) {
        LogMessage res = new LogMessage(classifier, errorCode, level.toString(), new Timestamp(time));
        JSONObject json = new JSONObject(properties);
        String props = JSONUtil.jsonToString(json, JSON_MAX_SIZE);
        res.setProperties(props);
        res.setSource("cselp");
        return res;
    }

    public static LogMessage createLogMessage(String classifier, String errorCode, Level level,
                                              Throwable thr) {
        LogMessage res = new LogMessage(classifier, errorCode, level.toString(),
                new Timestamp(System.currentTimeMillis()));
        try {
            JSONObject json = new JSONObject();
            json.put("message", thr.getLocalizedMessage());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, false, "UTF-8");
            thr.printStackTrace(ps);
            ps.flush();
            json.put("stackTrace", baos.toString("UTF-8"));
            String props = JSONUtil.jsonToString(json, JSON_MAX_SIZE);
            res.setProperties(props);
        } catch (Exception e) {
            log.error("createLogMessage error", e);
        }
        res.setSource("cselp");
        return res;
    }

    public static LogMessage createLogMessage(String classifier, String errorCode, Level level,
                                              Throwable thr, Map properties) {
        LogMessage res = new LogMessage(classifier, errorCode, level.toString(),
                new Timestamp(System.currentTimeMillis()));
        try {
            JSONObject json = new JSONObject(properties);
            json.put("message", thr.getLocalizedMessage());
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, false, "UTF-8");
            thr.printStackTrace(ps);
            ps.flush();
            json.put("stackTrace", baos.toString("UTF-8"));
            String props = JSONUtil.jsonToString(json, JSON_MAX_SIZE);
            res.setProperties(props);
        } catch (Exception e) {
            log.error("createLogMessage error", e);
        }
        res.setSource("cselp");
        return res;
    }

}

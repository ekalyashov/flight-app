package cselp;


import cselp.domain.local.Person;
import cselp.domain.local.PersonMinimum;
import cselp.util.DataUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;

@RunWith(BlockJUnit4ClassRunner.class)
public class CommonTest {

    @Test
    public void testChangedProperties() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Person p1 = new Person();
        p1.setFullName("fname1");
        Person p2 = new Person();
        p2.setId(2L);
        p2.setFullName("fname2");
        PersonMinimum pm2 = new PersonMinimum();
        pm2.setPersonId(p2.getId());
        pm2.setLandingVisHorizontal(2.2);
        pm2.setLandingVisVertical(3.3);
        p2.setPersonMinimum(pm2);
        Person p3 = new Person();
        p3.setId(3L);
        p3.setFullName("fname3");
        PersonMinimum pm3 = new PersonMinimum();
        pm3.setPersonId(p3.getId());
        pm3.setLandingVisHorizontal(4.4);
        pm3.setLandingVisVertical(5.5);
        p3.setPersonMinimum(pm3);
        List<String> ignoreProperties = Arrays.asList("id", "personId");
        List<Class> nestedClasses = new ArrayList<>();
        nestedClasses.add(Person.class);
        nestedClasses.add(PersonMinimum.class);
        Map<String, String> diffProps = DataUtil.getChangedProperties(p2, p3, ignoreProperties, nestedClasses);
        for (Map.Entry e : diffProps.entrySet()) {
            System.out.println("===difference " + e.getKey() + " : " + e.getValue());
        }
        assertTrue("difference", diffProps.size() > 0);
    }
}

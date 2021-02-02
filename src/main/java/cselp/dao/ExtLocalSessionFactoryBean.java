package cselp.dao;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate4.LocalSessionFactoryBuilder;


public class ExtLocalSessionFactoryBean extends LocalSessionFactoryBean {

    protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
        sfb.registerTypeOverride(UtcTimestampType.INSTANCE);
        return super.buildSessionFactory(sfb);
    }
}

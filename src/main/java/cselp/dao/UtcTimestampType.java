package cselp.dao;

import org.hibernate.HibernateException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.*;
import org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

public class UtcTimestampType extends AbstractSingleColumnStandardBasicType<Date>
        implements VersionType<Date>, LiteralType<Date> {

    public static final UtcTimestampType INSTANCE = new UtcTimestampType();

    public UtcTimestampType() {
        super(UtcTimestampTypeDescriptor.INSTANCE, JdbcTimestampTypeDescriptor.INSTANCE );
    }

    public String getName() {
        return "utcTimestamp";
    }

    @Override
    public String[] getRegistrationKeys() {
        return new String[] { getName() };
    }

    public Date next(Date current, SessionImplementor session) {
        return seed( session );
    }

    public Date seed(SessionImplementor session) {
        return new Timestamp( System.currentTimeMillis() );
    }

    public Comparator<Date> getComparator() {
        return getJavaTypeDescriptor().getComparator();
    }

    public String objectToSQLString(Date value, Dialect dialect) throws Exception {
        final Timestamp ts = Timestamp.class.isInstance( value )
                ? ( Timestamp ) value
                : new Timestamp( value.getTime() );
        return StringType.INSTANCE.objectToSQLString( ts.toString(), dialect );
    }

    public Date fromStringValue(String xml) throws HibernateException {
        return fromString( xml );
    }
}

package cselp.dao;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.type.descriptor.ValueExtractor;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.type.descriptor.sql.BasicExtractor;
import org.hibernate.type.descriptor.sql.TimestampTypeDescriptor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;


public class UtcTimestampTypeDescriptor extends TimestampTypeDescriptor {
    private static final Log log = LogFactory.getLog(UtcTimestampType.class);
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static final UtcTimestampTypeDescriptor INSTANCE = new UtcTimestampTypeDescriptor();

    @Override
    public <X> ValueExtractor<X> getExtractor(final JavaTypeDescriptor<X> javaTypeDescriptor) {
        return new BasicExtractor<X>( javaTypeDescriptor, this ) {
            @Override
            protected X doExtract(ResultSet rs, String name, WrapperOptions options) throws SQLException {
                //Timestamp local = rs.getTimestamp(name);
                Timestamp res = rs.getTimestamp(name, Calendar.getInstance(UTC));
                /*if (local != null && res != null) {
                    log.info("===[" + name + "] local ts : " + local.toString() + ", " + local.getTime() + "; " + local.getTimezoneOffset() +
                            ", UTC ts : " + res.toString() + ", " + res.getTime() + "; " + res.getTimezoneOffset());
                } */
                return javaTypeDescriptor.wrap(res, options);
            }
        };
    }
}

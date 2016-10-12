package cn.enilu.flash.core.db.support;

import org.joda.time.DateTime;
import org.springframework.core.convert.converter.Converter;

public class LongToDateTimeConverter implements Converter<Long, DateTime> {
    @Override
    public DateTime convert(Long source) {
        if (source == null) {
            return null;
        }
        return new DateTime(source.longValue());
    }
}

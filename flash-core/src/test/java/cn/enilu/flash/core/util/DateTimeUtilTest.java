package cn.enilu.flash.core.util;

import org.joda.time.DateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DateTimeUtilTest {
    private DateTime dateTime = new DateTime(2014, 1, 2, 3, 4, 5);

    @Test
    public void testFormatDate() {
        assertEquals("2014-01-02", DateTimeUtil.formatDate(dateTime));
    }

    @Test
    public void testFormatTime() {
        assertEquals("03:04:05", DateTimeUtil.formatTime(dateTime));
    }

    @Test
    public void testFormatDateTime() {
        assertEquals("2014-01-02 03:04:05", DateTimeUtil.formatDateTime(dateTime));
    }
    
    @Test
    public void testParseDate() {
        DateTime dt = DateTimeUtil.parseDate("2014-01-02");
        assertEquals(new DateTime(2014, 1, 2, 0, 0, 0), dt);
    }
    
    @Test
    public void testFormatHumanReadable() {
        assertEquals("", DateTimeUtil.formatHumanReadable((DateTime)null));
        
        DateTime now = DateTime.now();
        assertEquals("刚刚", DateTimeUtil.formatHumanReadable(now));
        assertEquals("1秒前", DateTimeUtil.formatHumanReadable(now.minusSeconds(1)));
        assertEquals("59秒前", DateTimeUtil.formatHumanReadable(now.minusSeconds(59)));
        assertEquals("1分钟前", DateTimeUtil.formatHumanReadable(now.minusMinutes(1)));
        assertEquals("59分钟前", DateTimeUtil.formatHumanReadable(now.minusMinutes(59)));
        assertEquals("1小时前", DateTimeUtil.formatHumanReadable(now.minusHours(1)));
        assertEquals("23小时前", DateTimeUtil.formatHumanReadable(now.minusHours(23)));
        assertEquals("1天前", DateTimeUtil.formatHumanReadable(now.minusDays(1)));
        assertEquals("364天前", DateTimeUtil.formatHumanReadable(now.minusDays(364)));
        assertEquals("1年前", DateTimeUtil.formatHumanReadable(now.minusDays(365)));

    }
}

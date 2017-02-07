package org.crud.core.util;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;

import static java.time.ZoneOffset.UTC;
import static org.crud.core.util.DateUtils.LOCAL_ZONE_OFFSET;
import static org.junit.Assert.*;

public class DateUtilsTest {

    @Test
    public void from_ms() throws Exception {
        assertEquals(ZonedDateTime.parse("1970-01-01T00:00:00Z"), DateUtils.fromMillis(0).withZoneSameInstant(UTC));
        assertEquals(ZonedDateTime.parse("2010-10-03T13:45:42.456Z"), DateUtils.fromMillis(1286113542456L).withZoneSameInstant(UTC));
    }

    @Test
    public void to_ms() throws Exception {
        assertEquals(1368353557954L, DateUtils.toMillis(ZonedDateTime.parse("2013-05-12T10:12:37.954Z")));

    }

    @Test
    public void current_time() throws Exception {
        Instant now = Instant.parse("2013-05-12T10:12:37.954Z");
        DateUtils.setClock(Clock.fixed(now, UTC));
        assertEquals(now.atZone(UTC), DateUtils.now());

    }

    @Test
    public void from_string() throws Exception {
        assertEquals(1196673330000L, DateUtils.parseZonedDateTime("2007-12-03T10:15:30+01:00[Europe/Paris]").toInstant().toEpochMilli());
        assertEquals(1196673330000L, DateUtils.parseZonedDateTime("2007-12-03T10:15:30").toInstant().toEpochMilli());

    }

    @Test
    public void from_date() throws Exception {
        assertEquals(0 - LOCAL_ZONE_OFFSET.getTotalSeconds() * 1000, DateUtils.fromDate(1970, 1, 1).toInstant().toEpochMilli());
        assertEquals(1196640000000L - LOCAL_ZONE_OFFSET.getTotalSeconds() * 1000, DateUtils.fromDate(2007, 12, 03).toInstant().toEpochMilli());

    }

    @Test
    public void normalize_date() throws Exception {
        assertEquals("2013-05-12T00:00Z", DateUtils.normalizeDate(ZonedDateTime.parse("2013-05-12T10:12:37.954Z")).toString());
    }

}

package org.crud.core.util

import spock.lang.Specification

import java.time.Clock
import java.time.Instant
import java.time.ZonedDateTime

import static java.time.ZoneOffset.UTC
import static org.crud.core.util.DateUtils.LOCAL_ZONE_OFFSET

/**
 *
 */
class DateUtilsTest extends Specification {
    def "Convert to a date from number of milliseconds (epoch)"() {
        expect:
        DateUtils.fromMillis(1286113542456).withZoneSameInstant(UTC) == ZonedDateTime.parse("2010-10-03T13:45:42.456Z")
    }

    def "Convert a date to number of milliseconds (epoch)"() {
        expect:
        DateUtils.toMillis(ZonedDateTime.parse("2013-05-12T10:12:37.954Z")) == 1368353557954L
    }

    def "Return current date-time"() {
        def now = Instant.parse("2013-05-12T10:12:37.954Z");

        setup:
        DateUtils.setClock(Clock.fixed(now, UTC));

        expect:
        DateUtils.now() == now.atZone(UTC);
    }

    def "Change clock"() {
        def now = Instant.parse("2013-05-12T10:12:37.954Z");
        def clock = Clock.fixed(now, UTC)

        when:
        DateUtils.setClock(clock);

        then:
        DateUtils.getClock() == clock;
    }

    def "Parse a date from string"(String date, long epochMilli) {
        expect:
        def result = DateUtils.parseZonedDateTime(date)

        result.toInstant().toEpochMilli() == epochMilli

        where:
        date                                      || epochMilli
        "2007-12-03T10:15:30+01:00[Europe/Paris]" || 1196673330000
        "2007-12-03T10:15:30"                     || 1196673330000
    }

    def "Parse a date from year, month and day of month"(int year, int month, int dayOfMonth, long epochMilli) {
        expect:
        def result = DateUtils.fromDate(year, month, dayOfMonth)

        result.toInstant().toEpochMilli() == epochMilli - LOCAL_ZONE_OFFSET.totalSeconds * 1000

        where:
        year | month | dayOfMonth || epochMilli
        1970 | 1     | 01         || 0
        2007 | 12    | 03         || 1196640000000
    }

    def "Normalize a date"(dateStr, expectedStr) {
        def date = ZonedDateTime.parse(dateStr)
        def expected = ZonedDateTime.parse(expectedStr)

        expect:
        DateUtils.normalizeDate(date) == expected

        where:
        dateStr                                   || expectedStr
        "2007-12-03T10:15:30+01:00[Europe/Paris]" || "2007-12-03T00:00:00+01:00[Europe/Paris]"
        "2013-05-12T10:12:37.954Z"                || "2013-05-12T00:00:00.000Z"
    }
}

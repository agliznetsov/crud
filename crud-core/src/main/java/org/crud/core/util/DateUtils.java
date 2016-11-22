package org.crud.core.util;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateUtils {
    public static final ZoneOffset LOCAL_ZONE_OFFSET = Clock.systemDefaultZone().getZone().getRules().getOffset(Instant.EPOCH);

    public static ZonedDateTime fromDate(Date date) {
        return fromMillis(date.getTime());
    }

    public static Date toDate(ZonedDateTime dateTime) {
        return new Date(toMillis(dateTime));
    }

    public static ZonedDateTime fromMillis(long time) {
        Instant instant = Instant.ofEpochMilli(time);
        return ZonedDateTime.ofInstant(instant, LOCAL_ZONE_OFFSET);
    }

    public static long toMillis(ZonedDateTime time) {
        return time.toInstant().toEpochMilli();
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(LOCAL_ZONE_OFFSET);
    }

    public static ZonedDateTime parseZonedDateTime(String text) {
        ZonedDateTime date;

        try {
            date = ZonedDateTime.parse(text);
        } catch (DateTimeParseException parseException) {
            // try to parse Local Date Time and translate to the system timezone
            LocalDateTime localTime = LocalDateTime.parse(text);
            date = ZonedDateTime.of(localTime, ZoneId.systemDefault());
        }

        return date;
    }

    public static ZonedDateTime fromDate(int year, int month, int dayOfMonth) {
        return ZonedDateTime.of(year, month, dayOfMonth, 0, 0, 0, 0, LOCAL_ZONE_OFFSET);
    }

    public static ZonedDateTime normalizeDate(ZonedDateTime date) {
        return date.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}

package org.crud.core.util;

import lombok.Getter;
import lombok.Setter;

import java.time.*;
import java.time.format.DateTimeParseException;

public abstract class DateUtils {
    @Getter
    @Setter
    protected static Clock clock = Clock.system(Clock.systemDefaultZone().getZone().getRules().getOffset(Instant.EPOCH));

    public static final ZoneOffset LOCAL_ZONE_OFFSET = Clock.systemDefaultZone().getZone().getRules().getOffset(Instant.EPOCH);

    public static ZonedDateTime fromMillis(long time) {
        Instant instant = Instant.ofEpochMilli(time);
        return ZonedDateTime.ofInstant(instant, LOCAL_ZONE_OFFSET);
    }

    public static long toMillis(ZonedDateTime time) {
        return time.toInstant().toEpochMilli();
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now(clock);
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

    /**
     * Empty time part of a given datetime
     * @param date
     * @return
     */
    public static ZonedDateTime normalizeDate(ZonedDateTime date) {
        return date.withHour(0).withMinute(0).withSecond(0).withNano(0);
    }
}

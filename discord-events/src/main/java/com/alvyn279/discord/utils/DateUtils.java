package com.alvyn279.discord.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Set of date utils pertaining to the Discord model
 */
public class DateUtils {

    public static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("America/Toronto");

    /**
     * Creates an {@link Instant} object from a date string and time string
     * @param dateString date string of format MM/DD/YYYY
     * @param timeString time of day of format 23:59
     * @return Instant object
     */
    public static Instant fromDateAndTime(String dateString, String timeString) {
        return LocalDateTime
            .parse(
                String.format("%1$s, %2$s", dateString, timeString),
                DateTimeFormatter.ofPattern("M/d/uuuu, H:mm", Locale.CANADA))
            .atZone(DEFAULT_TIMEZONE)
            .toInstant();
    }
}

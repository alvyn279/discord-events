package com.alvyn279.discord.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Set of date utils pertaining to the Discord model
 */
public class DateUtils {

    public static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("America/Toronto");
    public static final Locale DEFAULT_LOCALE = Locale.CANADA;

    /**
     * Creates an {@link Instant} object from a date string and time string
     *
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

    /**
     * Pretty prints an {@link Instant} object.
     * Ex: February 2, 2021 at 5:00:00 a.m. EST
     *
     * @param instant Instant object
     * @return String for Date
     */
    public static String prettyPrintInstantInLocalTimezone(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG)
            .withLocale(DEFAULT_LOCALE)
            .withZone(DEFAULT_TIMEZONE);
        return formatter.format(instant);
    }
}

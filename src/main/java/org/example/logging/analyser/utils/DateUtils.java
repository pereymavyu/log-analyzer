package org.example.logging.analyser.utils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss Z");

    public static Date parseFromZonedDateTimeString(String dateString) {
        return Date.from(ZonedDateTime.parse(dateString, FORMATTER).toInstant());
    }
}

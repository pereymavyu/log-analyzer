package org.example.logging.analyser.service;

import lombok.extern.slf4j.Slf4j;
import org.example.logging.analyser.model.LogRecord;
import org.example.logging.analyser.utils.DateTimeUtils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LogParser {
    private static final String NON_SPACE_GROUP_PATTERN_STRING = "(\\S+)";
    private static final String SPACE_SEPARATOR_PATTERN_STRING = "\\s";
    private static final String ZONED_DATE_TIME_PATTERN_STRING = "(?<date>\\[\\d{2}/\\d{2}/\\d{4}:\\d{2}:\\d{2}:\\d{2}\\s[+\\-]\\d{4}])";
    private static final String QUOTED_GROUP_PATTERN_STRING = "(\"[^\"]+\")";
    private static final String HTTP_CODE_PATTERN_STRING = "(?<httpCode>\\d{3})";
    private static final String REQUEST_TIME_PATTERN_STRING = "(?<requestTime>[\\d.]+)";
    private static final String ANY_SYMBOL_GROUP_PATTERN_STRING = "(.+)";

    private static final String LOG_PATTERN_STRING = String.join(
            SPACE_SEPARATOR_PATTERN_STRING,
            NON_SPACE_GROUP_PATTERN_STRING,
            NON_SPACE_GROUP_PATTERN_STRING,
            NON_SPACE_GROUP_PATTERN_STRING,
            ZONED_DATE_TIME_PATTERN_STRING,
            QUOTED_GROUP_PATTERN_STRING,
            HTTP_CODE_PATTERN_STRING,
            NON_SPACE_GROUP_PATTERN_STRING,
            REQUEST_TIME_PATTERN_STRING,
            ANY_SYMBOL_GROUP_PATTERN_STRING
    );

    private static final Pattern LOG_PATTERN = Pattern.compile(LOG_PATTERN_STRING);

    public static LogRecord parseLine(String logLine) {
        try {
            Matcher matcher = LOG_PATTERN.matcher(logLine);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("Log does not match the pattern");
            }
            String dateString = matcher.group("date");
            dateString = dateString.substring(1, dateString.length() - 1);

            Date date = DateTimeUtils.parseFromZonedDateTimeString(dateString);
            int httpCode = Integer.parseInt(matcher.group("httpCode"));
            double responseTime = Double.parseDouble(matcher.group("requestTime"));

            return new LogRecord(
                    date,
                    httpCode,
                    responseTime
            );
        } catch (Exception e) {
            log.error(String.format("Error during parsing record \"%s\"", logLine));
            throw e;
        }
    }
}
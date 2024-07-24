package org.example.logging.analyser;

import lombok.extern.slf4j.Slf4j;
import org.example.logging.analyser.model.LogRecord;
import org.example.logging.analyser.utils.DateUtils;

import java.util.Date;

@Slf4j
public class LogParser {

    public static LogRecord parseLine(String logLine) {
        try {
            String[] tokens = logLine.split("\\s");

            String dateString = tokens[3] + " " + tokens[4];
            dateString = dateString.substring(1, dateString.length() - 1);
            Date date = DateUtils.parseFromZonedDateTimeString(dateString);
            int httpCode = Integer.parseInt(tokens[8]);
            double responseTime = Double.parseDouble(tokens[10]);

            return new LogRecord(
                    date,
                    httpCode,
                    responseTime
            );
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("Error during parsing record \"%s\"", logLine),
                    e
            );
        }
    }
}
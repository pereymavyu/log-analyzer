package org.example.logging.analyser.model;

import lombok.Getter;

import java.util.Date;

@Getter
public class AvailabilityMonitoringPeriod {
    private final AvailabilityStatistics stats;
    private final Date start;
    private Date end;

    public AvailabilityMonitoringPeriod(LogRecord logRecord) {
        stats = new AvailabilityStatistics();
        stats.addLogRecord(logRecord);
        start = logRecord.getDateTime();
        end = logRecord.getDateTime();
    }

    public void addLogRecord(LogRecord logRecord) {
        stats.addLogRecord(logRecord);
        end = logRecord.getDateTime();
    }
}

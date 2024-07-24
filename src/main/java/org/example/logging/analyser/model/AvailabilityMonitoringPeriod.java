package org.example.logging.analyser.model;

import lombok.Getter;

import java.util.Date;

@Getter
public class AvailabilityMonitoringPeriod {
    private final AvailabilityStatistics stats;
    private final Date start;
    private Date end;

    public AvailabilityMonitoringPeriod(Date start) {
        stats = new AvailabilityStatistics();
        this.start = start;
    }

    public void addNewLogRecordInfo(boolean isSuccessful, Date requestDate) {
        stats.addNewLogRecordInfo(isSuccessful);
        end = requestDate;
    }
}

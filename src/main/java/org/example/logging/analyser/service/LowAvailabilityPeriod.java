package org.example.logging.analyser.service;

import lombok.Getter;

import java.util.Date;

@Getter
public class LowAvailabilityPeriod {
    private final AvailabilityStats stats;
    private final Date start;
    private Date end;

    public LowAvailabilityPeriod(Date start) {
        stats = new AvailabilityStats();
        this.start = start;
    }

    public void addNewLogRecordInfo(boolean isSuccessful, Date requestDate) {
        stats.addNewLogRecordInfo(isSuccessful);
        end = requestDate;
    }
}

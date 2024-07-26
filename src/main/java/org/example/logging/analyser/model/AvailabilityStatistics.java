package org.example.logging.analyser.model;

import lombok.Getter;

@Getter
public class AvailabilityStatistics {
    private long successRequestCount;
    private long failedRequestCount;

    public void addLogRecord(LogRecord logRecord) {
        if (logRecord.getIsSuccessful()) {
            ++successRequestCount;
        } else {
            ++failedRequestCount;
        }
    }

    public double calculateAvailability() {
        if (successRequestCount == 0 && failedRequestCount == 0) {
            return 100;
        }
        return (successRequestCount * 100.0) / (successRequestCount + failedRequestCount);
    }
}

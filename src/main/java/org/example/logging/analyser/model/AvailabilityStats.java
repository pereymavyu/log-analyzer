package org.example.logging.analyser.model;

import lombok.Getter;

@Getter
public class AvailabilityStats {
    private long successRequestCount;
    private long failedRequestCount;

    public void addNewLogRecordInfo(boolean isSuccess) {
        if (isSuccess) {
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

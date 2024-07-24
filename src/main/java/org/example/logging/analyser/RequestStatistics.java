package org.example.logging.analyser;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
public class RequestStatistics {
    private long successRequestCount;
    private long failedRequestCount;

    @Setter
    private Date start;
    @Setter
    private Date end;

    public void incrementRequestCount(boolean isSuccess) {
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

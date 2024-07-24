package org.example.logging.analyser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logging.analyser.model.AvailabilityStats;
import org.example.logging.analyser.model.LogRecord;
import org.example.logging.analyser.model.LowAvailabilityPeriod;
import org.example.logging.analyser.model.ServiceAvailabilityStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.example.logging.analyser.model.ServiceAvailabilityStatus.ACCEPTABLE;
import static org.example.logging.analyser.model.ServiceAvailabilityStatus.NOT_ACCEPTABLE;

@Slf4j
@RequiredArgsConstructor
public class LogAnalyser {
    private final double minAvailability;
    private final double maxResponseTime;
    private final AnalysisResultHandler resultLogger;

    public void analyze(InputStream inputStream) {
        log.info("Log analysis started");

        AvailabilityStats totalStats = new AvailabilityStats();
        String currentLine = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            LowAvailabilityPeriod lowAvailabilityPeriod = null;

            while ((currentLine = reader.readLine()) != null) {
                LogRecord logRecord = LogParser.parseLine(currentLine);
                boolean isSuccessfulRequest = isSuccessfulRequest(logRecord);
                totalStats.addNewLogRecordInfo(isSuccessfulRequest);

                ServiceAvailabilityStatus availabilityStatus = getAvailabilityStatus(
                        totalStats.calculateAvailability()
                );

                if (lowAvailabilityPeriod == null && availabilityStatus == NOT_ACCEPTABLE) {
                    lowAvailabilityPeriod = new LowAvailabilityPeriod(logRecord.getDateTime());
                    lowAvailabilityPeriod.addNewLogRecordInfo(isSuccessfulRequest, logRecord.getDateTime());
                } else if (lowAvailabilityPeriod != null && availabilityStatus == NOT_ACCEPTABLE) {
                    lowAvailabilityPeriod.addNewLogRecordInfo(isSuccessfulRequest, logRecord.getDateTime());
                } else if (lowAvailabilityPeriod != null && availabilityStatus == ACCEPTABLE) {
                    logLowAvailabilityPeriod(lowAvailabilityPeriod);
                    lowAvailabilityPeriod = null;
                }
            }
            if (lowAvailabilityPeriod != null) {
                logLowAvailabilityPeriod(lowAvailabilityPeriod);
            }

            log.info("Log analysis completed successfully");
        } catch (Exception e) {
            log.error(
                    String.format(
                            "Error during log analysis. Number of processed records \"%d\". Last processed record \"%s\"",
                            totalStats.getSuccessRequestCount() + totalStats.getFailedRequestCount(),
                            currentLine
                    ),
                    e
            );
        }
    }

    private boolean isSuccessfulRequest(LogRecord logRecord) {
        return !String.valueOf(logRecord.getHttpCode()).startsWith("5") && logRecord.getResponseTime() <= maxResponseTime;
    }

    private ServiceAvailabilityStatus getAvailabilityStatus(double availability) {
        return availability >= minAvailability
                ? ACCEPTABLE
                : NOT_ACCEPTABLE;
    }

    private void logLowAvailabilityPeriod(LowAvailabilityPeriod lowAvailabilityPeriod) {
        resultLogger.logAvailabilityStats(
                lowAvailabilityPeriod.getStart(),
                lowAvailabilityPeriod.getEnd(),
                lowAvailabilityPeriod.getStats().calculateAvailability()
        );
    }
}

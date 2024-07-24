package org.example.logging.analyser.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logging.analyser.model.AvailabilityMonitoringPeriod;
import org.example.logging.analyser.model.AvailabilityStatistics;
import org.example.logging.analyser.model.LogRecord;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@RequiredArgsConstructor
public class LogAnalyser {
    private final double minAvailability;
    private final double maxResponseTime;
    private final AnalysisResultHandler resultLogger;

    public void analyze(InputStream inputStream) {
        log.info("Log analysis started");

        AvailabilityStatistics totalStats = new AvailabilityStatistics();
        String currentLine = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            AvailabilityMonitoringPeriod lowAvailabilityPeriod = null;

            while ((currentLine = reader.readLine()) != null) {
                LogRecord logRecord = LogParser.parseLine(currentLine);
                logRecord.setIsSuccessful(isSuccessfulRequest(logRecord));

                totalStats.addLogRecord(logRecord);
                boolean isAvailabilityOk = totalStats.calculateAvailability() >= minAvailability;

                if (lowAvailabilityPeriod == null && !isAvailabilityOk) {
                    lowAvailabilityPeriod = new AvailabilityMonitoringPeriod(logRecord);
                } else if (lowAvailabilityPeriod != null && !isAvailabilityOk) {
                    lowAvailabilityPeriod.addLogRecord(logRecord);
                } else if (lowAvailabilityPeriod != null) {
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

    private void logLowAvailabilityPeriod(AvailabilityMonitoringPeriod lowAvailabilityPeriod) {
        resultLogger.logAvailabilityStats(
                lowAvailabilityPeriod.getStart(),
                lowAvailabilityPeriod.getEnd(),
                lowAvailabilityPeriod.getStats().calculateAvailability()
        );
    }
}

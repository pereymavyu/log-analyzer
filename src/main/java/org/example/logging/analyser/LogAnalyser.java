package org.example.logging.analyser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.logging.analyser.model.LogRecord;
import org.example.logging.analyser.model.ServiceAvailabilityStatus;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.example.logging.analyser.model.ServiceAvailabilityStatus.NOT_OK;
import static org.example.logging.analyser.model.ServiceAvailabilityStatus.OK;

@Slf4j
@RequiredArgsConstructor
public class LogAnalyser {
    private final double minAvailability;
    private final double maxResponseTime;
    private final AnalysisResultHandler resultLogger;

    public void analyze(InputStream inputStream) {
        log.info("Log analysis started");

        String currentLine = null;
        RequestStatistics totalStatistics = new RequestStatistics();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            RequestStatistics currentPeriodStatistics = null;

            while ((currentLine = reader.readLine()) != null) {
                LogRecord logRecord = LogParser.parseLine(currentLine);

                boolean isRequestSuccessful = isRequestSuccesful(logRecord);
                totalStatistics.incrementRequestCount(isRequestSuccessful);

                ServiceAvailabilityStatus currentStatus = getAvailabilityStatus(
                        totalStatistics.calculateAvailability()
                );

                if (currentPeriodStatistics == null && currentStatus == NOT_OK) {
                    currentPeriodStatistics = new RequestStatistics();
                    currentPeriodStatistics.incrementRequestCount(isRequestSuccessful);
                    currentPeriodStatistics.setStart(logRecord.getDateTime());
                    currentPeriodStatistics.setEnd(logRecord.getDateTime());
                } else if (currentPeriodStatistics != null && currentStatus == NOT_OK) {
                    currentPeriodStatistics.incrementRequestCount(isRequestSuccessful);
                    currentPeriodStatistics.setEnd(logRecord.getDateTime());
                } else if (currentPeriodStatistics != null && currentStatus == OK) {
                    resultLogger.logAvailabilityStatistics(
                            currentPeriodStatistics.getStart(),
                            currentPeriodStatistics.getEnd(),
                            currentPeriodStatistics.calculateAvailability()
                    );
                    currentPeriodStatistics = null;
                }
            }
            if (currentPeriodStatistics != null) {
                resultLogger.logAvailabilityStatistics(
                        currentPeriodStatistics.getStart(),
                        currentPeriodStatistics.getEnd(),
                        currentPeriodStatistics.calculateAvailability()
                );
            }

            log.info("Log analysis completed successfully");
        } catch (Exception e) {
            log.error(
                    String.format(
                            "Error during log analysis. Number of processed records \"%d\". Last processed record \"%s\"",
                            totalStatistics.getSuccessRequestCount() + totalStatistics.getFailedRequestCount(),
                            currentLine
                    ),
                    e
            );
        }
    }

    public ServiceAvailabilityStatus getAvailabilityStatus(double availability) {
        return availability >= minAvailability
                ? OK
                : NOT_OK;
    }

    private boolean isRequestSuccesful(LogRecord logRecord) {
        return !String.valueOf(logRecord.getHttpCode()).startsWith("5") && logRecord.getResponseTime() < maxResponseTime;
    }
}

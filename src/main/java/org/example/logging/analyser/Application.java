package org.example.logging.analyser;

import lombok.extern.slf4j.Slf4j;
import org.example.logging.analyser.service.AnalysisResultHandler;
import org.example.logging.analyser.service.CliParser;
import org.example.logging.analyser.service.LogAnalyser;

@Slf4j
public class Application {
    public static void main(String[] args) {
        double minAvailabilityLevel = 0;
        double maxResponseTime = 0;
        try {
            CliParser cliParser = new CliParser(args);
            minAvailabilityLevel = cliParser.getMinAvailabilityLevel();
            maxResponseTime = cliParser.getMaxResponseTime();
        } catch (Exception e) {
            log.error("Error during cli arguments parsing: ", e);
            System.exit(1);
        }

        AnalysisResultHandler analysisResultHandler = new AnalysisResultHandler();
        LogAnalyser logAnalyser = new LogAnalyser(minAvailabilityLevel, maxResponseTime, analysisResultHandler);
        logAnalyser.analyze(System.in);
    }

}
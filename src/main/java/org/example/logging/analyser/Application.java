package org.example.logging.analyser;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;

@Slf4j
public class Application {
    public static void main(String[] args) {
        double minAvailabilityLevel = 0;
        double maxResponseTime = 0;
        try {
            CommandLine commandLine = getCommandLine(args);
            minAvailabilityLevel = Double.parseDouble(commandLine.getOptionValue("minAvailabilityLevel"));
            maxResponseTime = Double.parseDouble(commandLine.getOptionValue("maxResponseTime"));
        } catch (ParseException e) {
            log.error("Error during CLI arguments parsing: ", e);
            System.exit(1);
        }

        AnalysisResultHandler analysisResultHandler = new AnalysisResultHandler();

        LogAnalyser logAnalyser = new LogAnalyser(minAvailabilityLevel, maxResponseTime, analysisResultHandler);
        logAnalyser.analyze(System.in);
    }

    private static CommandLine getCommandLine(String[] args) throws ParseException {
        Options options = new Options();
        Option minAvailabilityLevelOption = Option.builder("a")
                .longOpt("minAvailabilityLevel")
                .argName("minAvailabilityLevel")
                .desc("Minimal Service Availability Level")
                .hasArg()
                .required(true)
                .build();
        Option maxResponseTimeOption = Option.builder("t")
                .longOpt("maxResponseTime")
                .argName("maxResponseTime")
                .desc("Max Response Time")
                .hasArg()
                .required(true)
                .build();
        options.addOption(minAvailabilityLevelOption)
                .addOption(maxResponseTimeOption);

        CommandLineParser commandLineParser = new DefaultParser();
        return commandLineParser.parse(options, args);
    }
}
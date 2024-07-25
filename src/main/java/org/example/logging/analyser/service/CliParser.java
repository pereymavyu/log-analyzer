package org.example.logging.analyser.service;

import org.apache.commons.cli.*;

public class CliParser {
    private static final String MIN_AVAILABILITY_LEVEL_CLI_OPTION = "minAvailabilityLevel";
    private static final String MAX_RESPONSE_TIME_CLI_OPTION = "maxResponseTime";

    private final CommandLine commandLine;

    public CliParser(String[] args) throws ParseException {
        commandLine = getCommandLine(args);
    }

    public double getMinAvailabilityLevel() {
        double minAvailabilityLevel = Double.parseDouble(commandLine.getOptionValue(MIN_AVAILABILITY_LEVEL_CLI_OPTION));
        if (minAvailabilityLevel < 0 || minAvailabilityLevel > 100) {
            throw new IllegalArgumentException("minAvailabilityLevel must be non-negative and not above 100");
        }
        return minAvailabilityLevel;
    }

    public double getMaxResponseTime() {
        double maxResonseTime = Double.parseDouble(commandLine.getOptionValue(MAX_RESPONSE_TIME_CLI_OPTION));
        if (maxResonseTime <= 0) {
            throw new IllegalArgumentException("maxResonseTime must be positive");
        }
        return maxResonseTime;
    }

    private CommandLine getCommandLine(String[] args) throws ParseException {
        Options options = new Options();
        Option minAvailabilityLevelOption = Option.builder("a")
                .longOpt(MIN_AVAILABILITY_LEVEL_CLI_OPTION)
                .argName(MIN_AVAILABILITY_LEVEL_CLI_OPTION)
                .desc("Minimal Required Service Availability Level")
                .hasArg()
                .required(true)
                .build();
        Option maxResponseTimeOption = Option.builder("t")
                .longOpt(MAX_RESPONSE_TIME_CLI_OPTION)
                .argName(MAX_RESPONSE_TIME_CLI_OPTION)
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

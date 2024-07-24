package org.example.logging.analyser.service;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AnalysisResultHandler {
    private final SimpleDateFormat formatter = new SimpleDateFormat("hh:mm:ss");

    public void logAvailabilityStats(Date start, Date end, double availability) {
        System.out.printf(
                "%s %s %.1f%n",
                formatter.format(start),
                formatter.format(end),
                availability
        );
    }
}

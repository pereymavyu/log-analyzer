package org.example.logging.analyser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class LogAnalyserTest {

    @Mock
    AnalysisResultHandler resultHandler;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy:HH:mm:ss Z");

    @Test
    void analyze_success1() throws IOException, URISyntaxException {
        LogAnalyser sut = new LogAnalyser(99.0, 100, resultHandler);
        URL res = getClass().getClassLoader().getResource("log_analyzer_test_1.log");

        sut.analyze(Files.newInputStream(Paths.get(Objects.requireNonNull(res).toURI())));

        verify(resultHandler).logAvailabilityStatistics(
                Date.from(ZonedDateTime.parse("14/06/2017:16:47:01 +1000", formatter).toInstant()),
                Date.from(ZonedDateTime.parse("14/06/2017:16:47:05 +1000", formatter).toInstant()),
                60.0
        );
    }

    @Test
    void analyze_success2() throws IOException, URISyntaxException {
        LogAnalyser sut = new LogAnalyser(50.0, 100, resultHandler);
        URL res = getClass().getClassLoader().getResource("log_analyzer_test_2.log");

        sut.analyze(Files.newInputStream(Paths.get(Objects.requireNonNull(res).toURI())));

        verify(resultHandler).logAvailabilityStatistics(
                Date.from(ZonedDateTime.parse("14/06/2017:16:47:01 +1000", formatter).toInstant()),
                Date.from(ZonedDateTime.parse("14/06/2017:16:47:01 +1000", formatter).toInstant()),
                0.0
        );
        verify(resultHandler).logAvailabilityStatistics(
                Date.from(ZonedDateTime.parse("14/06/2017:16:47:03 +1000", formatter).toInstant()),
                Date.from(ZonedDateTime.parse("14/06/2017:16:47:03 +1000", formatter).toInstant()),
                0.0
        );
        verify(resultHandler).logAvailabilityStatistics(
                Date.from(ZonedDateTime.parse("14/06/2017:16:47:05 +1000", formatter).toInstant()),
                Date.from(ZonedDateTime.parse("14/06/2017:16:47:05 +1000", formatter).toInstant()),
                0.0
        );
        verifyNoMoreInteractions(resultHandler);
    }
}
package org.example.logging.analyser;

import org.example.logging.analyser.model.LogRecord;
import org.example.logging.analyser.service.LogParser;
import org.example.logging.analyser.utils.DateTimeUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class LogParserTest {

    @Test
    void parseLine_success() {
        String log = "192.168.32.181 - - [14/06/2017:16:48:51 +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=984500ec HTTP/1.1\" 200 2 21.409405 \"-\" \"@list-item-updater\" prio:0";
        LogRecord actual = LogParser.parseLine(log);
        Assertions.assertEquals(
                DateTimeUtils.parseFromZonedDateTimeString("14/06/2017:16:48:51 +1000"),
                actual.getDateTime()
        );
        Assertions.assertEquals(
                200,
                actual.getHttpCode()
        );
        Assertions.assertEquals(
                21.409405,
                actual.getResponseTime()
        );
    }

    @Test
    void parseLine_error() {
        String log = "192.168.32.181 - - [14/06/2017:WRONGTIMEFORMAT +1000] \"PUT /rest/v1.4/documents?zone=default&_rid=984500ec HTTP/1.1\" 200 2 21.409405 \"-\" \"@list-item-updater\" prio:0";
        RuntimeException actual = Assertions.assertThrows(RuntimeException.class, () -> LogParser.parseLine(log));
        Assertions.assertTrue(actual.getMessage().startsWith("Error during parsing record"));
    }
}
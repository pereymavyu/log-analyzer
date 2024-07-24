package org.example.logging.analyser.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class LogRecord {
    private final Date dateTime;
    private final int HttpCode;
    private final double responseTime;
    @Setter
    private Boolean isSuccessful;
}

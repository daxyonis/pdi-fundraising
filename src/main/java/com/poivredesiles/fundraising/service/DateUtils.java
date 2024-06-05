package com.poivredesiles.fundraising.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Component
public class DateUtils {

    @Value("${application.timezone}")
    private String timezone;
    private ZoneId zoneId;
    private ZoneOffset zoneOffset;

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    @PostConstruct
    public void init() {
        // Define the timezone
        zoneId = ZoneId.of(timezone);

        // Get the current offset for the timezone
        zoneOffset = zoneId.getRules().getOffset(Instant.now());
    }

    public Instant convertToInstant(LocalDate localDate) {
        return localDate.atStartOfDay().toInstant(zoneOffset);
    }

    public String today() {
        return LocalDate.now(zoneId).format(java.time.format.DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public String getDateFormat() {
        return DATE_FORMAT;
    }
}

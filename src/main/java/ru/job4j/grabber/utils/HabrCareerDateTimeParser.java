package ru.job4j.grabber.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerDateTimeParser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String value) {
        return LocalDateTime.parse(value, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }
}

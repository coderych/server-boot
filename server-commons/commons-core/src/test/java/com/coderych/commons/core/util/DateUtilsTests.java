package com.coderych.commons.core.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTests {

    @Test
    void formatLocalDateTimeShouldReturnNullForNull() {
        assertNull(DateUtils.format((LocalDateTime) null, "yyyy-MM-dd"));
    }

    @Test
    void formatLocalDateTimeShouldFormatCorrectly() {
        LocalDateTime ldt = LocalDateTime.of(2025, 3, 15, 10, 30, 0);
        assertEquals("2025-03-15", DateUtils.format(ldt, "yyyy-MM-dd"));
    }

    @Test
    void formatLocalDateShouldReturnNullForNull() {
        assertNull(DateUtils.format((LocalDate) null, "yyyy-MM-dd"));
    }

    @Test
    void formatLocalDateShouldFormatCorrectly() {
        LocalDate ld = LocalDate.of(2025, 3, 15);
        assertEquals("2025-03-15", DateUtils.format(ld, "yyyy-MM-dd"));
    }

    @Test
    void parseDateTimeShouldReturnNullForBlank() {
        assertNull(DateUtils.parseDateTime(null, "yyyy-MM-dd HH:mm:ss"));
        assertNull(DateUtils.parseDateTime("", "yyyy-MM-dd HH:mm:ss"));
        assertNull(DateUtils.parseDateTime("   ", "yyyy-MM-dd HH:mm:ss"));
    }

    @Test
    void parseDateTimeShouldParseCorrectly() {
        LocalDateTime result = DateUtils.parseDateTime("2025-03-15 10:30:00", "yyyy-MM-dd HH:mm:ss");
        assertEquals(LocalDateTime.of(2025, 3, 15, 10, 30, 0), result);
    }

    @Test
    void toLocalDateTimeShouldReturnNullForNull() {
        assertNull(DateUtils.toLocalDateTime(null, ZoneId.systemDefault()));
    }

    @Test
    void toLocalDateTimeShouldConvertCorrectly() {
        LocalDateTime expected = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        Date date = Date.from(expected.atZone(ZoneId.systemDefault()).toInstant());
        LocalDateTime result = DateUtils.toLocalDateTime(date, ZoneId.systemDefault());
        assertEquals(expected, result);
    }

    @Test
    void toDateShouldReturnNullForNull() {
        assertNull(DateUtils.toDate((LocalDateTime) null));
    }

    @Test
    void toDateShouldConvertWithSystemZone() {
        LocalDateTime ldt = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
        Date result = DateUtils.toDate(ldt);
        assertNotNull(result);
        assertEquals(ldt, DateUtils.toLocalDateTime(result, ZoneId.systemDefault()));
    }

    @Test
    void toDateWithZoneIdShouldConvertCorrectly() {
        LocalDateTime ldt = LocalDateTime.of(2025, 6, 1, 12, 0, 0);
        ZoneId zone = ZoneId.of("UTC");
        Date result = DateUtils.toDate(ldt, zone);
        assertNotNull(result);
        assertEquals(ldt, DateUtils.toLocalDateTime(result, zone));
    }

    @Test
    void startOfDayShouldReturnNullForNull() {
        assertNull(DateUtils.startOfDay((LocalDate) null));
    }

    @Test
    void startOfDayShouldReturnMidnight() {
        LocalDate ld = LocalDate.of(2025, 3, 15);
        LocalDateTime result = DateUtils.startOfDay(ld);
        assertEquals(LocalDateTime.of(2025, 3, 15, 0, 0, 0), result);
    }

    @Test
    void endOfDayShouldReturnNullForNull() {
        assertNull(DateUtils.endOfDay((LocalDate) null));
    }

    @Test
    void endOfDayShouldReturnEndOfDay() {
        LocalDate ld = LocalDate.of(2025, 3, 15);
        LocalDateTime result = DateUtils.endOfDay(ld);
        assertEquals(LocalDateTime.of(2025, 3, 15, 23, 59, 59, 999999999), result);
    }

    @Test
    void betweenDaysShouldCalculateCorrectly() {
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 10);
        assertEquals(9L, DateUtils.betweenDays(start, end));
    }

    @Test
    void betweenDaysShouldReturnNegativeForReversed() {
        LocalDate start = LocalDate.of(2025, 1, 10);
        LocalDate end = LocalDate.of(2025, 1, 1);
        assertEquals(-9L, DateUtils.betweenDays(start, end));
    }

    @Test
    void epochMilliShouldConvertCorrectly() {
        LocalDateTime ldt = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        long millis = DateUtils.epochMilli(ldt, ZoneId.of("UTC"));
        assertEquals(1735689600000L, millis);
    }

    @Test
    void epochMilliWithSystemZoneShouldWork() {
        LocalDateTime ldt = LocalDateTime.of(2025, 1, 1, 0, 0, 0);
        long millis = DateUtils.epochMilli(ldt);
        assertTrue(millis > 0);
    }

    @Test
    void fromEpochMilliShouldConvertCorrectly() {
        long millis = 1735689600000L;
        LocalDateTime result = DateUtils.fromEpochMilli(millis);
        assertNotNull(result);
    }

    @Test
    void fromEpochMilliAndEpochMilliShouldBeConsistent() {
        LocalDateTime original = LocalDateTime.of(2025, 6, 15, 12, 30, 0);
        long millis = DateUtils.epochMilli(original, ZoneId.of("UTC"));
        LocalDateTime restored = LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(millis), ZoneId.of("UTC"));
        assertEquals(original, restored);
    }
}

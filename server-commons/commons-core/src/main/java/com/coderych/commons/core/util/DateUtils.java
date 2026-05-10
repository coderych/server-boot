package com.coderych.commons.core.util;

import cn.hutool.core.date.DateUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期时间工具类，扩展 Hutool {@link DateUtil}，增加 {@link LocalDateTime} 相关操作。
 * <p>内部缓存 {@link DateTimeFormatter} 实例以避免重复创建。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils extends DateUtil {

    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    public static String format(LocalDateTime value, String pattern) {
        if (value == null) {
            return null;
        }
        return value.format(FORMATTER_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern));
    }

    public static String format(LocalDate value, String pattern) {
        if (value == null) {
            return null;
        }
        return value.format(FORMATTER_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern));
    }

    public static LocalDateTime parseDateTime(CharSequence text, String pattern) {
        if (STR.isBlank(text)) {
            return null;
        }
        return LocalDateTime.parse(text, FORMATTER_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern));
    }

    public static LocalDateTime toLocalDateTime(Date value, ZoneId zoneId) {
        if (value == null) {
            return null;
        }
        return LocalDateTime.ofInstant(value.toInstant(), zoneId);
    }

    public static Date toDate(LocalDateTime value) {
        return toDate(value, ZoneId.systemDefault());
    }

    public static Date toDate(LocalDateTime value, ZoneId zoneId) {
        if (value == null) {
            return null;
        }
        return Date.from(value.atZone(zoneId).toInstant());
    }

    public static LocalDateTime startOfDay(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.atStartOfDay();
    }

    public static LocalDateTime endOfDay(LocalDate value) {
        if (value == null) {
            return null;
        }
        return value.atTime(LocalTime.MAX);
    }

    public static long betweenDays(LocalDate start, LocalDate end) {
        Assert.notNull(start, "start date must not be null");
        Assert.notNull(end, "end date must not be null");
        return ChronoUnit.DAYS.between(start, end);
    }

    public static long epochMilli(LocalDateTime value) {
        return epochMilli(value, ZoneId.systemDefault());
    }

    public static long epochMilli(LocalDateTime value, ZoneId zoneId) {
        Assert.notNull(value, "date time must not be null");
        return value.atZone(zoneId).toInstant().toEpochMilli();
    }

    public static LocalDateTime fromEpochMilli(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
    }
}

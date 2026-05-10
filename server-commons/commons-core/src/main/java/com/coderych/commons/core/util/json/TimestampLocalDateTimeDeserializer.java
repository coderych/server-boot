package com.coderych.commons.core.util.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ValueDeserializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 毫秒时间戳反序列化为 {@link LocalDateTime}。
 *
 * @author YCH
 */
public class TimestampLocalDateTimeDeserializer extends ValueDeserializer<LocalDateTime> {
    public static final TimestampLocalDateTimeDeserializer INSTANCE = new TimestampLocalDateTimeDeserializer();

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws JacksonException {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getValueAsLong()), ZoneId.systemDefault());
    }
}
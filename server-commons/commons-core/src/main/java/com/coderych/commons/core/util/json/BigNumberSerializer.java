package com.coderych.commons.core.util.json;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.jdk.NumberSerializer;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 大数字 JSON 序列化器，超出 JavaScript 安全整数范围的数值序列化为字符串，
 * 避免前端精度丢失。
 *
 * @author YCH
 */
public class BigNumberSerializer extends NumberSerializer {
    public static final BigNumberSerializer INSTANCE = new BigNumberSerializer(Number.class);

    /**
     * JavaScript 可安全表示的最大整数。
     */
    private static final long MAX_SAFE_INTEGER = 9007199254740991L;

    /**
     * JavaScript 可安全表示的最小整数。
     */
    private static final long MIN_SAFE_INTEGER = -9007199254740991L;

    public BigNumberSerializer(Class<? extends Number> rawType) {
        super(rawType);
    }

    @Override
    public void serialize(Number value, JsonGenerator g, SerializationContext provider) throws JacksonException {
        if (isOutOfRange(value)) {
            g.writeString(value.toString());
        } else {
            super.serialize(value, g, provider);
        }
    }

    private boolean isOutOfRange(Number value) {
        if (value instanceof BigDecimal bd) {
            return bd.compareTo(BigDecimal.valueOf(MAX_SAFE_INTEGER)) > 0
                    || bd.compareTo(BigDecimal.valueOf(MIN_SAFE_INTEGER)) < 0;
        }
        if (value instanceof BigInteger bi) {
            return bi.compareTo(BigInteger.valueOf(MAX_SAFE_INTEGER)) > 0
                    || bi.compareTo(BigInteger.valueOf(MIN_SAFE_INTEGER)) < 0;
        }
        return value.longValue() > MAX_SAFE_INTEGER || value.longValue() < MIN_SAFE_INTEGER;
    }
}

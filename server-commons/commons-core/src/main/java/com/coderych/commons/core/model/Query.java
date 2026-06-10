package com.coderych.commons.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 通用查询参数基类，支持排序解析。
 * <p>orderBy 格式：{@code field1 asc, field2 desc}，默认升序。</p>
 *
 * @author YCH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class Query implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Pattern SAFE_FIELD_PATTERN = Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");

    private String orderBy;

    public static Query of() {
        return new Query();
    }

    public List<Pair<String, Boolean>> parseOrderBy() {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<Pair<String, Boolean>> result = new ArrayList<>();
        String[] orderFields = orderBy.split(",");

        for (String fieldOrder : orderFields) {
            if (fieldOrder == null || fieldOrder.trim().isEmpty()) {
                continue;
            }

            String[] parts = fieldOrder.trim().split("\\s+");
            String field = parts[0].trim();

            if (field.isEmpty() || !SAFE_FIELD_PATTERN.matcher(field).matches()) {
                continue;
            }

            boolean isAsc = true;
            if (parts.length >= 2) {
                isAsc = !"desc".equalsIgnoreCase(parts[1].trim());
            }

            result.add(Pair.of(field, isAsc));
        }

        return result;
    }
}

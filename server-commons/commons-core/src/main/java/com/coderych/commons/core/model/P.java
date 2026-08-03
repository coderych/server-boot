package com.coderych.commons.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 分页结果封装。
 *
 * @param <T> 记录类型
 * @author YCH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class P<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码。
     */
    private Long current;

    /**
     * 每页记录数。
     */
    private Long size;

    /**
     * 总页数。
     */
    private Long pages;

    /**
     * 总记录数。
     */
    private Long total;

    /**
     * 当前页数据。
     */
    private List<T> records;

    public static <T> P<T> of() {
        return new P<>(0L, 10L, 0L, 0L, List.of());
    }

    public static <T> P<T> of(List<T> records) {
        return new P<>(1L, 10L, 1L, (long) records.size(), records);
    }

    public static <T> P<T> of(Long current, Long size, Long total, List<T> records) {
        long pages = (total + size - 1) / size;
        return new P<>(current, size, pages, total, records);
    }

    public boolean hasRecords() {
        return records != null && !records.isEmpty();
    }

    public boolean isFirstPage() {
        return current == null || current <= 1;
    }

    public boolean isLastPage() {
        return pages != null && current != null && current >= pages;
    }
}

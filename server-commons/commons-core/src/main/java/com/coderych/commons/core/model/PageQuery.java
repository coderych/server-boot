package com.coderych.commons.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 分页查询参数，继承 {@link Query} 的排序能力。
 *
 * @author YCH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public class PageQuery extends Query {
    @Serial
    private static final long serialVersionUID = 1L;

    private Long current = 1L;

    private Long size = 10L;

    public static PageQuery of() {
        return new PageQuery();
    }

    public long getOffset() {
        return (current - 1) * size;
    }
}

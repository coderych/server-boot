package com.coderych.commons.mybatisflex.util;

import com.coderych.commons.core.model.P;
import com.coderych.commons.core.model.PageQuery;
import com.mybatisflex.core.paginate.Page;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * MyBatis-Flex 分页对象与通用分页模型 {@link P} 之间的转换工具类。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Of {
    public static <T> P<T> p(Page<T> page) {
        return P.of(page.getPageNumber(), page.getPageSize(), page.getTotalRow(), page.getRecords());
    }

    public static <T> Page<T> page(PageQuery pageQuery) {
        return new Page<>(pageQuery.current(), pageQuery.size());
    }
}

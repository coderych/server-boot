package com.coderych.commons.web.util;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

/**
 * XSS 内容清理工具，基于 Jsoup 的 {@link Safelist#none()} 策略移除所有 HTML 标签。
 *
 * @author YCH
 */
public class XssCleaner {

    /**
     * 清理字符串中的 XSS 内容，{@code null} 输入直接返回 {@code null}。
     */
    public static String clean(String value) {
        if (value == null) {
            return null;
        }
        return Jsoup.clean(value, Safelist.none());
    }
}

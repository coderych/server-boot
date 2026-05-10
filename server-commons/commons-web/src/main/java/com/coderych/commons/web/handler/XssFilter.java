package com.coderych.commons.web.handler;

import com.coderych.commons.web.autoconfigure.WebProperties;
import com.coderych.commons.web.util.XssCleaner;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * XSS 防护过滤器，对所有请求参数进行 XSS 清理。
 * <p>支持通过 {@code commons.web.xss.excludePaths} 配置排除路径。</p>
 *
 * @author YCH
 */
@RequiredArgsConstructor
public class XssFilter extends OncePerRequestFilter {

    private final WebProperties webProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return webProperties.getXss().getExcludePaths().stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(new XssRequestWrapper(request), response);
    }

    private static class XssRequestWrapper extends HttpServletRequestWrapper {
        private final Map<String, String[]> cleanedParameterMap = new HashMap<>();

        XssRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        private static String[] cleanValues(String[] values) {
            String[] cleaned = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                cleaned[i] = XssCleaner.clean(values[i]);
            }
            return cleaned;
        }

        @Override
        public String getParameter(String name) {
            String[] values = getParameterValues(name);
            return values != null && values.length > 0 ? values[0] : null;
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            return cleanedParameterMap.computeIfAbsent(name, k -> cleanValues(values));
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            super.getParameterMap().forEach(cleanedParameterMap::putIfAbsent);
            return Collections.unmodifiableMap(cleanedParameterMap);
        }

    }
}

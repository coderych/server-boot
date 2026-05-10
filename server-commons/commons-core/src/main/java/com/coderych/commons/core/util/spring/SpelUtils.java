package com.coderych.commons.core.util.spring;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * SpEL 表达式求值工具，将方法参数绑定到表达式上下文后求值。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SpelUtils {

    private static final ExpressionParser PARSER = new SpelExpressionParser();

    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    public static String evaluateToString(String expression, Method method, Object[] args) {
        if (!StringUtils.hasText(expression)) {
            return "";
        }
        EvaluationContext context = new StandardEvaluationContext();
        String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
        if (parameterNames != null) {
            for (int index = 0; index < parameterNames.length; index++) {
                context.setVariable(parameterNames[index], args[index]);
            }
        }
        return PARSER.parseExpression(expression).getValue(context, String.class);
    }
}

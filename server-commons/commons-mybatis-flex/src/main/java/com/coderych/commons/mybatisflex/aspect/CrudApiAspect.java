package com.coderych.commons.mybatisflex.aspect;

import com.coderych.commons.core.exception.BadRequestException;
import com.coderych.commons.mybatisflex.annotation.CrudApi;
import com.coderych.commons.mybatisflex.enums.Api;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * CRUD 接口访问控制切面，拦截 {@link com.coderych.commons.mybatisflex.controller.BaseController} 的所有方法，
 * 根据目标类上的 {@link CrudApi} 注解配置，判断当前接口是否被允许访问。
 * <p>内部使用缓存避免重复解析注解。</p>
 *
 * @author YCH
 */
@Slf4j
@Aspect
public class CrudApiAspect {

    private static final Map<Class<?>, Set<Api>> INCLUDES_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Set<Api>> EXCLUDES_CACHE = new ConcurrentHashMap<>();

    @Around("execution(* com.coderych.commons.mybatisflex.controller.BaseController+.*(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        log.debug(">>>>>>>>> CrudApiAspect —— CRUD 接口访问控制: {}.{}",
                point.getSignature().getDeclaringType().getSimpleName(), point.getSignature().getName());
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Class<?> targetClass = point.getTarget().getClass();

        Api api = resolveApi(method);
        if (api == null) {
            return point.proceed();
        }

        CrudApi crudApi = targetClass.getAnnotation(CrudApi.class);
        if (crudApi == null) {
            return point.proceed();
        }

        Set<Api> includes = INCLUDES_CACHE.computeIfAbsent(targetClass,
                k -> Arrays.stream(crudApi.includes()).collect(Collectors.toUnmodifiableSet()));
        Set<Api> excludes = EXCLUDES_CACHE.computeIfAbsent(targetClass,
                k -> Arrays.stream(crudApi.excludes()).collect(Collectors.toUnmodifiableSet()));

        if (!includes.isEmpty() && !excludes.isEmpty()) {
            throw new BadRequestException("@CrudApi 的 includes 和 excludes 不能同时指定");
        }

        if (!includes.isEmpty() && !includes.contains(api)) {
            throw new BadRequestException("接口 [" + api.name() + "] 未启用");
        }

        if (!excludes.isEmpty() && excludes.contains(api)) {
            throw new BadRequestException("接口 [" + api.name() + "] 已被禁用");
        }

        return point.proceed();
    }

    /**
     * 根据方法名解析对应的 API 类型，无法识别的方法返回 null（不进行拦截）。
     */
    private Api resolveApi(Method method) {
        String methodName = method.getName();
        return switch (methodName) {
            case "page" -> Api.PAGE;
            case "list" -> Api.LIST;
            case "getById" -> Api.GET;
            case "save" -> Api.SAVE;
            case "updateById" -> Api.UPDATE;
            case "removeById" -> Api.REMOVE;
            default -> null;
        };
    }
}

package com.coderych.commons.cache.aspect;

import com.coderych.commons.cache.annotation.Lock;
import com.coderych.commons.cache.util.LockManager;
import com.coderych.commons.core.util.spring.SpelUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * {@link Lock} 注解的切面，拦截标注了 {@code @Lock} 的方法，
 * 通过 SpEL 解析锁 key 后委托 {@link LockManager} 执行加锁逻辑。
 *
 * @author YCH
 */
@Slf4j
@Aspect
public class LockAspect {

    @Around("@annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, Lock lock) {
        log.debug(">>>>>>>>> LockAspect —— 分布式锁拦截: {}.{}",
                joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName());
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String key = SpelUtils.evaluateToString(lock.key(), method, joinPoint.getArgs());
        return LockManager.execute(lock.name(), key, () -> {
            try {
                return joinPoint.proceed();
            } catch (RuntimeException re) {
                throw re;
            } catch (Error e) {
                throw e;
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        }, lock.waitTime(), lock.leaseTime(), lock.timeUnit(), lock.message());
    }
}

package com.coderych.commons.cache.util;

import com.coderych.commons.cache.autoconfigure.CacheProperties;
import com.coderych.commons.cache.support.CacheKeyBuilder;
import com.coderych.commons.core.exception.InternalException;
import com.coderych.commons.core.util.STR;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁管理器，基于 Redisson 实现。
 * <p>提供 {@link #execute} / {@link #run} 的回调式用法和 {@link #lock} 的手动式用法，
 * 参数为 -1 时自动使用全局默认配置。</p>
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LockManager {

    private static volatile CacheProperties cacheProperties;

    private static volatile RedissonClient redissonClient;

    public static synchronized void init(CacheProperties properties, RedissonClient redissonClient) {
        if (properties == null || redissonClient == null) {
            throw new IllegalArgumentException("CacheProperties and RedissonClient must not be null");
        }
        LockManager.cacheProperties = properties;
        LockManager.redissonClient = redissonClient;
    }

    public static void run(String name, String key, Runnable action) {
        run(name, key, action, -1L, -1L, null, null);
    }

    public static <T> T execute(String name, String key, Callable<T> action) {
        return execute(name, key, action, -1L, -1L, null, null);
    }

    public static void run(String name, String key, Runnable action, long waitTime, long leaseTime, TimeUnit timeUnit, String message) {
        execute(name, key, () -> {
            action.run();
            return null;
        }, waitTime, leaseTime, timeUnit, message);
    }

    public static <T> T execute(String name, String key, Callable<T> action, long waitTime, long leaseTime, TimeUnit timeUnit, String message) {
        RLock lock = getLock(name, key);
        boolean locked = false;
        try {
            locked = lock.tryLock(resolveWaitTime(waitTime), resolveLeaseTime(leaseTime), resolveTimeUnit(timeUnit));
            if (!locked) {
                throw new InternalException(resolveMessage(message));
            }
            return action.call();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new InternalException(resolveMessage(message), exception);
        } catch (InternalException exception) {
            throw exception;
        } catch (Exception exception) {
            throw new InternalException("执行受保护操作失败", exception);
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public static HeldLock lock(String name, String key) {
        return lock(name, key, -1L, -1L, null, null);
    }

    public static HeldLock lock(String name, String key, long waitTime, long leaseTime, TimeUnit timeUnit, String message) {
        RLock lock = getLock(name, key);
        try {
            boolean locked = lock.tryLock(resolveWaitTime(waitTime), resolveLeaseTime(leaseTime), resolveTimeUnit(timeUnit));
            if (!locked) {
                throw new InternalException(resolveMessage(message));
            }
            return new HeldLock(lock, name, key);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new InternalException(resolveMessage(message), exception);
        }
    }

    public static boolean tryLock(String name, String key) {
        return tryLock(name, key, 0L, -1L, null);
    }

    public static boolean tryLock(String name, String key, long waitTime, long leaseTime, TimeUnit timeUnit) {
        try {
            return getLock(name, key).tryLock(resolveWaitTime(waitTime), resolveLeaseTime(leaseTime), resolveTimeUnit(timeUnit));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new InternalException("获取锁失败", exception);
        }
    }

    public static void unlock(String name, String key) {
        RLock lock = getLock(name, key);
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }

    private static RLock getLock(String name, String key) {
        return redissonClient.getLock(CacheKeyBuilder.buildLockKey(name, key));
    }

    private static TimeUnit resolveTimeUnit(TimeUnit timeUnit) {
        return timeUnit == null ? cacheProperties.getLock().getDefaultTimeUnit() : timeUnit;
    }

    private static long resolveWaitTime(long waitTime) {
        return waitTime < 0 ? cacheProperties.getLock().getDefaultWaitTime() : waitTime;
    }

    private static long resolveLeaseTime(long leaseTime) {
        return leaseTime < 0 ? cacheProperties.getLock().getDefaultLeaseTime() : leaseTime;
    }

    private static String resolveMessage(String message) {
        return STR.isNotBlank(message) ? message : cacheProperties.getLock().getDefaultMessage();
    }

    /**
     * 持有的锁实例，实现 {@link AutoCloseable} 以支持 try-with-resources 自动释放。
     */
    @Getter
    public static class HeldLock implements AutoCloseable {

        private final RLock lock;

        private final String name;

        private final String key;

        public HeldLock(RLock lock, String name, String key) {
            this.lock = lock;
            this.name = name;
            this.key = key;
        }

        public void unlock() {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        @Override
        public void close() {
            unlock();
        }
    }
}

package com.coderych.commons.core.util.thread;

import com.coderych.commons.core.exception.InternalException;
import com.coderych.commons.core.util.STR;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * 线程工具类，提供安全的 sleep/join 和虚拟线程创建。
 *
 * @author YCH
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadUtils {

    public static void sleep(Duration duration) {
        Assert.notNull(duration, "duration must not be null");
        sleepMillis(duration.toMillis());
    }

    public static void sleepMillis(long millis) {
        Assert.isTrue(millis >= 0, "millis must not be negative");
        try {
            Thread.sleep(millis);
        } catch (InterruptedException exception) {
            interruptIfNecessary();
            throw new InternalException("Thread was interrupted while sleeping", exception);
        }
    }

    public static void join(Thread thread) {
        Assert.notNull(thread, "thread must not be null");
        try {
            thread.join();
        } catch (InterruptedException exception) {
            interruptIfNecessary();
            throw new InternalException("Thread was interrupted while joining", exception);
        }
    }

    public static void interruptIfNecessary() {
        if (!Thread.currentThread().isInterrupted()) {
            Thread.currentThread().interrupt();
        }
    }

    public static Thread newVirtualThread(String name, Runnable task) {
        Assert.notNull(task, "task must not be null");
        Thread.Builder builder = Thread.ofVirtual();
        if (STR.isNotBlank(name)) {
            builder = builder.name(name);
        }
        return builder.unstarted(task);
    }

    public static Thread startVirtualThread(String name, Runnable task) {
        Thread thread = newVirtualThread(name, task);
        thread.start();
        return thread;
    }
}

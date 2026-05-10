package com.coderych.commons.cache.init;

import com.coderych.commons.cache.autoconfigure.CacheProperties;
import com.coderych.commons.core.exception.InternalException;
import com.coderych.commons.core.util.spring.SpringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 启动时自动执行所有 {@link CacheInitializer} 的 CommandLineRunner。
 * <p>支持串行和并行（虚拟线程）两种执行模式，执行顺序由 {@link CacheInitializer#getOrder()} 决定。</p>
 *
 * @author YCH
 */
@RequiredArgsConstructor
public class CacheInitializerRunner implements CommandLineRunner {

    private final CacheProperties properties;

    @Override
    public void run(String... args) {
        if (!properties.getInit().isAutoRun()) {
            return;
        }
        List<CacheInitializer> initializers = SpringUtils.getBeansOfType(CacheInitializer.class)
                .values()
                .stream()
                .sorted(Comparator.comparingInt(CacheInitializer::getOrder))
                .toList();
        if (properties.getInit().isParallel()) {
            runParallel(initializers);
            return;
        }
        initializers.stream()
                .filter(CacheInitializer::isAutoRun)
                .forEach(this::runInitializer);
    }

    private void runParallel(List<CacheInitializer> initializers) {
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Callable<Void>> tasks = initializers.stream()
                    .filter(CacheInitializer::isAutoRun)
                    .map(initializer -> (Callable<Void>) () -> {
                        runInitializer(initializer);
                        return null;
                    })
                    .toList();
            executorService.invokeAll(tasks).forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new InternalException("Cache initializer execution was interrupted", exception);
                } catch (ExecutionException exception) {
                    throw new InternalException("Failed to execute cache initializers", exception.getCause());
                }
            });
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new InternalException("Cache initializer execution was interrupted", exception);
        }
    }

    private void runInitializer(CacheInitializer initializer) {
        try {
            initializer.init();
        } catch (RuntimeException exception) {
            boolean failOnError = initializer.isFailOnError() != null
                    ? initializer.isFailOnError()
                    : properties.getInit().isDefaultFailOnError();
            if (failOnError) {
                throw exception;
            }
        }
    }
}

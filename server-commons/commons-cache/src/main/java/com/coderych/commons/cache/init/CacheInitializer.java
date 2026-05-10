package com.coderych.commons.cache.init;

/**
 * 缓存初始化器抽象基类。
 * <p>应用启动时由 {@link CacheInitializerRunner} 自动调用，子类实现具体的数据加载逻辑。</p>
 *
 * @author YCH
 */
public abstract class CacheInitializer {

    public abstract void init();

    public abstract <T> T get();

    public void reload() {
        clear();
        init();
    }

    public abstract void clear();

    public String getName() {
        return getClass().getSimpleName();
    }

    public int getOrder() {
        return 0;
    }

    public boolean isAutoRun() {
        return true;
    }

    public Boolean isFailOnError() {
        return null;
    }
}

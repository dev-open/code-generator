package com.lee.code.gen.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class LocalCacheUtil {

    private LocalCacheUtil() {}

    /** 不过期 */
    public static final long NOT_EXPIRE = Long.MAX_VALUE;

    /** Caffeine 缓存 */
    private static final Cache<String, CacheObject<?>> CAFFEINE = Caffeine.newBuilder()
            .expireAfter(new Expiry<String, CacheObject<?>>() {
                @Override
                public long expireAfterCreate(String key, CacheObject<?> value, long currentTime) {
                    return value.expire;
                }

                @Override
                public long expireAfterUpdate(String key, CacheObject<?> value, long currentTime, @NonNegative long currentDuration) {
                    return value.expire;
                }

                @Override
                public long expireAfterRead(String key, CacheObject<?> value, long currentTime, @NonNegative long currentDuration) {
                    return value.expire;
                }
            })
            .initialCapacity(100)
            .evictionListener((key, value, cause) -> {
                log.info("Cache {} has expired", key);
                if (value != null && value.evictionListener != null) {
                    value.evictionListener.accept(value);
                }
            })
            .maximumSize(1024)
            .build();

    /** 缓存对象 */
    @Data
    public static class CacheObject<T> {
        private T data;
        private long expire;
        private Consumer<CacheObject<?>> evictionListener;

        public CacheObject(T data, long second) {
            this.data = data;
            this.expire = TimeUnit.SECONDS.toNanos(second);
        }

        public CacheObject(T data, long second, Consumer<CacheObject<?>> func) {
            this.data = data;
            this.expire = TimeUnit.SECONDS.toNanos(second);
            this.evictionListener = func;
        }
    }

    /**
     * 放入缓存（含过期时间）
     *
     * @param key 键
     * @param value 值
     * @param expire 过期时间（S）
     * @param <T> T
     */
    public static <T> void put(String key, T value, long expire) {
        CacheObject<T> cacheObject = new CacheObject<>(value, expire);
        CAFFEINE.put(key, cacheObject);
    }

    /**
     * 放入缓存（不过期）
     *
     * @param key 键
     * @param value 值
     * @param <T> T
     */
    public static <T> void put(String key, T value) {
        CacheObject<T> cacheObject = new CacheObject<>(value, NOT_EXPIRE);
        CAFFEINE.put(key, cacheObject);
    }

    /**
     * 如果不存在，放入缓存（含过期时间）
     *
     * @param key 键
     * @param value 值
     * @param expire 过期时间（S）
     * @param <T> T
     */
    public static <T> void putIfAbsent(String key, T value, long expire) {
        var ret =  CAFFEINE.getIfPresent(key);
        if (ret == null) {
            CacheObject<T> cacheObject = new CacheObject<>(value, expire);
            CAFFEINE.put(key, cacheObject);
        }
    }

    /**
     * 如果不存在，放入缓存（含过期时间）
     *
     * @param key 键
     * @param value 值
     * @param expire 过期时间（S）
     * @param evictionListener 过期回调
     * @param <T> T
     */
    public static <T> void putIfAbsent(String key, T value, long expire, Consumer<CacheObject<?>> evictionListener) {
        var ret =  CAFFEINE.getIfPresent(key);
        if (ret == null) {
            CacheObject<T> cacheObject = new CacheObject<>(value, expire, evictionListener);
            CAFFEINE.put(key, cacheObject);
        }
    }

    /**
     * 如果不存在，放入缓存（不过期）
     *
     * @param key 键
     * @param value 值
     * @param <T> T
     */
    public static <T> void putIfAbsent(String key, T value) {
        var ret =  CAFFEINE.getIfPresent(key);
        if (ret == null) {
            CacheObject<T> cacheObject = new CacheObject<>(value, NOT_EXPIRE);
            CAFFEINE.put(key, cacheObject);
        }
    }

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     * @param <T> T
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        CacheObject<?> cacheObject = CAFFEINE.getIfPresent(key);
        if (cacheObject == null) {
            return null;
        }
        return (T) cacheObject.data;
    }

    /**
     * 移除缓存
     *
     * @param key 键
     */
    public static void remove(String key) {
        CAFFEINE.invalidate(key);
    }

    /**
     * 移除缓存
     *
     * @param keys 键
     */
    public static void remove(Collection<String> keys) {
        CAFFEINE.invalidateAll(keys);
    }

    /**
     * 不存在则加入缓存，并返回缓存
     *
     * @param key 键
     * @param expire 过期时间（S）
     * @param supplier 值
     * @return 值
     * @param <T> T
     */
    public static <T> T computeIfAbsent(String key, long expire, Supplier<T> supplier) {
        putIfAbsent(key, supplier.get(), expire);
        return get(key);
    }

    /**
     * 不存在则加入缓存，并返回缓存
     *
     * @param key 键
     * @param expire 过期时间（S）
     * @param supplier 值
     * @param evictionListener 过期回调
     * @return 值
     * @param <T> T
     */
    public static <T> T computeIfAbsent(String key, long expire, Supplier<T> supplier, Consumer<CacheObject<?>> evictionListener) {
        putIfAbsent(key, supplier.get(), expire, evictionListener);
        return get(key);
    }
}

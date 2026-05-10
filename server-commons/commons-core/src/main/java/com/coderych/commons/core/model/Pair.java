package com.coderych.commons.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 键值对泛型容器。
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author YCH
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pair<K, V> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private K key;

    private V value;

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }

    public static <K, V> Pair<K, V> empty() {
        return new Pair<>();
    }

    public boolean isEmpty() {
        return key == null && value == null;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public K getLeft() {
        return key;
    }

    public V getRight() {
        return value;
    }

    public K getFirst() {
        return key;
    }

    public V getSecond() {
        return value;
    }
}

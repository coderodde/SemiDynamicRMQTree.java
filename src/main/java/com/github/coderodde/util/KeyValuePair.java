package com.github.coderodde.util;

import java.util.Objects;

public final class KeyValuePair<K extends Comparable<? super K>, V>
        
implements Comparable<KeyValuePair<K, V>> {
    
    private final K key;
    private final V value;
    
    public KeyValuePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public int compareTo(KeyValuePair<K, V> o) {
        return key.compareTo(o.key);
    }
    
    @Override
    public String toString() {
        return String.format("[KeyValuePair: key = \"%s\", value = \"%s\"]",
                             Objects.toString(key),
                             Objects.toString(value));
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}

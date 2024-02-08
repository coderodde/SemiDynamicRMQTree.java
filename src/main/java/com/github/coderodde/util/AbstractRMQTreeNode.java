package com.github.coderodde.util;

public abstract class AbstractRMQTreeNode<V extends Comparable<? super V>> {
    
    protected V value;
    protected AbstractRMQTreeNode<V> parent;

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public AbstractRMQTreeNode<V> getParent() {
        return parent;
    }

    public void setParent(AbstractRMQTreeNode<V> parent) {
        this.parent = parent;
    }
}

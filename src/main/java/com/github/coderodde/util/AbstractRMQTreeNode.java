package com.github.coderodde.util;

abstract class AbstractRMQTreeNode<V> {
    
    private V value;
    private AbstractRMQTreeNode<V> parent;

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

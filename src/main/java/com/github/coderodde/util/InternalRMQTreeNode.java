package com.github.coderodde.util;

import java.util.Objects;

public final class InternalRMQTreeNode<V extends Comparable<? super V>>
extends AbstractRMQTreeNode<V> {
    
    private AbstractRMQTreeNode<V> leftChild;
    private AbstractRMQTreeNode<V> rightChild;

    public AbstractRMQTreeNode<V> getLeftChild() {
        return leftChild;
    }

    public AbstractRMQTreeNode<V> getRightChild() {
        return rightChild;
    }

    public void setLeftChild(AbstractRMQTreeNode<V> leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(AbstractRMQTreeNode<V> rightChild) {
        this.rightChild = rightChild;
    }
    
    @Override
    public String toString() {
        return String.format("[INTERNAL: value = \"%s\"]", 
                             Objects.toString(value));
    }
}

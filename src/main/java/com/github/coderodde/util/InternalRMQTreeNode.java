package com.github.coderodde.util;

import java.util.Objects;

final class InternalRMQTreeNode<V> extends AbstractRMQTreeNode<V> {
    
    private AbstractRMQTreeNode<V> leftChild;
    private AbstractRMQTreeNode<V> rightChild;

    AbstractRMQTreeNode<V> getLeftChild() {
        return leftChild;
    }

    AbstractRMQTreeNode<V> getRightChild() {
        return rightChild;
    }

    void setLeftChild(AbstractRMQTreeNode<V> leftChild) {
        this.leftChild = leftChild;
    }

    void setRightChild(AbstractRMQTreeNode<V> rightChild) {
        this.rightChild = rightChild;
    }
    
    @Override
    public String toString() {
        return String.format("[INTERNAL: value = \"%s\"]", 
                             Objects.toString(getValue()));
    }
}

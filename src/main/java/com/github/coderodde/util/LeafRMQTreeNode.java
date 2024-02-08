package com.github.coderodde.util;

import java.util.Objects;

public final class LeafRMQTreeNode<V extends Comparable<? super V>>
extends AbstractRMQTreeNode<V> {
    
    @Override
    public String toString() {
        return String.format("[LEAF: value = \"%s\"]",
                             Objects.toString(getValue()));
    }
}
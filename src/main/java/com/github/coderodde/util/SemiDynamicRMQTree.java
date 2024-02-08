package com.github.coderodde.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import com.github.coderodde.util.SemiDynamicRMQTreeBuilder.RMQTreeBuilderResult;
import static com.github.coderodde.util.Utils.min;

/**
 * This class implements a semi-dynamic RMQ (range minimum query) tree. While
 * it does not support modification of the tree, it supports update of leaf node
 * values. The operation 
 * {@link #update(java.lang.Comparable, java.lang.Comparable)} runs in exact
 * logarithmic time; so does the 
 * {@link #getRangeMinimum(java.lang.Comparable, java.lang.Comparable) }. 
 * <p>
 * Building the tree takes {@code O(n log n)} time.
 * 
 * @param <K> the key type.
 * @param <V> the value type.
 */
public final class SemiDynamicRMQTree<K extends Comparable<? super K>,
                                      V extends Comparable<? super V>> {
    
    private final AbstractRMQTreeNode<V> root;
    private final Map<K, LeafRMQTreeNode<V>> leafMap;
    
    /**
     * Construct an RMQ tree from the set of key/value pairs 
     * ({@link com.github.coderodde.util.KeyValuePair}). Runs in 
     * {@code O(n log n)} time.
     * 
     * @param keyValuePairSet the set of key/value pairs from which to construct
     *                        the RMQ tree.
     */
    public SemiDynamicRMQTree(Set<KeyValuePair<K, V>> keyValuePairSet) {
        RMQTreeBuilderResult<K, V> result = 
                SemiDynamicRMQTreeBuilder.buildRMQTree(keyValuePairSet);
        
        root = result.getRoot();
        leafMap = result.getLeafMap();
    }
    
    /**
     * Returns the root node of this tree. Is package-private in order to be
     * accessible from the unit tests.
     * 
     * @return the root node of this tree. 
     */
    AbstractRMQTreeNode<V> getRoot() {
        return root;
    }
    
    /**
     * Returns the string representation of this tree.
     * 
     * @return the string representation of this tree.
     */
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        toStringImpl(stringBuilder);
        return stringBuilder.toString();
    }
    
    /**
     * Associates the value {@code newValue} with the key {@code key}. Runs in
     * exact logarithmic time.
     * 
     * @param key      the target key.
     * @param newValue the new value for the target key.
     */
    public void update(K key, V newValue) {
        AbstractRMQTreeNode<V> node = leafMap.get(key);
        
        while (node != null) {
            node.setValue(min(node.getValue(), newValue));
            node = node.getParent();
        }
    }
    
    /**
     * Given the range {@code R = [leftKey ... rightKey]}, return the minimum
     * value in {@code R}. Runs in exact logarithmic time.
     * 
     * @param leftKey  the leftmost key of the range.
     * @param rightKey the rightmost key of the range.
     * @return the minimum value in {@code R}.
     */
    public V getRangeMinimum(K leftKey, K rightKey) {
        
        AbstractRMQTreeNode<V> leftLeaf  = leafMap.get(leftKey);
        
        Objects.requireNonNull(
                leftLeaf,
                String.format(
                        "The left key [%s] is not in this tree.",
                        leftKey));
        
        AbstractRMQTreeNode<V> rightLeaf = leafMap.get(rightKey);
        
        Objects.requireNonNull(
                rightLeaf,
                String.format(
                        "The right key [%s] is not in this tree.",
                        rightKey));
        
        if (leftKey.compareTo(rightKey) > 0) {
            String exceptionMessage = 
                    String.format(
                            "The specified range [%s, %s] is descending.", 
                            leftKey, 
                            rightKey);
            
            throw new IllegalArgumentException(exceptionMessage);
        }
        
        AbstractRMQTreeNode<V> splitNode =
                computeSplitNode(leftLeaf,
                                 rightLeaf);
        
        List<AbstractRMQTreeNode<V>> leftPath  = getPath(splitNode, leftLeaf);
        List<AbstractRMQTreeNode<V>> rightPath = getPath(splitNode, rightLeaf);
        
        List<AbstractRMQTreeNode<V>> leftPathV  = computeLeftPathV(leftPath);
        List<AbstractRMQTreeNode<V>> rightPathV = computeRightPartV(rightPath);
        
        V vl = computeMinimum(leftPathV);
        V vr = computeMinimum(rightPathV);
        
        if (vl == null) {
            vl = leftLeaf.getValue();
        }
        
        if (vr == null) {
            vr = rightLeaf.getValue();
        }
        
        vl = min(vl, leftLeaf.getValue());
        vr = min(vr, rightLeaf.getValue());
        
        return min(vl, vr);
    }
    
    /**
     * Computes the minimum value in {@code nodes}.
     * 
     * @param <V>   the value type.
     * @param nodes the list of values.
     * @return the minimum value or {@code null} if the input list is empty.
     */
    private <V extends Comparable<? super V>>   
        V computeMinimum(List<AbstractRMQTreeNode<V>> nodes) {
        
        if (nodes.isEmpty()) {
            return null;
        }
            
        V minValue = nodes.get(0).getValue();
        
        for (int i = 1; i < nodes.size(); i++) {
            AbstractRMQTreeNode<V> node = nodes.get(i);
            
            minValue = min(minValue, node.getValue());
        }
        
        return minValue;
    }
    
    /**
     * Computes the so called {@code V'}.
     * 
     * @param path the target path.
     * @return the {@code V'} set.
     */
    private List<AbstractRMQTreeNode<V>> 
        computeLeftPathV(List<AbstractRMQTreeNode<V>> path) {
            
        Set<AbstractRMQTreeNode<V>> pathSet   = new HashSet<>(path);
        List<AbstractRMQTreeNode<V>> nodeList = new ArrayList<>();
        
        for (AbstractRMQTreeNode<V> node : path) {
            InternalRMQTreeNode<V> parent = (InternalRMQTreeNode<V>) node;
            AbstractRMQTreeNode<V> leftChild  = parent.getLeftChild();
            AbstractRMQTreeNode<V> rightChild = parent.getRightChild();
            
            if (pathSet.contains(leftChild)) {
                nodeList.add(rightChild);
            }
        }
        
        return nodeList;
    }
        
    /**
     * Computes the so called {@code V''}.
     * 
     * @param path the target path.
     * @return the {@code V''} set.
     */
    private List<AbstractRMQTreeNode<V>>
        computeRightPartV(List<AbstractRMQTreeNode<V>> path) {
        Set<AbstractRMQTreeNode<V>> pathSet   = new HashSet<>(path);
        List<AbstractRMQTreeNode<V>> nodeList = new ArrayList<>();
        
        for (AbstractRMQTreeNode<V> node : path) {
            InternalRMQTreeNode<V> parent = 
                    (InternalRMQTreeNode<V>) node;
            
            AbstractRMQTreeNode<V> leftChild  = parent.getLeftChild();
            AbstractRMQTreeNode<V> rightChild = parent.getRightChild();
            
            if (pathSet.contains(rightChild)) {
                nodeList.add(leftChild);
            }
        }
        
        return nodeList;
    }
    
    /**
     * Gets the path from {@code splitNode} to the {@code leaf}. The returned
     * path will, however, exclude {@code splitNode} and {@code leaf}.
     *
     * @param splitNode the starting path node.
     * @param leafNode  the target node.
     * @return the path from {@code splitNode} to {@code leaf};
     */
    private List<AbstractRMQTreeNode<V>> 
        getPath(AbstractRMQTreeNode<V> splitNode, 
                AbstractRMQTreeNode<V> leafNode) {
        
        List<AbstractRMQTreeNode<V>> path = new ArrayList<>();
        
        AbstractRMQTreeNode<V> node = leafNode.getParent();
        
        while (node != null && !node.equals(splitNode)) {
            path.add(node);
            node = node.getParent();
        }
        
        return path;
    }
    
    /**
     * Computes the node at which the path from the root splits towards 
     * {@code leftLeaf} and {@code rightLeaf}.
     * 
     * @param leftLeaf  the left leaf.
     * @param rightLeaf the right leaf.
     * @return the split node.
     */
    private AbstractRMQTreeNode<V> 
        computeSplitNode(AbstractRMQTreeNode<V> leftLeaf,
                         AbstractRMQTreeNode<V> rightLeaf) {
            
        Set<AbstractRMQTreeNode<V>> leftPathSet = new HashSet<>();
        
        AbstractRMQTreeNode<V> node = leftLeaf;
        
        while (node != null) {
            leftPathSet.add(node);
            node = node.getParent();
        }
        
        node = rightLeaf;
        
        while (node != null) {
            if (leftPathSet.contains(node)) {
                return node;
            }
            
            node = node.getParent();
        }
        
        throw new IllegalStateException("Should not get here.");
    }
        
    /**
     * Implements the actual conversion from the tree to the string.
     * 
     * @param stringBuilder the string builder to which dump the string data.
     */
    private void toStringImpl(StringBuilder stringBuilder) {
        
        Deque<AbstractRMQTreeNode<V>> queue = 
                new ArrayDeque<>();
        
        queue.addLast(root);
        
        AbstractRMQTreeNode<V> levelEnd = root;
        
        while (!queue.isEmpty()) {
            AbstractRMQTreeNode<V> currentNode = queue.removeFirst();
            stringBuilder.append(String.format("%s ", currentNode));
            
            if (currentNode instanceof InternalRMQTreeNode) {
                
                AbstractRMQTreeNode<V> leftChild =
                        ((InternalRMQTreeNode<V>) currentNode)
                                .getLeftChild();
                
                AbstractRMQTreeNode<V> rightChild = 
                        ((InternalRMQTreeNode<V>) currentNode)
                                .getRightChild();
                
                queue.addLast(leftChild);
                queue.addLast(rightChild);
            } 
            
            if (currentNode.equals(levelEnd)) {
                if (!queue.isEmpty()) {
                    levelEnd = queue.getLast();
                }
                    
                stringBuilder.append("\n");
            }
        }
    }
}

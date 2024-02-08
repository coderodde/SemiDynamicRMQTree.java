package com.github.coderodde.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static com.github.coderodde.util.Utils.min;

public final class SemiDynamicRMQTreeBuilder<K extends Comparable<? super K>,
                                             V extends Comparable<? super V>> {

    /**
     * Implements the actual tree building.'
     * 
     * @param <K> the key type.
     * @param <V> the value type.
     * @param keyValuePairSet the set of key/value pairs.
     * @return the tree data.
     */
    static <K extends Comparable<? super K>,
            V extends Comparable<? super V>>
                    
    RMQTreeBuilderResult<K, V> 
        buildRMQTree(Set<KeyValuePair<K, V>> keyValuePairSet) {
        
        Objects.requireNonNull(
                keyValuePairSet,
                "The input KeyValuePair set is null.");
        
        if (keyValuePairSet.isEmpty()) {
            throw new IllegalArgumentException(
                    "No key/value pairs to process.");
        }
        
        List<KeyValuePair<K, V>> keyValuePairList = 
                new ArrayList<>(keyValuePairSet);
        
        Collections.sort(keyValuePairList);
        
        Map<K, LeafRMQTreeNode<V>> mapKeyToLeafNode = new HashMap<>();
        
        AbstractRMQTreeNode<V> root = 
                buildRMQTreeImpl(keyValuePairList, 
                                 mapKeyToLeafNode);
        
        return new RMQTreeBuilderResult<>(mapKeyToLeafNode, root);
    }
 
    /**
     * Implements the actual, recursive building routine.
     * <p>
     * This algorithm seems much like in Task9, yet it differs: this one does 
     * not stored actual keys to the internal nodes, except to the leaf nodes,
     * unlike the algorithm in Task9.java.
     * 
     * @param <K>               the key type.
     * @param <V>               the value type.
     * @param keyValuePairs     the set of key/value pairs.
     * @param mapKeyToLeafNodes the map mapping keys to leaf nodes.
     * @return local root of the tree constructed.
     */
    private static <K extends Comparable<? super K>,
                    V extends Comparable<? super V>>
                    
    AbstractRMQTreeNode<V> 
        buildRMQTreeImpl(List<KeyValuePair<K, V>> keyValuePairs, 
                         Map<K, LeafRMQTreeNode<V>> mapKeyToLeafNodes) {
            
        if (keyValuePairs.size() == 1) {
            KeyValuePair<K, V> keyValuePair = keyValuePairs.get(0);
            LeafRMQTreeNode<V> leaf = new LeafRMQTreeNode<>();
            leaf.setValue(keyValuePair.getValue());
            mapKeyToLeafNodes.put(keyValuePair.getKey(), leaf);
            return leaf;
        }
        
        // middleIndex goes to the right:
        int middleIndex = keyValuePairs.size() / 2;

        AbstractRMQTreeNode<V> leftSubTreeRoot
                = buildRMQTreeImpl(
                        keyValuePairs.subList(0, middleIndex),
                        mapKeyToLeafNodes);

        AbstractRMQTreeNode<V> rightSubTreeRoot
                = buildRMQTreeImpl(
                        keyValuePairs.subList(
                                middleIndex,
                                keyValuePairs.size()),
                        mapKeyToLeafNodes);

        InternalRMQTreeNode<V> localRoot = new InternalRMQTreeNode<>();
        
        // Link the children and their parent:
        localRoot.setLeftChild(leftSubTreeRoot);
        localRoot.setRightChild(rightSubTreeRoot);
        
        leftSubTreeRoot.setParent(localRoot);
        rightSubTreeRoot.setParent(localRoot);
        
        localRoot.setValue(min(leftSubTreeRoot.getValue(), // Important step!
                               rightSubTreeRoot.getValue()));

        return localRoot;
    }
    
    static final 
            class RMQTreeBuilderResult<K extends Comparable<? super K>,
                                       V extends Comparable<? super V>> {
        
        private final Map<K, LeafRMQTreeNode<V>> leafMap;
        private final AbstractRMQTreeNode<V> root;
        
        RMQTreeBuilderResult(Map<K, LeafRMQTreeNode<V>> leafMap,
                             AbstractRMQTreeNode<V> root) {
            this.leafMap = leafMap;
            this.root = root;
        }
        
        Map<K, LeafRMQTreeNode<V>> getLeafMap() {
            return leafMap;
        }
        
        AbstractRMQTreeNode<V> getRoot() {
            return root;
        }
    }
}
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
        
        Map<K, LeafRMQTreeNode<V>> leafMap = new HashMap<>();
        
        loadLeafMap(leafMap, keyValuePairSet);
        
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
        RMQTreeBuilderResult<K, V> result = new RMQTreeBuilderResult();
        AbstractRMQTreeNode<V> root = 
                buildRMQTreeImpl(keyValuePairList, 
                                 mapKeyToLeafNode);
        
        result.setLeafMap(mapKeyToLeafNode);
        result.setRoot(root);
        
        return result;
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
        
    /**
     * Loads the map mapping keys to leaf nodes.
     * '
     * @param <K>             the key type.
     * @param <V>             the value type.
     * @param leafMap         the map to populate.
     * @param keyValuePairSet the key/value pair set.
     */
    private static <K extends Comparable<? super K>,
                    V extends Comparable<? super V>>
                
    void loadLeafMap(Map<K, LeafRMQTreeNode<V>> leafMap, 
                         Set<KeyValuePair<K, V>> keyValuePairSet) {
        
        for (KeyValuePair<K, V> keyValuePair : keyValuePairSet) {
            LeafRMQTreeNode<V> leaf = new LeafRMQTreeNode<>();
            leaf.setValue(keyValuePair.getValue());
            leafMap.put(keyValuePair.getKey(), leaf);
        }
    }
    
    static final 
            class RMQTreeBuilderResult<K extends Comparable<? super K>,
                                       V extends Comparable<? super V>> {
        
        private Map<K, LeafRMQTreeNode<V>> leafMap;
        private AbstractRMQTreeNode<V> root;

        public void setLeafMap(Map<K, LeafRMQTreeNode<V>> leafMap) {
            this.leafMap = leafMap;
        }

        public void setRoot(AbstractRMQTreeNode<V> root) {
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
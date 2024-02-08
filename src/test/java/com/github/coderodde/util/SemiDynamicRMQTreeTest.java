package com.github.coderodde.util;

import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SemiDynamicRMQTreeTest {
    
    @Test
    public void passesOnTreeWith4Nodes() {
        Set<KeyValuePair<Integer, Long>> keyValuePairSet = new HashSet<>(4);
        
        keyValuePairSet.add(new KeyValuePair<>(2, 2L));
        keyValuePairSet.add(new KeyValuePair<>(4, 4L));
        keyValuePairSet.add(new KeyValuePair<>(1, 1L));
        keyValuePairSet.add(new KeyValuePair<>(3, 3L));
        
        SemiDynamicRMQTree<Integer, Long> tree = 
                new SemiDynamicRMQTree<>(keyValuePairSet);
        
        InternalRMQTreeNode<Long> root = 
                (InternalRMQTreeNode<Long>) tree.getRoot();
        
        InternalRMQTreeNode<Long> leftMiddleNode  = 
                (InternalRMQTreeNode<Long>) root.getLeftChild();
        
        InternalRMQTreeNode<Long> rightMiddleNode = 
                (InternalRMQTreeNode<Long>) root.getRightChild();
        
        LeafRMQTreeNode<Long> leaf1 = (LeafRMQTreeNode<Long>) leftMiddleNode.getLeftChild();
        LeafRMQTreeNode<Long> leaf2 = (LeafRMQTreeNode<Long>) leftMiddleNode.getRightChild();
        LeafRMQTreeNode<Long> leaf3 = (LeafRMQTreeNode<Long>) rightMiddleNode.getLeftChild();
        LeafRMQTreeNode<Long> leaf4 = (LeafRMQTreeNode<Long>) rightMiddleNode.getRightChild();
        
        assertEquals(Long.valueOf(1L), root.getValue());
        
        assertEquals(Long.valueOf(1L), leftMiddleNode.getValue());
        
        assertEquals(Long.valueOf(3L), rightMiddleNode.getValue());
        
        assertEquals(Long.valueOf(1L), leaf1.getValue());
        assertEquals(Long.valueOf(2L), leaf2.getValue());
        assertEquals(Long.valueOf(3L), leaf3.getValue());
        assertEquals(Long.valueOf(4L), leaf4.getValue());
        
        assertEquals(Long.valueOf(1L), 
                     tree.getRangeMinimum(Integer.valueOf(1),
                                          Integer.valueOf(2)));
        
        assertEquals(Long.valueOf(3L),
                     tree.getRangeMinimum(Integer.valueOf(3), 
                                          Integer.valueOf(4)));
        
        assertEquals(Long.valueOf(2L),
                     tree.getRangeMinimum(Integer.valueOf(2), 
                                          Integer.valueOf(4)));
        
        assertEquals(Long.valueOf(1L),
                     tree.getRangeMinimum(Integer.valueOf(1), 
                                          Integer.valueOf(4)));
        tree.update(4, -1L);
        
        assertEquals(Long.valueOf(-1L), leaf4.getValue());
        assertEquals(Long.valueOf(-1L), rightMiddleNode.getValue());
        assertEquals(Long.valueOf(-1L), root.getValue());
    }
    
    @Test
    public void passesOnTreeWith3Nodes() {
        Set<KeyValuePair<Integer, Long>> keyValuePairSet = new HashSet<>(4);
        
        keyValuePairSet.add(new KeyValuePair<>(2, 2L));
        keyValuePairSet.add(new KeyValuePair<>(1, 1L));
        keyValuePairSet.add(new KeyValuePair<>(3, 3L));
        
        SemiDynamicRMQTree<Integer, Long> tree = 
                new SemiDynamicRMQTree<>(keyValuePairSet);
        
        InternalRMQTreeNode<Long> root =
                (InternalRMQTreeNode<Long>) tree.getRoot();
        
        assertEquals(Long.valueOf(1L), root.getValue());
        
        InternalRMQTreeNode<Long> middleInternalNode = 
                (InternalRMQTreeNode<Long>) root.getRightChild();
        
        assertEquals(Long.valueOf(2L), middleInternalNode.getValue());
        
        LeafRMQTreeNode<Long> leaf1 = (LeafRMQTreeNode<Long>) root.getLeftChild();
        LeafRMQTreeNode<Long> leaf2 = (LeafRMQTreeNode<Long>) middleInternalNode.getLeftChild();
        LeafRMQTreeNode<Long> leaf3 = (LeafRMQTreeNode<Long>) middleInternalNode.getRightChild();
        
        assertEquals(Long.valueOf(1L), leaf1.getValue());
        assertEquals(Long.valueOf(2L), leaf2.getValue());
        assertEquals(Long.valueOf(3L), leaf3.getValue());
    }
}

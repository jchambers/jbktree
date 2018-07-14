package com.eatthepath.jbktree;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import static org.junit.Assert.*;

public class BKTreeNodeTest {

    private static final DiscreteDistanceFunction<Integer> DIFFERENCE_DISTANCE_FUNCTION =
            (first, second) -> Math.abs(first - second);

    @Test
    public void testAddAndSize() {
        final int i = 17;
        final BKTreeNode<Integer> node = new BKTreeNode<>(i);

        assertEquals(1, node.size());
        assertFalse(node.add(i, DIFFERENCE_DISTANCE_FUNCTION));
        assertEquals(1, node.size());

        assertTrue(node.add(i + 1, DIFFERENCE_DISTANCE_FUNCTION));
        assertEquals(2, node.size());
    }

    @Test
    public void testContains() {
        final int i = 17;
        final BKTreeNode<Integer> node = new BKTreeNode<>(i);

        assertTrue(node.contains(i, DIFFERENCE_DISTANCE_FUNCTION));
        assertFalse(node.contains(i + 1, DIFFERENCE_DISTANCE_FUNCTION));

        node.add(i + 1, DIFFERENCE_DISTANCE_FUNCTION);
        assertTrue(node.contains(i + 1, DIFFERENCE_DISTANCE_FUNCTION));
    }

    @Test
    public void testAddElementsToArray() {
        final Set<Integer> expectedElements = new HashSet<>(Arrays.asList(17, 18, 19, 20, 21));

        final BKTreeNode<Integer> node = new BKTreeNode<>(expectedElements.stream().findFirst().orElseThrow(RuntimeException::new));
        expectedElements.forEach(i -> node.add(i, DIFFERENCE_DISTANCE_FUNCTION));

        final Integer[] array = new Integer[expectedElements.size()];

        assertEquals(expectedElements.size(), node.addElementsToArray(array, 0));
        assertEquals(expectedElements, new HashSet<>(Arrays.asList(array)));
    }

    @Test
    public void getNearestNeighbors() {
        final BKTreeNode<Integer> node = new BKTreeNode<>(1);
        node.add(2, DIFFERENCE_DISTANCE_FUNCTION);
        node.add(3, DIFFERENCE_DISTANCE_FUNCTION);
        node.add(17, DIFFERENCE_DISTANCE_FUNCTION);

        final PriorityQueue<Integer> priorityQueue = new PriorityQueue<>();

        node.getNearestNeighbors(2, 1, priorityQueue, DIFFERENCE_DISTANCE_FUNCTION);

        assertEquals(3, priorityQueue.size());
        assertTrue(priorityQueue.containsAll(Arrays.asList(1, 2, 3)));
    }
}

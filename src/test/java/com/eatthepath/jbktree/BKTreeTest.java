package com.eatthepath.jbktree;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class BKTreeTest {

    private static final DiscreteDistanceFunction<Integer> DIFFERENCE_DISTANCE_FUNCTION =
            (first, second) -> Math.abs(first - second);

    @Test
    public void testBKTreeWithDistanceFunction() {
        // We're happy here as long as nothing explodes
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION);
    }

    @Test(expected = NullPointerException.class)
    public void testBKTreeWithNullDistanceFunction() {
        new BKTree<>(null);
    }

    @Test
    public void testBKTreeWithDistanceFunctionAndCollection() {
        // We're happy here as long as nothing explodes
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, Arrays.asList(1, 2, 3, 4));
    }

    @Test(expected = NullPointerException.class)
    public void testBKTreeWithNullDistanceFunctionAndCollection() {
        new BKTree<>(null, Arrays.asList(1, 2, 3, 4));
    }

    @Test(expected = NullPointerException.class)
    public void testBKTreeWithDistanceFunctionAndNullCollection() {
        new BKTree<>(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testBKTreeWithNullDistanceFunctionAndCollectionWithNullElement() {
        new BKTree<>(null, Arrays.asList(1, 2, 3, 4, null));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).isEmpty());
        assertFalse(new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, Collections.singleton(17)).isEmpty());
    }

    @Test
    public void testSize() {
        assertEquals(0, new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).size());
        assertEquals(1, new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, Collections.singleton(17)).size());
        assertEquals(3, new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, Arrays.asList(17, 18, 19)).size());
    }

    @Test
    public void testAdd() {
        final BKTree<Integer> tree = new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION);

        final int i = 17;

        assertTrue(tree.add(i));
        assertFalse(tree.add(i));
    }

    @Test(expected = NullPointerException.class)
    public void testAddNull() {
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).add(null);
    }

    @Test
    public void testAddAll() {
        final BKTree<Integer> tree = new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION);

        final Set<Integer> values = new HashSet<>(Arrays.asList(17, 18, 19));

        assertTrue(tree.addAll(values));
        assertFalse(tree.addAll(values));

        values.add(20);

        assertTrue(tree.addAll(values));
    }

    @Test(expected = NullPointerException.class)
    public void testAddAllNullCollection() {
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).addAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddAllCollectionWithNullElement() {
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).addAll(Arrays.asList(1, null, 3));
    }

    @Test
    public void testContains() {
        final BKTree<Integer> tree = new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION);

        final int i = 17;

        assertFalse(tree.contains(i));
        assertFalse(tree.contains(i + 1));

        tree.add(i);
        tree.add(i + 1);

        assertTrue(tree.contains(i));
        assertTrue(tree.contains(i + 1));
    }

    @Test(expected = NullPointerException.class)
    public void testContainsNull() {
        //noinspection ResultOfMethodCallIgnored
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).contains(null);
    }

    @Test(expected = ClassCastException.class)
    public void testContainsIncompatibleElement() {
        //noinspection ResultOfMethodCallIgnored,SuspiciousMethodCalls
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, Arrays.asList(17, 18, 19)).contains("This is not an integer");
    }

    @Test
    public void testContainsAll() {
        final Set<Integer> expectedValues = new HashSet<>(Arrays.asList(17, 18, 19));
        final BKTree<Integer> tree = new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, expectedValues);

        assertTrue(tree.containsAll(expectedValues));

        final Set<Integer> expectedValuesWithAdditionalElement = new HashSet<>(expectedValues);
        expectedValuesWithAdditionalElement.add(20);

        assertFalse(tree.containsAll(expectedValuesWithAdditionalElement));
    }

    @Test(expected = NullPointerException.class)
    public void testContainsAllNullCollection() {
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).containsAll(null);
    }

    @Test(expected = NullPointerException.class)
    public void testContainsAllNullElement() {
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).containsAll(Arrays.asList(null, 2, 3));
    }

    @Test(expected = ClassCastException.class)
    public void testContainsAllIncompatibleElement() {
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, Arrays.asList(17, 18, 19))
                .containsAll(Arrays.asList("These", "are", "not", "integeres"));
    }

    @Test
    public void testIterator() {
        final Set<Integer> expectedValues = new HashSet<>(Arrays.asList(17, 18, 19));
        final BKTree<Integer> tree = new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, expectedValues);

        final Iterator<Integer> iterator = tree.iterator();

        final Set<Integer> valuesFromIterator = new HashSet<>();

        while (iterator.hasNext()) {
            valuesFromIterator.add(iterator.next());
        }

        assertEquals(expectedValues, valuesFromIterator);
    }

    @Test
    public void testToArray() {
        final Set<Integer> expectedValues = new HashSet<>(Arrays.asList(17, 18, 19));
        final BKTree<Integer> tree = new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, expectedValues);

        final Object[] array = tree.toArray();

        assertEquals(expectedValues, new HashSet<>(Arrays.asList(array)));
    }

    @Test
    public void testToTypedArray() {
        final Set<Integer> expectedValues = new HashSet<>(Arrays.asList(17, 18, 19));
        final BKTree<Integer> tree = new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, expectedValues);

        {
            final Integer[] undersizedArray = new Integer[0];
            assertEquals(expectedValues, new HashSet<>(Arrays.asList(tree.toArray(undersizedArray))));
        }

        {
            final Integer[] arrayWithExactCapacity = new Integer[expectedValues.size()];
            final Integer[] arrayFromTree = tree.toArray(arrayWithExactCapacity);

            assertSame(arrayWithExactCapacity, arrayFromTree);
            assertEquals(expectedValues, new HashSet<>(Arrays.asList(arrayFromTree)));
        }

        {
            final Integer[] arrayWithExcessCapacity = new Integer[expectedValues.size() + 1];
            final Integer[] arrayFromTree = tree.toArray(arrayWithExcessCapacity);

            assertSame(arrayWithExcessCapacity, arrayFromTree);
            assertEquals(expectedValues, new HashSet<>(Arrays.asList(arrayFromTree).subList(0, tree.size())));
            assertNull(arrayFromTree[tree.size()]);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testToTypedArrayNullArray() {
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).toArray((Object[]) null);
    }

    @Test(expected = ArrayStoreException.class)
    public void testToTypedArrayMismatchedArray() {
        //noinspection SuspiciousToArrayCall
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, Arrays.asList(17, 18, 19)).toArray(new String[0]);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).remove(17);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRetainAll() {
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).retainAll(Collections.emptyList());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testRemoveAll() {
        //noinspection SuspiciousMethodCalls
        new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).removeAll(Collections.emptyList());
    }

    @Test
    public void testClear() {
        final BKTree<Integer> tree = new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, Collections.singleton(17));

        assertFalse(tree.isEmpty());
        tree.clear();
        assertTrue(tree.isEmpty());
    }

    @Test
    public void testGetNearestNeighbors() {
        final BKTree<Integer> tree = new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION, Arrays.asList(1, 3, 5, 7, 9, 11, 13));
        final PriorityQueue<Integer> nearestNeighbors = tree.getNearestNeighbors(5, 2);

        assertEquals(3, nearestNeighbors.size());
        //noinspection ConstantConditions
        assertEquals(5, (int) nearestNeighbors.peek());
        assertTrue(nearestNeighbors.containsAll(Arrays.asList(3, 5, 7)));
    }

    @Test
    public void testGetDistanceFunction() {
        assertEquals(DIFFERENCE_DISTANCE_FUNCTION, new BKTree<>(DIFFERENCE_DISTANCE_FUNCTION).getDistanceFunction());
    }
}

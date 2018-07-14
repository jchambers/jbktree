package com.eatthepath.jbktree;

import java.util.*;
import java.util.function.ToIntFunction;

public class BKTree<E> extends AbstractSet<E> {
    private BKTreeNode<E> rootNode;
    private final DiscreteDistanceFunction<? super E> distanceFunction;

    public BKTree(final DiscreteDistanceFunction<? super E> distanceFunction) {
        this(distanceFunction, Collections.emptyList());
    }

    public BKTree(final DiscreteDistanceFunction<? super E> distanceFunction, final Collection<E> c) {
        this.distanceFunction = distanceFunction;
        this.addAll(c);
    }

    @Override
    public boolean isEmpty() {
        return rootNode == null;
    }

    @Override
    public int size() {
        return rootNode != null ? rootNode.size() : 0;
    }

    @Override
    public boolean add(final E e) {
        Objects.requireNonNull(e);

        if (rootNode != null) {
            return rootNode.add(e, distanceFunction);
        } else {
            rootNode = new BKTreeNode<>(e);
            return true;
        }
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        Objects.requireNonNull(c);

        boolean addedAny = false;

        for (final E e : c) {
            addedAny = this.add(e) || addedAny;
        }

        return addedAny;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(final Object o) {
        try {
            return rootNode != null && rootNode.contains((E) o, distanceFunction);
        } catch (final ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<E> iterator() {
        // TODO This is very memory-inefficient and could certainly be optimized
        return (Iterator<E>) Arrays.asList(toArray()).iterator();
    }

    @Override
    public Object[] toArray() {
        final Object[] array = new Object[size()];

        if (rootNode != null) {
            rootNode.addElementsToArray(array, 0);
        }

        return array;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        final int size = size();

        if (a.length < size) {
            a = (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
        }

        if (rootNode != null) {
            rootNode.addElementsToArray(a, 0);
        }

        if (a.length > size) {
            a[size] = null;
        }

        return a;
    }

    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        rootNode = null;
    }

    public PriorityQueue<E> getNearestNeighbors(final E query, final int radius) {
        final PriorityQueue<E> results =
                new PriorityQueue<>(Comparator.comparingInt(value -> distanceFunction.getDistance(value, query)));

        if (rootNode != null) {
            rootNode.getNearestNeighbors(query, radius, results, distanceFunction);
        }

        return results;
    }
}

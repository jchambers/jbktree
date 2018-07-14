package com.eatthepath.jbktree;

import java.util.Arrays;

class BKTreeNode<E> {

    private final E value;
    private BKTreeNode<E>[] childNodes;

    @SuppressWarnings("unchecked")
    BKTreeNode(final E value) {
        this.value = value;
        this.childNodes = new BKTreeNode[0];
    }

    boolean add(final E e, final DiscreteDistanceFunction<? super E> distanceFunction) {
        final int distance = distanceFunction.getDistance(e, value);

        if (distance == 0) {
            return false;
        }

        if (childNodes.length < distance + 1) {
            childNodes = Arrays.copyOf(childNodes, distance + 1);
        }

        if (childNodes[distance] == null) {
            childNodes[distance] = new BKTreeNode<>(e);
            return true;
        }

        return childNodes[distance].add(e, distanceFunction);
    }

    int size() {
        int size = 1;

        for (final BKTreeNode<E> node : childNodes) {
            if (node != null) {
                size += node.size();
            }
        }

        return size;
    }

    boolean contains(final E e, final DiscreteDistanceFunction<? super E> distanceFunction) {
        final int distance = distanceFunction.getDistance(e, value);

        return distance == 0 ||
                ((childNodes.length > distance && childNodes[distance] != null) &&
                childNodes[distance].contains(e, distanceFunction));
    }

    int addElementsToArray(final Object[] array, int offset) {
        final int originalOffset = offset;

        array[offset] = value;
        offset += 1;

        for (final BKTreeNode<E> child : childNodes) {
            if (child != null) {
                offset += child.addElementsToArray(array, offset);
            }
        }

        return offset - originalOffset;
    }
}

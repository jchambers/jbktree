package com.eatthepath.jbktree;

import java.util.*;

/**
 * <p>A <a href="https://signal-to-noise.xyz/post/bk-tree/">BK-tree</a> is a
 * <a href="https://en.wikipedia.org/wiki/Metric_tree">metric tree</a> designed for use in discrete
 * <a href="https://en.wikipedia.org/wiki/Metric_space">metric spaces</a>, and is most commonly used for k-nearest
 * neighbor searches.</p>
 *
 * <p>BK-trees implement the {@link Set} interface and do not allow {@code null} elements. The iteration order over the
 * elements of a BK-tree is not defined. BK-trees do not allow for the removal of elements via the
 * {@link #remove(Object)}, {@link #removeAll(Collection)}, or {@link #retainAll(Collection)} methods.</p>
 *
 * <p><em>Note that this implementation is not synchronized.</em> If multiple threads access a BK-tree concurrently, and
 * at least one of the threads modifies the set, it must be synchronized externally. This is typically accomplished by
 * synchronizing on some object that naturally encapsulates the tree. If no such object exists, the tree should be
 * "wrapped" using the {@link Collections#synchronizedSet(Set)} method. This is best done at creation time, to prevent
 * accidental unsynchronized access to the tree:</p>
 *
 * <pre>    BKTree&lt;E&gt; bkTree = (BKTree&lt;E&gt;) Collections.synchronizedSet(new BKTree&lt;&gt;(...));</pre>
 *
 * <p>All BK-tree instances require a {@link DiscreteDistanceFunction} to calculate the distance between elements in the
 * tree. For the purposes of BK-trees, distance functions must conform to the rules of a metric space:</p>
 *
 * <ol>
 *  <li>d(x, y) &ge; 0</li>
 *  <li>d(x, y) = 0 if and only if x = y</li>
 *  <li>d(x, y) = d(y, x)</li>
 *  <li>d(x, z) &le; d(x, y) + d(y, z)</li>
 * </ol>
 *
 * @param <E> the type of elements maintained by this BK-tree
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
public class BKTree<E> extends AbstractSet<E> {

    private BKTreeNode<E> rootNode;
    private final DiscreteDistanceFunction<? super E> distanceFunction;

    /**
     * Constructs a new, empty BK-tree that uses the given distance function to calculate the distance between elements
     * in the tree.
     *
     * @param distanceFunction the distance function to be used to calculate the distance between elements in the tree
     *
     * @throws NullPointerException if the given distance function is {@code null}
     */
    public BKTree(final DiscreteDistanceFunction<? super E> distanceFunction) {
        this(distanceFunction, Collections.emptyList());
    }

    /**
     * Constructs a new BK-tree containing the elements of the given collection that uses the given distance function to
     * calculate the distance between elements in the tree.
     *
     * @param distanceFunction the distance function to be used to calculate the distance between elements in the tree
     * @param c the collection whose elements are to be placed into this tree
     *
     * @throws NullPointerException if the given distance function or collection of elements is {@code null}, or if the
     * given collection contains one or more {@code null} elements
     */
    public BKTree(final DiscreteDistanceFunction<? super E> distanceFunction, final Collection<E> c) {
        Objects.requireNonNull(distanceFunction, "Distance function must not be null.");
        Objects.requireNonNull(c, "Initial collection of elements may be empty, but must not be null.");

        this.distanceFunction = distanceFunction;
        this.addAll(c);
    }

    /**
     * Indicates whether this tree is empty.
     *
     * @return {@code true} if this tree contains no elements or {@code false} otherwise
     */
    @Override
    public boolean isEmpty() {
        return rootNode == null;
    }

    /**
     * Returns the number of elements contained in this tree.
     *
     * @return the number of elements contained in this tree
     */
    @Override
    public int size() {
        return rootNode != null ? rootNode.size() : 0;
    }

    /**
     * Adds the given element to this tree if it is not already present.
     *
     * @param e the element to add to the tree
     *
     * @return {@code true} if this tree did not already contain the given element or {@code false} otherwise
     *
     * @throws NullPointerException if the given element is {@code null}
     */
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

    /**
     * Adds all of the elements in the specified collection to this tree if they're not already present.
     *
     * @param c the collection of elements to add to this tree
     *
     * @return {@code true} if at least one element in the given collection was not already present in this tree or
     * {@code false} otherwise
     *
     * @throws NullPointerException if the specified collection contains one or more {@code null} elements, or if the
     * specified collection is {@code null}
     */
    @Override
    public boolean addAll(final Collection<? extends E> c) {
        Objects.requireNonNull(c);

        boolean addedAny = false;

        for (final E e : c) {
            addedAny = this.add(e) || addedAny;
        }

        return addedAny;
    }

    /**
     * Checks whether this tree contains the given object.
     *
     * @param o the object whose presence in this tree is to be tested
     *
     * @return {@code true} if this tree contains the given object or {@code false} otherwise
     *
     * @throws ClassCastException if the type of the specified element is incompatible with this tree
     * @throws NullPointerException if the given object is {@code null}
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(final Object o) {
        Objects.requireNonNull(o);

        final E e = (E) o;
        return rootNode != null && rootNode.contains(e, distanceFunction);
    }

    /**
     * Checks whether this tree contains all of the elements of the specified collection.
     *
     * @param c the collection to be checked for containment in this tree
     *
     * @return {@code true} if this set contains all of the elements of the specified collection or {@code false}
     * otherwise
     *
     * @throws ClassCastException if the types of one or more elements in the specified collection are incompatible with
     * this tree
     * @throws NullPointerException if the specified collection contains one or more {@code null} elements or if the
     * specified collection is {@code null}
     */
    @Override
    public boolean containsAll(final Collection<?> c) {
        for (final Object o : c) {
            if (!contains(o)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns an iterator over the elements in this tree. The order of the elements is not defined.
     *
     * @return an iterator over the elements in this tree
     */
    @Override
    @SuppressWarnings("unchecked")
    public Iterator<E> iterator() {
        // TODO This is very memory-inefficient and could certainly be optimized
        return (Iterator<E>) Arrays.asList(toArray()).iterator();
    }

    /**
     * Returns an array containing all of the elements in this tree. The order of elements within the array is not
     * defined. No references to the returned array are maintained by the tree.
     *
     * @return an array containing all of the elements in this tree
     */
    @Override
    public Object[] toArray() {
        final Object[] array = new Object[size()];

        if (rootNode != null) {
            rootNode.addElementsToArray(array, 0);
        }

        return array;
    }

    /**
     * <p>Returns an array containing all of the elements in this tree; the runtime type of the returned array is that
     * of the specified array. If the tree fits in the specified array, it is returned therein. Otherwise, a new array
     * is allocated with the runtime type of the specified array and the size of this tree. The order of elements within
     * the array is not defined.</p>
     * 
     * <p>If this tree fits in the specified array with room to spare (i.e., the array has more elements than this
     * tree), the element in the array immediately following the end of the tree is tree to {@code null}.</p>
     *
     * @param a the array into which the elements of this set are to be stored, if it is big enough; otherwise, a new
     * array of the same runtime type is allocated for this purpose.
     * @param <T> the runtime type of the array to contain the collection
     *
     * @return an array containing all the elements in this set
     *
     * @throws ArrayStoreException if the runtime type of the specified array is not a supertype of the runtime type of
     * every element in this tree
     * @throws NullPointerException if the specified array is null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        Objects.requireNonNull(a);

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

    /**
     * Unsupported; elements may not be removed from a BK-tree.
     *
     * @param o object to be removed from this tree, if present
     * @return {@code true} if this tree contained the specified element or {@code false} otherwise
     *
     * @throws UnsupportedOperationException under all circumstances
     */
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported; elements may not be removed from a BK-tree.
     *
     * @param c a collection containing elements to be retained in this tree
     *
     * @return {@code true} if one or more elements were removed from this tree or {@code false} otherwise
     *
     * @throws UnsupportedOperationException under all circumstances
     */
    @Override
    public boolean retainAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Unsupported; elements may not be removed from a BK-tree.
     *
     * @param c a collection containing elements to be removed from this tree
     *
     * @return {@code true} if this tree contained one or more elements in the given collection or {@code false}
     * otherwise
     *
     * @throws UnsupportedOperationException under all circumstances
     */
    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes all of the elements from this tree.
     */
    @Override
    public void clear() {
        rootNode = null;
    }

    /**
     * Returns a queue of elements in this tree that fall within the given radius (as measured by this tree's distance
     * function) of the given query element. Elements from this tree are included in the result set if their distance
     * from the query element is less than <em>or equal to</em> the given radius. The returned queue is ordered by
     * distance from the query element; if multiple elements in the result set have the same distance from the query
     * element, the order within their distance "band" is undefined.
     *
     * @param query the element for which to find similar elements
     * @param radius the maximum distance, as measured by this tree's distance function, at which to search for similar
     * elements; elements are included in the result set if their distance from the query element is less than or equal
     * to the given radius
     *
     * @return a queue of elements in this tree that fall within the given radius of the query element
     */
    @SuppressWarnings("WeakerAccess")
    public PriorityQueue<E> getNearestNeighbors(final E query, final int radius) {
        final PriorityQueue<E> results =
                new PriorityQueue<>(Comparator.comparingInt(value -> distanceFunction.getDistance(value, query)));

        if (rootNode != null) {
            rootNode.getNearestNeighbors(query, radius, results, distanceFunction);
        }

        return results;
    }

    /**
     * Returns this tree's distance function.
     *
     * @return the distance function used by this tree to calculate the distance between its elements
     */
    @SuppressWarnings("WeakerAccess")
    public DiscreteDistanceFunction<? super E> getDistanceFunction() {
        return distanceFunction;
    }
}

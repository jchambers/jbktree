package com.eatthepath.jbktree;

/**
 * <p>A function that calculates the discrete distance between two points. For the purposes of BK-trees, distance
 * functions must conform to the rules of a <a href="https://en.wikipedia.org/wiki/Metric_space">metric space</a>:</p>
 *
 * <ol>
 *  <li>d(x, y) &ge; 0</li>
 *  <li>d(x, y) = 0 if and only if x = y</li>
 *  <li>d(x, y) = d(y, x)</li>
 *  <li>d(x, z) &le; d(x, y) + d(y, z)</li>
 * </ol>
 *
 * @param <T> the type of points between which this function measures distance
 *
 * @author <a href="https://github.com/jchambers">Jon Chambers</a>
 */
@FunctionalInterface
public interface DiscreteDistanceFunction<T> {

    /**
     * Returns the discrete distance between two points.
     *
     * @param first the first point from which to measure distance
     * @param second the second point to which to measure distance
     *
     * @return the discrete distance between the given points
     */
    int getDistance(T first, T second);
}

package com.eatthepath.jbktree;

@FunctionalInterface
public interface DiscreteDistanceFunction<T> {
    int getDistance(T first, T second);
}

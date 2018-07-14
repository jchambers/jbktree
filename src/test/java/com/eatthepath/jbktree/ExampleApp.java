package com.eatthepath.jbktree;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class ExampleApp {

    public static void main(final String... args) throws Exception {
        final List<String> words;

        // Under macOS, /usr/share/dict/words contains a list of 235,886 English words
        try (final BufferedReader reader = new BufferedReader(new FileReader("/usr/share/dict/words"))) {
            words = reader.lines().collect(Collectors.toList());
        }

        final DiscreteDistanceFunction<String> distanceFunction = (first, second) ->
                LevenshteinDistance.getDefaultInstance().apply(first, second);

        final BKTree<String> bkTree = new BKTree<>(distanceFunction, words);

        final PriorityQueue<String> results = bkTree.getNearestNeighbors("exaple", 2);

        System.out.println("| Neighbor | Distance |");
        System.out.println("|----------|----------|");

        while (!results.isEmpty()) {
            final String word = results.poll();
            System.out.format("| %-8s | %-8d |\n", word, distanceFunction.getDistance("exaple", word));
        }
    }
}

# jbktree

[![Build Status](https://travis-ci.org/jchambers/jbktree.svg?branch=master)](https://travis-ci.org/jchambers/jbktree)

jbktree provides a [generic](https://docs.oracle.com/javase/tutorial/java/generics/) [BK-tree](https://signal-to-noise.xyz/post/bk-tree/) implemented as a [`java.util.Collection`](https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html). A BK-tree is a kind of [metric tree](https://en.wikipedia.org/wiki/Metric_tree) designed for use in discrete [metric spaces](https://en.wikipedia.org/wiki/Metric_space). BK-trees are generally used to efficiently conduct [_k_-nearest neighbor](https://en.wikipedia.org/wiki/K-nearest_neighbors_algorithm) searches.

A common use case for BK-trees (but certainly not the only use case) is "fuzzy matching" for strings, or "spell-checking." In this use case, we add a list of known words into a BK-tree, and can then search the tree for words that are within a certain [edit distance](https://en.wikipedia.org/wiki/Edit_distance) of a query term. To demonstrate this use case, we can start by loading a list of words into a `List`.

```java
final List<String> words;

// Under macOS, /usr/share/dict/words contains a list of 235,886 English words
try (final BufferedReader reader = new BufferedReader(new FileReader("/usr/share/dict/words"))) {
    words = reader.lines().collect(Collectors.toList());
}
```

Next, we define the distance function we'd like to use to calculate the edit distance between words in the list. In this case, we'll use the [Levenshtein distance](https://en.wikipedia.org/wiki/Levenshtein_distance) implementation from [Commons Text](https://commons.apache.org/proper/commons-text/):

```java
final DiscreteDistanceFunction<String> distanceFunction = (first, second) ->
        LevenshteinDistance.getDefaultInstance().apply(first, second);
```

With a distance function and a collection of words, constructing a BK-tree is staightforward:

```java
final BKTree<String> bkTree = new BKTree<>(distanceFunction, words);
```

Now, let's say we have a word that we think is misspelled (`"exaple"`), and we want to find some possible replacements. We can search the BK-tree for all other words that are within a certain edit distance ("radius") of our query term to get some suggestions:

```java
final PriorityQueue<String> results = bkTree.getNearestNeighbors("exaple", 2);
```

That gives us the following results:

| Neighbor | Distance |
|----------|----------|
| example  | 1        |
| hexapla  | 2        |
| vexable  | 2        |
| exciple  | 2        |
| exile    | 2        |
| exhale   | 2        |
| staple   | 2        |
| epaule   | 2        |
| enable   | 2        |
| elapse   | 2        |
| eagle    | 2        |
| saple    | 2        |
| maple    | 2        |
| exalt    | 2        |

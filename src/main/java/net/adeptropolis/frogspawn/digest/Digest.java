/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

import net.adeptropolis.frogspawn.graphs.labeled.labelings.Labeling;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * <p>A cluster digest</p>
 * <p>Digests are precursors of final cluster outputs and hold three index-aligned lists or vertices, weights and vertex
 * affiliation scores that aggregated from a given cluster and sorted according to some criterion. Digests may either store
 * all cluster vertices or just the top-ranked subset.
 * </p>
 */

public class Digest {

  private final int[] vertices;
  private final double[] weights;
  private final double[] scores;
  private final int totalSize;

  /**
   * Constructor
   *
   * @param vertices  cluster digest vertices
   * @param weights   Vertex weights
   * @param scores    Vertex affiliation scores
   * @param totalSize Total size of available vertices. Useful if the digest doesn't contain all cluster vertices.
   */

  Digest(int[] vertices, double[] weights, double[] scores, int totalSize) {
    this.vertices = vertices;
    this.weights = weights;
    this.scores = scores;
    this.totalSize = totalSize;
  }

  /**
   * @return Vertices
   */

  public int[] getVertices() {
    return vertices;
  }

  /**
   * @return Vertex weights
   */

  public double[] getWeights() {
    return weights;
  }

  /**
   * @return Vertex affiliation scores
   */

  public double[] getScores() {
    return scores;
  }

  /**
   * @return Total cluster size (In case the digest has been cut off at some point)
   */

  public int totalSize() {
    return totalSize;
  }

  /**
   * Provides a stream of custom digest members
   *
   * @param mapper Mapping between cluster digest vertices and custom cluster member objects
   * @param <T>    Type of the custom cluster member objects
   * @return Custom cluster member object
   */

  public <T> Stream<T> map(DigestMapping<T> mapper) {
    return IntStream.range(0, size())
            .mapToObj(i -> mapper.map(vertices[i], weights[i], scores[i]));
  }

  /**
   * @return Size of the digest
   */

  public int size() {
    return vertices.length;
  }

  /**
   * Provides a stream of custom digest members for a labeled graph
   *
   * @param mapper   Mapping between cluster digest vertices and custom cluster member objects
   * @param labeling Vertex labeling
   * @param <V>      Type of the custom cluster member objects
   * @param <T>      Mapping result type
   * @return Custom cluster member object
   */

  public <V, T> Stream<T> map(LabeledDigestMapping<V, T> mapper, Labeling<V> labeling) {
    return IntStream.range(0, size())
            .mapToObj(i -> mapper.map(labeling.label(vertices[i]), weights[i], scores[i]));
  }


  /**
   * Yield labeled digest instances
   *
   * @param labeling Graph labeling
   * @param consumer Instance of LabeledDigestConsumer
   * @param <V>      Vertex type
   */

  public <V> void forEach(Labeling<V> labeling, LabeledDigestConsumer<V> consumer) {
    for (int i = 0; i < size(); i++) {
      consumer.accept(labeling.label(vertices[i]), weights[i], scores[i]);
    }
  }

}

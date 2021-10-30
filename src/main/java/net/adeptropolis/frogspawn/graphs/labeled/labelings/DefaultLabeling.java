/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled.labelings;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Default graph labeling using a simple map and and array, Although this implementation allows for
 * very generic usage, please try to use some type-optimized labeling implementation instead.
 *
 * @param <T> Label type
 */

public class DefaultLabeling<T> implements Labeling<T> {

  private final Object2IntOpenHashMap<T> indices;
  private Object[] labels;

  /**
   * Constructor
   */

  public DefaultLabeling() {
    this.indices = new Object2IntOpenHashMap<>();
    this.labels = null;
  }

  /**
   * @param v Global vertex id
   * @return The label associated with a given global vertex id
   */

  @SuppressWarnings("unchecked")
  @Override
  public T label(int v) {
    ensureLabels();
    if (v >= labels.length) {
      return null;
    }
    return (T) labels[v];
  }

  /**
   * Either lookup or update (depending on whether the label buffer has been built yet)
   *
   * @param label Vertex label
   * @return Global vertex id (or -1 in case of mismatch)
   */

  @Override
  public int id(T label) {

    if (indices.containsKey(label)) {
      return indices.getInt(label);
    }

    if (labels != null) {
      return -1;
    }

    int index = indices.size();
    indices.put(label, index);

    return index;
  }

  /**
   * @return Stream of all labels
   */

  @SuppressWarnings("unchecked")
  @Override
  public Stream<T> labels() {
    ensureLabels();
    return Arrays.stream(labels).map(label -> (T) label);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public Labeling<T> newInstance() {
    return new DefaultLabeling<>();
  }

  /**
   * Ensure that the label buffer has been properly set up
   */

  private void ensureLabels() {
    if (labels == null) {
      commit();
    }
  }

  /**
   * Build the label buffer
   */

  private void commit() {
    labels = new Object[indices.size()];
    indices.forEach((label, idx) -> labels[idx] = label);
  }

}

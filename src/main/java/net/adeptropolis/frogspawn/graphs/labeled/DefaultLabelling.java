/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Default graph labelling using a simple map and and array, Although this implementation allows for
 * very generic usage, please try to use some type-optimized labelling implementation instead.
 * @param <T> Label type
 */

public class DefaultLabelling<T extends Serializable> implements Labelling<T>, Serializable {

  private final Object2IntOpenHashMap<T> indices;
  private T[] labels;
  private final Class<T> labelClass;

  /**
   * Constructor
   * @param labelClass Class of vertex labels
   */

  public DefaultLabelling(Class<T> labelClass) {
    this.indices = new Object2IntOpenHashMap<>();
    this.labelClass = labelClass;
    this.labels = null;
  }

  /**
   * @param v Global vertex id
   * @return The label associated with a given global vertex id
   */

  @Override
  public T label(int v) {
    ensureLabels();
    if (v >= labels.length) {
      return null;
    }
    return labels[v];
  }

  /**
   * Either lookup or update (depending on whether the label buffer has been built yet)
   *
   * @param label Vertex label
   * @return Global vertex id (or -1 in case of mismatch)
   */

  @Override
  public int index(T label) {

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

  @Override
  public Stream<T> labels() {
    ensureLabels();
    return Arrays.stream(labels);
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

  @SuppressWarnings("unchecked")
  private void commit() {
    labels = (T[]) Array.newInstance(labelClass, indices.size());
    indices.forEach((label, idx) -> labels[idx] = label);
  }

}

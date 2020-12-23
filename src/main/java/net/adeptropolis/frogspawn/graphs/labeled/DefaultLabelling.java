/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class DefaultLabelling<T extends Serializable> implements Labelling<T>, Serializable {

  private final Object2IntOpenHashMap<T> indices;
  private T[] labels;
  private final Class<T> labelClass;

  public DefaultLabelling(Class<T> labelClass) {
    this.indices = new Object2IntOpenHashMap<>();
    this.labelClass = labelClass;
    this.labels = null;
  }

  @Override
  public T label(int v) {
    ensureLabels();
    return labels[v];
  }

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

  @Override
  public Stream<T> labels() {
    ensureLabels();
    return Arrays.stream(labels);
  }

  private void ensureLabels() {
    if (labels == null) {
      commit();
    }
  }

  @SuppressWarnings("unchecked")
  private void commit() {
    labels = (T[]) Array.newInstance(labelClass, indices.size());
    indices.forEach((label, idx) -> labels[idx] = label);
  }

}

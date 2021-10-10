/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled.labelings;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.Arrays;
import java.util.stream.Stream;

public class StringLabeling implements Labeling<String> {

  private final Object2IntOpenHashMap<String> indices;
  private String[] labels;

  /**
   * Constructor
   */

  public StringLabeling() {
    this.indices = new Object2IntOpenHashMap<>();
    this.labels = null;
  }


  /**
   * {@inheritDoc}
   */

  @Override
  public String label(int v) {
    ensureLabels();
    if (v >= labels.length) {
      return null;
    }
    return labels[v];
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int id(String label) {

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
   * {@inheritDoc}
   */

  @Override
  public Stream<String> labels() {
    ensureLabels();
    return Arrays.stream(labels);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public Labeling<String> newInstance() {
    return new StringLabeling();
  }

  /**
   * Ensure that the label buffer has been properly set up
   */

  private void ensureLabels() {
    if (labels == null) {
      labels = new String[indices.size()];
      indices.forEach((label, idx) -> labels[idx] = label);
    }
  }

}

/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

// NOTE: Must use default traversal!

public class DegreeCounter<V> implements LabeledEdgeConsumer<V> {

  private final Object2IntOpenHashMap<V> degrees;

  public DegreeCounter() {
    this.degrees = new Object2IntOpenHashMap<>();
  }

  @Override
  public void accept(V left, V right, double weight) {
    degrees.addTo(left, 1);
  }

  public int get(V label) {
    return degrees.getOrDefault(label, -1);
  }

}

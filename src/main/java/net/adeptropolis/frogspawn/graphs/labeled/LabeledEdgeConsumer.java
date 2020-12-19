/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

@FunctionalInterface
public interface LabeledEdgeConsumer<V> {

  void accept(V left, V right, double weight);

}

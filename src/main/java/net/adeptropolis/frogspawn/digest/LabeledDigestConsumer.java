/*
 * Copyright (c) Florian Schaefer 2021.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

@FunctionalInterface
public interface LabeledDigestConsumer<V> {

  void accept(V vertex, double weight, double score);

}

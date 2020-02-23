/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs;

@FunctionalInterface
public interface EdgeConsumer {

  void accept(int u, int v, double weight);

}

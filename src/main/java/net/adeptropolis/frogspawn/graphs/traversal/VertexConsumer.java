/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.traversal;

@FunctionalInterface
public interface VertexConsumer {

  void accept(int u);

}

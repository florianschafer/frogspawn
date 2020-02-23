/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs;

public interface VertexIterator {

  boolean hasNext();

  int localId();

  int globalId();

}

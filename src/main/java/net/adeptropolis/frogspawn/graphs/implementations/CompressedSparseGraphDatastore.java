/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigDoubles;
import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigInts;

/**
 * <p>Storage backend for large, sparse graphs</p>
 * <p>More precisely, this class provides a CSR storage representation for adjacency matrices of large sparse graphs.
 * Please note that vertex indices/ids are expected to be zero-offset ascending ints.<br/>
 * <p>
 * Technically, the edges are sorted by (1st) the index of the left and (2nd) the index of the right endpoint.
 * This is internally represented by a data structure consisting of 3 arrays:
 * <ol>
 * <li><code>edges</code> Array containing the right endpoints of all sorted edges</li>
 * <li><code>pointers</code> Array providing a map between vertex ids (taken as left endpoints of an edge) and their
 * offset pointer within the right endpoints array from (1)</li>
 * <li><code>weights</code> Array containing the edge weights</li>
 * </ol>
 * </p>
 */

public class CompressedSparseGraphDatastore {

  public final long[] pointers;
  public final BigInts edges;
  public final BigDoubles weights;
  private final int size;
  private final long edgeCount;

  /**
   * Constructor
   *
   * @param size      Number of vertices
   * @param edgeCount Number of edges
   * @param pointers  Vertex pointers
   * @param edges     Right endpoints of all edges
   * @param weights   Edge weights
   */

  CompressedSparseGraphDatastore(int size, long edgeCount, long[] pointers, BigInts edges, BigDoubles weights) {
    this.size = size;
    this.edgeCount = edgeCount;
    this.pointers = pointers;
    this.edges = edges;
    this.weights = weights;
  }

  /**
   * @return Number of vertices in the graph
   */

  public int size() {
    return size;
  }

  /**
   * @return Number of edges in the graph
   */

  long edgeCount() {
    return edgeCount;
  }

}

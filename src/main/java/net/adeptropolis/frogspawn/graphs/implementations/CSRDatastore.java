/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigDoubles;
import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigInts;

import java.io.Serializable;

/**
 * Storage class for large, sparse graphs
 * <p>
 * This class provides a CSR storage representation for adjacency matrices of large sparse graphs
 * with double-valued weights. For further details, see
 * <a href="https://en.wikipedia.org/wiki/Sparse_matrix#Compressed_sparse_row_(CSR,_CRS_or_Yale_format)">
 * Wikipedia: Compressed sparse row (CSR, CRS or Yale format)
 * </a>
 * </p>
 *
 * @see SparseGraphBuilder
 */

public class CSRDatastore implements Serializable {

  static final long serialVersionUID = 5572670833943799413L;

  public final long[] pointers;
  public final BigInts edges;
  public final BigDoubles weights;
  private final int order;
  private final long size;

  /**
   * Constructor
   *
   * @param order    Number of vertices
   * @param size     Number of edges
   * @param pointers Vertex pointers
   * @param edges    Right endpoints of all edges
   * @param weights  Edge weights
   */

  CSRDatastore(int order, long size, long[] pointers, BigInts edges, BigDoubles weights) {
    this.order = order;
    this.size = size;
    this.pointers = pointers;
    this.edges = edges;
    this.weights = weights;
  }

  /**
   * @return Number of vertices
   */

  public int order() {
    return order;
  }

  /**
   * @return Number of edges
   */

  long size() {
    return size;
  }

}

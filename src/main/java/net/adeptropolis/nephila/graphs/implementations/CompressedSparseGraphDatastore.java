/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.implementations;

import net.adeptropolis.nephila.graphs.implementations.arrays.BigDoubles;
import net.adeptropolis.nephila.graphs.implementations.arrays.BigInts;

/**
 * <p>Storage backend for large, sparse graphs</p>
 * <p>More precisely, this class provides a CSR storage representation for adjacency matrices of large sparse graphs.
 * Please note that vertex indices/ids are expected to be zero-offset ascending ints.<br/>
 * <p>
 * Technically, the edges are sorted by (1st) the index of the left and (2nd) the index of the right endpoint.
 * Internally, this is represented by a data structure consisting of 3 arrays:<br/>
 * <ol>
 * <li><code>edges</code> Array containing the right endpoints of all sorted edges</li>
 * <li><code>pointers</code> Array providing a map between vertex ids (taken as left endpoints of an edge) and their
 * offset pointer within the right endpoints array from (1)</li>
 * <li><code>weights</code> Array containing the edge weights</li>
 * </ol>
 * </p>
 *
 * @author Florian Schaefer
 * @author florian@adeptropolis.net
 * @version 1.0
 */

public class CompressedSparseGraphDatastore {

  public final long[] pointers;
  public final BigInts edges;
  public final BigDoubles weights;
  private final int size;
  private final long edgeCount;

  CompressedSparseGraphDatastore(int size, long edgeCount, long[] pointers, BigInts edges, BigDoubles weights) {
    this.size = size;
    this.edgeCount = edgeCount;
    this.pointers = pointers;
    this.edges = edges;
    this.weights = weights;
  }

  @Deprecated
  public View defaultView() {
    int[] indices = new int[size];
    for (int i = 0; i < size; i++) {
      indices[i] = i;
    }
    return view(indices);
  }

  @Deprecated
  public View view(int[] indices) {
    return new View(this, indices);
  }

  public int size() {
    return size;
  }

  long edgeCount() {
    return edgeCount;
  }

}

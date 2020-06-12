/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.VertexIterator;
import net.adeptropolis.frogspawn.graphs.traversal.EdgeConsumer;
import net.adeptropolis.frogspawn.graphs.traversal.ParallelEdgeOps;

import java.io.Serializable;

/**
 * A compressed sparse graph
 * <p><b>Note: </b> The vertex set of this type of graph always consists of
 * consecutive integers starting at 0. So, even if adding a single vertex n	&gt; 0,
 * the vertex set will be <code>{0...n}</code></p>
 */

public class CompressedSparseGraph extends Graph implements Serializable {

  static final long serialVersionUID = 3908340146557361096L;

  private final CompressedSparseGraphDatastore datastore;

  /**
   * Constructor
   *
   * @param datastore Graph datastore
   */

  public CompressedSparseGraph(CompressedSparseGraphDatastore datastore) {
    this.datastore = datastore;
  }

  /**
   * Create a builder for this type of graphs
   *
   * @return A new builder instance
   */

  public static CompressedSparseGraphBuilder builder() {
    return new CompressedSparseGraphBuilder();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int order() {
    return datastore.size();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public long size() {
    return datastore.edges.size();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public VertexIterator vertexIterator() {
    return new DefaultVertexIterator();
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int[] collectVertices() {
    int[] vertices = new int[order()];
    for (int i = 0; i < order(); i++) {
      vertices[i] = i;
    }
    return vertices;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public IntIterator globalVertexIdIterator() {
    return IntIterators.fromTo(0, order());
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void traverseParallel(EdgeConsumer consumer) {
    ParallelEdgeOps.traverse(this, consumer);
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void traverseIncidentEdges(int v, EdgeConsumer consumer) {

    if (order() == 0 || v < 0) {
      return;
    }

    long low = datastore.pointers[v];
    long high = datastore.pointers[v + 1];
    if (low == high) {
      return;
    }

    int rightEndpoint;
    for (long ptr = low; ptr < high; ptr++) {
      rightEndpoint = datastore.edges.get(ptr);
      consumer.accept(v, rightEndpoint, datastore.weights.get(ptr));
      if (rightEndpoint + 1 >= order()) {
        break;
      }
    }

  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int localVertexId(int globalVertexId) {
    return globalVertexId;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int globalVertexId(int localVertexId) {
    return localVertexId;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public Graph inducedSubgraph(IntIterator vertices) {
    return new CompressedInducedSparseSubgraph(datastore, vertices);
  }

  /**
   * Iterator over the vertex set
   */

  public class DefaultVertexIterator implements VertexIterator {

    int idx = 0;

    /**
     * {@inheritDoc}
     */

    @Override
    public boolean hasNext() {
      return idx++ < order();
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public int localId() {
      return idx - 1;
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public int globalId() {
      return idx - 1;
    }
  }

}

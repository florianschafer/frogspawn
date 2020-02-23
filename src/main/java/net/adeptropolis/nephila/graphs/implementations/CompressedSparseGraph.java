/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graphs.EdgeConsumer;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.ParallelEdgeOps;
import net.adeptropolis.nephila.graphs.VertexIterator;

/**
 * A compressed sparse graph
 * <p><b>Note: </b> The vertex set of this type of graph always consists of
 * consecutive integers startng at 0. So, even if adding a single vertex n	&gt; 0,
 * the vertex set will be <code>{0...n}</code></p>
 */

public class CompressedSparseGraph extends Graph {

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
   * @return The number of vertices of the graph
   */

  @Override
  public int size() {
    return datastore.size();
  }

  /**
   * <b>Note:</b>The edges are accounted for in a directed fashion!
   * That is, an undirected graph has 2x the expected number of edges
   *
   * @return number of edges
   */

  @Override
  public long numEdges() {
    return datastore.edges.size();
  }

  /**
   * Return the vertex set
   *
   * @return An iterator for the vertex set
   */


  @Override
  public VertexIterator vertexIterator() {
    return new DefaultVertexIterator();
  }

  @Override
  public int[] collectVertices() {
    int[] vertices = new int[size()];
    for (int i = 0; i < size(); i++) {
      vertices[i] = i;
    }
    return vertices;
  }

  /**
   * Traverse all edges of the graph
   *
   * @param consumer Instance of <code>EdgeConsumer</code>
   */

  @Override
  public void traverseParallel(EdgeConsumer consumer) {
    ParallelEdgeOps.traverse(this, consumer);
  }

  /**
   * Traverse all neighhours of a given vertex
   *
   * @param v        A (local!) vertex
   * @param consumer Instance of <code>EdgeConsumer</code>
   */

  @Override
  public void traverseParallel(int v, EdgeConsumer consumer) {

    if (size() == 0 || v < 0) {
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
      if (rightEndpoint + 1 >= size()) {
        break;
      }
    }

  }

  /**
   * Translate between global and local vertex ids
   *
   * @param globalVertexId A global vertex id
   * @return A local vertex id
   */

  @Override
  public int localVertexId(int globalVertexId) {
    return globalVertexId;
  }

  /**
   * Translate between locao and global vertex ids
   *
   * @param localVertexId A global vertex id
   * @return A global vertex id
   */

  @Override
  public int globalVertexId(int localVertexId) {
    return localVertexId;
  }

  /**
   * Return a new induces subgraph
   *
   * @param vertices The vertex set of the new subgraph
   * @return A new graph
   */

  @Override
  public Graph inducedSubgraph(IntIterator vertices) {
    return new CompressedInducedSparseSubgraph(datastore, vertices);
  }

  /**
   * @return The compressed datastore
   */

  public CompressedSparseGraphDatastore getDatastore() {
    return datastore;
  }

  /**
   * Iterator over the vertex set
   */

  public class DefaultVertexIterator implements VertexIterator {

    int idx = 0;

    @Override
    public boolean hasNext() {
      return idx++ < size();
    }

    @Override
    public int localId() {
      return idx - 1;
    }

    @Override
    public int globalId() {
      return idx - 1;
    }
  }

}

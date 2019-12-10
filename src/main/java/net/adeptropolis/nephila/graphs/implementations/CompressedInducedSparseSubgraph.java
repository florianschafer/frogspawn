/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.nephila.graphs.EdgeConsumer;
import net.adeptropolis.nephila.graphs.EdgeOps;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;
import net.adeptropolis.nephila.graphs.implementations.arrays.InterpolationSearch;

import java.util.Arrays;

/**
 * Induced subgraph.
 * <p>That is, a graph whose vertex set is limited to a subset of another graph.
 * The edge set is restricted to those whose endpoints are members of the given vertex set.</p>
 */

public class CompressedInducedSparseSubgraph extends Graph {

  private final CompressedSparseGraphDatastore datastore;
  private final int[] vertices;

  /**
   * Constructor
   *
   * @param datastore The underlying graph datastore
   * @param vertices  An iterator of global vertex ids
   */

  public CompressedInducedSparseSubgraph(CompressedSparseGraphDatastore datastore, IntIterator vertices) {
    this.datastore = datastore;
    this.vertices = IntIterators.unwrap(vertices);
    Arrays.parallelSort(this.vertices, 0, size());
  }

  /**
   * @return The number of vertices of the graph
   */

  @Override
  public int size() {
    return vertices.length;
  }

  /**
   * Return the vertex set
   *
   * @return An iterator for the vertex set
   */

  @Override
  public VertexIterator vertexIterator() {
    return new SubgraphVertexIterator().reset(vertices);
  }

  /**
   * Traverse all edges of the graph
   *
   * @param consumer Instance of <code>EdgeConsumer</code>
   */

  @Override
  public void traverse(EdgeConsumer consumer) {
    EdgeOps.traverse(this, consumer);
  }

  /**
   * Traverse all neighhours of a given vertex
   *
   * @param v        A (local!) vertex
   * @param consumer Instance of <code>EdgeConsumer</code>
   */

  @Override
  public void traverse(int v, EdgeConsumer consumer) {

    if (size() == 0 || v < 0) {
      return;
    }

    int globalId = globalVertexId(v);

    long low = datastore.pointers[globalId];
    long high = datastore.pointers[globalId + 1];

    if (low == high) {
      return;
    }

    if (size() > high - low) {
      traverseByAdjacent(v, consumer, low, high);
    } else {
      traverseByVertices(v, consumer, low, high);
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
    return InterpolationSearch.search(vertices, globalVertexId, 0, size() - 1);
  }

  /**
   * Translate between locao and global vertex ids
   *
   * @param localVertexId A global vertex id
   * @return A global vertex id
   */

  @Override
  public int globalVertexId(int localVertexId) {
    return vertices[localVertexId];
  }

  /**
   * Internal: Traverse all neighbours of a given local vertex by the non-zero entries of the adjacency matrix
   *
   * @param leftEndpoint A local vertex id
   * @param consumer     An instance of <code>EdgeConsumer</code>
   * @param low          Initial edge pointer
   * @param high         Maximum edge pointer (inclusive!)
   */

  private void traverseByAdjacent(final int leftEndpoint, final EdgeConsumer consumer, final long low, final long high) {
    int secPtr = 0;
    int rightEndpoint;
    for (long ptr = low; ptr < high; ptr++) {
      rightEndpoint = InterpolationSearch.search(vertices, datastore.edges.get(ptr), secPtr, size() - 1);
      if (rightEndpoint >= 0) {
        consumer.accept(leftEndpoint, rightEndpoint, datastore.weights.get(ptr));
        secPtr = rightEndpoint + 1;
      }
      if (secPtr >= size()) break;
    }
  }

  /**
   * Internal: Traverse all neighbours of a given local vertex by the vertex set
   *
   * @param leftEndpoint A local vertex id
   * @param consumer     An instance of <code>EdgeConsumer</code>
   * @param low          Initial edge pointer
   * @param high         Maximum edge pointer (inclusive!)
   */

  private void traverseByVertices(final int leftEndpoint, final EdgeConsumer consumer, final long low, final long high) {
    long ptr = low;
    long retrievedIdx;
    for (int i = 0; i < size(); i++) {
      retrievedIdx = InterpolationSearch.search(datastore.edges, vertices[i], ptr, high - 1);
      if (retrievedIdx >= 0 && retrievedIdx < high) {
        consumer.accept(leftEndpoint, i, datastore.weights.get(retrievedIdx));
        ptr = retrievedIdx + 1;
      }
      if (ptr >= high) break;
    }
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

}

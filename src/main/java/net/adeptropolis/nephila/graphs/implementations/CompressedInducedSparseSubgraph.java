package net.adeptropolis.nephila.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.nephila.graphs.EdgeConsumer;
import net.adeptropolis.nephila.graphs.EdgeOps;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;
import net.adeptropolis.nephila.graphs.implementations.arrays.InterpolationSearch;

import java.util.Arrays;

public class CompressedInducedSparseSubgraph extends Graph {

  private final CompressedSparseGraphDatastore datastore;
  private int[] vertices;

  public CompressedInducedSparseSubgraph(CompressedSparseGraphDatastore datastore, IntIterator vertices) {
    this.datastore = datastore;
    this.vertices = IntIterators.unwrap(vertices);
    Arrays.parallelSort(this.vertices, 0, size());
  }

  @Override
  public int size() {
    return vertices.length;
  }

  @Override
  public VertexIterator vertices() {
    return new SubgraphVertexIterator().reset(vertices, size());
  }

  @Override
  public void traverse(EdgeConsumer visitor) {
    EdgeOps.traverse(this, visitor);
  }

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

  @Override
  public int localVertexId(int globalVertexId) {
    return InterpolationSearch.search(vertices, globalVertexId, 0, size() - 1);
  }

  @Override
  public int globalVertexId(int localVertexId) {
    return vertices[localVertexId];
  }

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

  private void traverseByVertices(final int leftEndpoint, final EdgeConsumer visitor, final long low, final long high) {
    long ptr = low;
    long retrievedIdx;
    for (int i = 0; i < size(); i++) {
      retrievedIdx = InterpolationSearch.search(datastore.edges, vertices[i], ptr, high - 1);
      if (retrievedIdx >= 0 && retrievedIdx < high) {
        visitor.accept(leftEndpoint, i, datastore.weights.get(retrievedIdx));
        ptr = retrievedIdx + 1;
      }
      if (ptr >= high) break;
    }
  }

  @Override
  public Graph inducedSubgraph(IntIterator vertices) {
    return new CompressedInducedSparseSubgraph(datastore, vertices);
  }

}

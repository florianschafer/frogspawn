package net.adeptropolis.nephila.graph.backend;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.nephila.graph.Graph;
import net.adeptropolis.nephila.graph.backend.arrays.InterpolationSearch;

import java.util.Arrays;

public class CompressedInducedSparseSubgraph implements Graph {

  // TODO: Think about reusing vertex iterators again

  private int[] vertices; // TODO: Think about reusable arrays
  private final CompressedSparseGraphDatastore datastore;

  public CompressedInducedSparseSubgraph(CompressedSparseGraphDatastore datastore, IntIterator vertices) {
    this.datastore = datastore;
    this.vertices = IntIterators.unwrap(vertices);
    Arrays.parallelSort(this.vertices, 0, size()); // TODO: Might be sorted already
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
  public void traverse(int leftEndpoint, EdgeConsumer consumer) {

    if (size() == 0 || leftEndpoint < 0) {
      return;
    }

    int globalId = globalVertexId(leftEndpoint);

    long low = datastore.pointers[globalId];
    long high = datastore.pointers[globalId + 1];

    if (low == high) {
      return;
    }

    if (size() > high - low) {
      traverseByAdjacent(leftEndpoint, consumer, low, high);
    } else {
      traverseByVertices(leftEndpoint, consumer, low, high);
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

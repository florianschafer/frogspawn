package net.adeptropolis.nephila.graph.backend;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graph.Graph;

public class CompressedSparseGraph implements Graph {

  private final ThreadLocal<DefaultVertexIterator> vertexIterators = ThreadLocal.withInitial(DefaultVertexIterator::new);
  private final CompressedSparseGraphDatastore datastore;

  public CompressedSparseGraph(CompressedSparseGraphDatastore datastore) {
    this.datastore = datastore;
  }

  @Override
  public int size() {
    return datastore.size();
  }

  @Override
  public VertexIterator vertices() {
    return vertexIterators.get().reset();
  }

  @Override
  public void traverse(EdgeConsumer consumer) {
    new ParallelEdgeTraversal().traverse(consumer, this);
  }

  @Override
  public void traverseByGlobalId(int leftEndpoint, EdgeConsumer consumer) {

    if (size() == 0) {
      return;
    }

    long low = datastore.pointers[leftEndpoint];
    long high = datastore.pointers[leftEndpoint + 1];
    if (low == high) {
      return;
    }

    int rightEndpoint;
    for (long ptr = low; ptr < high; ptr++) {
      rightEndpoint = datastore.edges.get(ptr);
      consumer.accept(leftEndpoint, rightEndpoint, datastore.weights.get(ptr));
      if (rightEndpoint + 1 >= size()) {
        break;
      }
    }

  }

  @Override
  public int localVertexId(int globalVertexId) {
    return globalVertexId;
  }

  @Override
  public int globalVertexId(int localVertexId) {
    return localVertexId;
  }

  @Override
  public Graph inducedSubgraph(IntIterator vertices) {
    return new CompressedInducedSparseSubgraph(datastore, vertices);
  }

  public class DefaultVertexIterator implements VertexIterator {

    int idx;

    DefaultVertexIterator reset() {
      idx = 0;
      return this;
    }

    @Override
    public boolean proceed() {
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

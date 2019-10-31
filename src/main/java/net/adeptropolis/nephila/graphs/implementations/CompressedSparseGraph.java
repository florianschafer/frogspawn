package net.adeptropolis.nephila.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graphs.EdgeConsumer;
import net.adeptropolis.nephila.graphs.EdgeOps;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;

public class CompressedSparseGraph extends Graph {

  private final CompressedSparseGraphDatastore datastore;

  public CompressedSparseGraph(CompressedSparseGraphDatastore datastore) {
    this.datastore = datastore;
  }

  public static CompressedSparseGraphBuilder builder() {
    return new CompressedSparseGraphBuilder();
  }

  @Override
  public int size() {
    return datastore.size();
  }

  @Override
  public VertexIterator vertices() {
    return new DefaultVertexIterator();
  }

  @Override
  public void traverse(EdgeConsumer consumer) {
    EdgeOps.traverse(this, consumer);
  }

  @Override
  public void traverse(int v, EdgeConsumer consumer) {

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

    int idx = 0;

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

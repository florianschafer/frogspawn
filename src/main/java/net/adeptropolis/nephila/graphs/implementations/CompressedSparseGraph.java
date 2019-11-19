package net.adeptropolis.nephila.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graphs.EdgeConsumer;
import net.adeptropolis.nephila.graphs.EdgeOps;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;

/**
 * A compressed sparse graph
 *
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
   *
   * @return The number of vertices of the graph
   */

  @Override
  public int size() {
    return datastore.size();
  }

  /**
   * Return the vertex set
   *
   * @return An iterator for the vertex set
   */


  @Override
  public VertexIterator vertices() {
    return new DefaultVertexIterator();
  }

  /**
   * Traverse all edges of the graph
   * @param consumer Instance of <code>EdgeConsumer</code>
   */

  @Override
  public void traverse(EdgeConsumer consumer) {
    EdgeOps.traverse(this, consumer);
  }

  /**
   * Traverse all neighhours of a given vertex
   * @param v A (local!) vertex
   * @param consumer Instance of <code>EdgeConsumer</code>
   */

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
   * Iterator over the vertex set
   */

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

package net.adeptropolis.nephila.graphs;

import it.unimi.dsi.fastutil.ints.IntIterator;


public abstract class Graph {

  private double[] cachedWeights = null;

  public abstract int size();

  public abstract VertexIterator vertexIterator();

  /**
   * @param v        A (local!) vertex
   * @param consumer
   */

  public abstract void traverse(int v, EdgeConsumer consumer);

  public void traverse(EdgeConsumer consumer) {
    EdgeOps.traverse(this, consumer);
  }

  public abstract int localVertexId(int globalVertexId);

  public abstract int globalVertexId(int localVertexId);

  /**
   * <p></p>Compute the induced subgraph from the given global vertices</p>
   *
   * @param vertices The vertex set of the new subgraph
   * @return
   */

  public abstract Graph inducedSubgraph(IntIterator vertices); // TODO: Think about just passing int[]

  /**
   * <p></p>Compute the induced subgraph from the given local vertices</p>
   *
   * @param vertices The vertex set (as local vertex ids) of the new subgraph
   * @return
   */

  public Graph localInducedSubgraph(IntIterator vertices) {
    return inducedSubgraph(new VertexMappingIterator(vertices));
  }

  /**
   * @return The vertex weights of the graph.
   */

  public double[] weights() {
    if (cachedWeights == null) {
      cachedWeights = VertexWeights.compute(this);
    }
    return cachedWeights;
  }

  /**
   * Return the fractional weights of a subgraph relative to its supergraph
   * <p><b>Note: The subgraph must be fully contained within the supergraph!</b></p>
   *
   * @param supergraph The supergraph
   * @return The array of relative weights
   */

  public double[] relativeWeights(Graph supergraph) {
    double[] relWeights = new double[size()];
    VertexIterator it = vertexIterator();
    while (it.hasNext()) {
      int v = supergraph.localVertexId(it.globalId());
      assert v >= 0;
      double supergraphWeight = supergraph.weights()[v];
      relWeights[it.localId()] = (supergraphWeight != 0) ? weights()[it.localId()] / supergraphWeight : 0;
    }
    return relWeights;
  }

  public interface Builder {

    Builder add(int u, int v, double weight);

    Graph build();
  }

  /**
   * Iterator providing a mapping between internal -> external vertex ids
   */

  class VertexMappingIterator implements IntIterator {

    private final IntIterator localIds;

    public VertexMappingIterator(IntIterator localIds) {
      this.localIds = localIds;
    }

    @Override
    public int nextInt() {
      return globalVertexId(localIds.nextInt());
    }

    @Override
    public boolean hasNext() {
      return localIds.hasNext();
    }
  }

}

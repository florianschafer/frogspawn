package net.adeptropolis.nephila.graphs;

import it.unimi.dsi.fastutil.ints.IntIterator;

public abstract class Graph {

  public abstract int size();

  public abstract VertexIterator vertices();

  /**
   * @param v A (local!) vertex
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
   * @param vertices The vertex set of the new subgraph
   * @return
   */

  public abstract Graph inducedSubgraph(IntIterator vertices);

  /**
   * <p></p>Compute the induced subgraph from the given local vertices</p>
   * @param vertices The vertex set (as local vertex ids) of the new subgraph
   * @return
   */

  public Graph localInducedSubgraph(IntIterator vertices) {
    return inducedSubgraph(new VertexMappingIterator(vertices));
  }

  public double[] computeWeights() {
    return VertexWeights.compute(this);
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

package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;

/**
 * Ensures the consistency of a new subgraph. That is, given a parent cluster and a potential subgraph,
 * vertices from the subgraph are shifted from the subgraph to the parent cluster's remainder bucket until
 * all remaining satisfy the required minimum likelihood.
 */

//TODO: Test

public class Consistency {

  private final Graph graph;
  private final int minClusterSize;
  private final double minClusterLikelihood;

  public Consistency(Graph graph, int minClusterSize, double minClusterLikelihood) {
    this.graph = graph;
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
  }

  /**
   * Produces a subgraph with guaranteed vertex consistencies
   *
   * @param parentCluster An existing cluster that the new graph should be assigned to as subcluster
   * @param candidate     The subcluster graph candidate
   * @return Either a new subgraph with all vertices guaranteed to exhibit >= minClusterLikelihood or null if that graph would be smaller than the allowed min size
   */

  public Graph ensure(Cluster parentCluster, Graph candidate) {
    IntRBTreeSet survivors = initSurvivors(candidate);
    for (Graph subgraph = candidate; true; subgraph = candidate.inducedSubgraph(survivors.iterator())) {
      int prevSize = survivors.size();
      shiftInconsistentVertices(subgraph, parentCluster, survivors);
      if (survivors.size() < minClusterSize) {
        parentCluster.addToRemainder(survivors.iterator());
        return null;
      } else if (survivors.size() == prevSize) {
        return subgraph;
      }
    }
  }

  private void shiftInconsistentVertices(Graph subgraph, Cluster parentCluster, IntRBTreeSet survivors) {
    double[] likelihoods = subgraph.relativeWeights(graph);
    VertexIterator it = subgraph.vertexIterator();
    while (it.hasNext()) {
      if (likelihoods[it.localId()] < minClusterLikelihood) {
        parentCluster.addToRemainder(it.globalId());
        survivors.remove(it.globalId());
      }
    }
  }

  private IntRBTreeSet initSurvivors(Graph candidate) {
    IntRBTreeSet remainingVertices = new IntRBTreeSet();
    VertexIterator vertexIt = candidate.vertexIterator();
    while (vertexIt.hasNext()) {
      remainingVertices.add(vertexIt.globalId());
    }
    return remainingVertices;
  }

}

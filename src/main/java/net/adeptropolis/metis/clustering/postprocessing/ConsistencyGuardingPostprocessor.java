/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.consistency.ConsistencyGuard;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.VertexIterator;

/**
 * <p>Ensures the consistency of a cluster after postprocessing, namely after the ancestor similarity step.</p>
 * <p>
 * This is very much akin to the in-flight consistency guard, but guarantees that only vertices from the
 * cluster's remainder are shifted upwards. For this reason, this postprocessor must always be applied bottom-up.
 * </p>
 *
 * @see ConsistencyGuard
 */


class ConsistencyGuardingPostprocessor implements Postprocessor {

  private final Graph graph;
  private final int minClusterSize;
  private final double minClusterLikelihood;

  public ConsistencyGuardingPostprocessor(Graph graph, int minClusterSize, double minClusterLikelihood) {
    this.graph = graph;
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
  }

  /**
   * Ensure that all remainder vertices of a cluster fulfil the <code>minClusterLikelihood</code> criterion.
   *
   * @param cluster A cluster. Not necessarily root.
   * @return true if the cluster has been modified. Otherwise false.
   */

  @Override
  public boolean apply(Cluster cluster) {

    Cluster parent = cluster.getParent();
    if (parent == null) {
      return false;
    }

    IntRBTreeSet clusterVertices = new IntRBTreeSet(cluster.getRemainder());
    Graph clusterGraph = cluster.aggregateGraph(graph);
    IntRBTreeSet survivors = initSurvivors(clusterGraph);
    for (Graph subgraph = clusterGraph; true; subgraph = graph.inducedSubgraph(survivors.iterator())) {
      int prevSize = clusterVertices.size();
      shiftInconsistentVertices(clusterVertices, parent, survivors, subgraph);
      if (clusterVertices.size() < minClusterSize) {
        parent.addToRemainder(clusterVertices.iterator());
        parent.assimilateChild(cluster, false);
        return true;
      } else if (clusterVertices.size() == prevSize) {
        break;
      }
    }

    if (clusterVertices.size() == cluster.getRemainder().size()) {
      return false;
    }

    cluster.setRemainder(new IntArrayList(clusterVertices));
    return true;

  }

  /**
   * Shift inconsistent vertices upwards into the parent's remainder
   *
   * @param clusterVertices All original vertices of the cluster
   * @param parent          The cluster's parent
   * @param survivors       Vertices surviving the procedure (only updated here)
   * @param subgraph        The subgraph created from the survivors
   */

  private void shiftInconsistentVertices(IntRBTreeSet clusterVertices, Cluster parent, IntRBTreeSet survivors, Graph subgraph) {
    double[] likelihoods = subgraph.relativeWeights(graph);
    VertexIterator it = subgraph.vertexIterator();
    while (it.hasNext()) {
      if (likelihoods[it.localId()] < minClusterLikelihood) {
        if (clusterVertices.contains(it.globalId())) {
          parent.addToRemainder(it.globalId());
          clusterVertices.remove(it.globalId());
        }
        survivors.remove(it.globalId());
      }
    }
  }

  /**
   * Initialize the survivor set
   *
   * @param candidate Graph created from the original cluster
   * @return Initial survivor set
   */

  private IntRBTreeSet initSurvivors(Graph candidate) {
    IntRBTreeSet remainingVertices = new IntRBTreeSet();
    VertexIterator vertexIt = candidate.vertexIterator();
    while (vertexIt.hasNext()) {
      remainingVertices.add(vertexIt.globalId());
    }
    return remainingVertices;
  }

}

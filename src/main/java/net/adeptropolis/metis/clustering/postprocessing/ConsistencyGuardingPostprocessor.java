/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.consistency.ConsistencyGuard;
import net.adeptropolis.metis.clustering.consistency.ConsistencyMetric;
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

  private final ConsistencyMetric consistencyMetric;
  private final int minClusterSize;
  private final double minClusterLikelihood;

  /**
   * Constructor
   *
   * @param consistencyMetric Consistency metric to be used
   * @param minClusterSize Minimum cluster size
   * @param minClusterLikelihood Minimum cluster likelihood
   */

  public ConsistencyGuardingPostprocessor(ConsistencyMetric consistencyMetric, int minClusterSize, double minClusterLikelihood) {
    this.consistencyMetric = consistencyMetric;
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
    Graph clusterGraph = cluster.aggregateGraph();
    IntRBTreeSet survivors = new IntRBTreeSet(clusterGraph.globalVertexIdIterator());
    for (Graph subgraph = clusterGraph; true; subgraph = cluster.rootGraph().inducedSubgraph(survivors.iterator())) {
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
    double[] likelihoods = consistencyMetric.compute(parent.rootGraph(), subgraph);
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

}

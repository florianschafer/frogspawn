/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.ConsistencyGuard;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.VertexIterator;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>Ensures the consistency of a cluster during postprocessing</p>
 * <p>
 * This is very much akin to the in-flight consistency guard, but guarantees that only vertices from the
 * cluster's remainder are shifted upwards.
 * </p>
 *
 * @see ConsistencyGuard
 */


class ConsistencyGuardingPostprocessor implements Postprocessor {

  private static final Logger LOG = LoggerFactory.getLogger(ConsistencyGuardingPostprocessor.class.getSimpleName());

  private final Graph graph;
  private final int minClusterSize;
  private final double minClusterLikelihood;

  public ConsistencyGuardingPostprocessor(Graph graph, int minClusterSize, double minClusterLikelihood) {
    this.graph = graph;
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
  }

  @Override
  public boolean apply(Cluster cluster) {

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    Cluster parent = cluster.getParent();
    if (parent == null) {
      return false;
    }

    IntRBTreeSet clusterVertices = new IntRBTreeSet(cluster.getRemainder());
    Graph clusterGraph = cluster.aggregateGraph(graph);
    IntRBTreeSet survivors = initSurvivors(clusterGraph);
    for (Graph subgraph = clusterGraph; true; subgraph = inducedSubgraph(survivors)) {
      int prevSize = clusterVertices.size();
      shiftInconsistentVertices(clusterVertices, parent, survivors, subgraph);
      if (clusterVertices.size() < minClusterSize) {
        parent.addToRemainder(clusterVertices.iterator());
        parent.assimilateChild(cluster, false);
        stopWatch.stop();
        LOG.trace("Finished after {}. There were changes to the cluster structure", stopWatch);
        return true;
      } else if (clusterVertices.size() == prevSize) {
        break;
      }
    }

    if (clusterVertices.size() == cluster.getRemainder().size()) {
      stopWatch.stop();
      return false;
    } else {
      cluster.setRemainder(new IntArrayList(clusterVertices));
      stopWatch.stop();
      LOG.trace("Finished after {}. There were changes to the cluster structure", stopWatch);
      return true;
    }

  }

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

  private Graph inducedSubgraph(IntRBTreeSet survivors) {
    return graph.inducedSubgraph(survivors.iterator());
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

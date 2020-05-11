/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.affiliation.VertexAffiliationGuard;
import net.adeptropolis.frogspawn.clustering.affiliation.VertexAffiliationMetric;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessor;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.VertexIterator;

/**
 * <p>Ensures the cluster affiliation of individual vertices after postprocessing, namely after the ancestor similarity step.</p>
 * <p>
 * This is very much akin to the in-flight affiliation guard, but guarantees that only vertices from the
 * cluster's remainder are shifted upwards. For this reason, this postprocessor must always be applied bottom-up.
 * </p>
 *
 * @see VertexAffiliationGuard
 */


public class VertexAffiliationGuardingPostprocessor implements Postprocessor {

  private final VertexAffiliationMetric vertexAffiliationMetric;
  private final int minClusterSize;
  private final double minVertexAffiliation;

  /**
   * Constructor
   *
   * @param vertexAffiliationMetric Affiliation metric to be used
   * @param minClusterSize          Minimum cluster size
   * @param minVertexAffiliation    Minimum vertex affiliation wrt. to a cluster
   */

  public VertexAffiliationGuardingPostprocessor(VertexAffiliationMetric vertexAffiliationMetric, int minClusterSize, double minVertexAffiliation) {
    this.vertexAffiliationMetric = vertexAffiliationMetric;
    this.minClusterSize = minClusterSize;
    this.minVertexAffiliation = minVertexAffiliation;
  }

  /**
   * Ensure that all remainder vertices of a cluster fulfil the <code>minVertexAffiliation</code> criterion.
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
      shiftUnaffiliatedVertices(clusterVertices, parent, survivors, subgraph);
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
   * @return Generic bottom-to-top traversal mode
   */

  @Override
  public TreeTraversalMode traversalMode() {
    return TreeTraversalMode.LOCAL_BOTTOM_TO_TOP;
  }

  /**
   * This postprocessor guarantees that all vertex affinity scores are consistent.
   *
   * @return <code>false</code>
   */

  @Override
  public boolean compromisesVertexAffinity() {
    return false;
  }

  /**
   * This kind of postprocessor does not require checking for idempotency
   *
   * @return <code>false</code>
   */

  @Override
  public boolean requiresIdempotency() {
    return false;
  }

  /**
   * Shift non-affiliated vertices upwards into the parent's remainder
   *
   * @param clusterVertices All original vertices of the cluster
   * @param parent          The cluster's parent
   * @param survivors       Vertices surviving the procedure (only updated here)
   * @param subgraph        The subgraph created from the survivors
   */

  private void shiftUnaffiliatedVertices(IntRBTreeSet clusterVertices, Cluster parent, IntRBTreeSet survivors, Graph subgraph) {
    double[] affiliationScores = vertexAffiliationMetric.compute(parent.rootGraph(), subgraph);
    VertexIterator it = subgraph.vertexIterator();
    while (it.hasNext()) {
      if (affiliationScores[it.localId()] < minVertexAffiliation) {
        if (clusterVertices.contains(it.globalId())) {
          parent.addToRemainder(it.globalId());
          clusterVertices.remove(it.globalId());
        }
        survivors.remove(it.globalId());
      }
    }
  }

}

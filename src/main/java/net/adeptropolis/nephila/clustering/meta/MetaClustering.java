/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.meta;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.nephila.ClusteringSettings;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ConsistencyMetric;
import net.adeptropolis.nephila.clustering.RecursiveClustering;
import net.adeptropolis.nephila.clustering.postprocessing.Postprocessing;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;
import net.adeptropolis.nephila.graphs.WeightSortedVertexSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaClustering {

  /**
   * TODO: This approach needlessly re-computes the root CCs at every iteration (not that it'd be sooo expensive...)
   */

  private static final Logger LOG = LoggerFactory.getLogger(MetaClustering.class.getSimpleName());

  public static void run(Graph rootGraph, ConsistencyMetric metric, ClusteringSettings settings) {

    IntOpenHashSet depletedRepresentatives = new IntOpenHashSet();

    Cluster rootCluster = cluster(rootGraph, metric, settings);

    // WARNING: With the current implementation, a metacluster may be overwritten at a later stage
    // (exactly then when a vertex is used twice as collapsed id)

    // TODO: Make a MetaCluster builder and use it like below

    Int2ObjectOpenHashMap<MetaCluster> metaClusters = new Int2ObjectOpenHashMap<>();


    for (int rounds = 1; true; rounds++) { // TODO: Stopping criterion!
      ContractedLeafView view = new ContractedLeafView(rootCluster, rootGraph, depletedRepresentatives);
      rootGraph = view.getGraph();
      LOG.debug("Contracted view size is {} after {} rounds", rootGraph.size(), rounds);

      // --------------------
      VertexIterator it = rootGraph.vertexIterator();
      while (it.hasNext()) {
        WeightSortedVertexSet cluster = view.getCluster(it.globalId());
        Preconditions.checkNotNull(cluster);
        MetaCluster metaCluster = new MetaCluster();
        metaClusters.put(it.globalId(), metaCluster);
        for (int i = 0; i < cluster.size(); i++) {
          int v = cluster.getVertices()[i];
          double weight = cluster.getWeights()[i];
          if (metaClusters.containsKey(v)) {
            metaCluster.addChild(metaClusters.get(v), weight);
          } else {
            metaCluster.addVertex(v, weight);
          }
        }
      }
      // --------------------

      rootCluster = cluster(rootGraph, metric, settings);
    }
  }

  private static Cluster cluster(Graph graph, ConsistencyMetric metric, ClusteringSettings settings) {
    RecursiveClustering recursiveClustering = new RecursiveClustering(graph, metric, settings);
    Cluster root = recursiveClustering.run();
    return new Postprocessing(root, graph, settings).apply();
  }

}

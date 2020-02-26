/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila;

import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ConsistencyMetric;
import net.adeptropolis.nephila.clustering.RecursiveClusterSieve;
import net.adeptropolis.nephila.clustering.postprocessing.Postprocessing;
import net.adeptropolis.nephila.graphs.Graph;

public class Clustering {

  private Clustering() {

  }

  public static Cluster run(Graph graph, ConsistencyMetric metric, ClusteringSettings settings) {
    RecursiveClusterSieve recursiveClusterSieve = new RecursiveClusterSieve(graph, metric, settings);
    Cluster root = recursiveClusterSieve.run();
    return new Postprocessing(root, graph, settings).apply();
  }
}
/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.clustering.postprocessing.Postprocessing;
import net.adeptropolis.nephila.graphs.Graph;

public class Clustering {

  public static Cluster run(Graph graph, ClusteringSettings settings) {
    RecursiveClustering recursiveClustering = new RecursiveClustering(graph, settings);
    Cluster root = recursiveClustering.run();
    Cluster postprocessed = new Postprocessing(root, graph, settings).apply();
    return postprocessed;
  }
}
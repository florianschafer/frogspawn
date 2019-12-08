/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.graphs.Graph;

class ConsistencyGuardingPostprocessor implements Postprocessor {

  private final CascadingPostprocessorWrapper postprocessor;

  public ConsistencyGuardingPostprocessor(double minClusterLikelihood, int minClusterSize, Graph graph) {
    UnsafeConsistencyGuardingPostprocessor unsafePostprocessor = new UnsafeConsistencyGuardingPostprocessor(graph, minClusterSize, minClusterLikelihood);
    postprocessor = new CascadingPostprocessorWrapper(unsafePostprocessor);
  }

  @Override
  public boolean apply(Cluster cluster) {
    return postprocessor.apply(cluster);
  }

}

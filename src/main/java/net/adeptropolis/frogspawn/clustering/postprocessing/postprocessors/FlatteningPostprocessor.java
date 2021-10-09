/*
 * Copyright (c) Florian Schaefer 2021.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessor;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;

public class FlatteningPostprocessor implements Postprocessor {

  @Override
  public PostprocessingState apply(Cluster cluster) {
    if (cluster.depth() > 1) {
      cluster.getParent().assimilateChild(cluster, true);
      return PostprocessingState.CHANGED;
    }
    return PostprocessingState.UNCHANGED;
  }

  @Override
  public TreeTraversalMode traversalMode() {
    return TreeTraversalMode.LOCAL_BOTTOM_TO_TOP;
  }

  @Override
  public boolean compromisesVertexAffinity() {
    return false;
  }

  @Override
  public boolean requiresIdempotency() {
    return false;
  }
}

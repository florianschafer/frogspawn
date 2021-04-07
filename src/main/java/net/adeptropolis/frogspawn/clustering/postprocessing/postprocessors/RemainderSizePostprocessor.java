/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessor;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;

/**
 * Postprocessor that ensures all cluster remainders obey the minimum cluster size criterion
 */

public class RemainderSizePostprocessor implements Postprocessor {

  private final int minClusterSize;

  public RemainderSizePostprocessor(int minClusterSize) {
    this.minClusterSize = minClusterSize;
  }

  /**
   * Check whether a cluster's remainder is below the minimum cluster size.
   * If so, assign its remainder and children to the parent and remove it.
   *
   * @param cluster A cluster. Not necessarily root.
   * @return State after applying this postprocessor
   */

  @Override
  public PostprocessingState apply(Cluster cluster) {
    if (cluster.getRemainder().size() < minClusterSize && cluster.getParent() != null) {
      cluster.getParent().assimilateChild(cluster, true);
      return PostprocessingState.CHANGED;
    }
    return PostprocessingState.UNCHANGED;
  }

  /**
   * @return Generic bottom-to-top traversal mode
   */

  @Override
  public TreeTraversalMode traversalMode() {
    return TreeTraversalMode.LOCAL_BOTTOM_TO_TOP;
  }

  /**
   * This postprocessor cannot compromise any vertex affinity scores
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

}

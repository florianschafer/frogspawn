/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing.postprocessors;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.postprocessing.Postprocessor;
import net.adeptropolis.metis.clustering.postprocessing.TreeTraversalMode;

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
   * @return true if the underlying cluster has been modified, else false
   */

  @Override
  public boolean apply(Cluster cluster) {
    if (cluster.getRemainder().size() < minClusterSize && cluster.getParent() != null) {
      cluster.getParent().assimilateChild(cluster, true);
      return true;
    }
    return false;
  }

  /**
   * @return Generic bottom-to-top traversal mode
   */

  @Override
  public TreeTraversalMode traversalMode() {
    return TreeTraversalMode.LOCAL_BOTTOM_TO_TOP;
  }

}

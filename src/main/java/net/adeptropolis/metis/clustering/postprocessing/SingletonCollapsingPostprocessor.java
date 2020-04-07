/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;

/**
 * Singleton collapsing postprocessor
 */

class SingletonCollapsingPostprocessor implements Postprocessor {

  /**
   * Check whether a cluster is a singleton. If so, assign its remainder and children to the parent and remove it.
   *
   * @param cluster A cluster. Not necessarily root.
   * @return true if the underlying cluster has been modified, else false
   */

  @Override
  public boolean apply(Cluster cluster) {
    Cluster parent = cluster.getParent();
    if (parent != null && parent.getChildren().size() == 1) {
      parent.assimilateChild(cluster, true);
      return true;
    } else {
      return false;
    }
  }
}

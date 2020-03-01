/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;

class SingletonCollapsingPostprocessor implements Postprocessor {

  @Override
  public boolean apply(Cluster cluster) {
    Cluster parent = cluster.getParent();
    if (parent != null && parent.getChildren().size() == 1) {
      parent.assimilateChild(cluster);
      return true;
    } else {
      return false;
    }
  }
}

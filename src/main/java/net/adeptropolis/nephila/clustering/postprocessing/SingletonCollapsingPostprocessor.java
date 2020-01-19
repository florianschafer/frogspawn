/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;

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

/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ClusteringSettings;

public class SingletonCollapsingPostprocessor implements Postprocessor {

  private final boolean collapseSingletons;

  public SingletonCollapsingPostprocessor(ClusteringSettings settings) {
    this.collapseSingletons = settings.getCollapseSingletons();
  }

  @Override
  public boolean apply(Cluster cluster) {
    Cluster parent = cluster.getParent();
    if (collapseSingletons && parent != null && parent.getChildren().size() == 1) {
      parent.addToRemainder(cluster.getRemainder().iterator());
      parent.getChildren().remove(cluster);
      parent.addChildren(cluster.getChildren());
      for (Cluster child : cluster.getChildren()) {
        child.setParent(parent);
      }
      return true;
    } else {
      return false;
    }
  }
}

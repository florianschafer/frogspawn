/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessor;
import net.adeptropolis.frogspawn.clustering.postprocessing.TreeTraversalMode;

/**
 * Attempts to restructure the cluster tree s.t. every cluster yield a certain minimum number of children
 */

public class DescendantCollapsingPostprocessor implements Postprocessor {

  private final int minChildren;

  /**
   * Constructor
   *
   * @param minChildren Minimum number of child clusters
   */

  public DescendantCollapsingPostprocessor(int minChildren) {
    this.minChildren = minChildren;
  }

  /**
   * Apply the postprocessor locally. Attaches the cluster to its grandparent unless it already
   * yields the min required number of children.
   *
   * @param cluster A cluster. Not necessarily root.
   * @return true if the underlying cluster has been modified, else false
   */

  @Override
  public boolean apply(Cluster cluster) {

    if (cluster.getParent() == null || cluster.getParent().getParent() == null) {
      return false;
    }

    Cluster grandparent = cluster.getParent().getParent();

    if (grandparent.getChildren().size() < minChildren) {
      grandparent.annex(cluster);
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

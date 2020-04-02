/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;

/**
 * Cluster hierarchy postprocessor
 */

@FunctionalInterface
public
interface Postprocessor {

  /**
   * Impose a particular structure upon the current cluster or change its place in the cluster tree
   *
   * @param cluster A cluster. Not necessarily root.
   * @return true if the underlying cluster has been modified, else false
   */

  boolean apply(Cluster cluster);

}

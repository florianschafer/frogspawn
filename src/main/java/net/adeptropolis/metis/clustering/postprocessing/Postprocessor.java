/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import net.adeptropolis.metis.clustering.Cluster;

@FunctionalInterface
interface Postprocessor {

  /**
   * Impose a particular structure upon the current (local) cluster
   *
   * @param cluster A cluster. Not necessarily root.
   * @return true of the underlying cluster has been modified, else false
   */

  boolean apply(Cluster cluster);

}

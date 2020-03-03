/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

import net.adeptropolis.metis.clustering.Cluster;

/**
 * Cluster digest factory
 *
 * @see Digest
 */

@FunctionalInterface
public interface ClusterDigester {

  /**
   * Create a cluster digest
   *
   * @param cluster Cluster from which to create a new digest
   * @return New digest
   */

  Digest create(Cluster cluster);

}

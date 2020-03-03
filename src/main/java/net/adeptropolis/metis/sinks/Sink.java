/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.sinks;

import net.adeptropolis.metis.clustering.Cluster;

/**
 * <p>A cluster hierarchy sink. Typically used in combination with digests.</p>
 *
 * @see net.adeptropolis.metis.digest.Digest
 */

@FunctionalInterface
public interface Sink {

  /**
   * Fully consume a cluster tree, creating the output in the process
   *
   * @param root The root note of the cluster tree
   */

  void consume(Cluster root);

}

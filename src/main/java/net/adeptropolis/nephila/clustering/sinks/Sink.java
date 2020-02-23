/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.clustering.sinks;

import net.adeptropolis.nephila.clustering.Cluster;

@FunctionalInterface
public interface Sink {

  void consume(Cluster root);

}

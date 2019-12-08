/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.sinks;

import net.adeptropolis.nephila.clustering.Cluster;

@FunctionalInterface
public interface Sink {

  void consume(Cluster root);

}

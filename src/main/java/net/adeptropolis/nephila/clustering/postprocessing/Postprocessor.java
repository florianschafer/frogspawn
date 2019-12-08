/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;

@FunctionalInterface
public interface Postprocessor {

  /**
   * Impose a particular structure upon the current (local) cluster
   *
   * @param cluster A cluster. Not necessarily root.
   * @return true of the underlying cluster has been modified, else false
   */

  boolean apply(Cluster cluster);

}

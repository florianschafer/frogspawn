package net.adeptropolis.nephila.clustering.shapers;

import net.adeptropolis.nephila.clustering.Cluster;

@FunctionalInterface
public interface Shaper {

  /**
   * Impose a particular structure upon the current protocluster
   *
   * @param cluster A cluster
   * @return true of the underlying cluster has been modified, else false
   */

  boolean imposeStructure(Cluster cluster);

}

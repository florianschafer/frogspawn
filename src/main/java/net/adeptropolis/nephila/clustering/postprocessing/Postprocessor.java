package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;

@FunctionalInterface
public interface Postprocessor {

  /**
   * Impose a particular structure upon the current protocluster
   *
   * @param cluster A cluster
   * @return true of the underlying cluster has been modified, else false
   */

  boolean apply(Cluster cluster);

}

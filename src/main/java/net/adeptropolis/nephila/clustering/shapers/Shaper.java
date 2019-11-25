package net.adeptropolis.nephila.clustering.shapers;

import net.adeptropolis.nephila.clustering.Protocluster;

@FunctionalInterface
public interface Shaper {

  /**
   * Impose a particular structure upon the current protocluster
   *
   * @param protocluster A protocluster
   * @return true of the underlying cluster has been modified, else false
   */

  boolean imposeStructure(Protocluster protocluster);

}

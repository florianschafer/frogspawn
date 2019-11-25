package net.adeptropolis.nephila.clustering.shapers;

import net.adeptropolis.nephila.clustering.Protocluster;

@FunctionalInterface
public interface Shaper {

  Protocluster imposeStructure(Protocluster protocluster);

}

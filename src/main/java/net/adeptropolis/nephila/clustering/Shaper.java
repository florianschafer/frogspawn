package net.adeptropolis.nephila.clustering;

@FunctionalInterface
public interface Shaper {

  Protocluster imposeStructure(Protocluster protocluster);

}

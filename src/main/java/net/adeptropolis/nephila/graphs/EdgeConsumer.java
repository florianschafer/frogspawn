package net.adeptropolis.nephila.graphs;

@FunctionalInterface
public interface
EdgeConsumer {

  void accept(int u, int v, double weight);

}

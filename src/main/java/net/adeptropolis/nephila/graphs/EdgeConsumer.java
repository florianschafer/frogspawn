package net.adeptropolis.nephila.graphs;

public interface EdgeConsumer {

  void accept(int u, int v, double weight);

  void reset();

}

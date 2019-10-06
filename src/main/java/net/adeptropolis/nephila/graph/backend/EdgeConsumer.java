package net.adeptropolis.nephila.graph.backend;

public interface EdgeConsumer {

  void accept(int u, int v, double weight);

  void reset();

}

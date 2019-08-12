package net.adeptropolis.nephila.graph.backend;

public interface EdgeVisitor {

  void visit(int u, int v, double weight);

  void reset();

}

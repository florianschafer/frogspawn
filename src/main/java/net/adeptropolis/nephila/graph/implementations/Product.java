package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.DoubleBuffer;

public interface Product {

  double innerProduct(int i, int j, double aij, double vj);

  void createResultEntry(int row, double value, DoubleBuffer arg);

}

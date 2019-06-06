package net.adeptropolis.nephila.graph.implementations;

public interface Product {

  double innerProduct(int i, int j, double aij, double vj);

  void createResultEntry(int row, double value, double[] arg);

}

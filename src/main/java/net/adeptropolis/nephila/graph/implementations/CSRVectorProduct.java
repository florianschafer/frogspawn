package net.adeptropolis.nephila.graph.implementations;

import java.util.Arrays;

public class CSRVectorProduct implements EntryVisitor {

  private final CSRStorage.View view;
  private double[] argument;
  private final double[] result;

  public CSRVectorProduct(CSRStorage.View view) {
    this.view = view;
    this.result = new double[view.maxSize()]; // Preallocate a single, reusable instance
  }

  public synchronized double[] multiply(double[] argument) {
    this.argument = argument;
    view.traverse(this);
    return result;
  }

  @Override
  public void visit(int rowIdx, int colIdx, double value) {
    // TODO: Do something against range checks on the LHS (extend visitor interface to rows)
    result[rowIdx] += value * argument[colIdx];
  }

  @Override
  public void reset() {
    Arrays.fill(result, 0, view.indicesSize, 0);
  }

}

package net.adeptropolis.nephila.graph.implementations;

import java.util.Arrays;

public class RowWeights implements EntryVisitor {

//  todo: cleanup or make CSR Traversal reusable

  private final CSRStorage.View view;
  private final CSRViewTraversal CSRViewTraversal;
  private final double[] weights;

  public RowWeights(CSRStorage.View view) {
    this.view = view;
    this.CSRViewTraversal = new CSRViewTraversal(this.view);
    this.weights = new double[view.maxSize()];
  }

  public void update() {
    CSRViewTraversal.traverse(this);
  }

  public double[] get() {
    return weights;
  }

  @Override
  public void visit(final int rowIdx, final int colIdx, final double value) {
    weights[rowIdx] += value;
  }

  @Override
  public void reset() {
    Arrays.fill(weights, 0, view.indicesSize, 0);
  }

}

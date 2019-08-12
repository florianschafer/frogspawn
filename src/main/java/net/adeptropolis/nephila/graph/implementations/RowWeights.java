package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.backend.EdgeVisitor;
import net.adeptropolis.nephila.graph.backend.View;

import java.util.Arrays;

public class RowWeights implements EdgeVisitor {

  private final View view;
  private final double[] weights;

  public RowWeights(View view) {
    this.view = view;
    this.weights = new double[view.size()];
    view.traverse(this);
  }

  public double[] get() {
    return weights;
  }

  @Override
  public void visit(final int u, final int v, final double weight) {
    weights[u] += weight;
  }

  @Override
  public void reset() {
    Arrays.fill(weights, 0, view.size(), 0);
  }

}

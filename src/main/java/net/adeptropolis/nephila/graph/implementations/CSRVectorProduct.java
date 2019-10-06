package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.backend.EdgeConsumer;
import net.adeptropolis.nephila.graph.backend.View;

import java.util.Arrays;

public class CSRVectorProduct implements EdgeConsumer {

  private final View view;
  private final double[] result;
  private double[] argument;

  public CSRVectorProduct(View view) {
    this.view = view;
    this.result = new double[view.size()]; // Preallocate a single, reusable instance
  }

  public synchronized double[] multiply(double[] argument) {
    this.argument = argument;
    view.traverse(this);
    return result;
  }

  @Override
  public void accept(int u, int v, double weight) {
    // TODO: Do something against range checks on the LHS (extend visitor interface to rows)
    result[u] += weight * argument[v];
  }

  @Override
  public void reset() {
    Arrays.fill(result, 0, view.size(), 0);
  }

}

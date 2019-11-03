package net.adeptropolis.nephila.graphs.operators;

import com.google.common.base.Preconditions;
import net.adeptropolis.nephila.graphs.EdgeConsumer;
import net.adeptropolis.nephila.graphs.Graph;

import java.util.Arrays;

public class CanonicalLinearOperator implements LinearGraphOperator, EdgeConsumer {

  private final Graph graph;
  private final double[] result;
  private double[] argument;

  public CanonicalLinearOperator(Graph graph) {
    this.graph = graph;
    this.result = new double[graph.size()];
  }

  public synchronized double[] apply(double[] argument) {
    Arrays.fill(result, 0);
    Preconditions.checkArgument(argument.length == graph.size(), "Argument length mismatch");
    this.argument = argument;
    graph.traverse(this);
    return result;
  }

  @Override
  public void accept(int u, int v, double weight) {
    result[u] += weight * argument[v];
  }

}

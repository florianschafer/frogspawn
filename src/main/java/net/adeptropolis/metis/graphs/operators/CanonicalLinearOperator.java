/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.operators;

import com.google.common.base.Preconditions;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.traversal.EdgeConsumer;

import java.util.Arrays;

/**
 * <p>The canonical linear graph operator</p>
 * <p>Multiplies the adjacency matrix with an argument from the vertex space.</p>
 */

public class CanonicalLinearOperator implements LinearGraphOperator, EdgeConsumer {

  private final Graph graph;
  private final double[] result;
  private double[] argument;

  /**
   * Constructor for a new operator instance
   *
   * @param graph The underlying graph
   */

  public CanonicalLinearOperator(Graph graph) {
    this.graph = graph;
    this.result = new double[graph.order()];
  }

  /**
   * @param argument A vertex-indexed vector
   * @return The product Av, with A being the adjacency matrix of the graph and v the argument.
   */

  public double[] apply(double[] argument) {
    Arrays.fill(result, 0);
    Preconditions.checkArgument(argument.length == graph.order(), "Argument length mismatch");
    this.argument = argument;
    graph.traverseParallel(this);
    return result;
  }

  @Override
  public int size() {
    return graph.order();
  }

  /**
   * Internal: Callback for graph traversal
   *
   * @param u      Left vertex
   * @param v      Right vertex
   * @param weight Edge weight
   */

  @Override
  public void accept(int u, int v, double weight) {
    result[u] += weight * argument[v];
  }

}

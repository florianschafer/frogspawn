/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.matrices;

import com.google.common.base.Preconditions;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.traversal.EdgeConsumer;

import java.util.Arrays;

/**
 * <p>Adjacency matrix of a graph</p>
 */

public class AdjacencyMatrix implements SquareMatrix, EdgeConsumer {

  private final Graph graph;
  private final double[] result;
  private double[] argument;

  /**
   * Constructor
   *
   * @param graph Graph
   */

  public AdjacencyMatrix(Graph graph) {
    this.graph = graph;
    this.result = new double[graph.order()];
  }

  /**
   * @param argument Vertex-indexed vector
   * @return The product Av, with A being this instance and v the argument.
   */

  public double[] multiply(double[] argument) {
    Arrays.fill(result, 0);
    Preconditions.checkArgument(argument.length == graph.order(), "Argument length mismatch");
    this.argument = argument;
    graph.traverseParallel(this);
    return result;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int size() {
    return graph.order();
  }

  /**
   * Internal: Callback for graph traversal
   *
   * @param i row
   * @param j column
   * @param v value
   */

  @Override
  public void accept(int i, int j, double v) {
    result[i] += v * argument[j];
  }

}

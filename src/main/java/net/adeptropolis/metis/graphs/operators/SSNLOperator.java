/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.operators;

import com.google.common.annotations.VisibleForTesting;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.helpers.Vectors;

/**
 * <p>A spectrally shifted normalized laplacian operator</p>
 *
 * <p>Provides a spectrally shifted version of the normalized laplacian of an undirected, connected graph</p>
 * <p>The shifting is performed in such a way that the eigenvector originally belonging to the second-smallest eigenvalue of the
 * normalized laplacian is now assigned to the largest eigenvalue of this new operator</p>
 *
 * <p><b>Important:</b> This operator has two strict requirements:
 * <ul>
 *   <li>The underlying graph is required to be connected, undirected and have non-negative edge weights</li>
 *   <li>Any argument passed to this operator must be normalized, i.e. ||x||<sub>2</sub> == 1</li>
 * </ul>
 * </p>
 * <p>This implementation does not validate any of those requirements. Any result stemming from ignoring one of the above
 * is simply undefined.</p>
 * </p>
 */

public class SSNLOperator implements LinearGraphOperator {

  private final Graph graph;
  private final double[] weights;
  private final double[] argument;
  private final CanonicalLinearOperator linOp;
  private final double[] v0;

  /**
   * <p>Creates a new SSNLOperator instance.</p>
   *
   * @param graph The underlying graph. Must be connected, undirected and have non-negative edge weights
   */

  public SSNLOperator(Graph graph) {
    this.graph = graph;
    this.weights = graph.weights();
    this.v0 = computeV0(graph);
    this.argument = new double[graph.order()];
    this.linOp = new CanonicalLinearOperator(graph);
  }

  /**
   * Compute the eigenvector associated with the smallest eigenvalue of the regular normalized laplacian of the graph.
   *
   * @param graph A graph
   * @return The desired eigenvector
   */

  @VisibleForTesting
  static double[] computeV0(Graph graph) {
    double[] v0 = new double[graph.order()];
    double norm = Math.sqrt(graph.totalWeight());
    for (int i = 0; i < graph.order(); i++) {
      v0[i] = Math.sqrt(graph.weights()[i]) / norm;
    }
    return v0;
  }

  /**
   * Apply the spectrally shifted normalized laplacian
   *
   * @param x A normalized vertex-indexed vector
   * @return The result of applying the spectrally shifted normalized laplacian to the given argument x.
   */

  public double[] apply(double[] x) {
    double mu = 2 * Vectors.scalarProduct(v0, x);
    for (int i = 0; i < graph.order(); i++) {
      argument[i] = x[i] / Math.sqrt(weights[i]);
    }
    double[] result = linOp.apply(argument);
    for (int i = 0; i < graph.order(); i++) {
      result[i] = x[i] + result[i] / Math.sqrt(weights[i]) - mu * v0[i];
    }
    return result;
  }

  /**
   * @return Size of the operator
   */

  @Override
  public int size() {
    return graph.order();
  }

}
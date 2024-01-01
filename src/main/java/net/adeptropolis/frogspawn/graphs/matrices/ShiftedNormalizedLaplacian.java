/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.matrices;

import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.helpers.Vectors;

/**
 * <p>Provides a spectrally shifted version of the normalized laplacian of an undirected, connected graph</p>
 * <p>The shifting is performed in such a way that the eigenvector originally belonging to the second-smallest
 * eigenvalue of the normalized laplacian is now assigned to the largest eigenvalue of this matrix.
 * All computations are performed on the fly.</p>
 */

public class ShiftedNormalizedLaplacian implements SquareMatrix {

  private final Graph graph;
  private final double[] invSqrtWeights;
  private final double[] argument;
  private final AdjacencyMatrix adjacencyMatrix;
  private final double[] v0;

  /**
   * Constructor
   *
   * @param graph The underlying graph. Must be connected, undirected and have non-negative edge weights
   */

  public ShiftedNormalizedLaplacian(Graph graph) {
    this.graph = graph;
    this.argument = new double[graph.order()];
    this.adjacencyMatrix = new AdjacencyMatrix(graph);
    double[] sqrtWeights = computeSqrtWeights(graph);
    this.v0 = computeV0(graph, sqrtWeights);
    this.invSqrtWeights = invertEntries(sqrtWeights);
  }

  /**
   * Compute the square roots of a graph's vertex weighs
   *
   * @param graph Graph
   * @return Square root vertex weights vector
   */

  static double[] computeSqrtWeights(Graph graph) {
    double[] weights = graph.weights();
    double[] sqrtWeights = new double[weights.length];
    for (int i = 0; i < weights.length; i++) {
      sqrtWeights[i] = Math.sqrt(weights[i]);
    }
    return sqrtWeights;
  }

  /**
   * In-place inversion of all elements of a vector (assuming non-zeros)
   *
   * @param vector Vector whose elements should be inverted
   * @return Original vector object with its entries having been inverted
   */

  private static double[] invertEntries(double[] vector) {
    for (int i = 0; i < vector.length; i++) {
      if (vector[i] == 0d) {
        throw new IllegalArgumentException("Unable to invert zero-valued element");
      }
      vector[i] = 1.0 / vector[i];
    }
    return vector;
  }

  /**
   * Compute the eigenvector associated with the smallest eigenvalue of the regular normalized laplacian of the graph.
   * Note that this eigenvector is known a priori and can be directly computed from the original graph's weights.
   *
   * @param graph       A graph
   * @param sqrtWeights Square roots of vertex weights
   * @return The desired eigenvector
   */

  static double[] computeV0(Graph graph, double[] sqrtWeights) {
    double[] v0 = new double[graph.order()];
    double norm = Math.sqrt(graph.totalWeight());
    for (int i = 0; i < graph.order(); i++) {
      v0[i] = sqrtWeights[i] / norm;
    }
    return v0;
  }

  /**
   * {@inheritDoc}
   */

  public double[] multiply(double[] x) {
    for (int i = 0; i < graph.order(); i++) {
      argument[i] = x[i] * invSqrtWeights[i];
    }
    double[] result = adjacencyMatrix.multiply(argument);
    double mu = 2 * Vectors.scalarProduct(v0, x);
    for (int i = 0; i < graph.order(); i++) {
      result[i] = x[i] + result[i] * invSqrtWeights[i] - mu * v0[i];
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public int size() {
    return graph.order();
  }

}
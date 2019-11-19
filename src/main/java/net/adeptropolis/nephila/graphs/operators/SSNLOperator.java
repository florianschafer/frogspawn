package net.adeptropolis.nephila.graphs.operators;

import com.google.common.annotations.VisibleForTesting;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.helpers.Vectors;

/**
 * <p>A spectrally shifted normalized laplacian operator</p>
 *
 * <p>Provides a spectrally shifted version of the normalized laplacian of an undirected, connected, bipartite graph</p>
 * <p>The shifting is performed in such a way that the eigenvector originally belonging to the second-smallest eigenvalue of the
 * normalized laplacian is now assigned to the largest eigenvalue of this new operator</p>
 *
 * <p><b>Important:</b> This operator has two strict requirements:
 * <ul>
 *   <li>The underlying graph is required to be strictly bipartite, connected, undirected and have non-negative edge weights</li>
 *   <li>Any argument passed to this operator must be L2-normalized, i.e. ||x||<sub>2</sub> == 1</li>
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
  private final double[] v0; // The first eigenvector of the original Laplacian (i.e. with corr. eigenval 0). It is a priori known to be a function of the vertex weights. BEWARE! As an eigenvector it is supposed to be NORMALIZED!

  /**
   * <p>Creates a new SSNLOperator instance.</p>
   *
   * @param graph The underlying graph. Must be strictly bipartite, connected, undirected and have non-negative edge weights
   */

  public SSNLOperator(Graph graph) {
    this.graph = graph;
    this.weights = graph.computeWeights();
    this.v0 = computeV0(weights);
    this.argument = new double[graph.size()];
    this.linOp = new CanonicalLinearOperator(graph);
  }

  /**
   * Compute the eigenvector associated with the smallest eigenvalue of the regular normalized laplacian of the graph.
   *
   * @param weights Array of vertex weights of the graph
   * @return The desired eigenvector
   */

  @VisibleForTesting
  static double[] computeV0(double[] weights) {
    double[] v0 = new double[weights.length];
    double norm = Math.sqrt(Vectors.L1Norm(weights));
    for (int i = 0; i < weights.length; i++) v0[i] = Math.sqrt(weights[i]) / norm;
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
    for (int i = 0; i < graph.size(); i++) argument[i] = x[i] / Math.sqrt(weights[i]);
    double[] result = linOp.apply(argument);
    for (int i = 0; i < graph.size(); i++) {
      result[i] = x[i] + result[i] / Math.sqrt(weights[i]) - mu * v0[i];
    }
    return result;
  }

  /**
   * Return size
   *
   * @return Size of the operator
   */

  @Override
  public int size() {
    return graph.size();

  }

}
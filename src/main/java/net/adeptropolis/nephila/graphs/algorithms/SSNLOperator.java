package net.adeptropolis.nephila.graphs.algorithms;

import com.google.common.annotations.VisibleForTesting;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.helpers.Vectors;

/**
 * <p>A spectrally shifted normalized laplacian operator</p>
 *
 * <p>Provides a spectrally shifted version for the normalized laplacian of a connected component of an undirected, bipartite graph</p>
 * <p>The shifting is performed in such a way that the eigenvector originally belonging to the second-smallest eigenvalue of the
 * normalized laplacian is now assigned to the largest eigenvalue of this new operator</p>
 *
 * <p><b>Important:</b> This operator has two strict requirements:
 * <ul>
 *   <li>The underlying graph is required to be strictly bipartite, undirected and have non-negative edge weights</li>
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

  public SSNLOperator(Graph graph) {
    this.graph = graph;
    this.weights = graph.computeWeights();
    this.v0 = computeV0(weights);
    this.argument = new double[graph.size()];
    this.linOp = new CanonicalLinearOperator(graph);
  }

  // NOTE: The argument vector needs ||x|| == 1 !!!
  public double[] apply(double[] x) {
    double mu = 2 * Vectors.scalarProduct(v0, x);
    for (int i = 0; i < graph.size(); i++) argument[i] = x[i] / Math.sqrt(weights[i]);
    double[] result = linOp.apply(argument);
    for (int i = 0; i < graph.size(); i++){
      result[i] = x[i] + result[i] / Math.sqrt(weights[i]) - mu * v0[i];
    }
    return result;
  }

  @VisibleForTesting
  static double[] computeV0(double[] weights) {
    double[] v0 = new double[weights.length];
    double norm = Math.sqrt(Vectors.L1Norm(weights));
    for (int i = 0; i < weights.length; i++) v0[i] = Math.sqrt(weights[i]) / norm;
    return v0;
  }

}
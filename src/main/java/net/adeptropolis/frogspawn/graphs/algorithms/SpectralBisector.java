/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms;

import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.algorithms.power_iteration.*;
import net.adeptropolis.frogspawn.graphs.matrices.ShiftedNormalizedLaplacian;

import java.util.function.Consumer;

public class SpectralBisector {

  /**
   * <p>Spectral bisector for biparite graphs</p>
   * <p>The original graph will be split into two partitions such that the normalized cut is minimized</p>
   */

  private final ClusteringSettings settings;
  private final RandomInitialVectorsSource ivSource;

  public SpectralBisector(ClusteringSettings settings, RandomInitialVectorsSource ivSource) {
    this.settings = settings;
    this.ivSource = ivSource;
  }

  /**
   * Create a subgraph based on the signs of the eigenvector for the second-smallest eigenvalue of the Normalized Laplacian
   *
   * @param graph        The input graph
   * @param v2           The approximate second-smallest eigenvector of the Normalized Laplacian of the Graph
   * @param consumer     A consumer for the resulting partition
   * @param selectSignum Select either all non-negative (selectSignum ≥ 0) or negative (selectSignum &lt; 0) entries from the eigenvector.
   */

  private static void yieldSubgraph(Graph graph, double[] v2, Consumer<Graph> consumer, int selectSignum) {
    SignumSelectingIndexIterator vertices = new SignumSelectingIndexIterator(v2, selectSignum, null);
    consumer.accept(graph.localSubgraph(vertices));
  }

  /**
   * Bisects the given graph into two partitons
   *
   * @param graph         The input graph
   * @param consumer      A consumer for the resulting partitions
   * @throws PowerIteration.MaxIterationsExceededException if the number of iterations has been exceeded
   */

  public void bisect(Graph graph, Consumer<Graph> consumer) throws PowerIterationException {
    PartialConvergenceCriterion convergenceCriterion = createConvergenceCriterion(graph);
    ShiftedNormalizedLaplacian ssnl = new ShiftedNormalizedLaplacian(graph);
    double[] iv = ivSource.generate(graph.order());
    double[] v2 = PowerIteration.apply(ssnl, convergenceCriterion, iv, settings.getMaxIterations(), false);
    convergenceCriterion.postprocess(v2);
    yieldSubgraph(graph, v2, consumer, 1);
    yieldSubgraph(graph, v2, consumer, -1);
  }

  /**
   * Return a new (partial) convergence criterion instance for a given graph.
   * Currently, this always returns an instance of <code>ConstantSigTrailConvergence</code>
   *
   * @param graph A graph
   * @return A new <code>PartialConvergenceCriterion</code> instance
   */

  private PartialConvergenceCriterion createConvergenceCriterion(Graph graph) {
    return new ConstantSigTrailConvergence(graph, settings.getTrailSize(), settings.getConvergenceThreshold());
  }

}

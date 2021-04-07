/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms.power_iteration;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.algorithms.SignumSelectingIndexIterator;

import java.util.Arrays;

/**
 * <p>Power iteration convergence criterion based on the sign trail of multiple iterations.</p>
 *
 * <p>More precisely, over multiple iterations, the eigenvector entry signs are being stored in a sliding window fashion.
 * Once a certain fraction of entries remains constant over the full window size, the iteration process will be terminated.
 * During postprocessing, the remainder is then classified into <code>{-1, 1}</code> based on the relative weight of the graph
 * vertices associated with the eigenvector indices with respect to the bipartitioning established by those entries that
 * have already fully converged </p>
 *
 * <p>This is a much more relaxed and remarkably faster alternative to more established convergence criteria. However, it
 * may not be applicable outside of this particular domain where only the signs of the desired eigenvector entries are of
 * any interest (not to mention having a concept of vertex weights in the first place)</p>
 *
 * @see PowerIteration
 * @see net.adeptropolis.frogspawn.helpers.Vectors#normalize2Sig(double[])
 */

public class ConstantSigTrailConvergence implements PartialConvergenceCriterion {

  private final Graph graph;
  private final int trailSize;
  private final int threshold;
  private final byte[] prevSig;
  private final int[] constSigTrail;

  /**
   * Constructor
   *
   * @param graph                The graph whose spectrally shifted normalized Laplacian eigenvector is to be computed
   * @param trailSize            Size of the sliding window
   * @param convergenceThreshold Fraction of entries that is required to be constant over the full window size
   */

  public ConstantSigTrailConvergence(Graph graph, int trailSize, double convergenceThreshold) {
    this.graph = graph;
    this.prevSig = new byte[graph.order()];
    this.constSigTrail = new int[graph.order()];
    this.trailSize = trailSize;
    this.threshold = (int) (convergenceThreshold * graph.order());
    Arrays.fill(prevSig, (byte) -2);
    Arrays.fill(constSigTrail, 0);
  }

  /**
   * <p>Assess whether the power iteration has (partially) converged</p>
   *
   * @param previous   Result of the previous iteration
   * @param current    Result of the current iteration
   * @param iterations Number of iterations
   * @return True if and only if the convergence criterion is satisfied.
   */

  @Override
  public boolean satisfied(double[] previous, double[] current, int iterations) {
    int converged = 0;
    for (int v = 0; v < graph.order(); v++) {
      byte sig = (byte) Math.signum(current[v]);
      if (sig == prevSig[v]) {
        constSigTrail[v]++;
        if (hasConstantTrail(v)) {
          converged++;
        }
      } else {
        constSigTrail[v] = 0;
      }
      prevSig[v] = sig;
    }
    return converged >= threshold;
  }

  /**
   * Determine whether an eigenvector entry signum is constant over the full window
   *
   * @param i Index of a particular eigenvector entry
   * @return <code>true</code> if and only if the signum at index <code>i</code> is constant over the full window.
   */

  private boolean hasConstantTrail(int i) {
    return constSigTrail[i] >= trailSize - 1;
  }

  /**
   * Postprocess a partially converged eigenvector (see class documentation above)
   *
   * @param vec The partially converged eigenvector
   */

  @Override
  public void postprocess(double[] vec) throws PartialConvergencePostprocessingException {
    Preconditions.checkState(vec.length == graph.order(), "Vector length does not match graph size");
    Graph lGraph = extractPostprocessingSubgraph(vec, -1);
    Graph rGraph = extractPostprocessingSubgraph(vec, 1);
    if (lGraph.order() > 0 && rGraph.order() > 0) {
      classifyNonConvergent(vec, lGraph, rGraph);
    } else if (lGraph.order() > 0) {
      classifyNonConvergentFallback(vec, -1);
    } else if (rGraph.order() > 0) {
      classifyNonConvergentFallback(vec, 1);
    } else {
      throw new PartialConvergencePostprocessingException("Postprocessing failed: Both graphs are empty");
    }
  }

  /**
   * Provide a subgraph where either the selected signum matches or the entry has not converged
   *
   * @param vec      A vector
   * @param selector Either -1 or 1
   * @return A new subgraph
   */

  private Graph extractPostprocessingSubgraph(double[] vec, int selector) {
    IntIterator localIndices = new SignumSelectingIndexIterator(vec, selector, i -> !hasConstantTrail(i));
    return graph.localSubgraph(localIndices);
  }

  /**
   * Classify non-converged entries into <code>{-1, 1}</code> based on relative weights
   *
   * @param vec    A partially converged eigenvector
   * @param lGraph Subgraph established by the (converged) entries with value -1
   * @param rGraph Subgraph established by the (converged) entries with value 1
   */

  private void classifyNonConvergent(double[] vec, Graph lGraph, Graph rGraph) {
    for (int i = 0; i < vec.length; i++) {
      if (!hasConstantTrail(i)) {
        if (relativeWeight(lGraph, i) >= relativeWeight(rGraph, i)) {
          vec[i] = -1;
        } else {
          vec[i] = 1;
        }
      }
    }
  }

  /**
   * Classify non-converged entries into <code>{-1, 1}</code> based on a given selector
   *
   * @param vec      A partially converged eigenvector
   * @param selector Either <code>-1</code> or <code>1</code>
   */

  private void classifyNonConvergentFallback(double[] vec, int selector) {
    for (int i = 0; i < vec.length; i++) {
      if (!hasConstantTrail(i)) {
        vec[i] = selector;
      }
    }
  }

  /**
   * @param subgraph A subgraph
   * @param i        Vertex index (wrt. to the eigenvector)
   * @return Relative weight of the vertex indexed by <code>i</code> with respect to the given (sub-)graph
   */

  private double relativeWeight(Graph subgraph, int i) {
    return subgraph.weightForGlobalId(graph.globalVertexId(i)) / subgraph.totalWeight();
  }

  /**
   * @return Currently used minimum constant trail size
   */

  public int getTrailSize() {
    return trailSize;
  }

  /**
   * @return Currently used convergent vertex threshold
   */

  public int getThreshold() {
    return threshold;
  }


}

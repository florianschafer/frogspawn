/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.algorithms.SignumSelectingIndexIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @see net.adeptropolis.nephila.helpers.Vectors#normalize2Sig(double[])
 */

// TODO: check whether caching the results of hasConstantTrail would further improve performance

public class ConstantSigTrailConvergence implements PartialConvergenceCriterion {

  private static final Logger LOG = LoggerFactory.getLogger(ConstantSigTrailConvergence.class.getSimpleName());
  private final Graph graph;
  private final double convergenceThreshold;
  private final byte[][] sigTrail; // TODO: Check whether using BitSet[] instead would affect the performance

  /**
   * Constructor
   *
   * @param graph                The graph whose spectrally shifted normalized Laplacian eigenvector is to be computed
   * @param trailSize            Size of the sliding window
   * @param convergenceThreshold Fraction of entries that is required to be constant over the full window size
   */

  public ConstantSigTrailConvergence(Graph graph, int trailSize, double convergenceThreshold) {
    this.graph = graph;
    this.sigTrail = new byte[trailSize][];
    for (int i = 0; i < trailSize; i++) {
      this.sigTrail[i] = new byte[graph.size()];
    }
    this.convergenceThreshold = convergenceThreshold;
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
    for (int v = 0; v < graph.size(); v++) {
      sigTrail[iterations % sigTrail.length][v] = (byte) Math.signum(current[v]);
    }
    if (iterations < sigTrail.length) {
      return false;
    }
    return convergedFraction() >= convergenceThreshold;
  }

  /**
   * Postprocess a partially converged eigenvector (see class documentation above)
   *
   * @param vec The partially converged eigenvector
   */

  @Override
  public void postprocess(double[] vec) {
    Preconditions.checkState(vec.length == graph.size(), "Vector length does not match graph size");
    Graph lGraph = extractPostprocessingSubgraph(vec, -1);
    Graph rGraph = extractPostprocessingSubgraph(vec, 1);
    if (lGraph.size() > 0 && rGraph.size() > 0) {
      classifyNonConvergent(vec, lGraph, rGraph);
    } else if (lGraph.size() > 0) {
      classifyNonConvergentFallback(vec, -1);
    } else if (rGraph.size() > 0) {
      classifyNonConvergentFallback(vec, 1);
    } else {
      LOG.warn("Postprocessing failed. Both graphs are empty.");
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
   * @param subgraph A subgraph
   * @param i        Vertex index (wrt. to the eigenvector)
   * @return Relative weight of the vertex indexed by <code>i</code> with respect to the given (sub-)graph
   */

  private double relativeWeight(Graph subgraph, int i) {
    return subgraph.weights()[subgraph.localVertexId(graph.globalVertexId(i))] / subgraph.totalWeight();
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
    return graph.localInducedSubgraph(localIndices);
  }

  /**
   * @return Fraction of eigenvector entries that have fully converged already
   */

  private double convergedFraction() {
    AtomicDouble converged = new AtomicDouble();
    graph.traverseVerticesParallel(v -> {
      if (hasConstantTrail(v)) {
        converged.addAndGet(1d);
      }
    });
    return converged.get() / graph.size();
  }


  /**
   * Determine whether an eigenvector entry signum is constant over the full window
   *
   * @param i Index of a particular eigenvector entry
   * @return <code>true</code> if and only if the signum at index <code>i</code> is constant over the full window.
   */

  private boolean hasConstantTrail(int i) {
    byte refSig = (byte) Math.signum(sigTrail[0][i]);
    for (int round = 1; round < sigTrail.length; round++) {
      if (sigTrail[round][i] != refSig) {
        return false;
      }
    }
    return true;
  }

}

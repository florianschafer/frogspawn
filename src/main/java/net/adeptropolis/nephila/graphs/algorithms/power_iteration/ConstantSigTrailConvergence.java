/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.algorithms.SignumSelectingIndexIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConstantSigTrailConvergence implements IncompleteConvergenceCriterion {

  private static final Logger LOG = LoggerFactory.getLogger(ConstantSigTrailConvergence.class.getSimpleName());

  private final Graph graph;
  private final byte[][] sigTrail; // TODO: This might be stored in long[] instead (mod sig == 0)
  private final double convergenceThreshold;

  public ConstantSigTrailConvergence(Graph graph, int trailSize, double convergenceThreshold) {
    this.graph = graph;
    this.sigTrail = new byte[trailSize][];
    for (int i = 0; i < trailSize; i++) {
      this.sigTrail[i] = new byte[graph.size()];
    }
    this.convergenceThreshold = convergenceThreshold;
  }

  @Override
  public boolean satisfied(double[] previous, double[] current, int iterations) {
    for (int v = 0; v < graph.size(); v++) {
      sigTrail[iterations % sigTrail.length][v] = (byte) Math.signum(current[v]);
    }
    if (iterations < sigTrail.length) {
      return false;
    }
    return convergenceRate() >= convergenceThreshold;
  }

  /**
   * <p>Postprocess a not fully converged eigenvector.</p>
   * <p>More precisely, all non-converged entries will be assigned either -1 or 1 depending on the relative weight within
   * a particular partition</p>
   * @param vec
   */

  @Override
  public void postprocess(double[] vec) {
    Preconditions.checkState(vec.length == graph.size(), "Vector length does not match graph size");
    Graph lGraph = extractPostprocessingSubgraph(vec, -1);
    Graph rGraph = extractPostprocessingSubgraph(vec, 1);
    if (lGraph.size() > 0 && rGraph.size() > 0) {
      substituteNonConvergent(vec, lGraph, rGraph);
    } else if (lGraph.size() > 0) {
      substituteConstant(vec, -1);
    } else if (rGraph.size() > 0) {
      substituteConstant(vec, 1);
    } else {
      LOG.warn("Postprocessing failed. Both graphs are empty");
    }
  }

  private void substituteConstant(double[] vec, int selector) {
    for (int i = 0; i < vec.length; i++) {
      if (!hasConstantTrail(i)) { // TODO: It seems this method is called a bit often. Think about caching...
        vec[i] = selector;
      }
    }
  }

  private void substituteNonConvergent(double[] vec, Graph lGraph, Graph rGraph) {
    for (int i = 0; i < vec.length; i++) {
      if (!hasConstantTrail(i)) { // TODO: It seems this method is called a bit often. Think about caching...
        if (relativeWeight(lGraph, i) >= relativeWeight(rGraph, i)) {
          vec[i] = -1;
        } else {
          vec[i] = 1;
        }
      }
    }
  }

  private double relativeWeight(Graph subgraph, int i) {
    // TODO: Exception handling
    return subgraph.weights()[subgraph.localVertexId(graph.globalVertexId(i))] / subgraph.totalWeight();
  }

  /**
   * Provide a subgraph where either the selected signum matches or the entry has not converged
   * @param vec A vector
   * @param selector Either -1 or 1
   * @return A new subgraph
   */

  private Graph extractPostprocessingSubgraph(double[] vec, int selector) {
    IntIterator localIndices = new SignumSelectingIndexIterator(vec, selector, i -> !hasConstantTrail(i));
    return graph.localInducedSubgraph(localIndices);
  }

  private double convergenceRate() {
    double converged = 0;
    for (int v = 0; v < graph.size(); v++) {
      if (hasConstantTrail(v)) {
        converged++;
      }
    }
    return converged / graph.size();
  }

  private boolean hasConstantTrail(int v) {
    byte refSig = (byte) Math.signum(sigTrail[0][v]);
    for (int version = 1; version < sigTrail.length; version++) {
      if (sigTrail[version][v] != refSig) {
        return false;
      }
    }
    return true;
  }

}

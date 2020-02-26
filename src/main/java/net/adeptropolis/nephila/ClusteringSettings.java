/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila;

import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.ConstantSigTrailConvergence;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.PartialConvergenceCriterion;

/**
 * Stores all settings required for clustering
 */

public class ClusteringSettings {

  private final int minClusterSize;
  private final double minClusterLikelihood;
  private final double minAncestorOverlap;
  private final int trailSize;
  private final double convergenceThreshold;
  private final int maxIterations;

  /**
   * Constructor
   *
   * @param minClusterSize Minimum cluster size
   * @param minClusterLikelihood Minimum cluster likelihood of a vertex
   * @param minAncestorOverlap Minimum ancestor overlap of a child cluster node wrt. to its parent
   * @param trailSize Window size for constant trail convergence (Number of iterations where a vertex must not change its sign)
   * @param convergenceThreshold Fraction of converged vertices
   * @param maxIterations Maximum number of iterations
   */

  public ClusteringSettings(int minClusterSize, double minClusterLikelihood, double minAncestorOverlap, int trailSize, double convergenceThreshold, int maxIterations) {
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
    this.minAncestorOverlap = minAncestorOverlap;
    this.trailSize = trailSize;
    this.convergenceThreshold = convergenceThreshold;
    this.maxIterations = maxIterations;
  }

  /**
   * @return Minumum cluster size
   */

  public int getMinClusterSize() {
    return minClusterSize;
  }

  /**
   * @return Minimum cluster likelihood of a vertex
   */

  public double getMinClusterLikelihood() {
    return minClusterLikelihood;
  }

  /**
   * @return Max allowed number of power method iterations
   */

  public int getMaxIterations() {
    return maxIterations;
  }

  /**
   * Return a new instance of ConvergenceCriterion for a given graph.
   * Currently, this always returns an instance of <code>ConstantSigTrailConvergence</code>
   * @param graph A graph
   * @return A new <code>PartialConvergenceCriterion</code> instance
   */

  public PartialConvergenceCriterion convergenceCriterionForGraph(Graph graph) {
    return new ConstantSigTrailConvergence(graph, trailSize, convergenceThreshold);
  }

  /**
   * @return Minimum ancestor overlap of a child cluster node wrt. to its parent
   */

  public double getMinAncestorOverlap() {
    return minAncestorOverlap;
  }
}

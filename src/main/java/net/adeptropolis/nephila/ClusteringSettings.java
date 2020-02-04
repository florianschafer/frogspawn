/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila;

import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.ConstantSigTrailConvergence;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.PartialConvergenceCriterion;

public class ClusteringSettings {

  private final int minClusterSize;
  private final double minClusterLikelihood;
  private final boolean collapseSingletons;
  private final double minAncestorOverlap;
  private final int trailSize;
  private final double convergenceThreshold;
  private final int maxIterations;

  public ClusteringSettings(int minClusterSize, double minClusterLikelihood, double minAncestorOverlap, int trailSize, double convergenceThreshold, boolean collapseSingletons, int maxIterations) {
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
    this.minAncestorOverlap = minAncestorOverlap;
    this.trailSize = trailSize;
    this.convergenceThreshold = convergenceThreshold;
    this.maxIterations = maxIterations;
    this.collapseSingletons = collapseSingletons;
  }

  public int getMinClusterSize() {
    return minClusterSize;
  }

  public double getMinClusterLikelihood() {
    return minClusterLikelihood;
  }

  public int getMaxIterations() {
    return maxIterations;
  }

  public boolean getCollapseSingletons() {
    return collapseSingletons;
  }

  public PartialConvergenceCriterion convergenceCriterionForGraph(Graph graph) {
    return new ConstantSigTrailConvergence(graph, trailSize, convergenceThreshold);
  }

  public double getMinAncestorOverlap() {
    return minAncestorOverlap;
  }
}

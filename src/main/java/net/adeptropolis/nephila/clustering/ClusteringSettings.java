/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.algorithms.power_iteration.ConvergenceCriterion;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.SignumConvergence;

public class ClusteringSettings {

  private final int minClusterSize;
  private final double minClusterLikelihood;
  private final boolean collapseSingletons;
  private final double minAncestorOverlap;
  private final ConvergenceCriterion convergenceCriterion;
  private final int maxIterations;

  public ClusteringSettings(int minClusterSize, double minClusterLikelihood, double minAncestorOverlap, double maxUnstable, boolean collapseSingletons, int maxIterations) {
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
    this.minAncestorOverlap = minAncestorOverlap;
    this.maxIterations = maxIterations;
    this.collapseSingletons = collapseSingletons;
    convergenceCriterion = new SignumConvergence(maxUnstable);
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

  public ConvergenceCriterion getConvergenceCriterion() {
    return convergenceCriterion;
  }

  public double getMinAncestorOverlap() {
    return minAncestorOverlap;
  }
}

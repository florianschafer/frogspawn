package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.algorithms.power_iteration.ConvergenceCriterion;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.SignumConvergence;

public class ClusteringSettings {

  private final int minClusterSize;
  private final double minClusterLikelihood;
  private final int maxIterations;
  private boolean collapseSingletons;
  private final ConvergenceCriterion convergenceCriterion;

  public ClusteringSettings(int minClusterSize, double minClusterLikelihood, int maxIterations, boolean collapseSingletons, double maxUnstable) {
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
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
}

package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.algorithms.power_iteration.ConvergenceCriterion;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.SignumConvergence;

public class ClusteringSettings {

  private final int minClusterSize;
  private final double minClusterLikelihood;
  private final int maxIterations;
  private final ConvergenceCriterion convergenceCriterion;

  public ClusteringSettings(int minClusterSize, double minClusterLikelihood, int maxIterations, double maxUnstable) {
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
    this.maxIterations = maxIterations;
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

  public ConvergenceCriterion getConvergenceCriterion() {
    return convergenceCriterion;
  }

}

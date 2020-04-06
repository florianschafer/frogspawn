/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis;

import com.google.common.collect.Lists;
import net.adeptropolis.metis.clustering.consistency.ConsistencyMetric;
import net.adeptropolis.metis.clustering.consistency.RelativeWeightConsistencyMetric;
import net.adeptropolis.metis.clustering.postprocessing.Postprocessor;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.algorithms.power_iteration.ConstantSigTrailConvergence;
import net.adeptropolis.metis.graphs.algorithms.power_iteration.PartialConvergenceCriterion;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

/**
 * Stores all relevant clustering settings
 */

public class ClusteringSettings {

  private final ConsistencyMetric consistencyMetric;
  private final int minClusterSize;
  private final double minClusterLikelihood;
  private final double minParentOverlap;
  private final int parentSearchStepSize;
  private final int trailSize;
  private final double convergenceThreshold;
  private final int maxIterations;
  private final long randomSeed;
  private final List<Postprocessor> customPostprocessors;

  /**
   * Constructor
   *
   * @param consistencyMetric    Vertex/cluster consistency metric to be used
   * @param minClusterSize       Minimum cluster size
   * @param minClusterLikelihood Minimum cluster likelihood of a vertex
   * @param minParentOverlap     Minimum ancestor overlap of a child cluster node wrt. to its parent
   * @param parentSearchStepSize Step size of parent search
   * @param trailSize            Window size for constant trail convergence (Number of iterations where a vertex must not change its sign)
   * @param convergenceThreshold Fraction of converged vertices
   * @param maxIterations        Maximum number of iterations
   * @param randomSeed           Seed value for random initial value generation
   * @param customPostprocessors List of custom postprocessors to be executed at the end of the default pipeline
   */

  private ClusteringSettings(ConsistencyMetric consistencyMetric, int minClusterSize, double minClusterLikelihood,
                             double minParentOverlap, int parentSearchStepSize, int trailSize,
                             double convergenceThreshold, int maxIterations, long randomSeed, List<Postprocessor> customPostprocessors) {
    this.consistencyMetric = consistencyMetric;
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
    this.minParentOverlap = minParentOverlap;
    this.parentSearchStepSize = parentSearchStepSize;
    this.trailSize = trailSize;
    this.convergenceThreshold = convergenceThreshold;
    this.maxIterations = maxIterations;
    this.randomSeed = randomSeed;
    this.customPostprocessors = customPostprocessors;
  }

  /**
   * Convenience method
   *
   * @return A new builder
   */

  public static Builder builder() {
    return new Builder();
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
   * @return Seed for random initial vector generation
   */

  public long getRandomSeed() {
    return randomSeed;
  }

  /**
   * Return a new instance of ConvergenceCriterion for a given graph.
   * Currently, this always returns an instance of <code>ConstantSigTrailConvergence</code>
   *
   * @param graph A graph
   * @return A new <code>PartialConvergenceCriterion</code> instance
   */

  public PartialConvergenceCriterion convergenceCriterionForGraph(Graph graph) {
    return new ConstantSigTrailConvergence(graph, trailSize, convergenceThreshold);
  }

  /**
   * @return Minimum ancestor overlap of a child cluster node wrt. to its parent
   */

  public double getMinparentOverlap() {
    return minParentOverlap;
  }

  /**
   * @return Step size of parent search
   */

  public int getParentSearchStepSize() {
    return parentSearchStepSize;
  }

  /**
   * @return Currently used consistency metric
   */

  public ConsistencyMetric getConsistencyMetric() {
    return consistencyMetric;
  }

  /**
   * @return The list of custom postprocessors
   */

  public List<Postprocessor> getCustomPostprocessors() {
    return customPostprocessors;
  }

  /**
   * @return Settings string representation
   */

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
            .append("consistencyMetric", consistencyMetric)
            .append("minClusterSize", minClusterSize)
            .append("minClusterLikelihood", minClusterLikelihood)
            .append("minParentOverlap", minParentOverlap)
            .append("parentSearchStepSize", parentSearchStepSize)
            .append("trailSize", trailSize)
            .append("convergenceThreshold", convergenceThreshold)
            .append("maxIterations", convergenceThreshold)
            .append("randomSeed", randomSeed)
            .append("customPostprocessors", customPostprocessors)
            .build();
  }

  public static class Builder {

    private ConsistencyMetric consistencyMetric = new RelativeWeightConsistencyMetric();
    private int minClusterSize = 50;
    private double minClusterLikelihood = 0.1;
    private double minParentOverlap = 0.55;
    private int parentSearchStepSize = 32;
    private int trailSize = 20;
    private double convergenceThreshold = 0.95; // Note that values <= ~0.75-0.8 actually degrade performance
    private long randomSeed = 42133742L;
    private List<Postprocessor> customPostprocessors = Lists.newArrayList();
    private int maxIterations = 540; // Set as twice the 99.9% quantile of the required iterations on a large sample within a parameter range of 15-35 for trail size and 0.9-0.98 for convergence threshold

    /**
     * Set consistency metric. Default is <code>RelativeWeightConsistencyMetric</code>
     *
     * @param metric Metric
     * @return this
     */

    public Builder withConsistencyMetric(ConsistencyMetric metric) {
      this.consistencyMetric = metric;
      return this;
    }

    /**
     * Set minimum cluster size. Default is 50
     *
     * @param minClusterSize Minimum cluster size
     * @return this
     */

    public Builder withMinClusterSize(int minClusterSize) {
      this.minClusterSize = minClusterSize;
      return this;
    }

    /**
     * Set minimum cluster likelihood. Default is 0.1
     *
     * @param minClusterLikelihood Minimum likelihood
     * @return this
     */

    public Builder withMinClusterLikelihood(double minClusterLikelihood) {
      this.minClusterLikelihood = minClusterLikelihood;
      return this;
    }

    /**
     * Set Minimum ancestor overlap. Default is 0.55
     *
     * @param minParentOverlap Minimum ancestor overlap
     * @return this
     */

    public Builder withMinparentOverlap(double minParentOverlap) {
      this.minParentOverlap = minParentOverlap;
      return this;
    }

    /**
     * Set parent search step size. Default is 32
     *
     * @param stepSize Parent search step size
     * @return this
     */

    public Builder withParentSearchStepSize(int stepSize) {
      this.parentSearchStepSize = stepSize;
      return this;
    }

    /**
     * Set trail size of convergence criterion. Default is 20
     *
     * @param trailSize Trail size
     * @return this
     */

    public Builder withTrailSize(int trailSize) {
      this.trailSize = trailSize;
      return this;
    }

    /**
     * Set convergence threshold (fraction of vertices that have already settled into a cluster). Default is 0.95
     *
     * @param convergenceThreshold Convergene threshold
     * @return this
     */

    public Builder withConvergenceThreshold(double convergenceThreshold) {
      this.convergenceThreshold = convergenceThreshold;
      return this;
    }

    /**
     * Set maxmimum number of iterations for the power method. Default is 540
     *
     * @param maxIterations Maximum number of iterations
     * @return this
     */
    public Builder withMaxIterations(int maxIterations) {
      this.maxIterations = maxIterations;
      return this;
    }

    /**
     * Set random initial vector generation seed
     *
     * @param seed Seed value
     * @return this
     */
    public Builder withRandomSeed(long seed) {
      this.randomSeed = seed;
      return this;
    }

    /**
     * Add a custom postprocessor to the pipeline
     *
     * @param postprocessor A postprocessor
     * @return this
     */

    public Builder withCustomPostprocessor(Postprocessor postprocessor) {
      this.customPostprocessors.add(postprocessor);
      return this;
    }

    /**
     * Build settings
     *
     * @return A new instance of <code>ClusteringSettings</code>
     */

    public ClusteringSettings build() {
      return new ClusteringSettings(consistencyMetric, minClusterSize, minClusterLikelihood, minParentOverlap,
              parentSearchStepSize, trailSize, convergenceThreshold, maxIterations, randomSeed, customPostprocessors);
    }

  }
}

/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn;

import net.adeptropolis.frogspawn.clustering.affiliation.AffiliationMetric;
import net.adeptropolis.frogspawn.clustering.affiliation.DefaultAffiliationMetric;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.algorithms.power_iteration.ConstantSigTrailConvergence;
import net.adeptropolis.frogspawn.graphs.algorithms.power_iteration.PartialConvergenceCriterion;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Stores all relevant clustering settings
 */

public class ClusteringSettings {

  private final AffiliationMetric affiliationMetric;
  private final double minAffiliation;
  private final int minClusterSize;
  private final int trailSize;
  private final double convergenceThreshold;
  private final int maxIterations;
  private final long randomSeed;

  /**
   * Constructor
   *
   * @param affiliationMetric    Vertex/cluster affiliation metric to be used
   * @param minAffiliation       Minimum affiliation score
   * @param minClusterSize       Minimum cluster size
   * @param trailSize            Window size for constant trail convergence (Number of iterations where a vertex must not change its sign)
   * @param convergenceThreshold Fraction of converged vertices
   * @param maxIterations        Maximum number of iterations
   * @param randomSeed           Seed value for random initial value generation
   */

  private ClusteringSettings(AffiliationMetric affiliationMetric, double minAffiliation,
                             int minClusterSize, int trailSize, double convergenceThreshold, int maxIterations,
                             long randomSeed) {
    this.affiliationMetric = affiliationMetric;
    this.minAffiliation = minAffiliation;
    this.minClusterSize = minClusterSize;
    this.trailSize = trailSize;
    this.convergenceThreshold = convergenceThreshold;
    this.maxIterations = maxIterations;
    this.randomSeed = randomSeed;
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
   * @return Minimum affiliation of a vertex wrt. to a cluster
   */

  public double getMinAffiliation() {
    return minAffiliation;
  }

  /**
   * @return Minumum cluster size
   */

  public int getMinClusterSize() {
    return minClusterSize;
  }

  /**
   * @return Max allowed number of power method iterations
   */

  public int getMaxIterations() {
    return maxIterations;
  }

  /**
   * @return Trail size for partial convergence criterion
   */
  public int getTrailSize() {
    return trailSize;
  }

  /**
   * @return Convergence threshold
   */

  public double getConvergenceThreshold() {
    return convergenceThreshold;
  }


  /**
   * @return Seed for random initial vector generation
   */

  public long getRandomSeed() {
    return randomSeed;
  }
  
  /**
   * @return Currently used vertex affiliation metric
   */

  public AffiliationMetric getAffiliationMetric() {
    return affiliationMetric;
  }

  /**
   * @return Settings string representation
   */

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
            .append("affiliationMetric", affiliationMetric)
            .append("minAffiliation", minAffiliation)
            .append("minClusterSize", minClusterSize)
            .append("trailSize", trailSize)
            .append("convergenceThreshold", convergenceThreshold)
            .append("maxIterations", maxIterations)
            .append("randomSeed", randomSeed)
            .build();
  }

  public static class Builder {

    private AffiliationMetric affiliationMetric = new DefaultAffiliationMetric();
    private double minAffiliation = 0.2;
    private int minClusterSize = 50;
    private int trailSize = 20;
    private double convergenceThreshold = 0.95; // Note that values <= ~0.75-0.8 actually degrade performance
    private long randomSeed = 42133742L;
    private int maxIterations = 540; // Set as twice the 99.9% quantile of the required iterations on a large sample within a parameter range of 15-35 for trail size and 0.9-0.98 for convergence threshold

    /**
     * Set affiliation metric. Default is <code>DefaultAffiliationMetric</code>
     *
     * @param metric Metric
     * @return this
     */

    public Builder withAffiliationMetric(AffiliationMetric metric) {
      this.affiliationMetric = metric;
      return this;
    }

    /**
     * Set minimum affiliation score. Default is 0.2
     *
     * @param minAffiliation Minimum affiliation score
     * @return this
     */

    public Builder withMinAffiliation(double minAffiliation) {
      this.minAffiliation = minAffiliation;
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
     * Build settings
     *
     * @return A new instance of <code>ClusteringSettings</code>
     */

    public ClusteringSettings build() {
      return new ClusteringSettings(affiliationMetric, minAffiliation, minClusterSize, trailSize, convergenceThreshold,
              maxIterations, randomSeed);
    }

  }
}

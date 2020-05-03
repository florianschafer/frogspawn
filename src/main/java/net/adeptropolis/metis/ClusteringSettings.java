/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis;

import net.adeptropolis.metis.clustering.affiliation.RelativeWeightVertexAffiliationMetric;
import net.adeptropolis.metis.clustering.affiliation.VertexAffiliationMetric;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.algorithms.power_iteration.ConstantSigTrailConvergence;
import net.adeptropolis.metis.graphs.algorithms.power_iteration.PartialConvergenceCriterion;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Stores all relevant clustering settings
 */

public class ClusteringSettings {

  private final VertexAffiliationMetric vertexAffiliationMetric;
  private final double minVertexAffiliation;
  private final int minClusterSize;
  private final int trailSize;
  private final double convergenceThreshold;
  private final int maxIterations;
  private final long randomSeed;

  /**
   * Constructor
   *
   * @param vertexAffiliationMetric Vertex/cluster affiliation metric to be used
   * @param minVertexAffiliation    Minimum affiliation score of a vertex wrt. to a cluster
   * @param minClusterSize          Minimum cluster size
   * @param trailSize               Window size for constant trail convergence (Number of iterations where a vertex must not change its sign)
   * @param convergenceThreshold    Fraction of converged vertices
   * @param maxIterations           Maximum number of iterations
   * @param randomSeed              Seed value for random initial value generation
   */

  private ClusteringSettings(VertexAffiliationMetric vertexAffiliationMetric, double minVertexAffiliation,
                             int minClusterSize, int trailSize, double convergenceThreshold, int maxIterations,
                             long randomSeed) {
    this.vertexAffiliationMetric = vertexAffiliationMetric;
    this.minVertexAffiliation = minVertexAffiliation;
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

  public double getMinVertexAffiliation() {
    return minVertexAffiliation;
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
   * @return Currently used vertex affiliation metric
   */

  public VertexAffiliationMetric getVertexAffiliationMetric() {
    return vertexAffiliationMetric;
  }

  /**
   * @return Settings string representation
   */

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
            .append("affiliationMetric", vertexAffiliationMetric)
            .append("minVertexAffiliation", minVertexAffiliation)
            .append("minClusterSize", minClusterSize)
            .append("trailSize", trailSize)
            .append("convergenceThreshold", convergenceThreshold)
            .append("maxIterations", convergenceThreshold)
            .append("randomSeed", randomSeed)
            .build();
  }

  public static class Builder {

    private VertexAffiliationMetric vertexAffiliationMetric = new RelativeWeightVertexAffiliationMetric();
    private double minVertexAffiliation = 0.1;
    private int minClusterSize = 50;
    private int trailSize = 20;
    private double convergenceThreshold = 0.95; // Note that values <= ~0.75-0.8 actually degrade performance
    private long randomSeed = 42133742L;
    private int maxIterations = 540; // Set as twice the 99.9% quantile of the required iterations on a large sample within a parameter range of 15-35 for trail size and 0.9-0.98 for convergence threshold

    /**
     * Set vertex affiliation metric. Default is <code>RelativeWeightVertexAffiliationMetric</code>
     *
     * @param metric Metric
     * @return this
     */

    public Builder withVertexAffiliationMetric(VertexAffiliationMetric metric) {
      this.vertexAffiliationMetric = metric;
      return this;
    }

    /**
     * Set minimum vertex affiliation score. Default is 0.1
     *
     * @param minVertexAffiliation Minimum affiliation score
     * @return this
     */

    public Builder withMinVertexAffiliation(double minVertexAffiliation) {
      this.minVertexAffiliation = minVertexAffiliation;
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
      return new ClusteringSettings(vertexAffiliationMetric, minVertexAffiliation, minClusterSize, trailSize, convergenceThreshold,
              maxIterations, randomSeed);
    }

  }
}

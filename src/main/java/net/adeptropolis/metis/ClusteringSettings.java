/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis;

import com.google.common.collect.Lists;
import net.adeptropolis.metis.clustering.affiliation.RelativeWeightVertexAffiliationMetric;
import net.adeptropolis.metis.clustering.affiliation.VertexAffiliationMetric;
import net.adeptropolis.metis.clustering.postprocessing.Postprocessor;
import net.adeptropolis.metis.digest.DigestRanking;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.algorithms.power_iteration.ConstantSigTrailConvergence;
import net.adeptropolis.metis.graphs.algorithms.power_iteration.PartialConvergenceCriterion;
import net.adeptropolis.metis.graphs.similarity.GraphSimilarityMetric;
import net.adeptropolis.metis.graphs.similarity.OverlapGraphSimilarityMetric;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

import static net.adeptropolis.metis.digest.DigestRankings.COMBINED_RANKING;

/**
 * Stores all relevant clustering settings
 */

public class ClusteringSettings {

  private final List<Postprocessor> customPostprocessors;
  private final VertexAffiliationMetric vertexAffiliationMetric;

  private final GraphSimilarityMetric similarityMetric;
  private final int minClusterSize;
  private final double minVertexAffiliation;
  private final int minChildren;

  // Power iteration
  private final int trailSize;
  private final double convergenceThreshold;
  private final int maxIterations;
  private final long randomSeed;

  // Digest creation
  private final int maxDigestSize;
  private final boolean aggregateDigests;
  private final DigestRanking digestRanking;

  /**
   * Constructor
   *
   * @param vertexAffiliationMetric Vertex/cluster affiliation metric to be used
   * @param similarityMetric        Graph similarity metric
   * @param minClusterSize          Minimum cluster size
   * @param minVertexAffiliation    Minimum affiliation score of a vertex wrt. to a cluster
   * @param minChildren             Minimum number of children for each cluster
   * @param trailSize               Window size for constant trail convergence (Number of iterations where a vertex must not change its sign)
   * @param convergenceThreshold    Fraction of converged vertices
   * @param maxIterations           Maximum number of iterations
   * @param randomSeed              Seed value for random initial value generation
   * @param customPostprocessors    List of custom postprocessors to be executed at the end of the default pipeline
   * @param maxDigestSize           Maximum size of cluster digests
   * @param aggregateDigests        Whether the digester should aggregate descendant clusters
   * @param digestRanking           Vertex ranking function for cluster digests
   */

  @SuppressWarnings("squid:S00107")
  private ClusteringSettings(VertexAffiliationMetric vertexAffiliationMetric, GraphSimilarityMetric similarityMetric,
                             int minClusterSize, double minVertexAffiliation, int minChildren, int trailSize,
                             double convergenceThreshold, int maxIterations, long randomSeed,
                             List<Postprocessor> customPostprocessors, int maxDigestSize, boolean aggregateDigests,
                             DigestRanking digestRanking) {
    this.vertexAffiliationMetric = vertexAffiliationMetric;
    this.similarityMetric = similarityMetric;
    this.minClusterSize = minClusterSize;
    this.minVertexAffiliation = minVertexAffiliation;
    this.minChildren = minChildren;
    this.trailSize = trailSize;
    this.convergenceThreshold = convergenceThreshold;
    this.maxIterations = maxIterations;
    this.randomSeed = randomSeed;
    this.customPostprocessors = customPostprocessors;
    this.maxDigestSize = maxDigestSize;
    this.aggregateDigests = aggregateDigests;
    this.digestRanking = digestRanking;
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
   * @return Minimum affiliation of a vertex wrt. to a cluster
   */

  public double getMinVertexAffiliation() {
    return minVertexAffiliation;
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
   * @return Minimum number of children for each cluster
   */

  public int getMinChildren() {
    return minChildren;
  }

  /**
   * @return Currently used vertex affiliation metric
   */

  public VertexAffiliationMetric getVertexAffiliationMetric() {
    return vertexAffiliationMetric;
  }

  /**
   * @return Currently used graph similarity metric
   */

  public GraphSimilarityMetric getSimilarityMetric() {
    return similarityMetric;
  }

  /**
   * @return The list of custom postprocessors
   */

  public List<Postprocessor> getCustomPostprocessors() {
    return customPostprocessors;
  }

  /**
   * @return Maximum size of cluster digests
   */

  public int getMaxDigestSize() {
    return maxDigestSize;
  }

  /**
   * @return Whether the digester should aggregate descendant clusters
   */

  public boolean isAggregateDigests() {
    return aggregateDigests;
  }

  /**
   * @return Vertex ranking function for cluster digests
   */

  public DigestRanking getDigestRanking() {
    return digestRanking;
  }

  /**
   * @return Settings string representation
   */

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
            .append("affiliationMetric", vertexAffiliationMetric)
            .append("minClusterSize", minClusterSize)
            .append("minVertexAffiliation", minVertexAffiliation)
            .append("minChildren", minChildren)
            .append("trailSize", trailSize)
            .append("convergenceThreshold", convergenceThreshold)
            .append("maxIterations", convergenceThreshold)
            .append("randomSeed", randomSeed)
            .append("customPostprocessors", customPostprocessors)
            .append("maxDigestSize", maxDigestSize)
            .append("aggregateDigests", aggregateDigests)
            .append("digestRanking", digestRanking)
            .build();
  }

  public static class Builder {

    private final List<Postprocessor> customPostprocessors = Lists.newArrayList();
    private VertexAffiliationMetric vertexAffiliationMetric = new RelativeWeightVertexAffiliationMetric();
    private GraphSimilarityMetric similarityMetric = new OverlapGraphSimilarityMetric();
    private int minClusterSize = 50;
    private double minVertexAffiliation = 0.1;
    private int minChildren = 10;

    // Power iteration
    private int trailSize = 20;
    private double convergenceThreshold = 0.95; // Note that values <= ~0.75-0.8 actually degrade performance
    private long randomSeed = 42133742L;
    private int maxIterations = 540; // Set as twice the 99.9% quantile of the required iterations on a large sample within a parameter range of 15-35 for trail size and 0.9-0.98 for convergence threshold

    // Digest creation
    private int maxDigestSize = 0;
    private boolean aggregateDigests = false;
    private DigestRanking digestRanking = COMBINED_RANKING.apply(1.75);

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
     * Set the graph similarity metric. Default is <code>OverlapGraphSimilarityMetric</code>
     *
     * @param metric Metric
     * @return this
     */

    public Builder withSimilarityMetric(GraphSimilarityMetric metric) {
      this.similarityMetric = metric;
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
     * Set Minimum number of children for each cluster. Default is 10
     *
     * @param minChildren Minimum number of children
     * @return this
     */

    public Builder withMinChildren(int minChildren) {
      this.minChildren = minChildren;
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
     * Set maximum size of cluster digests. Default is 0 (use all available vertices)
     *
     * @param maxDigestSize Maximum digest size
     * @return this
     */

    public Builder withMaxDigestSize(int maxDigestSize) {
      this.maxDigestSize = maxDigestSize;
      return this;
    }

    /**
     * Configure whether the digestor should aggregate all vertices from a cluster's descentents. Default is false,
     * in which case only the cluster's remainder is processed.
     *
     * @param aggregateDigests Whether the digester should aggregate descendant clusters
     * @return this
     */

    public Builder withAggregateDigests(boolean aggregateDigests) {
      this.aggregateDigests = aggregateDigests;
      return this;
    }

    /**
     * Configure the vertex ranking function for cluster digests. All vertices will be sorted accordingly.
     * Default is <code>COMBINED_RANKING.apply(1.75)</code> (i.e. weight^1.75 * affiliation score)
     *
     * @param digestRanking Ranking function for cluster digests
     * @return this
     */

    public Builder withDigestRanking(DigestRanking digestRanking) {
      this.digestRanking = digestRanking;
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
      return new ClusteringSettings(vertexAffiliationMetric, similarityMetric, minClusterSize, minVertexAffiliation,
              minChildren, trailSize, convergenceThreshold, maxIterations, randomSeed, customPostprocessors,
              maxDigestSize, aggregateDigests, digestRanking);
    }

  }
}

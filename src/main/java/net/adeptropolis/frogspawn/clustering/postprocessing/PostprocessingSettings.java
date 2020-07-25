/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

import com.google.common.collect.Lists;
import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.clustering.affiliation.AffiliationMetric;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;
import net.adeptropolis.frogspawn.graphs.similarity.NormalizedCutMetric;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class PostprocessingSettings {

  private final ClusteringSettings clusteringSettings;
  private final GraphSimilarityMetric similarityMetric;

  private final double minParentSimilarity;
  private final double maxParentSimilarity;
  private final double targetParentSimilarity;
  private final double parentSimilarityAcceptanceLimit;
  private final int minChildren;

  private final SingletonMode singletonMode;
  private final List<Postprocessor> customPostprocessors;

  /**
   * Constructor
   * @param clusteringSettings   Primary clustering settings
   * @param similarityMetric     Graph similarity metric
   * @param minParentSimilarity  Minimum parent similarity wrt. to the given similarity metric
   * @param maxParentSimilarity  Maximum parent similarity wrt. to the given similarity metric
   * @param targetParentSimilarity Minimum parent similarity for vertical shifting
   * @param parentSimilarityAcceptanceLimit Minimum fraction of clusters that obey the above similarity boundaries
   * @param minChildren          Minimum number of children for each cluster
   * @param singletonMode        Determine how singleton clusters should be treated
   * @param customPostprocessors List of custom postprocessors to be executed at the end of the default pipeline
   */

  private PostprocessingSettings(ClusteringSettings clusteringSettings, GraphSimilarityMetric similarityMetric,
                                 double minParentSimilarity, double maxParentSimilarity, double targetParentSimilarity,
                                 double parentSimilarityAcceptanceLimit, int minChildren, SingletonMode singletonMode,
                                 List<Postprocessor> customPostprocessors) {
    this.clusteringSettings = clusteringSettings;
    this.similarityMetric = similarityMetric;
    this.minParentSimilarity = minParentSimilarity;
    this.maxParentSimilarity = maxParentSimilarity;
    this.targetParentSimilarity = targetParentSimilarity;
    this.parentSimilarityAcceptanceLimit = parentSimilarityAcceptanceLimit;
    this.minChildren = minChildren;
    this.singletonMode = singletonMode;
    this.customPostprocessors = customPostprocessors;
  }

  /**
   * Convenience method
   *
   * @param clusteringSettings Primary clustering settings
   * @return A new builder
   */

  public static Builder builder(ClusteringSettings clusteringSettings) {
    return new Builder(clusteringSettings);
  }

  /**
   * @return Currently used vertex affiliation metric
   */

  public AffiliationMetric getAffiliationMetric() {
    return clusteringSettings.getAffiliationMetric();
  }

  /**
   * @return Currently used graph similarity metric
   */

  public GraphSimilarityMetric getSimilarityMetric() {
    return similarityMetric;
  }

  /**
   * @return Minimum parent similarity
   */

  public double getMinParentSimilarity() {
    return minParentSimilarity;
  }

  /**
   * @return Maximum parent similarity
   */

  public double getMaxParentSimilarity() {
    return maxParentSimilarity;
  }

  /**
   * @return Target parent similarity
   */

  public double getTargetParentSimilarity() {
    return targetParentSimilarity;
  }

  /**
   * @return Parent similarity precision
   */

  public double getParentSimilarityAcceptanceLimit() {
    return parentSimilarityAcceptanceLimit;
  }

  /**
   * @return Minimum affiliation of a vertex wrt. to a cluster
   */

  public double getMinAffiliation() {
    return clusteringSettings.getMinAffiliation();
  }

  /**
   * @return Minumum cluster size
   */

  public int getMinClusterSize() {
    return clusteringSettings.getMinClusterSize();
  }

  /**
   * @return Minimum number of children for each cluster
   */

  public int getMinChildren() {
    return minChildren;
  }

  /**
   * @return Currently used singleton treatment mode
   */

  public SingletonMode getSingletonMode() {
    return singletonMode;
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
            .append("affiliationMetric", getAffiliationMetric())
            .append("minAffiliation", getMinAffiliation())
            .append("similarityMetric", getSimilarityMetric())
            .append("minParentSimilarity", getMinParentSimilarity())
            .append("maxParentSimilarity", getMaxParentSimilarity())
            .append("targetParentSimilarity", getTargetParentSimilarity())
            .append("parentSimilarityAcceptanceLimit", getParentSimilarityAcceptanceLimit())
            .append("minClusterSize", getMinClusterSize())
            .append("minChildren", getMinChildren())
            .append("singletonMode", getSingletonMode())
            .append("customPostprocessors", getCustomPostprocessors())
            .build();
  }

  public static class Builder {

    private final ClusteringSettings clusteringSettings;
    private final List<Postprocessor> customPostprocessors = Lists.newArrayList();
    private GraphSimilarityMetric similarityMetric = new NormalizedCutMetric();
    private double minParentSimilarity = 0.05;
    private double maxParentSimilarity = 0.45;
    private double targetParentSimilarity = 0.15;
    private double parentSimilarityAcceptanceLimit = 0.98;
    private int minChildren = 0;
    private SingletonMode singletonMode = SingletonMode.ASSIMILATE;

    /**
     * Constructor
     *
     * @param clusteringSettings Primary clustering settings
     */

    public Builder(ClusteringSettings clusteringSettings) {
      this.clusteringSettings = clusteringSettings;
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
     * Set the minimum graph similarity. Default is 0.05
     *
     * @param minParentSimilarity Minimum similarity between a cluster and its parent wrt. to the similarity metric
     * @return this
     */

    public Builder withMinParentSimilarity(double minParentSimilarity) {
      this.minParentSimilarity = minParentSimilarity;
      return this;
    }

    /**
     * Set the maximum graph similarity. Default is 0.45
     *
     * @param maxParentSimilarity Minimum similarity between a cluster and its parent wrt. to the similarity metric
     * @return this
     */

    public Builder withMaxParentSimilarity(double maxParentSimilarity) {
      this.maxParentSimilarity = maxParentSimilarity;
      return this;
    }

    /**
     * Set the target parent-child similarity. Default is 0.15
     *
     * @param targetParentSimilarity Minimum similarity between relocated clusters and their prospective parents
     * @return this
     */

    public Builder withTargetParentSimilarity(double targetParentSimilarity) {
      this.targetParentSimilarity = targetParentSimilarity;
      return this;
    }

    /**
     * Set the parent similarity acceptance limit. Default is 0.99
     *
     * @param limit Minimum fraction of clusters that obey all similarity boundaries
     * @return this
     */

    public Builder withParentSimilarityAcceptanceLimit(double limit) {
      this.parentSimilarityAcceptanceLimit = limit;
      return this;
    }

    /**
     * Set Minimum number of children for each cluster. Default is 0
     *
     * @param minChildren Minimum number of children
     * @return this
     */

    public Builder withMinChildren(int minChildren) {
      this.minChildren = minChildren;
      return this;
    }

    /**
     * Set the singleton mode for postprocessing. Default is <code>ASSIMILATE</code>
     *
     * @param mode Singleton mode
     * @return this
     * @see SingletonMode
     */

    public Builder withSingletonMode(SingletonMode mode) {
      this.singletonMode = mode;
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
     * @return A new instance of <code>PostprocessingSettings</code>
     */

    public PostprocessingSettings build() {
      return new PostprocessingSettings(clusteringSettings, similarityMetric, minParentSimilarity, maxParentSimilarity, targetParentSimilarity, parentSimilarityAcceptanceLimit, minChildren, singletonMode, customPostprocessors);
    }

  }


}

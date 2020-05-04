/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

import com.google.common.collect.Lists;
import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.clustering.affiliation.VertexAffiliationMetric;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;
import net.adeptropolis.frogspawn.graphs.similarity.OverlapGraphSimilarityMetric;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

public class PostprocessingSettings {

  private final ClusteringSettings clusteringSettings;
  private final GraphSimilarityMetric similarityMetric;
  private final int minChildren;
  private final List<Postprocessor> customPostprocessors;

  /**
   * Constructor
   *
   * @param clusteringSettings   Primary clustering settings
   * @param similarityMetric     Graph similarity metric
   * @param minChildren          Minimum number of children for each cluster
   * @param customPostprocessors List of custom postprocessors to be executed at the end of the default pipeline
   */

  private PostprocessingSettings(ClusteringSettings clusteringSettings, GraphSimilarityMetric similarityMetric,
                                 int minChildren, List<Postprocessor> customPostprocessors) {
    this.clusteringSettings = clusteringSettings;
    this.similarityMetric = similarityMetric;
    this.minChildren = minChildren;
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

  public VertexAffiliationMetric getVertexAffiliationMetric() {
    return clusteringSettings.getVertexAffiliationMetric();
  }

  /**
   * @return Currently used graph similarity metric
   */

  public GraphSimilarityMetric getSimilarityMetric() {
    return similarityMetric;
  }

  /**
   * @return Minimum affiliation of a vertex wrt. to a cluster
   */

  public double getMinVertexAffiliation() {
    return clusteringSettings.getMinVertexAffiliation();
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
            .append("affiliationMetric", getVertexAffiliationMetric())
            .append("minVertexAffiliation", getMinVertexAffiliation())
            .append("similarityMetric", getSimilarityMetric())
            .append("minClusterSize", getMinClusterSize())
            .append("minChildren", getMinChildren())
            .append("customPostprocessors", getCustomPostprocessors())
            .build();
  }

  public static class Builder {

    private final ClusteringSettings clusteringSettings;
    private final List<Postprocessor> customPostprocessors = Lists.newArrayList();
    private GraphSimilarityMetric similarityMetric = new OverlapGraphSimilarityMetric();
    private int minChildren = 10;

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
      return new PostprocessingSettings(clusteringSettings, similarityMetric, minChildren, customPostprocessors);
    }


  }


}

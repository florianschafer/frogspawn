/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.clustering.affiliation.VertexAffiliationMetric;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static net.adeptropolis.frogspawn.digest.DigestRankings.DEFAULT_COMBINED_RANKING;

public class DigesterSettings {

  private final ClusteringSettings clusteringSettings;
  private final int maxDigestSize;
  private final boolean aggregateDigests;
  private final DigestRanking digestRanking;

  /**
   * Constructor
   *
   * @param clusteringSettings Primary clustering settings
   * @param maxDigestSize      Maximum size of cluster digests
   * @param aggregateDigests   Whether the digester should aggregate descendant clusters
   * @param digestRanking      Vertex ranking function for cluster digests
   */

  private DigesterSettings(ClusteringSettings clusteringSettings, int maxDigestSize, boolean aggregateDigests, DigestRanking digestRanking) {
    this.clusteringSettings = clusteringSettings;
    this.maxDigestSize = maxDigestSize;
    this.aggregateDigests = aggregateDigests;
    this.digestRanking = digestRanking;
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
   * @return Maximum size of cluster digests
   */

  public int getMaxDigestSize() {
    return maxDigestSize;
  }

  /**
   * @return Whether the digester should aggregate descendant clusters
   */

  public boolean doAggregateDigests() {
    return aggregateDigests;
  }

  /**
   * @return Vertex ranking function for cluster digests
   */

  public DigestRanking getDigestRanking() {
    return digestRanking;
  }

  /**
   * @return Currently used vertex affiliation metric
   */

  public VertexAffiliationMetric getVertexAffiliationMetric() {
    return clusteringSettings.getVertexAffiliationMetric();
  }

  /**
   * @return Settings string representation
   */

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
            .append("affiliationMetric", getVertexAffiliationMetric())
            .append("maxDigestSize", getMaxDigestSize())
            .append("aggregateDigests", doAggregateDigests())
            .append("digestRanking", getDigestRanking())
            .build();
  }

  public static class Builder {

    private final ClusteringSettings clusteringSettings;
    private int maxDigestSize = 0;
    private boolean aggregateDigests = false;
    private DigestRanking digestRanking = DEFAULT_COMBINED_RANKING;

    /**
     * Constructor
     *
     * @param clusteringSettings Primary clustering settings
     */

    public Builder(ClusteringSettings clusteringSettings) {
      this.clusteringSettings = clusteringSettings;
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
     * Configure whether the digestor should aggregate all vertices from a cluster's descentants. Default is false,
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
     * Build settings
     *
     * @return A new instance of <code>PostprocessingSettings</code>
     */

    public DigesterSettings build() {
      return new DigesterSettings(clusteringSettings, maxDigestSize, aggregateDigests, digestRanking);
    }


  }


}

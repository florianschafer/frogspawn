/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn;

import net.adeptropolis.frogspawn.clustering.affiliation.AffiliationMetric;
import net.adeptropolis.frogspawn.graphs.Graph;

public class SettingsTestBase {

  protected ClusteringSettings clusteringSettings() {
    return ClusteringSettings.builder()
            .affiliationMetric(new FakeAffiliationMetric())
            .minAffiliation(0.465)
            .minClusterSize(4242)
            .maxIterations(42356)
            .trailSize(783)
            .convergenceThreshold(0.74)
            .randomSeed(23857L)
            .build();
  }

  public static class FakeAffiliationMetric implements AffiliationMetric {

    @Override
    public double[] compute(Graph supergraph, Graph subgraph) {
      return new double[0];
    }

    @Override
    public double[] compute(Graph supergraph, Graph subgraph, Graph subsubgraph) {
      return new double[0];
    }
  }

}
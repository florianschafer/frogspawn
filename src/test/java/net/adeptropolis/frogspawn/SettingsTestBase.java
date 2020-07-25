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
            .withAffiliationMetric(new FakeAffiliationMetric())
            .withMinAffiliation(0.465)
            .withMinClusterSize(4242)
            .withMaxIterations(42356)
            .withTrailSize(783)
            .withConvergenceThreshold(0.74)
            .withRandomSeed(23857L)
            .build();
  }

  protected static class FakeAffiliationMetric implements AffiliationMetric {

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
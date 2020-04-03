/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.algorithms;

import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.graphs.GraphTestBase;
import net.adeptropolis.metis.graphs.algorithms.power_iteration.PowerIteration;
import net.adeptropolis.metis.graphs.algorithms.power_iteration.RandomInitialVectorsSource;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThrows;

public class SpectralBisectorTest extends GraphTestBase {

  private static final RandomInitialVectorsSource IV_SOURCE = new RandomInitialVectorsSource(1337421337L);

  private static final ClusteringSettings settings = ClusteringSettings.builder()
          .withMinClusterSize(0)
          .withMinClusterLikelihood(0)
          .withMinparentOverlap(0)
          .withTrailSize(100)
          .withConvergenceThreshold(0.999)
          .build();

  @Test
  public void completeBipartiteGraphs() throws PowerIteration.MaxIterationsExceededException {
    SpectralBisector bisector = new SpectralBisector(settings);
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    bisector.bisect(completeBipartiteWithWeakLink(), 100000, IV_SOURCE, c);
    List<List<Integer>> partitions = c.vertices();
    assertThat(partitions.get(0), containsInAnyOrder(0, 1, 2, 3, 4));
    assertThat(partitions.get(1), containsInAnyOrder(5, 6, 7, 8));
  }

  @Test
  public void iterationExcessYieldsException() {
    SpectralBisector bisector = new SpectralBisector(settings);
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    assertThrows(PowerIteration.MaxIterationsExceededException.class, () -> {
      bisector.bisect(largeCircle(), 10, IV_SOURCE, c);
    });
  }

}
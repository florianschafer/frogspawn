/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.algorithms;

import net.adeptropolis.nephila.ClusteringSettings;
import net.adeptropolis.nephila.graphs.GraphTestBase;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.PowerIteration;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.SignumConvergence;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class SpectralBisectorTest extends GraphTestBase {

  private static final ClusteringSettings settings = new ClusteringSettings(0, 0, 0, 100, 0.999, 10000);

  private static final SignumConvergence conv = new SignumConvergence(1E-6);
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void completeBipartiteGraphs() throws PowerIteration.MaxIterationsExceededException {
    SpectralBisector bisector = new SpectralBisector(settings);
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    bisector.bisect(completeBipartiteWithWeakLink(), 100000, c);
    List<List<Integer>> partitions = c.vertices();
    assertThat(partitions.get(0), containsInAnyOrder(0, 1, 2, 3, 4));
    assertThat(partitions.get(1), containsInAnyOrder(5, 6, 7, 8));
  }

  @Test
  public void iterationExcessYieldsException() throws PowerIteration.MaxIterationsExceededException {
    exception.expect(PowerIteration.MaxIterationsExceededException.class);
    SpectralBisector bisector = new SpectralBisector(settings);
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    bisector.bisect(largeCircle(), 10, c);
  }

}
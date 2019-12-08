/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.nephila.graphs.Graph;
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

  private static final SignumConvergence conv = new SignumConvergence(1E-6);
  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void completeBipartiteGraphs() throws PowerIteration.MaxIterationsExceededException {
    SpectralBisector bisector = new SpectralBisector(conv);
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    bisector.bisect(completeBipartiteWithWeakLink(), 100000, c);
    List<List<Integer>> partitions = c.vertices();
    assertThat(partitions.get(0), containsInAnyOrder(0, 1, 2, 3, 4));
    assertThat(partitions.get(1), containsInAnyOrder(5, 6, 7, 8));
  }

  @Test
  public void paths() throws PowerIteration.MaxIterationsExceededException {
    SpectralBisector bisector = new SpectralBisector(conv);
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    bisector.bisect(pathWithWeakLink(), 100000, c);
    List<List<Integer>> partitions = c.vertices();
    assertThat(partitions.get(0), containsInAnyOrder(0, 1, 2, 3));
    assertThat(partitions.get(1), containsInAnyOrder(4, 5, 6, 7, 8, 9, 10, 11));
  }

  @Test
  public void iterationExcessYieldsException() throws PowerIteration.MaxIterationsExceededException {
    exception.expect(PowerIteration.MaxIterationsExceededException.class);
    SpectralBisector bisector = new SpectralBisector(new SignumConvergence(0));
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    bisector.bisect(largeCircle(), 10, c);
  }

  @Test
  public void subgraphs() throws PowerIteration.MaxIterationsExceededException {
    SpectralBisector bisector = new SpectralBisector(conv);
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    Graph graph = pathWithWeakLinkEmbeddedIntoLargerGraph()
            .inducedSubgraph(IntIterators.wrap(new int[]{0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110}));
    bisector.bisect(graph, 100000, c);
    List<List<Integer>> partitions = c.vertices();
    assertThat(partitions.get(0), containsInAnyOrder(0, 10, 20, 30));
    assertThat(partitions.get(1), containsInAnyOrder(40, 50, 60, 70, 80, 90, 100, 110));
  }

}
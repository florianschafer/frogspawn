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
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

public class ConnectedComponentsTest extends GraphTestBase {

  @Test
  public void fullyConnectedGraph() {
    Graph graph = butterflyGraph();
    List<List<Integer>> subgraphs = getSubgraphs(graph);
    assertThat(subgraphs, hasSize(1));
    assertThat(subgraphs.get(0), contains(0, 1, 2, 3, 4, 5, 6));
  }

  @Test
  public void removingNonBridgePreservesComponent() {
    Graph graph = butterflyGraph().inducedSubgraph(IntIterators.wrap(new int[]{0, 2, 3, 4, 5, 6}));
    List<List<Integer>> subgraphs = getSubgraphs(graph);
    assertThat(subgraphs, hasSize(1));
    assertThat(subgraphs.get(0), contains(0, 2, 3, 4, 5, 6));
  }

  @Test
  public void splitAtMiddleHub() {
    Graph graph = butterflyGraph().inducedSubgraph(IntIterators.wrap(new int[]{1, 2, 3, 4, 5, 6}));
    List<List<Integer>> subgraphs = getSubgraphs(graph);
    assertThat(subgraphs, hasSize(2));
    assertThat(subgraphs.get(0), contains(1, 2, 3));
    assertThat(subgraphs.get(1), contains(4, 5, 6));
  }

  @Test
  public void splitMultiple() {
    Graph graph = butterflyGraph().inducedSubgraph(IntIterators.wrap(new int[]{1, 3, 4, 5, 6}));
    List<List<Integer>> subgraphs = getSubgraphs(graph);
    assertThat(subgraphs, hasSize(3));
    assertThat(subgraphs.get(0), contains(1));
    assertThat(subgraphs.get(1), contains(3));
    assertThat(subgraphs.get(2), contains(4, 5, 6));
  }

  private List<List<Integer>> getSubgraphs(Graph graph) {
    SubgraphCollectingConsumer consumer = new SubgraphCollectingConsumer();
    ConnectedComponents.find(graph, consumer);
    return consumer.vertices();
  }

}
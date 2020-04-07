/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.labeled;

import com.google.common.collect.ImmutableList;
import net.adeptropolis.metis.graphs.Edge;
import net.adeptropolis.metis.graphs.GraphTestBase;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LabeledGraphBuilderTest extends GraphTestBase {

  @Test
  public void vertexMappings() {
    LabeledGraph<String> labeledGraph = new LabeledGraphBuilder<>(String.class)
            .add("0", "1", 2)
            .add("1", "2", 3)
            .add("3", "4", 5)
            .add("2", "1", 7)
            .build();
    for (int i = 0; i <= 4; i++) {
      assertThat(labeledGraph.getLabel(i), is(String.valueOf(i)));
    }
  }

  @Test
  public void graph() {
    CompressedSparseGraph graph = new LabeledGraphBuilder<>(String.class)
            .add("0", "1", 2)
            .add("1", "2", 3)
            .build()
            .getGraph();
    assertThat(graph.order(), is(3));
    assertThat(graph.size(), is(2 * 2L));
    CollectingEdgeConsumer consumer = new CollectingEdgeConsumer();
    graph.traverseParallel(consumer);
    List<Edge> edges = consumer.getEdges();
    assertThat(edges, is(ImmutableList.of(
            Edge.of(0, 1, 2),
            Edge.of(1, 0, 2),
            Edge.of(1, 2, 3),
            Edge.of(2, 1, 3)
    )));

  }

}
/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import net.adeptropolis.frogspawn.graphs.Edge;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class LabeledGraphSourceTest {

  @Test
  public void tsv() {
    LabeledGraph<String> labeledGraph = LabeledGraphSource.fromTSV(Stream.of("2\t0\t1", "3\t1\t2"));
    assertThat(labeledGraph.getLabel(0), is("0"));
    assertThat(labeledGraph.getLabel(1), is("1"));
    assertThat(labeledGraph.getLabel(2), is("2"));
    assertThat(labeledGraph.getGraph().order(), is(3));
    assertThat(labeledGraph.getGraph().size(), is(2 * 2L));
    GraphTestBase.CollectingEdgeConsumer consumer = new GraphTestBase.CollectingEdgeConsumer();
    labeledGraph.getGraph().traverseParallel(consumer);
    List<Edge> edges = consumer.getEdges();
    assertThat(edges, is(Arrays.asList(
            Edge.of(0, 1, 2),
            Edge.of(1, 0, 2),
            Edge.of(1, 2, 3),
            Edge.of(2, 1, 3)
    )));


  }

}
/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.filters.DegreeFilter;
import net.adeptropolis.frogspawn.graphs.filters.GraphFilter;
import net.adeptropolis.frogspawn.graphs.labeled.labelings.DefaultLabeling;
import net.adeptropolis.frogspawn.graphs.labeled.labelings.Labeling;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

public class LabeledGraphTest extends LabeledGraphTestBase {

  private static Labeling<String> labeling;
  private static LabeledGraph<String> graph;

  @BeforeClass
  public static void setup() {
    labeling = new DefaultLabeling<>(String.class);
    graph = new LabeledGraphBuilder<>(labeling)
            .add("a", "b", 1)
            .add("a", "c", 2)
            .add("c", "d", 3)
            .add("d", "e", 4)
            .add("d", "f", 5)
            .add("d", "g", 6)
            .build();
  }

  @Test
  public void sequentialTraversal() {
    EdgeFingerprinter edges = new EdgeFingerprinter();
    graph.traverse(edges);
    assertThat(edges.fingerprint(), is("a#b#1|a#c#2|b#a#1|c#a#2|c#d#3|d#c#3|d#e#4|d#f#5|d#g#6|e#d#4|f#d#5|g#d#6"));
  }

  @Test
  public void vertexlTraversalAll() {
    EdgeFingerprinter edges = new EdgeFingerprinter();
    graph.traverse("a", edges);
    assertThat(edges.fingerprint(), is("a#b#1|a#c#2"));
  }

  @Test
  public void vertexlTraversalSubset() {
    EdgeFingerprinter edges = new EdgeFingerprinter();
    graph.traverse("b", edges);
    assertThat(edges.fingerprint(), is("b#a#1"));
  }

  @Test
  public void subgraphFromGraph() {
    EdgeFingerprinter edges = new EdgeFingerprinter();
    Graph subgraph = graph.getGraph().subgraph(IntIterators.fromTo(1, graph.getGraph().order()));
    LabeledGraph<String> labelledSubgraph = new LabeledGraph<>(subgraph, labeling);
    labelledSubgraph.traverse(edges);
    assertThat(edges.fingerprint(), is("c#d#3|d#c#3|d#e#4|d#f#5|d#g#6|e#d#4|f#d#5|g#d#6"));
  }

  @Test
  public void subgraphFromVertexStream() {
    EdgeFingerprinter edges = new EdgeFingerprinter();
    LabeledGraph<String> labelledSubgraph = graph.subgraph(Stream.of("b", "c", "d"));
    labelledSubgraph.traverse(edges);
    assertThat(edges.fingerprint(), is("c#d#3|d#c#3"));
  }

  @Test
  public void subgraphFromLabelPredicate() {
    EdgeFingerprinter edges = new EdgeFingerprinter();
    LabeledGraph<String> labelledSubgraph = graph
            .subgraph(Stream.of("b", "c", "d", "e"))
            .subgraph(label -> label.equals("d") || label.equals("e"));
    labelledSubgraph.traverse(edges);
    assertThat(edges.fingerprint(), is("d#e#4|e#d#4"));
  }

  @Test
  public void order() {
    assertThat(graph.order(), is(7));
  }

  @Test
  public void size() {
    assertThat(graph.size(), is(12L));
  }

  @Test
  public void filter() {
    LabeledGraph<String> graph = new LabeledGraphBuilder<>(new DefaultLabeling<>(String.class))
            .add("0", "1", 1)
            .add("1", "2", 1)
            .add("2", "0", 1)
            .add("2", "3", 1)
            .add("3", "4", 1)
            .build();
    GraphFilter filter = new DegreeFilter(2, 0);
    assertThat(graph.filter(filter, false).order(), is(4));
    assertThat(graph.filter(filter, true).order(), is(3));
  }

  @Test
  public void collapse() {
    LabeledGraph<String> graph = new LabeledGraphBuilder<>(new DefaultLabeling<>(String.class))
            .add("0", "1", 1)
            .add("1", "2", 1)
            .add("2", "3", 1)
            .add("3", "1", 1)
            .add("3", "4", 1)
            .build()
            .subgraph(Stream.of("1", "2", "3"))
            .collapse();
    assertThat(graph.order(), is(3));
    assertThat(graph.size(), is(6L));
    assertThat(graph.getLabeling().labels().collect(Collectors.toList()), containsInAnyOrder("1", "2", "3"));
  }

}
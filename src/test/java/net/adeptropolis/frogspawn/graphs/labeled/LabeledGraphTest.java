/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

// TODO: Test on subgraphs!

public class LabeledGraphTest {

  private static Labelling<String> labelling;
  private static LabeledGraph<String> graph;

  @BeforeClass
  public static void setup() {
    Labelling<String> labelling = new DefaultLabelling<>(String.class);
    graph = new LabeledGraphBuilder<>(labelling)
            .add("a", "b", 1)
            .add("a", "c", 2)
            .build();
  }

  @Test
  public void sequentialTraversal() {
    EdgeFingerprinter edges = new EdgeFingerprinter();
    graph.traverse(edges);
    assertThat(edges.fingerprint(), is("a#b#1|a#c#2|b#a#1|c#a#2"));
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


  static class EdgeFingerprinter implements LabeledEdgeConsumer<String> {

    private final Stream.Builder<String> builder = Stream.builder();

    @Override
    public void accept(String left, String right, double weight) {
      builder.add(String.format("%s#%s#%.0f", left, right, weight));
    }

    public String fingerprint() {
      return builder.build().collect(Collectors.joining("|"));
    }

  }

}
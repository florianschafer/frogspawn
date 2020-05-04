/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.traversal;

import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.VertexIterator;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ParallelVertexOpsTest extends GraphTestBase {

  @Test
  public void smallGraph() {
    Graph g = completeGraph(10);
    verify(g);
  }

  @Test
  public void largeGraph() {
    Graph g = largeCircle(150000);
    verify(g);
  }

  private void verify(Graph g) {
    Set<Integer> vertices = Collections.synchronizedSet(new HashSet<>());
    ParallelVertexOps.traverse(g, vertices::add);
    int[] collected = vertices.stream().mapToInt(x -> x).sorted().toArray();
    assertThat(collected.length, is(g.order()));
    VertexIterator it = g.vertexIterator();
    while (it.hasNext()) {
      assertThat(collected[it.localId()], is(it.globalId()));
    }
  }

}
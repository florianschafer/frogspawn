/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.filters;

import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.*;

public class GraphFilterTest extends GraphTestBase {

  private static final GraphFilter shrinkingFilter = graph -> graph.subgraph(v -> graph.vertexIterator().localId() > 0);

  @Test
  public void iterativeApplication() {
    assertThat(shrinkingFilter.applyIteratively(completeGraph(10)).order(), is(0));
  }

}
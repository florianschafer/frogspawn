/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ParentSimilarityPostprocessorTest extends GraphTestBase {

  private Cluster root, c1, c2, c21, c22, c211, c212;
  private ParentSimilarityPostprocessor pp;

  @Before
  public void setup() {
    MockSimilarityMetric metric = new MockSimilarityMetric();
    pp = new ParentSimilarityPostprocessor(metric, 0.5, 2, 0.2);
    root = new Cluster(completeGraph(213));
    root.addToRemainder(IntIterators.wrap(new int[]{0}));
    c1 = new Cluster(root);
    c1.addToRemainder(IntIterators.wrap(new int[]{1}));
    c2 = new Cluster(root);
    c2.addToRemainder(IntIterators.wrap(new int[]{2}));
    c21 = new Cluster(c2);
    c21.addToRemainder(IntIterators.wrap(new int[]{21}));
    c22 = new Cluster(c2);
    c22.addToRemainder(IntIterators.wrap(new int[]{22}));
    c211 = new Cluster(c21);
    c211.addToRemainder(IntIterators.wrap(new int[]{211}));
    c212 = new Cluster(c21);
    c212.addToRemainder(IntIterators.wrap(new int[]{212}));
    pp.apply(root);
  }

  @Test
  public void postprocessorBehavesAsExpected() {
    assertThat(c1.getParent(), is(root));
    assertThat(c2.getParent(), is(root));
    assertThat(c21.getParent(), is(root));
    assertThat(c211.getParent(), is(root));
    assertThat(c22.getParent(), is(c2));
    assertThat(c212.getParent(), is(c21));
  }

  private static class MockSimilarityMetric implements GraphSimilarityMetric {

    @Override
    public double compute(Graph supergraph, Graph subgraph) {
      int superId = clusterFor(supergraph);
      int subId = clusterFor(subgraph);
      if (superId == 0 && subId == 1) return 0.1;
      else if (superId == 0 && subId == 2) return 0.1;
      else if (superId == 2 && subId == 21) return 0.1;
      else if (superId == 2 && subId == 22) return 0.6;
      else if (superId == 21 && subId == 211) return 0.1;
      else if (superId == 21 && subId == 212) return 0.6;
      else if (superId == 2 && subId == 211) return 0.1;
      else throw new RuntimeException(String.format("Undefined Cluster pair %d/%d", superId, subId));
    }

    // Due to the way the remainders are crafted, the lowest vertex id corresponds to the cluster id
    private int clusterFor(Graph graph) {
      return graph.globalVertexIdIterator().nextInt();
    }

  }

}
/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.postprocessing;

import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.GraphTestBase;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.core.Is.is;

@Ignore("This postprocessor is bogus at best. Replace.")
public class ParentSimilarityPostprocessorTest extends GraphTestBase {

  private Graph defaultGraph;
  private Cluster[] clusters;

  @Before
  public void setUp() {
    defaultGraph = completeGraph(18);
    clusters = new Cluster[9];
    clusters[0] = new Cluster(defaultGraph);
    clusters[0].addToRemainder(IntIterators.wrap(new int[]{0, 1}));
    addCluster(1, 0, 2, 3);
    addCluster(2, 0, 4, 5);
    addCluster(3, 2, 6, 7);
    addCluster(4, 2, 8, 9);
    addCluster(5, 4, 10, 11);
    addCluster(6, 4, 12, 13);
    addCluster(7, 6, 14, 15);
    addCluster(8, 6, 16, 17);
  }

  @Test
  public void skipIfRootNode() {
    Postprocessor pp = new ParentSimilarityPostprocessor(0, 15);
    assertThat(pp.apply(clusters[0]), is(false));
  }

  @Test
  public void skipIfParentIsRootNode() {
    Postprocessor pp = new ParentSimilarityPostprocessor(0, 15);
    assertThat(pp.apply(clusters[1]), is(false));
    assertThat(pp.apply(clusters[2]), is(false));
  }

  @Test
  public void parentSatisfiesCriterion() {
    Postprocessor pp = new ParentSimilarityPostprocessor(0.19, 15);
    assertThat(pp.apply(clusters[8]), is(false));
    assertThat(clusters[8].getParent(), is(clusters[6]));
  }

  @Test
  public void leafPushedUpOneLevel() {
    Postprocessor pp = new ParentSimilarityPostprocessor(0.18, 15);
    assertThat(pp.apply(clusters[8]), is(true));
    assertThat(clusters[8].getParent(), is(clusters[4]));
  }

  @Test
  public void leafPushedUpTwoLevels() {
    Postprocessor pp = new ParentSimilarityPostprocessor(0.10, 15);
    assertThat(pp.apply(clusters[8]), is(true));
    assertThat(clusters[8].getParent(), is(clusters[2]));
  }

  @Test
  public void leafPushedUpToRoot() {
    Postprocessor pp = new ParentSimilarityPostprocessor(0.06, 15);
    assertThat(pp.apply(clusters[8]), is(true));
    assertThat(clusters[8].getParent(), is(clusters[0]));
  }

  @Test
  public void clustersFailingThresholdArePushedToRoot() {
    Postprocessor pp = new ParentSimilarityPostprocessor(0.01, 15);
    assertThat(pp.apply(clusters[8]), is(true));
    assertThat(clusters[8].getParent(), is(clusters[0]));
  }

  @Test
  public void overlap() {
    ParentSimilarityPostprocessor pp = new ParentSimilarityPostprocessor(0.01, 15);
    Graph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 3)
            .add(1, 2, 4)
            .add(2, 0, 5)
            .add(1, 3, 6)
            .add(3, 2, 7)
            .add(3, 4, 8)
            .build();
    assertThat(pp.overlap(graph, graph), closeTo(1.0, 1E-6));
    Graph subgraph = graph.inducedSubgraph(IntIterators.wrap(new int[]{0, 1, 2}));
    double overlap = pp.overlap(subgraph, graph);
    assertThat(overlap, closeTo(24d / 37d, 1E-6));
  }

  private void addCluster(int idx, int parent, int... vertices) {
    clusters[idx] = new Cluster(clusters[parent]);
    clusters[idx].addToRemainder(IntIterators.wrap(vertices));
  }

}
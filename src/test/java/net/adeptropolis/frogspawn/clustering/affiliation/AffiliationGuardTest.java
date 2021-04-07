/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.affiliation;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.VertexIterator;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import org.junit.Test;

import static it.unimi.dsi.fastutil.ints.IntComparators.NATURAL_COMPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;

public class AffiliationGuardTest extends GraphTestBase {

  private static final AffiliationMetric METRIC = new DefaultAffiliationMetric();

  @Test
  public void sizeBelowThreshold() {
    SparseGraph graph = new SparseGraphBuilder()
            .add(50, 51, 1)
            .add(51, 52, 1)
            .add(52, 53, 1)
            .build();
    Cluster cluster = new Cluster(graph);
    Graph candidate = graph.subgraph(IntIterators.wrap(new int[]{50, 51, 52}));
    AffiliationGuard affiliationGuard = new AffiliationGuard(METRIC, graph, 10, 0.0);
    Graph subgraphWithGuaranteedAffiliations = affiliationGuard.ensure(cluster, candidate);
    assertThat(subgraphWithGuaranteedAffiliations, is(nullValue()));
    assertThat(cluster.getRemainder(), is(IntArrayList.wrap(new int[]{50, 51, 52})));
  }

  @Test
  public void filteringOutCascade() {
    SparseGraph graph = defaultGraph();
    Cluster cluster = new Cluster(graph);
    Graph candidate = defaultCandidate(graph);
    AffiliationGuard affiliationGuard = new AffiliationGuard(METRIC, graph, 0, 0.75);
    Graph subgraphWithGuaranteedAffiliations = affiliationGuard.ensure(cluster, candidate);
    assertThat(subgraphWithGuaranteedAffiliations, is(notNullValue()));
    cluster.getRemainder().sort(NATURAL_COMPARATOR);
    assertThat(cluster.getRemainder(), is(IntArrayList.wrap(new int[]{52, 53})));
    IntArrayList verticesFulFillingAffiliation = new IntArrayList();
    VertexIterator it = subgraphWithGuaranteedAffiliations.vertexIterator();
    while (it.hasNext()) {
      verticesFulFillingAffiliation.add(it.globalId());
    }
    verticesFulFillingAffiliation.sort(NATURAL_COMPARATOR);
    assertThat(verticesFulFillingAffiliation, is(IntArrayList.wrap(new int[]{50, 51})));
  }

  private SparseGraph defaultGraph() {
    return new SparseGraphBuilder()
            .add(50, 51, 10)
            .add(51, 52, 1)
            .add(52, 53, 1)
            .add(53, 54, 9)
            .build();
  }

  private Graph defaultCandidate(SparseGraph graph) {
    return graph.subgraph(IntIterators.wrap(new int[]{50, 51, 52, 53}));
  }

  @Test
  public void sizeFallsShortDuringIteration() {
    SparseGraph graph = defaultGraph();
    Cluster cluster = new Cluster(graph);
    Graph candidate = defaultCandidate(graph);
    AffiliationGuard affiliationGuard = new AffiliationGuard(METRIC, graph, 4, 0.75);
    Graph subgraphWithGuaranteedAffiliations = affiliationGuard.ensure(cluster, candidate);
    assertThat(subgraphWithGuaranteedAffiliations, is(nullValue()));
    cluster.getRemainder().sort(NATURAL_COMPARATOR);
    assertThat(cluster.getRemainder(), is(IntArrayList.wrap(new int[]{50, 51, 52, 53})));
  }

}
/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering.consistency;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.GraphTestBase;
import net.adeptropolis.metis.graphs.VertexIterator;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static it.unimi.dsi.fastutil.ints.IntComparators.NATURAL_COMPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsNull.notNullValue;

public class ConsistencyGuardTest extends GraphTestBase {

  private static final ConsistencyMetric METRIC = new RelativeWeightConsistencyMetric();

  @Test
  public void sizeBelowThreshold() {
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
            .add(50, 51, 1)
            .add(51, 52, 1)
            .add(52, 53, 1)
            .build();
    Cluster cluster = new Cluster(graph);
    Graph candidate = graph.inducedSubgraph(IntIterators.wrap(new int[]{50, 51, 52}));
    ConsistencyGuard consistencyGuard = new ConsistencyGuard(METRIC, graph, 10, 0.0);
    Graph consistentSubgraph = consistencyGuard.ensure(cluster, candidate);
    assertThat(consistentSubgraph, is(nullValue()));
    assertThat(cluster.getRemainder(), is(IntArrayList.wrap(new int[]{50, 51, 52})));
  }

  @Test
  public void filteringOutCascade() {
    CompressedSparseGraph graph = defaultGraph();
    Cluster cluster = new Cluster(graph);
    Graph candidate = defaultCandidate(graph);
    ConsistencyGuard consistencyGuard = new ConsistencyGuard(METRIC, graph, 0, 0.75);
    Graph consistentSubgraph = consistencyGuard.ensure(cluster, candidate);
    assertThat(consistentSubgraph, is(notNullValue()));
    cluster.getRemainder().sort(NATURAL_COMPARATOR);
    assertThat(cluster.getRemainder(), is(IntArrayList.wrap(new int[]{52, 53})));
    IntArrayList consistentVertices = new IntArrayList();
    VertexIterator it = consistentSubgraph.vertexIterator();
    while (it.hasNext()) {
      consistentVertices.add(it.globalId());
    }
    consistentVertices.sort(NATURAL_COMPARATOR);
    assertThat(consistentVertices, is(IntArrayList.wrap(new int[]{50, 51})));
  }

  @Test
  public void sizeFallsShortDuringIteration() {
    CompressedSparseGraph graph = defaultGraph();
    Cluster cluster = new Cluster(graph);
    Graph candidate = defaultCandidate(graph);
    ConsistencyGuard consistencyGuard = new ConsistencyGuard(METRIC, graph, 4, 0.75);
    Graph consistentSubgraph = consistencyGuard.ensure(cluster, candidate);
    assertThat(consistentSubgraph, is(nullValue()));
    cluster.getRemainder().sort(NATURAL_COMPARATOR);
    assertThat(cluster.getRemainder(), is(IntArrayList.wrap(new int[]{50, 51, 52, 53})));
  }

  private CompressedSparseGraph defaultGraph() {
    return new CompressedSparseGraphBuilder()
            .add(50, 51, 10)
            .add(51, 52, 1)
            .add(52, 53, 1)
            .add(53, 54, 9)
            .build();
  }

  private Graph defaultCandidate(CompressedSparseGraph graph) {
    return graph.inducedSubgraph(IntIterators.wrap(new int[]{50, 51, 52, 53}));
  }

}
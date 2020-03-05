/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.clustering.consistency.ConsistencyMetric;
import net.adeptropolis.metis.clustering.consistency.RelativeWeightConsistencyMetric;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class RecursiveClusteringTest {

  private static Graph graph;
  private static Cluster root;

  @BeforeClass
  public static void initialize() {
    graph = loadSmallGraph();
    ConsistencyMetric metric = new RelativeWeightConsistencyMetric();
    ClusteringSettings settings = ClusteringSettings.builder()
            .withConsistencyMetric(metric)
            .withMinClusterSize(50)
            .withMinClusterLikelihood(0.1)
            .withMinparentOverlap(0.4)
            .withTrailSize(25)
            .withConvergenceThreshold(0.95)
            .withMaxIterations(10000)
            .build();
    root = new RecursiveClustering(graph, settings).run();
  }

  @Test
  public void emptyGraph() {
    Graph emptyGraph = new CompressedSparseGraphBuilder().build();
    ClusteringSettings settings = ClusteringSettings.builder().build();
    Cluster root = new RecursiveClustering(emptyGraph, settings).run();
    assertThat(root.aggregateClusters(), hasSize(1));
    assertThat(root.aggregateClusters(), contains(root));
    assertThat(root.aggregateVertices().size(), is(0));
  }

  @Test
  public void recursionExcessPreservesVertices() {
    ClusteringSettings settings = ClusteringSettings.builder().withMaxIterations(0).build();
    root = new RecursiveClustering(graph, settings).run();
    IntOpenHashSet allClusterVertices = new IntOpenHashSet(root.aggregateVertices().iterator());
    IntOpenHashSet allGraphVertices = new IntOpenHashSet(graph.collectVertices());
    assertThat(allClusterVertices.size(), is(allGraphVertices.size()));
    assertThat(allClusterVertices, is(allGraphVertices));
  }

  @Test
  public void verticesAreUnique() {
    int distinctClusterVertices = new IntOpenHashSet(root.aggregateVertices().iterator()).size();
    assertThat(root.aggregateVertices().size(), is(distinctClusterVertices));
  }

  @Test
  public void allVerticesArePreserved() {
    IntOpenHashSet allClusterVertices = new IntOpenHashSet(root.aggregateVertices().iterator());
    IntOpenHashSet allGraphVertices = new IntOpenHashSet(graph.collectVertices());
    assertThat(allClusterVertices.size(), is(allGraphVertices.size()));
    assertThat(allClusterVertices, is(allGraphVertices));
  }

  private static Graph loadSmallGraph() {
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    try {
      Files.lines(Paths.get(ClassLoader.getSystemResource("small_graph.tsv").toURI()))
              .forEach(line -> {
                String[] comps = line.split("\t");
                builder.add(Integer.parseInt(comps[1]), Integer.parseInt(comps[2]), Double.parseDouble(comps[0]));
              });
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
    return builder.build();
  }

}
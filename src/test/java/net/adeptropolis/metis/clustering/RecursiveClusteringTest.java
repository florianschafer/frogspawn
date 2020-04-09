/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.digest.ClusterDigester;
import net.adeptropolis.metis.digest.Digest;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RecursiveClusteringTest {

  private static Graph defaultGraph;
  private static Cluster root;
  private static ClusteringSettings defaultSettings;

  @BeforeClass
  public static void initialize() {
    defaultGraph = loadGraph("small_graph.tsv");
    defaultSettings = ClusteringSettings.builder()
            .withMinClusterSize(50)
            .withMinVertexConsistency(0.1)
            .withMinparentOverlap(0.4)
            .build();
    root = RecursiveClustering.run(defaultGraph, defaultSettings);
  }

  private static Graph loadGraph(String filename) {
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    try {
      Files.lines(Paths.get(ClassLoader.getSystemResource(filename).toURI()))
              .forEach(line -> {
                String[] comps = line.split("\t");
                builder.add(Integer.parseInt(comps[1]), Integer.parseInt(comps[2]), Double.parseDouble(comps[0]));
              });
    } catch (IOException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
    return builder.build();
  }

  @Test
  public void emptyGraph() {
    Graph emptyGraph = new CompressedSparseGraphBuilder().build();
    ClusteringSettings settings = ClusteringSettings.builder().build();
    Cluster root = RecursiveClustering.run(emptyGraph, settings);
    assertThat(root.aggregateClusters(), hasSize(1));
    assertThat(root.aggregateClusters(), contains(root));
    assertThat(root.aggregateVertices().size(), is(0));
  }

  @Test
  public void recursionExcessPreservesVertices() {
    ClusteringSettings settings = ClusteringSettings.builder().withMaxIterations(0).build();
    root = RecursiveClustering.run(defaultGraph, settings);
    IntOpenHashSet allClusterVertices = new IntOpenHashSet(root.aggregateVertices().iterator());
    IntOpenHashSet allGraphVertices = new IntOpenHashSet(defaultGraph.collectVertices());
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
    IntOpenHashSet allGraphVertices = new IntOpenHashSet(defaultGraph.collectVertices());
    assertThat(allClusterVertices.size(), is(allGraphVertices.size()));
    assertThat(allClusterVertices, is(allGraphVertices));
  }

  @Test
  public void determinismSmall() {
    verifyDeterminism(defaultGraph, defaultSettings, 10);
  }

  @Test
  public void determinismMedium() {
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinClusterSize(50)
            .withMinVertexConsistency(0.05)
            .withMinparentOverlap(0.65)
            .build();
    Graph graph = loadGraph("medium_graph.tsv");
    verifyDeterminism(graph, settings, 5);
  }

  private void verifyDeterminism(Graph graph, ClusteringSettings settings, int rounds) {
    ClusterDigester digester = new ClusterDigester(settings);
    long refFp = hierarchyFingerprint(RecursiveClustering.run(graph, settings), digester);
    for (int i = 0; i < rounds - 1; i++) {
      long fp = hierarchyFingerprint(RecursiveClustering.run(graph, settings), digester);
      assertThat(fp, is(refFp));
    }
  }

  private long hierarchyFingerprint(Cluster cluster, ClusterDigester digester) {
    Digest digest = digester.digest(cluster);
    long fp = cluster.depth() + digestFingerprint(digest);
    for (Cluster child : cluster.getChildren()) {
      fp += hierarchyFingerprint(child, digester);
    }
    return fp;
  }

  private long digestFingerprint(Digest digest) {
    long fp = 0;
    for (int i = 0; i < digest.size(); i++) {
      fp += Math.round(1000 * (i + 1) * digest.getVertices()[i] * digest.getWeights()[i] * digest.getScores()[i]);
    }
    return fp;
  }

}
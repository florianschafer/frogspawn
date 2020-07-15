/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessing;
import net.adeptropolis.frogspawn.clustering.postprocessing.PostprocessingSettings;
import net.adeptropolis.frogspawn.digest.ClusterDigester;
import net.adeptropolis.frogspawn.digest.Digest;
import net.adeptropolis.frogspawn.digest.DigesterSettings;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import static net.adeptropolis.frogspawn.digest.DigestRankings.COMBINED_RANKING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RecursiveClusteringTest {

  private static final Pattern COMMENT_PATTERN = Pattern.compile("^\\s*#.*");
  private static Graph defaultGraph;
  private static Cluster root;
  private static ClusteringSettings defaultSettings;

  @BeforeClass
  public static void initialize() {
    defaultGraph = loadGraph("small_graph.tsv");
    defaultSettings = ClusteringSettings.builder()
            .withMinClusterSize(50)
            .withMinVertexAffiliation(0.1)
            .build();
    root = RecursiveClustering.run(defaultGraph, defaultSettings);
  }

  private static Graph loadGraph(String filename) {
    SparseGraphBuilder builder = new SparseGraphBuilder();
    try {
      Files.lines(Paths.get(ClassLoader.getSystemResource(filename).toURI()))
              .filter(line -> !COMMENT_PATTERN.matcher(line).matches())
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
    Graph emptyGraph = new SparseGraphBuilder().build();
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
            .withMinVertexAffiliation(0.05)
            .build();
    Graph graph = loadGraph("medium_graph.tsv");
    verifyDeterminism(graph, settings, 5);
  }

  @Test
  public void problematicSmallGraph() {
    SparseGraph graph = new SparseGraphBuilder()
            .add(0, 1, 1)
            .add(1, 2, 1)
            .add(2, 3, 1)
            .add(3, 4, 1)
            .add(4, 5, 1)
            .add(0, 6, 1)
            .add(6, 7, 1)
            .add(7, 8, 1)
            .add(8, 5, 1)
            .add(9, 10, 1)
            .add(10, 9, 1)
            .build();
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinVertexAffiliation(0.5)
            .withMinClusterSize(1)
            .build();
    IntOpenHashSet vertices = new IntOpenHashSet();
    RecursiveClustering.run(graph, settings).traverse(cluster -> {
      digester(settings).digest(cluster)
              .map((vertexId, weight, score) -> vertexId)
              .forEach(v -> vertices.add((int) v));
    });
    assertThat(vertices.size(), is(11));
  }

  private void verifyDeterminism(Graph graph, ClusteringSettings settings, int rounds) {
    long refFp = fingerprintWithPostprocessing(graph, settings);
    for (int i = 0; i < rounds - 1; i++) {
      long fp = fingerprintWithPostprocessing(graph, settings);
      assertThat(fp, is(refFp));
    }
  }

  private long fingerprintWithPostprocessing(Graph graph, ClusteringSettings settings) {
    Cluster root = RecursiveClustering.run(graph, settings);
    Cluster postprocessed = Postprocessing.apply(root, PostprocessingSettings.builder(settings).build());
    return hierarchyFingerprint(postprocessed, digester(settings));
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

  private final ClusterDigester digester(ClusteringSettings settings) {
    DigesterSettings digesterSettings = DigesterSettings.builder(settings)
            .withDigestRanking(COMBINED_RANKING.apply(1.75)).build();
    return new ClusterDigester(digesterSettings);

  }

}
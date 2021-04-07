/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.persistence;

import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.labeled.LabeledGraph;
import net.adeptropolis.frogspawn.graphs.labeled.LabeledGraphBuilder;
import net.adeptropolis.frogspawn.graphs.labeled.labelings.DefaultLabeling;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class SnapshotTest extends GraphTestBase {

  private LabeledGraph<String> graph;
  private Cluster root;
  private Snapshot restored;

  @Before
  public void setup() {
    createCompleteGraph();
    setupClusters();
    File snapshot = save(root, graph);
    restored = Snapshot.load(snapshot);
  }

  private void createCompleteGraph() {
    LabeledGraphBuilder<String> builder = new LabeledGraphBuilder<>(new DefaultLabeling<>(String.class));
    for (int i = 0; i < 20; i++) {
      for (int j = i + 1; j < 20; j++) {
        builder.add(String.valueOf(i), String.valueOf(j), i + j);
      }
    }
    graph = builder.build();
  }

  private void setupClusters() {
    root = new Cluster(graph.getGraph());
    root.addToRemainder(IntIterators.fromTo(0, 3));
    Cluster c1 = new Cluster(root);
    c1.addToRemainder(IntIterators.fromTo(3, 7));
    Cluster c2 = new Cluster(root);
    c2.addToRemainder(IntIterators.fromTo(7, 11));
    Cluster c11 = new Cluster(c1);
    c11.addToRemainder(IntIterators.fromTo(11, 20));
  }

  private File save(Cluster root, LabeledGraph<String> graph) {
    try {
      File tmpFile = File.createTempFile(UUID.randomUUID().toString(), null);
      tmpFile.deleteOnExit();
      Snapshot.save(tmpFile, root, graph);
      return tmpFile;
    } catch (IOException e) {
      throw new SnapshotException(e);
    }
  }

  @Test
  public void ClusterIds() {
    IntOpenHashSet clusterIds = new IntOpenHashSet();
    restored.getRoot().traverse(cluster -> clusterIds.add(cluster.getId()));
    IntOpenHashSet restoredClusterIds = new IntOpenHashSet();
    restored.getRoot().traverse(cluster -> restoredClusterIds.add(cluster.getId()));
    assertThat(restoredClusterIds, is(clusterIds));
  }

  @Test
  public void children() {
    assertThat(restored.getRoot().getParent(), nullValue());
    assertThat(restored.getRoot().getChildren().size(), is(2));
    assertThat(restored.getRoot().getChildren().iterator().next().getChildren().size(), is(1));
  }

  @Test
  public void remainders() {
    assertThat(restored.getRoot().getRemainder(), is(root.getRemainder()));
  }

  @Test
  public void graphs() {
    assertThat(restored.getGraph().getGraph().size(), is(graph.getGraph().size()));
    assertThat(restored.getGraph().getGraph().order(), is(graph.getGraph().order()));
  }

}
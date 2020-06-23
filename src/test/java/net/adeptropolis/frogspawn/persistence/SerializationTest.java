/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.persistence;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.graphs.Edge;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import net.adeptropolis.frogspawn.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigDoubles;
import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigInts;
import net.adeptropolis.frogspawn.graphs.labeled.LabeledGraph;
import net.adeptropolis.frogspawn.graphs.labeled.LabeledGraphBuilder;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static net.adeptropolis.frogspawn.graphs.implementations.arrays.BigDoubles.BIN_BITS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class SerializationTest extends GraphTestBase {

  private static final Random RAND = new Random(1337L);

  @Test
  public void bigInts() throws IOException {
    BigInts bigInts = new BigInts(10);
    for (int i = 0; i < 3 * (1 << BIN_BITS) + 10; i++) {
      bigInts.set(i, RAND.nextInt());
    }
    verifyEqualsAfterSerialization(bigInts);
  }

  @Test
  public void bigDoubles() throws IOException {
    BigDoubles bigDoubles = new BigDoubles(10);
    for (int i = 0; i < 3 * (1 << BIN_BITS) + 10; i++) {
      bigDoubles.set(i, RAND.nextDouble());
    }
    verifyEqualsAfterSerialization(bigDoubles);
  }

  @Test
  public void sparseGraph() throws IOException {
    CompressedSparseGraph graph = WEIGHTED_K20;
    verifyGraph(graph);
  }

  @Test
  public void subgraph() throws IOException {
    Graph graph = WEIGHTED_K20
            .inducedSubgraph(IntIterators.wrap(new int[]{1, 2, 3, 4, 5, 8}));
    verifyGraph(graph);
  }

  @Test
  public void labeledGraph() throws IOException {
    LabeledGraphBuilder<String> builder = new LabeledGraphBuilder<>(String.class);
    for (int i = 0; i < 200; i++) {
      for (int j = i + 1; j < 200; j++) {
        builder.add(String.valueOf(i), String.valueOf(j), i + j);
      }
    }
    LabeledGraph<String> graph = builder.build();
    List<String> graphLabels = Arrays.stream(graph.getLabels()).collect(Collectors.toList());
    LabeledGraph<String> deserialized = Serialization.load(save(graph));
    List deserializedLabels = Arrays.stream(deserialized.getLabels()).collect(Collectors.toList());
    assertThat(deserializedLabels, is(graphLabels));
    assertThat(deserialized.getGraph().order(), is(graph.getGraph().order()));
    assertThat(deserialized.getGraph().size(), is(graph.getGraph().size()));
  }

  @Test
  public void cluster() throws IOException {
    Graph graph = completeGraph(20);
    Cluster root = new Cluster(graph);
    root.addToRemainder(IntIterators.fromTo(0, 9));
    Cluster child = new Cluster(root);
    child.addToRemainder(IntIterators.fromTo(10, 20));
    Cluster deserialized = Serialization.load(save(root));
    assertThat(deserialized.getChildren(), hasSize(1));
    assertThat(deserialized.getRemainder(), is(new IntArrayList(IntIterators.fromTo(0, 9))));
    assertThat(deserialized.getChildren().iterator().next().getRemainder(), is(new IntArrayList(IntIterators.fromTo(10, 20))));
  }

  private void verifyGraph(Graph graph) throws IOException {
    int[] graphVertices = IntIterators.unwrap(graph.globalVertexIdIterator());
    List<Edge> graphEdges = getEdges(graph);
    Graph deserialized = Serialization.load(save(graph));
    int[] deserializedVertices = IntIterators.unwrap(deserialized.globalVertexIdIterator());
    List<Edge> deserializedEdges = getEdges(deserialized);
    assertThat(deserialized.order(), is(graph.order()));
    assertThat(deserialized.size(), is(graph.size()));
    assertThat(ArrayUtils.toObject(deserializedVertices), is(ArrayUtils.toObject(graphVertices)));
    assertThat(deserializedEdges, is(graphEdges));
  }

  private List<Edge> getEdges(Graph graph) {
    CollectingEdgeConsumer graphEdgeConsumer = new CollectingEdgeConsumer();
    for (int i = 0; i < graph.order(); i++) {
      graph.traverseIncidentEdges(i, graphEdgeConsumer, TraversalMode.DEFAULT);
    }
    return graphEdgeConsumer.getEdges();
  }

  private <T> void verifyEqualsAfterSerialization(Object object) throws IOException {
    T deserialized = Serialization.load(save(object));
    assertThat(deserialized, is(object));
  }

  private File save(Object object) throws IOException {
    File tmpFile = File.createTempFile(UUID.randomUUID().toString(), null);
    tmpFile.deleteOnExit();
    Serialization.save(object, tmpFile);
    return tmpFile;
  }

}
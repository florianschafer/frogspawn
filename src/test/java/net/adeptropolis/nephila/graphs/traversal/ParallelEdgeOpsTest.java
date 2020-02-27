/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.traversal;

import net.adeptropolis.nephila.Edge;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.GraphTestBase;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ParallelEdgeOpsTest extends GraphTestBase implements Thread.UncaughtExceptionHandler {

  @Test
  @Ignore("Intended for performance debugging")
  public void perfTest() {
    Graph graph = bandedGraph(120000, 20);
    while (true) {
      traverseFingerprint(graph);
    }
  }

  @Test
  public void emptyGraph() {
    CompressedSparseGraph graph = CompressedSparseGraph.builder().build();
    ParallelEdgeOps.traverse(graph, consumer);
    assertThat(consumer.getEdges(), is(empty()));
  }

  @Test
  public void singleEdgeGraph() {
    CompressedSparseGraph graph = CompressedSparseGraph.builder()
            .add(2, 3, 3.14)
            .build();
    ParallelEdgeOps.traverse(graph, consumer);
    assertThat(consumer.getEdges(), hasSize(2));
    MatcherAssert.assertThat(consumer.getEdges(), Matchers.containsInAnyOrder(
            Edge.of(2, 3, 3.14),
            Edge.of(3, 2, 3.14)));
  }

  @Test
  public void largeBandedGraph() {
    Graph graph = bandedGraph(20000, 100);
    assertThat("Fingerprint mismatch", traverseFingerprint(graph), is(bandedGraphFingerprint(20000, 100)));
  }

  @Test
  public void parallelTraversal() {
    List<Thread> threads = IntStream.range(0, 50).mapToObj(i -> {
      Thread thread = new Thread(() -> {
        Graph graph = bandedGraph(10000, 30);
        FingerprintingEdgeConsumer fp = new FingerprintingEdgeConsumer();
        ParallelEdgeOps.traverse(graph, fp);
        assertThat("Fingerprint mismatch", fp.getFingerprint(), is(bandedGraphFingerprint(10000, 30)));
      });
      thread.setUncaughtExceptionHandler(this);
      thread.start();
      return thread;
    }).collect(Collectors.toList());
    threads.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public void uncaughtException(Thread thread, Throwable throwable) {
    throw new RuntimeException(thread.getName(), throwable);
  }
}


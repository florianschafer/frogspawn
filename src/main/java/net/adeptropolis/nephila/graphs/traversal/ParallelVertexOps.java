/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs.traversal;

import net.adeptropolis.nephila.graphs.Graph;

import java.util.concurrent.CountDownLatch;

/**
 * Parallel Vertex Operations
 */

public final class ParallelVertexOps extends ParallelOps implements Runnable {

  private final VertexConsumer consumer;

  /**
   * Constructor
   *
   * @param graph    Graph whose vertices should be traversed
   * @param consumer Instance of VertexConsumer
   * @param slice    Slice (essentially an identifier for the individual thread workload)
   * @param latch    Countdown latch
   */

  private ParallelVertexOps(Graph graph, VertexConsumer consumer, int slice, CountDownLatch latch) {
    super(graph, slice, latch);
    this.consumer = consumer;
  }

  /**
   * Traverse over all vertices of a given graph. Graphs larger than <code>PARALLELIZATION_THRESHOLD</code>
   * will be traversed in parallel.
   *
   * @param graph    Graph
   * @param consumer Instance of VertexConsumer
   */

  public static void traverse(Graph graph, VertexConsumer consumer) {
    if (graph.order() >= PARALLELIZATION_THRESHOLD) {
      traverseParallel(graph, consumer);
    } else {
      for (int i = 0; i < graph.order(); i++) {
        consumer.accept(i);
      }
    }
  }

  /**
   * Parallel traversal over all vertices of a given graph
   *
   * @param graph    Graph
   * @param consumer Instance of VertexConsumer
   */

  private static void traverseParallel(Graph graph, VertexConsumer consumer) {
    CountDownLatch latch = new CountDownLatch(THREAD_POOL_SIZE);
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      EXECUTOR.submit(new ParallelVertexOps(graph, consumer, i, latch));
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new ParallelOpsException(e);
    }
  }

  /**
   * Runnable entry point. Traverse all vertices from the current slice and decrease the latch upon completion.
   */

  @Override
  public void run() {
    int v;
    for (int i = 0; (v = i * THREAD_POOL_SIZE + slice) < graph.order(); i++) {
      consumer.accept(v);
    }
    latch.countDown();
  }

}

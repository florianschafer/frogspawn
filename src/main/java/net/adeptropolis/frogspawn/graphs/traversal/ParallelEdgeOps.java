/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.traversal;

import net.adeptropolis.frogspawn.graphs.Graph;

import java.util.concurrent.CountDownLatch;

/**
 * Parallel Edge Operations
 */

public final class ParallelEdgeOps extends ParallelOps implements Runnable {

  private final EdgeConsumer consumer;

  /**
   * Constructor
   *
   * @param graph    Graph whose edges should be traversed
   * @param consumer Instance of EdgeConsumer
   * @param slice    Slice (essentially an identifier for the individual thread workload)
   * @param latch    Countdown latch
   */

  private ParallelEdgeOps(Graph graph, EdgeConsumer consumer, int slice, CountDownLatch latch) {
    super(graph, slice, latch);
    this.consumer = consumer;
  }

  /**
   * Parallel traversal over all edges of a given graph
   *
   * @param graph    Graph
   * @param consumer Instance of EdgeConsumer
   */

  public static void traverse(Graph graph, EdgeConsumer consumer) {
    if (graph.order() >= PARALLELIZATION_THRESHOLD) {
      traverseParallel(graph, consumer);
    } else {
      for (int i = 0; i < graph.order(); i++) {
        graph.traverseIncidentEdges(i, consumer);
      }
    }
  }

  /**
   * Parallel traversal over all edges of a given graph
   *
   * @param graph    Graph
   * @param consumer Instance of EdgeConsumer
   */

  private static void traverseParallel(Graph graph, EdgeConsumer consumer) {
    CountDownLatch latch = new CountDownLatch(THREAD_POOL_SIZE);
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      EXECUTOR.submit(new ParallelEdgeOps(graph, consumer, i, latch));
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ParallelOpsException(e);
    }
  }

  /**
   * Runnable entry point. Traverse all edges from the current slice and decrease the latch upon completion.
   */

  @Override
  public void run() {
    int v;
    for (int i = 0; (v = i * THREAD_POOL_SIZE + slice) < graph.order(); i++) {
      graph.traverseIncidentEdges(v, consumer);
    }
    latch.countDown();
  }

}

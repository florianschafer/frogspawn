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
  private final TraversalMode mode;

  /**
   * Constructor
   *
   * @param graph    Graph whose edges should be traversed
   * @param consumer Instance of EdgeConsumer
   * @param slice    Slice (essentially an identifier for the individual thread workload)
   * @param mode     Selected traversal mode
   * @param latch    Countdown latch
   */

  private ParallelEdgeOps(Graph graph, EdgeConsumer consumer, int slice, TraversalMode mode, CountDownLatch latch) {
    super(graph, slice, latch);
    this.consumer = consumer;
    this.mode = mode;
  }

  /**
   * Parallel traversal over all edges of a given graph
   *
   * @param graph    Graph
   * @param consumer Instance of EdgeConsumer
   * @param mode     Traversal mode
   */

  public static void traverse(Graph graph, EdgeConsumer consumer, TraversalMode mode) {
    if (graph.order() >= PARALLELIZATION_THRESHOLD) {
      traverseParallel(graph, mode, consumer);
    } else {
      for (int i = 0; i < graph.order(); i++) {
        graph.traverseIncidentEdges(i, consumer, mode);
      }
    }
  }

  /**
   * Parallel traversal over all edges of a given graph
   *
   * @param graph    Graph
   * @param mode     Traversal mode
   * @param consumer Instance of EdgeConsumer
   */

  private static void traverseParallel(Graph graph, TraversalMode mode, EdgeConsumer consumer) {
    CountDownLatch latch = new CountDownLatch(THREAD_POOL_SIZE);
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      EXECUTOR.submit(new ParallelEdgeOps(graph, consumer, i, mode, latch));
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ParallelOpsException(e);
    }
  }

  /**
   * Return the slice a vertex will fall into during traversal.
   *
   * @param v Local vertex id
   * @return Slice index
   */

  public static int slice(int v) {
    return v % THREAD_POOL_SIZE;
  }

  /**
   * Return the maximum number of slices
   *
   * @return Maximum slice index (exclusive)
   */

  public static int slices() {
    return THREAD_POOL_SIZE;
  }

  /**
   * Runnable entry point. Traverse all edges from the current slice and decrease the latch upon completion.
   */

  @Override
  public void run() {
    int v;
    for (int i = 0; (v = i * THREAD_POOL_SIZE + slice) < graph.order(); i++) {
      graph.traverseIncidentEdges(v, consumer, mode);
    }
    latch.countDown();
  }

}

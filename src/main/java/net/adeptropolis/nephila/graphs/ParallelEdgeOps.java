/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs;

import java.util.concurrent.CountDownLatch;

public final class ParallelEdgeOps extends ParallelOps implements Runnable {

  private final EdgeConsumer consumer;

  private ParallelEdgeOps(Graph graph, EdgeConsumer consumer, int slice, CountDownLatch latch) {
    super(graph, slice, latch);
    this.consumer = consumer;
  }

  public static void traverse(Graph graph, EdgeConsumer consumer) {
    if (graph.order() >= PARALLELIZATION_THRESHOLD) {
      traverseParallel(graph, consumer);
    } else {
      for (int i = 0; i < graph.order(); i++) {
        graph.traverseParallel(i, consumer);
      }
    }
  }

  private static void traverseParallel(Graph graph, EdgeConsumer consumer) {
    CountDownLatch latch = new CountDownLatch(THREAD_POOL_SIZE);
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      EXECUTOR.submit(new ParallelEdgeOps(graph, consumer, i, latch));
    }
    try {
      latch.await();
    } catch (InterruptedException e) {
      throw new ParallelOpsException(e);
    }
  }

  @Override
  public void run() {
    int v;
    for (int i = 0; (v = i * THREAD_POOL_SIZE + slice) < graph.order(); i++) {
      graph.traverseParallel(v, consumer);
    }
    latch.countDown();
  }

}

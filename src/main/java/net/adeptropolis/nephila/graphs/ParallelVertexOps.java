/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila.graphs;

import java.util.concurrent.CountDownLatch;

// TODO: Tie this one and ParallelEdgeOps together
public final class ParallelVertexOps extends ParallelOps implements Runnable {

  private final VertexConsumer consumer;

  private ParallelVertexOps(Graph graph, VertexConsumer consumer, int slice, CountDownLatch latch) {
    super(graph, slice, latch);
    this.consumer = consumer;
  }

  public static void traverse(Graph graph, VertexConsumer consumer) {
    if (graph.order() >= PARALLELIZATION_THRESHOLD) {
      traverseParallel(graph, consumer);
    } else {
      for (int i = 0; i < graph.order(); i++) {
        consumer.accept(i);
      }
    }
  }

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

  @Override
  public void run() {
    int v;
    for (int i = 0; (v = i * THREAD_POOL_SIZE + slice) < graph.order(); i++) {
      consumer.accept(v);
    }
    latch.countDown();
  }

}

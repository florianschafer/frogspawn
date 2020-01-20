/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs;

import java.util.concurrent.CountDownLatch;

// TODO: Tie this one ane ParallelEdgeOps together
public final class ParallelVertexOps extends ParallelOps implements Runnable {

  private final VertexConsumer consumer;

  private ParallelVertexOps(Graph graph, VertexConsumer consumer, int slice, CountDownLatch latch) {
    super(graph, slice, latch);
    this.consumer = consumer;
  }

  public static void traverse(Graph graph, VertexConsumer consumer) {
    if (graph.size() >= PARALLELIZATION_THRESHOLD) {
      traverseParallel(graph, consumer);
    } else {
      for (int i = 0; i < graph.size(); i++) {
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
    for (int i = 0; (v = i * THREAD_POOL_SIZE + slice) < graph.size(); i++) {
      consumer.accept(v);
    }
    latch.countDown();
  }

}

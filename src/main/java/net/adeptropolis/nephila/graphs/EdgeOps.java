/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs;

import java.util.concurrent.*;

public final class EdgeOps implements Runnable {

  public static final int PARALLELIZATION_THRESHOLD = 2000;
  private static final int THREAD_POOL_SIZE = 2 * Runtime.getRuntime().availableProcessors();
  private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
          THREAD_POOL_SIZE, THREAD_POOL_SIZE, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

  private final Graph graph;
  private final EdgeConsumer consumer;
  private final int slice;

  private EdgeOps(Graph graph, EdgeConsumer consumer, int slice) {
    this.graph = graph;
    this.consumer = consumer;
    this.slice = slice;
  }

  public static void traverse(Graph graph, EdgeConsumer consumer) {
    if (graph.size() >= PARALLELIZATION_THRESHOLD) {
      traverseParallel(graph, consumer);
    } else {
      for (int i = 0; i < graph.size(); i++) {
        graph.traverse(i, consumer);
      }
    }
  }

  private static void traverseParallel(Graph graph, EdgeConsumer consumer) {
    Future[] futures = new Future[THREAD_POOL_SIZE];
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      futures[i] = EXECUTOR.submit(new EdgeOps(graph, consumer, i));
    }
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      awaitFuture(futures[i]);
    }
  }

  @SuppressWarnings("squid:S2142")
  private static void awaitFuture(Future<?> future) {
    try {
      future.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new EdgeOpsException(e);
    }
  }

  @Override
  public void run() {
    int v;
    for (int i = 0; (v = i * THREAD_POOL_SIZE + slice) < graph.size(); i++) {
      graph.traverse(v, consumer);
    }
  }

}

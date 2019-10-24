package net.adeptropolis.nephila.graphs;

import net.adeptropolis.nephila.graphs.implementations.View;

import java.util.concurrent.*;

public final class EdgeOps implements Runnable {

  private static final int THREAD_POOL_SIZE = 2 * Runtime.getRuntime().availableProcessors();
  private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
          THREAD_POOL_SIZE, THREAD_POOL_SIZE, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

  private Graph graph;
  private EdgeConsumer consumer;
  private int slice;
  @Deprecated
  private View view;

  private EdgeOps(Graph graph, EdgeConsumer consumer, int slice, View view) {
    this.graph = graph;
    this.consumer = consumer;
    this.slice = slice;
    this.view = view;
  }

  public static void traverse(Graph graph, EdgeConsumer consumer) {
    Future[] futures = new Future[THREAD_POOL_SIZE];
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      futures[i] = EXECUTOR.submit(new EdgeOps(graph, consumer, i, null));
    }
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      awaitFuture(futures[i]);
    }
  }

  @Deprecated
  public static void traverse(View view, EdgeConsumer consumer) {
    Future[] futures = new Future[THREAD_POOL_SIZE];
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      futures[i] = EXECUTOR.submit(new EdgeOps(null, consumer, i, view));
    }
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      awaitFuture(futures[i]);
    }
  }

  private static void awaitFuture(Future<?> x) {
    try {
      x.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void run() {
    int v;
    if (graph != null) {
      for (int i = 0; (v = i * THREAD_POOL_SIZE + slice) < graph.size(); i++) {
        graph.traverse(v, consumer);
      }
    } else if (view != null) {
      for (int i = 0; (v = i * THREAD_POOL_SIZE + slice) < view.size(); i++) {
        view.traverseAdjacent(v, consumer);
      }
    }
  }

}

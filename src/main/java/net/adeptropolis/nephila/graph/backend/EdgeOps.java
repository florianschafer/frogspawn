package net.adeptropolis.nephila.graph.backend;

import net.adeptropolis.nephila.graph.Graph;

import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public final class EdgeOps implements Runnable {

  private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
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

  private static void awaitFuture(Future<?> x) {
    try {
      x.get();
    } catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

}

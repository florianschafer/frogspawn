package net.adeptropolis.nephila.graph.backend;

import net.adeptropolis.nephila.graph.Graph;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;


// TODO: Improve / optimize threading, object churn and runnable re-use

public class ParallelEdgeTraversal {

  private static final int THREAD_BATCH_SIZE = 64;
  private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

  private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
  private static final Future[] futures = new Future[THREAD_POOL_SIZE];

  private final AtomicInteger workPtr;

  public ParallelEdgeTraversal() {
    this.workPtr = new AtomicInteger();
  }

  public void traverse(EdgeConsumer consumer, Graph graph) {
    consumer.reset();
    workPtr.set(0);
    for (int i = 0; i < THREAD_POOL_SIZE; i++)
      futures[i] = executorService.submit(() -> fetchAndProcess(graph, consumer));
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      try {
        futures[i].get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void fetchAndProcess(Graph graph, EdgeConsumer consumer) {
    int i;
    while ((i = workPtr.getAndAdd(THREAD_BATCH_SIZE)) < graph.size()) {
      for (int j = i; j < Math.min(i + THREAD_BATCH_SIZE, graph.size()); j++) {
        graph.traverseByLocalId(j, consumer);
      }
    }
  }

  @Deprecated
  void traverse(EdgeConsumer visitor, View view) {
    visitor.reset();
    workPtr.set(0);
    for (int i = 0; i < THREAD_POOL_SIZE; i++)
      futures[i] = executorService.submit(() -> fetchAndProcess(view, visitor));
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      try {
        futures[i].get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Deprecated
  private void fetchAndProcess(View view, EdgeConsumer visitor) {
    int i;
    while ((i = workPtr.getAndAdd(THREAD_BATCH_SIZE)) < view.size()) {
      for (int j = i; j < Math.min(i + THREAD_BATCH_SIZE, view.size()); j++) {
        view.traverseAdjacent(j, visitor);
      }
    }
  }

  void cleanup() {
    executorService.shutdown();
  }

}

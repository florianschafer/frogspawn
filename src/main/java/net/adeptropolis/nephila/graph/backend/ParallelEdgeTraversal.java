package net.adeptropolis.nephila.graph.backend;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;


// TODO: Improve / optimize threading, object churn and runnable re-use

class ParallelEdgeTraversal {

  private static final int THREAD_BATCH_SIZE = 64;
  private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

  private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
  private static final Future[] futures = new Future[THREAD_POOL_SIZE];

  private final AtomicInteger workPtr;

  ParallelEdgeTraversal() {
    this.workPtr = new AtomicInteger();
  }

  void traverse(final EdgeVisitor visitor, View view) {
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

  private void fetchAndProcess(View view, final EdgeVisitor visitor) {
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

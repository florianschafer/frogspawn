package net.adeptropolis.nephila.graph.implementations;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;


// TODO: Improve / optimize threading, object churn and runnable re-use

class CSRTraversal {

  private static final int THREAD_BATCH_SIZE = 64;
  private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
  private final ExecutorService executorService;
  private final Future[] futures;

  private final AtomicInteger workPtr;

  CSRTraversal() {
    this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    this.futures = new Future[THREAD_POOL_SIZE];
    this.workPtr = new AtomicInteger();
  }

  synchronized void traverse(final EntryVisitor visitor, CSRStorage.View view) {
    visitor.reset();
    workPtr.set(0);
    for (int i = 0; i < THREAD_POOL_SIZE; i++)
      futures[i] = executorService.submit(() -> fetchAndProcess(visitor, view));
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      try {
        futures[i].get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void fetchAndProcess(final EntryVisitor visitor, CSRStorage.View view) {
    int i;
    while ((i = workPtr.getAndAdd(THREAD_BATCH_SIZE)) < view.size()) {
      for (int j = i; j < Math.min(i + THREAD_BATCH_SIZE, view.size()); j++) {
        view.traverseRow(j, visitor);
      }
    }
  }

  void cleanup() {
    executorService.shutdown();
  }

}

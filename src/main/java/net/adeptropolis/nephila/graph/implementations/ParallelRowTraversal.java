package net.adeptropolis.nephila.graph.implementations;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;


// TODO: Improve / optimize threading, object churn and runnable re-use

public class ParallelRowTraversal {

  private static final int THREAD_BATCH_SIZE = 64;
  private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
  private final ExecutorService executorService;
  private final Future[] futures;

  private final RowTraversal rowTraversal;
  private final CSRStorage.View view;
  private final AtomicInteger workPtr;

  public ParallelRowTraversal(RowTraversal rowTraversal, CSRStorage.View view) {
    this.rowTraversal = rowTraversal;
    this.view = view;
    this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    this.futures =  new Future[THREAD_POOL_SIZE];
    this.workPtr = new AtomicInteger();
  }

  public synchronized void traverseRows() {
    rowTraversal.reset();
    workPtr.set(0);
    for (int i = 0; i < THREAD_POOL_SIZE; i++) futures[i] = executorService.submit(this::fetchAndProcess);
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      try {
        futures[i].get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void fetchAndProcess() {
    int i;
    while ((i = workPtr.getAndAdd(THREAD_BATCH_SIZE)) < view.indicesSize) {
      for (int j = i; j < Math.min(i + THREAD_BATCH_SIZE, view.indicesSize); j++) {
        view.traverseRow(j, rowTraversal);
      }
    }
  }

  public void cleanup() {
    executorService.shutdown();
  }

}

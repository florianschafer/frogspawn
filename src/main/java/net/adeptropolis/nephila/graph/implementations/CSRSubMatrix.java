package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.ArrayDoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.DoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.IntBuffer;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CSRSubMatrix {

  private static final int THREAD_BATCH_SIZE = 64;
  private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
  private static final ExecutorService multiplicationService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
  private static final Future[] futures = new Future[THREAD_POOL_SIZE];

  private final CSRStorage data;
  private final IntBuffer indices;
  private final DoubleBuffer multResult;

  public CSRSubMatrix(CSRStorage data, IntBuffer indices) {
    this.data = data;
    this.indices = indices;
    this.multResult = new ArrayDoubleBuffer(indices.size());
  }

  public synchronized DoubleBuffer multiply(final DoubleBuffer v) {
    int threads = Math.min(THREAD_POOL_SIZE, 1 + (int) (indices.size() / (THREAD_POOL_SIZE * THREAD_BATCH_SIZE)));
    AtomicInteger workPtr = new AtomicInteger(0);
    for (int threadId = 0; threadId < threads; threadId++) {
      futures[threadId] = multiplicationService.submit(new MultiplicationRunnable(v, workPtr, THREAD_BATCH_SIZE));
    }
    for (int threadId = 0; threadId < threads; threadId++) {
      try {
        futures[threadId].get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }
    return multResult;

  }

  double rowScalarProduct(final int row, final DoubleBuffer v) {
    if (indices.size() == 0) return 0;
    long low = data.getRowPtrs().get(row);
    long high = data.getRowPtrs().get(row + 1);
    if (low == high) return 0; // Empty row

    double prod = 0;
    int col;
    long retrievedIdx;
    long secPtr;

    if (indices.size() > high - low) {
      secPtr = 0L;
      for (long ptr = low; ptr < high; ptr++) {
        col = data.getColIndices().get(ptr);
        retrievedIdx = indices.searchSorted(col, secPtr, indices.size() - 1);
        if (retrievedIdx >= 0) {
          prod += data.getValues().get(ptr) * v.get(retrievedIdx);
          secPtr = retrievedIdx + 1;
        }
        if (secPtr >= indices.size()) break;
      }
    } else {
      secPtr = low;
      for (long ptr = 0; ptr < indices.size(); ptr++) {
        col = indices.get(ptr);
        retrievedIdx = data.getColIndices().searchSorted(col, secPtr, high);
        if (retrievedIdx >= 0 && retrievedIdx < high) {
          prod += data.getValues().get(retrievedIdx) * v.get(ptr);
          secPtr = retrievedIdx + 1;
        }
        if (secPtr >= high) break;
      }
    }
    return prod;
  }

  public void free() {
    multResult.free();
  }

  private class MultiplicationRunnable implements Runnable {

    private final DoubleBuffer v;
    private final AtomicInteger workPtr;
    private final int batchSize;

    private MultiplicationRunnable(DoubleBuffer v, AtomicInteger workPtr, int batchSize) {
      this.v = v;
      this.workPtr = workPtr;
      this.batchSize = batchSize;
    }

    @Override
    public void run() {
      int i;
      while ((i = workPtr.getAndAdd(batchSize)) < indices.size()) {
        for (int j = i; j < Math.min(i + batchSize, indices.size()); j++) {
          int row = indices.get(j);
          double p = rowScalarProduct(row, v);
          multResult.set(j, p);
        }
      }
    }

  }

}

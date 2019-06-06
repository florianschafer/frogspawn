package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.DoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.IntBuffer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class CSRSubmatrix {

  private static final int THREAD_BATCH_SIZE = 64;
  private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
  private static final ExecutorService multiplicationService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
  private static final Future[] futures = new Future[THREAD_POOL_SIZE];

  final CSRStorage data;
  protected final IntBuffer indices;

  public CSRSubmatrix(CSRStorage data, IntBuffer indices) {
    this.data = data;
    this.indices = indices;
  }

  public synchronized void multiply(final DoubleBuffer v, final Product product) {
    int threads = Math.min(THREAD_POOL_SIZE, 1 + (int) (indices.size() / (THREAD_POOL_SIZE * THREAD_BATCH_SIZE)));
    AtomicInteger workPtr = new AtomicInteger(0);
    for (int threadId = 0; threadId < threads; threadId++) {
      futures[threadId] = multiplicationService.submit(new MultiplicationRunnable(v, product, workPtr, THREAD_BATCH_SIZE));
    }
    for (int threadId = 0; threadId < threads; threadId++) {
      try {
        futures[threadId].get();
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public synchronized void multiply(final DoubleBuffer v, final DoubleBuffer resultBuf) {
    multiply(v, new DefaultProduct(resultBuf));
  }

  double rowScalarProduct(final int row, final DoubleBuffer v, final Product product) {
    if (indices.size() == 0) return 0;
    int origRow = indices.get(row);
    long low = data.getRowPtrs().get(origRow);
    long high = data.getRowPtrs().get(origRow + 1);
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
          prod += product.innerProduct(row, (int)retrievedIdx, data.getValues().get(ptr), v.get(retrievedIdx));
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
          prod += product.innerProduct(row, col, data.getValues().get(retrievedIdx), v.get(ptr));
          secPtr = retrievedIdx + 1;
        }
        if (secPtr >= high) break;
      }
    }
    return prod;
  }

  public void free() {
  }

  public int size() {
    return (int) indices.size();
  }

  private class MultiplicationRunnable implements Runnable {

    private final DoubleBuffer v;
    private final Product product;
    private final AtomicInteger workPtr;
    private final int batchSize;

    private MultiplicationRunnable(DoubleBuffer v, Product product, AtomicInteger workPtr, int batchSize) {
      this.v = v;
      this.product = product;
      this.workPtr = workPtr;
      this.batchSize = batchSize;
    }

    @Override
    public void run() {
      int i;
      while ((i = workPtr.getAndAdd(batchSize)) < indices.size()) {
        for (int j = i; j < Math.min(i + batchSize, indices.size()); j++) {
//          int row = indices.get(j);
          double p = rowScalarProduct(j, v, product);
          product.createResultEntry(j, p, v);
        }
      }
    }

  }

  static class DefaultProduct implements Product {

    private final DoubleBuffer resultBuf;

    DefaultProduct(DoubleBuffer resultBuf) {
      this.resultBuf = resultBuf;
    }

    @Override
    public double innerProduct(int i, int j, double aij, double vj) {
      return aij * vj;
    }

    @Override
    public void createResultEntry(int row, double value, DoubleBuffer arg) {
      resultBuf.set(row, value);
    }
  }

}

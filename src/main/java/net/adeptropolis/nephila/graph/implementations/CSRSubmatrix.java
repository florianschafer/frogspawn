package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.primitives.search.InterpolationSearch;

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
  protected final int[] indices;
  final CSRStorage data;

  public CSRSubmatrix(CSRStorage data, int[] indices) {
    this.data = data;
    this.indices = indices;
  }

  public synchronized void multiply(final double[] v, final double[] result) {
    multiply(v, new DefaultProduct(result));
  }

  public synchronized void multiply(final double[] v, final Product product) {
    int threads = Math.min(THREAD_POOL_SIZE, 1 + (indices.length / (THREAD_POOL_SIZE * THREAD_BATCH_SIZE)));
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

  double rowScalarProduct(final int row, final double[] v, final Product product) {
    if (indices.length == 0) return 0;
    int origRow = indices[row];
    long low = data.getRowPtrs()[origRow];
    long high = data.getRowPtrs()[origRow + 1];
    if (low == high) return 0; // Empty row

    double prod = 0;
    int col;


    if (indices.length > high - low) {
      int secPtr = 0;
      int retrievedIdx;
      for (long ptr = low; ptr < high; ptr++) {
        col = data.getColIndices().get(ptr);
        retrievedIdx = InterpolationSearch.search(indices, col, secPtr, indices.length - 1);
        if (retrievedIdx >= 0) {
          prod += product.innerProduct(row, retrievedIdx, data.getValues().get(ptr), v[retrievedIdx]);
          secPtr = retrievedIdx + 1;
        }
        if (secPtr >= indices.length) break;
      }
    } else {
      long secPtr = low;
      long retrievedIdx;
      for (int ptr = 0; ptr < indices.length; ptr++) {
        col = indices[ptr];
        retrievedIdx = InterpolationSearch.search(data.getColIndices(), col, secPtr, high);
        if (retrievedIdx >= 0 && retrievedIdx < high) {
          prod += product.innerProduct(row, col, data.getValues().get(retrievedIdx), v[ptr]);
          secPtr = retrievedIdx + 1;
        }
        if (secPtr >= high) break;
      }
    }
    return prod;
  }

  public int size() {
    return indices.length;
  }

  static class DefaultProduct implements Product {

    private final double[] resultBuf;

    DefaultProduct(double[] resultBuf) {
      this.resultBuf = resultBuf;
    }

    @Override
    public double innerProduct(int i, int j, double aij, double vj) {
      return aij * vj;
    }

    @Override
    public void createResultEntry(int row, double value, double[] arg) {
      resultBuf[row] = value;
    }
  }

  private class MultiplicationRunnable implements Runnable {

    private final double[] v;
    private final Product product;
    private final AtomicInteger workPtr;
    private final int batchSize;

    private MultiplicationRunnable(double[] v, Product product, AtomicInteger workPtr, int batchSize) {
      this.v = v;
      this.product = product;
      this.workPtr = workPtr;
      this.batchSize = batchSize;
    }

    @Override
    public void run() {
      int i;
      while ((i = workPtr.getAndAdd(batchSize)) < indices.length) {
        for (int j = i; j < Math.min(i + batchSize, indices.length); j++) {
//          int row = indices.get(j);
          double p = rowScalarProduct(j, v, product);
          product.createResultEntry(j, p, v);
        }
      }
    }

  }

}

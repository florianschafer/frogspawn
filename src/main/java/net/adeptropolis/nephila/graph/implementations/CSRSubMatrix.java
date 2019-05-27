package net.adeptropolis.nephila.graph.implementations;

import com.google.common.collect.Lists;
import net.adeptropolis.nephila.graph.implementations.buffers.ArrayDoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.DoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.IntBuffer;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/* NOTE
* Multiplication is NOT thread-safe (reused buffer for result vector) !!!
* */

public class CSRSubMatrix {

  private final CSRStorage data;
  private final IntBuffer indices;
  private final DoubleBuffer multResult;

  public CSRSubMatrix(CSRStorage data, IntBuffer indices) {
    this.data = data;
    this.indices = indices;
    this.multResult = new ArrayDoubleBuffer(indices.size());
  }


  public DoubleBuffer multiply(final DoubleBuffer v, int batchSize) {
    // TODO: Check parallel streams are efficient. Look at RecursiveAction
    for (int i = 0; i < indices.size(); i++) multResult.set(i, 0);
//    LongStream.range(0, indices.size()).parallel().forEach(i -> {
//      int row = indices.get(i);
//      double p = rowScalarProduct(row, v);
//      multResult.set(i, p);
//    });


//    final int foo = (int) (indices.size() / 1024);
//    IntStream.range(0, 1024).parallel().forEach(i -> {
//      int low = foo * i;
//      int high = (int) Math.min(foo * (i + 1), indices.size());
//      multiplyRowRange(v, low, high);
//    });


    AtomicInteger idx = new AtomicInteger(0);
    List<Thread> threads = IntStream.range(0, 18).mapToObj(threadId -> {
      return new Thread(new Runnable() {
        @Override
        public void run() {
          int i;
          // Have thread-local workload:
          // 1 ->
          // 2 ->
          // 4 -> 163ms 165ms
          // 8 -> 159ms 143ms 152ms
          // 16 -> 130ms 145ms 145ms
          // 32 -> 151ms 145ms 145ms
          while ((i = idx.getAndAdd(batchSize)) < indices.size()) {
            for (int j = i; j < Math.min(i + batchSize, indices.size()); j++) {
              int row = indices.get(j);
              double p = rowScalarProduct(row, v);
              multResult.set(j, p);
            }
          }
        }
      });
    }).collect(Collectors.toList());

    threads.forEach(t -> t.start());
    threads.forEach(t -> {
      try {
        t.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });


    return multResult;

  }


  private void multiplyRowRange(final DoubleBuffer v, final int low, final int high) {
    // TODO: Weird. When this is changed to a parallel stream (wrapped in the parallel stream above), resource utilization seems much better
//    for (int i = low; i < high; i++) {
//      int row = indices.get(i);
//      double p = rowScalarProduct(row, v);
//      multResult.set(i, p);
//    }
    IntStream.range(low, high).parallel().forEach( i -> {
      int row = indices.get(i);
      double p = rowScalarProduct(row, v);
      multResult.set(i, p);
    });

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



//  private class Foo extends RecursiveAction {
//
//    private final DoubleBuffer v;
//    private final int low;
//    private final int high;
//
//    private Foo(DoubleBuffer v, int low, int high) {
//      this.v = v;
//      this.low = low;
//      this.high = high;
//    }
//
//
//    @Override
//    protected void compute() {
//
//      if (high - low > 10000) {
//        int mid = (low + high) >> 1;
//        invokeAll(new Foo(v, low, mid), new Foo(v, mid, high));
//
//      } else {
//        System.out.println(high - low);
//        multiplyRowRange(v, low, high);
//      }
//
//
//    }
//  }

}

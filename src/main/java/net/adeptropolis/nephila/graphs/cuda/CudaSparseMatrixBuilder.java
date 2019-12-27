/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda;

import com.google.common.base.Preconditions;
import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcusparse.cusparseHandle;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CUDAMallocException;

public class CudaSparseMatrixBuilder {

  static final int BUF_SIZE = 8 * (1 << 20);

  private final Graph graph;
  private final cusparseHandle sparseHandle;

  private Pointer rowPtrs;
  private Pointer colIndices;
  private Pointer values;

  private final Buffer buffer;

  public CudaSparseMatrixBuilder(Graph graph, cusparseHandle sparseHandle) throws CUDAMallocException {
    this(graph, sparseHandle, BUF_SIZE);
  }

  public CudaSparseMatrixBuilder(Graph graph, cusparseHandle sparseHandle, int bufSize) throws CUDAMallocException {
    this.graph = graph;
    this.sparseHandle = sparseHandle;
    this.rowPtrs = CUDA.malloc(Sizeof.LONG * (graph.size() + 1));
    this.colIndices = CUDA.malloc(Sizeof.INT * graph.numEdges());
    this.values = CUDA.malloc(Sizeof.FLOAT * graph.numEdges());
    this.buffer = new Buffer(rowPtrs, colIndices, values, bufSize);
  }

  public CUDASparseMatrix build() throws CUDAMallocException {
    for (int i = 0; i < graph.size(); i++) {
      buffer.addRow();
      graph.traverse(i, (u, v, w) ->  {
        buffer.addEdge(v, (float) w);
      });
    }
    buffer.addRow(); // Terminator
    buffer.flush();
    Preconditions.checkState(buffer.ptr == graph.numEdges());
    Preconditions.checkState(buffer.rowPtr == graph.size() + 1);
    return new CUDASparseMatrix(sparseHandle, graph.size(), graph.numEdges(), rowPtrs, colIndices, values);
  }

  private static class Buffer {

    private final Pointer rowPtrs;
    private final Pointer colIndices;
    private final Pointer values;
    private final int bufSize;
    private long ptr;
    private long rowPtr;

    private final long[] bufRowPtrs;
    private final int[] bufColIndices;
    private final float[] bufValues;
    private int bufPtr;
    private int bufRowPtr;

    private Buffer(Pointer rowPtrs, Pointer colIndices, Pointer values, int bufSize) {
      this.rowPtrs = rowPtrs;
      this.colIndices = colIndices;
      this.values = values;
      this.bufSize = bufSize;
      this.bufValues = new float[bufSize];
      this.bufColIndices = new int[bufSize];
      this.bufRowPtrs = new long[bufSize + 1];
    }

    void addRow() {
      bufRowPtrs[bufRowPtr] = ptr + bufPtr;
      if (bufRowPtr++ == bufRowPtrs.length) {
        flushRowPointers();
      }
    }

    void addEdge(int v, float weight) {
      bufColIndices[bufPtr] = v;
      bufValues[bufPtr] = weight;
      if (bufPtr++ == bufColIndices.length) {
        flushEdges();
      }

    }

    void flush() {
      flushRowPointers();
      flushEdges();
    }

    void flushEdges() {
      if (bufPtr == 0) {
        return;
      }
      CUDA.copyToDevice(bufColIndices, colIndices, ptr, bufPtr);
      CUDA.copyToDevice(bufValues, values, ptr, bufPtr);
      ptr += bufPtr;
      bufPtr = 0;
    }

    private void flushRowPointers() {
      if (bufRowPtr == 0) {
        return;
      }
      CUDA.copyToDevice(bufRowPtrs, rowPtrs, rowPtr, bufRowPtr);
      rowPtr += bufRowPtr;
      bufRowPtr = 0;
    }

  }

}

/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda;

import jcuda.Pointer;
import jcuda.jcusparse.cusparseHandle;
import jcuda.jcusparse.cusparseMatDescr;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CUDAException;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CUSparseException;

public class CUDASparseMatrix {

  private final cusparseHandle sparseHandle;
  private final cusparseMatDescr matrixDescriptor;

  private final int size;
  private final long numEdges;

  private final Pointer rowPtrs;
  private final Pointer colIndices;
  private final Pointer values;

  public CUDASparseMatrix(cusparseHandle sparseHandle, int size, long numEdges, Pointer rowPtrs, Pointer colIndices, Pointer values) {
    this.sparseHandle = sparseHandle;
    this.size = size;
    this.numEdges = numEdges;
    this.rowPtrs = rowPtrs;
    this.colIndices = colIndices;
    this.values = values;
    try {
      this.matrixDescriptor = CUSparse.createMatrixDescriptor();
    } catch (CUSparseException e) {
      throw new RuntimeException(e);
    }
  }

  public void destroy() {
    try {
      CUDA.free(rowPtrs);
      CUDA.free(colIndices);
      CUDA.free(values);
      CUSparse.destroyMatDescr(matrixDescriptor);
    } catch (CUDAException e) {
      throw new RuntimeException(e);
    }
  }

  public Pointer getRowPtrs() {
    return rowPtrs;
  }

  public Pointer getColIndices() {
    return colIndices;
  }

  public Pointer getValues() {
    return values;
  }

  public int getSize() {
    return size;
  }

  public long getNumEdges() {
    return numEdges;
  }
}

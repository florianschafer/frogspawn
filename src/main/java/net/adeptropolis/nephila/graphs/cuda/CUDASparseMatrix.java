/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcusparse.cusparseHandle;
import jcuda.jcusparse.cusparseMatDescr;
import net.adeptropolis.nephila.graphs.Graph;

import static jcuda.jcusparse.JCusparse.*;
import static jcuda.jcusparse.cusparseIndexBase.CUSPARSE_INDEX_BASE_ZERO;
import static jcuda.jcusparse.cusparseMatrixType.CUSPARSE_MATRIX_TYPE_GENERAL;

public class CUDASparseMatrix {

  private final cusparseHandle sparseHandle;
  private final cusparseMatDescr matrixDescriptor;

  public CUDASparseMatrix(Graph graph, cusparseHandle sparseHandle) throws CUSparseException {
    this.sparseHandle = sparseHandle;
    this.matrixDescriptor = CUSparse.createMatrixDescriptor();
//    this.rowPtrs = CUDA.malloc(graph.size() * Sizeof.INT); // Check! (+1?)
//    this.colIndices = CUDA.malloc()
  }

  public void destroy() throws CUDAException, CUSparseException {
    CUSparse.destroyMatDescr(matrixDescriptor);
//    CUDA.free(values);
//    CUDA.free(colIndices);
//    CUDA.free(rowPtrs);
  }

  public static class Builder {

    private Pointer rowPtrs;
    private Pointer colIndices;
    private Pointer values;

    public Builder(Graph graph) throws CUDAException {
       this.rowPtrs = CUDA.malloc(Sizeof.INT * graph.size());
//       this.colIndices = CUDA.malloc(Sizeof.INT * graph.)
    }

  }

}

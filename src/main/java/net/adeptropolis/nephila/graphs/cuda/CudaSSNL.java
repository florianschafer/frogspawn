/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.cudaDataType;
import jcuda.jcublas.JCublas;
import jcuda.jcublas.cublasHandle;
import jcuda.jcurand.curandGenerator;
import jcuda.jcusparse.JCusparse;
import jcuda.jcusparse.cusparseHandle;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CUDAMallocException;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CudaHelpers;

import static jcuda.jcurand.JCurand.*;
import static jcuda.jcurand.curandRngType.CURAND_RNG_PSEUDO_DEFAULT;
import static jcuda.jcusparse.cusparseAlgMode.CUSPARSE_ALG_NAIVE;
import static jcuda.jcusparse.cusparseOperation.CUSPARSE_OPERATION_NON_TRANSPOSE;

public class CudaSSNL {

  private static cublasHandle blas;
  private static cusparseHandle sparse;
  private static curandGenerator rand;

  static {
    blas = new cublasHandle();
    sparse = new cusparseHandle();
    rand = new curandGenerator();
    curandCreateGenerator(rand, CURAND_RNG_PSEUDO_DEFAULT);
    curandSetPseudoRandomGeneratorSeed(rand, 1337421337L);
  }


  private final CUDASparseMatrix matrix;

  public CudaSSNL(CUDASparseMatrix matrix) {
    this.matrix = matrix;
  }

  public void perform() throws CUDAMallocException {

    int n = matrix.getSize();

    Pointer vec = CUDA.malloc(n * Sizeof.FLOAT);
    curandGenerateUniform(rand, vec, matrix.getSize());
    normalize2(vec, matrix.getSize());

    Pointer ones = CUDA.malloc(n * Sizeof.FLOAT);
    CudaHelpers.fillWithFloatOnes(ones, n);

//    Pointer y = CUDA.malloc(n * Sizeof.FLOAT);
//    CudaHelpers.fillWithFloatOnes(ones, n);

    Pointer weights = CUDA.malloc(n * Sizeof.FLOAT);

    Pointer alpha = Pointer.to(new float[]{1f});
    long[] bufSize = new long[1];

    JCusparse.cusparseCsrmvEx_bufferSize(sparse,
            CUSPARSE_ALG_NAIVE,
            CUSPARSE_OPERATION_NON_TRANSPOSE,
            n,
            n,
            Math.toIntExact(matrix.getNumEdges()),
            alpha,
            cudaDataType.CUDA_R_32F,
            matrix.getMatrixDescriptor(),
            matrix.getValues(), cudaDataType.CUDA_R_32F,
            matrix.getRowPtrs(),
            matrix.getColIndices(),
            ones, cudaDataType.CUDA_R_32F,
            Pointer.to(new float[]{0f}), cudaDataType.CUDA_R_32F,
            weights, cudaDataType.CUDA_R_32F,
            cudaDataType.CUDA_R_32F,
            bufSize);

    System.out.println("BufSize: " + bufSize[0]);

    Pointer buffer = CUDA.malloc(bufSize[0]);


    JCusparse.cusparseCsrmvEx(sparse,
            CUSPARSE_ALG_NAIVE,
            CUSPARSE_OPERATION_NON_TRANSPOSE,
            n,
            n,
            (int) matrix.getNumEdges(),
            alpha,
            cudaDataType.CUDA_R_32F,
            matrix.getMatrixDescriptor(),
            matrix.getValues(), cudaDataType.CUDA_R_32F,
            matrix.getRowPtrs(),
            matrix.getColIndices(),
            ones, cudaDataType.CUDA_R_32F,
            Pointer.to(new float[]{0f}),
            cudaDataType.CUDA_R_32F,
            weights,
            cudaDataType.CUDA_C_32F,
            cudaDataType.CUDA_C_32F,
            buffer);
    


    float[] foo = new float[matrix.getSize()];
    CUDA.copyToHost(vec, foo, matrix.getSize());
    System.out.println();
    for (int i = 0; i < foo.length; i++) System.out.printf("%f ", foo[i]);
    System.out.println();



    CUDA.free(buffer);
//    CUDA.free(y);
    CUDA.free(weights);
    CUDA.free(ones);
    CUDA.free(vec);

  }

  private static void normalize2(Pointer vector, int size) {
    float vecMagnitude = JCublas.cublasSnrm2(size, vector, 1);
    JCublas.cublasSscal(size,1 /vecMagnitude, vector, 1);
  }
}

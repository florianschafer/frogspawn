/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda;

import jcuda.Pointer;
import jcuda.jcusparse.JCusparse;
import jcuda.runtime.JCuda;
import jcuda.runtime.cudaError;

import static jcuda.runtime.JCuda.cudaFree;
import static jcuda.runtime.JCuda.cudaMalloc;
import static jcuda.runtime.cudaError.cudaSuccess;

public class CUDA {

  static {
    JCuda.setExceptionsEnabled(false);
  }

  static Pointer malloc(long size) throws CUDAException {
    Pointer ptr = new Pointer();
    verifyOp(cudaMalloc(ptr, size), "Allocate memory");
    return ptr;
  }

  static void free(Pointer ptr) throws CUDAException {
    verifyOp(cudaFree(ptr), "Free memory");
  }

  static void verifyOp(int status, String prefix) throws CUDAException {
    if (status != cudaSuccess) {
      throw new CUDAException(String.format("%s: %s", prefix, cudaError.stringFor(status)));
    }
  }




}

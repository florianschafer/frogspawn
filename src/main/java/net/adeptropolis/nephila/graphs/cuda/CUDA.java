/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.runtime.JCuda;
import jcuda.runtime.cudaError;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CUDAException;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CUDAMallocException;

import static jcuda.runtime.JCuda.*;
import static jcuda.runtime.cudaError.cudaSuccess;
import static jcuda.runtime.cudaMemcpyKind.cudaMemcpyDeviceToHost;
import static jcuda.runtime.cudaMemcpyKind.cudaMemcpyHostToDevice;

public class CUDA {

  static {
    JCuda.setExceptionsEnabled(false);
  }

  static Pointer malloc(long size) throws CUDAMallocException {
    Pointer ptr = new Pointer();
    int status = cudaMalloc(ptr, size);
    if (status != cudaSuccess) {
      throw new CUDAMallocException();
    }
    return ptr;
  }

  static void free(Pointer ptr) throws CUDAException {
    verifyOp(cudaFree(ptr), "Free memory");
  }

  public static void copyToDevice(int[] src, Pointer dst, long dstOffset, long count) throws CUDAException {
    copyToDevice(Pointer.to(src), dst, dstOffset, count, Sizeof.INT);
  }

  public static void copyToDevice(float[] src, Pointer dst, long dstOffset, long count) throws CUDAException {
    copyToDevice(Pointer.to(src), dst, dstOffset, count, Sizeof.FLOAT);
  }

  public static void copyToDevice(long[] src, Pointer dst, long dstOffset, long count) throws CUDAException {
    copyToDevice(Pointer.to(src), dst, dstOffset, count, Sizeof.LONG);
  }

  private static void copyToDevice(Pointer src, Pointer dst, long dstOffset, long count, int elementSize) throws CUDAException {
    int status = cudaMemcpy(dst.withByteOffset(dstOffset * elementSize), src, count * elementSize, cudaMemcpyHostToDevice);
    verifyOp(status, "Copy to device");
  }

  public static void copyToHost(Pointer src, int[] dst, long count) throws CUDAException {
    copyToHost(src, Pointer.to(dst),count, Sizeof.INT);
  }

  public static void copyToHost(Pointer src, float[] dst, long count) throws CUDAException {
    copyToHost(src, Pointer.to(dst),count, Sizeof.FLOAT);
  }

  public static void copyToHost(Pointer src, long[] dst, long count) throws CUDAException {
    copyToHost(src, Pointer.to(dst),count, Sizeof.LONG);
  }

  private static void copyToHost(Pointer src, Pointer dst, long count, int elementSize) throws CUDAException {
    int status = cudaMemcpy(dst, src, count * elementSize, cudaMemcpyDeviceToHost);
    verifyOp(status, "Copy to host");
  }

  static void verifyOp(int status, String prefix) throws CUDAException {
    if (status != cudaSuccess) {
      throw new CUDAException(String.format("%s: %s", prefix, cudaError.stringFor(status)));
    }
  }




}

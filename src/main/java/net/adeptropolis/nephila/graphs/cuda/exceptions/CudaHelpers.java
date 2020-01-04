/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda.exceptions;

import jcuda.Pointer;
import jcuda.runtime.JCuda;
import net.adeptropolis.nephila.graphs.cuda.CUDA;

import java.util.Arrays;

public class CudaHelpers {

  private static final float[] ONES = new float[1 << 20];

  static {
    Arrays.fill(ONES, 1);
  }

  public static void fillWithFloatOnes(Pointer floatVec, int size) {
    int batches = size / ONES.length;
    for (int i = 0; i < batches; i++) {
      CUDA.copyToDevice(ONES, floatVec, i * ONES.length, ONES.length);
    }
    int remainder = size - batches * ONES.length;
    if (remainder > 0) {
      CUDA.copyToDevice(ONES, floatVec, batches * ONES.length, remainder);
    }
  }

}

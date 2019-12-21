/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda;

import jcuda.Pointer;
import org.junit.Test;

import static jcuda.runtime.JCuda.cudaFree;
import static jcuda.runtime.JCuda.cudaMalloc;
import static org.junit.Assert.*;

public class CudaUtilsTest {

  @Test
  public void foo() {

    Pointer ptr = new Pointer();
    int status = cudaMalloc(ptr, 1000);
    System.out.println(status);
    status = cudaFree(ptr);
    System.out.println(status);

  }

}
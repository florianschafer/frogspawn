/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda;

import jcuda.jcusparse.cusparseHandle;
import net.adeptropolis.nephila.graphs.GraphTestBase;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CUDAMallocException;
import org.junit.Test;

public class CudaSSNLTest extends GraphTestBase {

  private static cusparseHandle sparseHandle = new cusparseHandle();

  @Test
  public void foo() throws CUDAMallocException {

    CUDASparseMatrix mat = new CudaSparseMatrixBuilder(WEIGHTED_K20, sparseHandle).build();
    new CudaSSNL(mat).perform();
    mat.destroy();

  }

}
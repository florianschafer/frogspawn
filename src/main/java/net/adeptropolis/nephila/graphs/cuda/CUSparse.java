/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda;

import jcuda.jcusparse.JCusparse;
import jcuda.jcusparse.cusparseMatDescr;
import jcuda.jcusparse.cusparseStatus;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CUSparseException;

import static jcuda.jcusparse.JCusparse.*;
import static jcuda.jcusparse.cusparseIndexBase.CUSPARSE_INDEX_BASE_ZERO;
import static jcuda.jcusparse.cusparseMatrixType.CUSPARSE_MATRIX_TYPE_GENERAL;

public class CUSparse {

  static {
    JCusparse.setExceptionsEnabled(false);
  }

  static cusparseMatDescr createMatrixDescriptor() {
    cusparseMatDescr matDescr = new cusparseMatDescr();
    verifyOp(cusparseCreateMatDescr(matDescr), "Create matrix descriptor");
    verifyOp(cusparseSetMatType(matDescr, CUSPARSE_MATRIX_TYPE_GENERAL), "Set matrix type");
    verifyOp(cusparseSetMatIndexBase(matDescr, CUSPARSE_INDEX_BASE_ZERO), "Set matrix index base");
    return matDescr;
  }

  static void destroyMatDescr(cusparseMatDescr matDescr) {
    verifyOp(cusparseDestroyMatDescr(matDescr), "Destroy matrix descriptor");
  }

  static void verifyOp(int status, String prefix) {
    if (status != cusparseStatus.CUSPARSE_STATUS_SUCCESS) {
      throw new CUSparseException(String.format("%s: %s", prefix, cusparseStatus.stringFor(status)));
    }

  }

}

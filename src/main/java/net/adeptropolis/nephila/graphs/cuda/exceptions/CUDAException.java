/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda.exceptions;

public class CUDAException extends Exception {

  public CUDAException() {
  }

  public CUDAException(String message) {
    super(message);
  }

  public CUDAException(String message, Throwable cause) {
    super(message, cause);
  }

  public CUDAException(Throwable cause) {
    super(cause);
  }

  public CUDAException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}

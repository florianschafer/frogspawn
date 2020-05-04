/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms.power_iteration;

public class PartialConvergencePostprocessingException extends PowerIterationException {
  PartialConvergencePostprocessingException(String message) {
    super(message);
  }
}

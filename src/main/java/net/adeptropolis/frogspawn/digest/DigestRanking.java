/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.digest;

@FunctionalInterface
public interface DigestRanking {

  double compute(int vertexId, double weight, double score);

}

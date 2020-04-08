/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

@FunctionalInterface
public interface ClusterMemberRanking {

  double compute(int vertexId, double weight, double score);

}

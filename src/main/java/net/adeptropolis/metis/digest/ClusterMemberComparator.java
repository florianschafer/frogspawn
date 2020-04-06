/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.digest;

@FunctionalInterface
public interface ClusterMemberComparator {

  int compare(int[] vertices, double[] weights, double[] scores, int i, int j);

}

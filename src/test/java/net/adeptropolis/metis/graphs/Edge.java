/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs;

@SuppressWarnings("squid:ClassVariableVisibilityCheck")
public class Edge {

  int u;
  int v;
  double weight;

  Edge(int u, int v, double weight) {
    this.u = u;
    this.v = v;
    this.weight = weight;
  }

  public static Edge of(int u, int v, double weight) {
    return new Edge(u, v, weight);
  }

  @Override
  public String toString() {
    return String.format("[%d,%d:%f]", u, v, weight);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Edge)) {
      return false;
    }
    Edge other = (Edge) obj;
    return u == other.u && v == other.v && weight == other.weight;
  }

  @Override
  public int hashCode() {
    return (int) ((((long) u << 32 | v) + Math.round(weight * 1000)) % 2147483647);
  }
}

/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila;

@SuppressWarnings("squid:ClassVariableVisibilityCheck")
public class LabeledEdge<T> {

  public T u;
  public T v;
  public double weight;

  public LabeledEdge(T u, T v, double weight) {
    this.u = u;
    this.v = v;
    this.weight = weight;
  }

}

/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
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

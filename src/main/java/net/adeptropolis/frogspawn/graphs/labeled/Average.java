/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

// TODO: Test, document
public class Average {

  long elements;
  double sum;

  public Average() {

  }

  public double get() {
    return sum / elements;
  }

  public void add(double value) {
    elements += 1;
    sum += value;
  }



}

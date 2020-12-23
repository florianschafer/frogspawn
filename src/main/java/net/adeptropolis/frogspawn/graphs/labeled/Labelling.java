/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import java.io.Serializable;
import java.util.stream.Stream;

public interface Labelling<T extends Serializable> {

  T label(int v);

  int index(T label);

  Stream<T> labels();

}

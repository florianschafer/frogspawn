/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * A Vertex labelling, providing both the mapping between vertices and labels and its inverse.
 *
 * @param <T> Label type
 */

public interface Labelling<T extends Serializable> {

  /**
   * @param v Global vertex id
   * @return Label
   */

  T label(int v);

  /**
   * @param label Label
   * @return Global vertex id
   */

  int index(T label);

  /**
   * @return Stream of all labels
   */

  Stream<T> labels();

}

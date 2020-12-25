/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled.labelings;

import java.io.Serializable;
import java.util.stream.Stream;

/**
 * A Vertex labeling, providing both the mapping between vertices and labels and its inverse.
 *
 * @param <T> Label type
 */

public interface Labeling<T extends Serializable> {

  /**
   * @param v Global vertex id
   * @return Label
   */

  T label(int v);

  /**
   * @param label Label
   * @return Global vertex id
   */

  int id(T label);

  /**
   * @return Stream of all labels
   */

  Stream<T> labels();

  /**
   * @return Borderline factory impersonation: A new instance for this Labeling implementation
   */

  Labeling<T> newInstance();

}

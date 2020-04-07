/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.playground;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface GraphSource {

  IntStream vertices();

  int vertexCount();

  Stream<Edge> edges();

}

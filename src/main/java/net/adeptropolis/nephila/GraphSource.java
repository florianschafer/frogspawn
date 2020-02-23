/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila;

import net.adeptropolis.nephila.graphs.Edge;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface GraphSource {

  IntStream vertices();

  int vertexCount();

  Stream<Edge> edges();

}

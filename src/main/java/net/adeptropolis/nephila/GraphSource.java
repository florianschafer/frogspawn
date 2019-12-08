/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
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

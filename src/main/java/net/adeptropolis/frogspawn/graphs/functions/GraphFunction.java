/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.functions;

import net.adeptropolis.frogspawn.graphs.Graph;

@FunctionalInterface
public
interface GraphFunction<T> {

  T apply(Graph graph);

}

/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs;

import it.unimi.dsi.fastutil.ints.IntIterator;

import java.util.function.IntPredicate;

// TODO: Test!
// Note: returns global ids!
public class PredicateVertexIterator implements IntIterator {

  private final Graph graph;

  private final IntPredicate predicate;
  private int ptr;
  private int next;

  public PredicateVertexIterator(Graph graph, IntPredicate predicate) {
    this.graph = graph;
    this.predicate = predicate;
    this.ptr = 0;
    this.next = seekNext();
  }

  @Override
  public int nextInt() {
    int nextInt = graph.globalVertexId(next);
    next = seekNext();
    return nextInt;
  }

  @Override
  public boolean hasNext() {
    return ptr < graph.order() && next >= 0;
  }

  private int seekNext() {
    for (; ptr < graph.order(); ptr++) {
      if (predicate.test(ptr)) {
        return ptr++;
      }
    }
    return -1;
  }


}

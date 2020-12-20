/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs;

import it.unimi.dsi.fastutil.ints.IntIterator;

import java.util.function.IntPredicate;

// TODO: Test!
public class PredicateIterator implements IntIterator {

  private final int N;
  private final IntPredicate predicate;
  private int ptr;
  private int next;

  public PredicateIterator(int n, IntPredicate predicate) {
    this.N = n;
    this.predicate = predicate;
    this.ptr = 0;
    this.next = seekNext();
  }

  @Override
  public int nextInt() {
    int nextInt = next;
    next = seekNext();
    return nextInt;
  }

  @Override
  public boolean hasNext() {
    return ptr < N && next >= 0;
  }

  private int seekNext() {
    for (; ptr < N; ptr++) {
      if (predicate.test(ptr)) {
        return ptr++;
      }
    }
    return -1;
  }


}

/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.helpers;

import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public class SequencePredicates {

  /**
   * Helper functions for dealing with the application of expensive predicates to large sequences
   */

  private SequencePredicates() {

  }

  /**
   * Returns a binary search for the lowest integer satisfying a given predicate of sorted consecutive integers.
   * <p>It is assumed that <code>∃N: ∀i ≥ N: predicate(i) == true</code></p>
   * <p>Furthermore, It is assumed that the given <code>high</code> boundary is already known to satisfiy the predicate.
   *
   * @param low       Lower bound. Inclusive
   * @param high      Upper bound. Inclusive. Must satisfy the predicate.
   * @param predicate The predicate to check against
   * @return Lowest int ∈ [low, high] for which the predicate holds true
   */

  public static int findFirst(int low, int high, IntPredicate predicate) {
    if (low >= high) {
      return high;
    }
    int min = high;
    while (true) {
      int mid = (low + high) >> 1;
      if (!predicate.test(mid)) {
        if (mid == min - 1) {
          return min;
        }
        low = mid + 1;
      } else {
        high = mid - 1;
        min = mid;
      }
    }
  }

  /**
   * Find the first object satisfying a given predicate in a sequence of candidates defined in an induction-style manner.
   * <p>
   * Use this whenever either the sequence length or the cost of predicate testing is too high to naively test all available
   * elements. The sequence elements are first tested in a step-like manner before a binary search is applied.
   * </p
   * <p>
   * It is explicitly assumed that once the predicate has been satisfied, all succeeding elements also satisfy the predicate.
   * </p>
   *
   * @param initialValue    First element to be tested
   * @param stepSize        Step size of the initial search
   * @param advanceOperator Operator accepting an object of type <code>T</code> and returning
   *                        its successor or <code>null</code> if no such element is available.
   * @param predicate       Predicate to be checked against
   * @return First element of type <code>T</code> for which the predicate holds true.
   * <code>null</code> if no such element could be found.
   */

  public static <T> T findFirst(T initialValue, int stepSize, UnaryOperator<T> advanceOperator, Predicate<T> predicate) {

    if (predicate.test(initialValue)) {
      return initialValue;
    }

    @SuppressWarnings("unchecked")
    T[] buffer = (T[]) new Object[stepSize];
    IntPredicate intPredicate = i -> predicate.test(buffer[i]);

    while (true) {
      int bufSize = fillBuffer(initialValue, buffer, advanceOperator);
      if (bufSize == 1) {
        return null;
      }
      T last = buffer[bufSize - 1];
      if (predicate.test(last)) {
        int min = findFirst(0, bufSize - 1, intPredicate);
        return buffer[min];
      }
      initialValue = last;
    }

  }

  /**
   * Fill a size-limited search buffer
   *
   * @param initialValue    First element of the buffer
   * @param buffer          Buffer to be filled
   * @param advanceOperator Operator accepting an object of type <code>T</code> and returning
   *                        its successor or <code>null</code> if no such element is available.
   * @return Size of the buffer
   */

  private static <T> int fillBuffer(T initialValue, T[] buffer, UnaryOperator<T> advanceOperator) {
    buffer[0] = initialValue;
    for (int i = 1; i < buffer.length; i++) {
      buffer[i] = advanceOperator.apply(buffer[i - 1]);
      if (buffer[i] == null) {
        return i;
      }
    }
    return buffer.length;
  }

}

/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.implementations.arrays;

/**
 * Perform an indirect merge sort on big (i.e. long-indexed) arrays
 */

public class LongMergeSort {

  private static final long MERGESORT_NO_REC = 16;

  /**
   * Sorts the specified range of elements using the specified swapper and according to the order induced by the specified
   * comparator using mergesort.
   *
   * <p>This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result
   * of the sort. The sorting algorithm is an in-place mergesort that is significantly slower than a
   * standard mergesort, as its running time is <i>O</i>(<var>n</var>&nbsp;(log&nbsp;<var>n</var>)<sup>2</sup>), but it does not allocate additional memory; as a result, it can be
   * used as a generic sorting algorithm.
   *
   * @param from the index of the first element (inclusive) to be sorted.
   * @param to   the index of the last element (exclusive) to be sorted.
   * @param ops  the SortOps implementation for swapping and comparing elements
   */
  public static void mergeSort(final long from, final long to, final SortOps ops) {
    /*
     * We retain the same method signature as quickSort. Given only a comparator and swapper we
     * do not know how to copy and move elements from/to temporary arrays. Hence, in contrast to
     * the JDK mergesorts this is an "in-place" mergesort, i.e. does not allocate any temporary
     * arrays. A non-inplace mergesort would perhaps be faster in most cases, but would require
     * non-intuitive delegate objects...
     */
    final long length = to - from;

    // Insertion sort on smallest arrays
    if (length < MERGESORT_NO_REC) {
      for (long i = from; i < to; i++) {
        for (long j = i; j > from && (ops.compare(j - 1, j) > 0); j--) {
          ops.swap(j, j - 1);
        }
      }
      return;
    }

    // Recursively sort halves
    long mid = (from + to) >>> 1;
    mergeSort(from, mid, ops);
    mergeSort(mid, to, ops);

    // If list is already sorted, nothing left to do. This is an
    // optimization that results in faster sorts for nearly ordered lists.
    if (ops.compare(mid - 1, mid) <= 0) return;

    // Merge sorted halves
    inPlaceMerge(from, mid, to, ops);
  }

  /**
   * Transforms two consecutive sorted ranges into a single sorted range. The initial ranges are
   * {@code [first..middle)} and {@code [middle..last)}, and the resulting range is
   * {@code [first..last)}. Elements in the first input range will precede equal elements in
   * the second.
   */
  private static void inPlaceMerge(final long from, long mid, final long to, final SortOps ops) {
    if (from >= mid || mid >= to) return;
    if (to - from == 2) {
      if (ops.compare(mid, from) < 0) ops.swap(from, mid);
      return;
    }

    long firstCut;
    long secondCut;

    if (mid - from > to - mid) {
      firstCut = from + (mid - from) / 2;
      secondCut = lowerBound(mid, to, firstCut, ops);
    } else {
      secondCut = mid + (to - mid) / 2;
      firstCut = upperBound(from, mid, secondCut, ops);
    }

    long first2 = firstCut;
    long middle2 = mid;
    long last2 = secondCut;
    if (middle2 != first2 && middle2 != last2) {
      long first1 = first2;
      long last1 = middle2;
      while (first1 < --last1)
        ops.swap(first1++, last1);
      first1 = middle2;
      last1 = last2;
      while (first1 < --last1)
        ops.swap(first1++, last1);
      first1 = first2;
      last1 = last2;
      while (first1 < --last1)
        ops.swap(first1++, last1);
    }

    mid = firstCut + (secondCut - mid);
    inPlaceMerge(from, firstCut, mid, ops);
    inPlaceMerge(mid, secondCut, to, ops);
  }

  /**
   * Performs a binary search on an already-sorted range: finds the first position where an
   * element can be inserted without violating the ordering. Sorting is by a user-supplied
   * comparison function.
   *
   * @param from the index of the first element (inclusive) to be included in the binary search.
   * @param to   the index of the last element (exclusive) to be included in the binary search.
   * @param pos  the position of the element to be searched for.
   * @param ops  the SortOps implementation for swapping and comparing elements
   * @return the largest index i such that, for every j in the range {@code [first..i)},
   * {@code comp.compare(j, pos)} is {@code true}.
   */
  private static long lowerBound(long from, final long to, final long pos, final SortOps ops) {
    long len = to - from;
    while (len > 0) {
      long half = len / 2;
      long middle = from + half;
      if (ops.compare(middle, pos) < 0) {
        from = middle + 1;
        len -= half + 1;
      } else {
        len = half;
      }
    }
    return from;
  }


  /**
   * Performs a binary search on an already sorted range: finds the last position where an element
   * can be inserted without violating the ordering. Sorting is by a user-supplied comparison
   * function.
   *
   * @param from the index of the first element (inclusive) to be included in the binary search.
   * @param pos  the position of the element to be searched for.
   * @param ops  the SortOps implementation for swapping and comparing elements
   * @return The largest index i such that, for every j in the range {@code [first..i)},
   * {@code comp.compare(pos, j)} is {@code false}.
   */
  private static long upperBound(long from, final long mid, final long pos, final SortOps ops) {
    long len = mid - from;
    while (len > 0) {
      long half = len / 2;
      long middle = from + half;
      if (ops.compare(pos, middle) < 0) {
        len = half;
      } else {
        from = middle + 1;
        len -= half + 1;
      }
    }
    return from;
  }

  public interface SortOps {

    /**
     * Swaps the data at the given positions.
     *
     * @param a the first position to swap.
     * @param b the second position to swap.
     */
    void swap(long a, long b);

    /**
     * Compares its two primitive-type arguments for order. Returns a negative
     * integer, zero, or a positive integer as the first argument is less than,
     * equal to, or greater than the second.
     *
     * @return a negative integer, zero, or a positive integer as the first argument
     * is less than, equal to, or greater than the second.
     * @see java.util.Comparator
     */
    int compare(long k1, long k2);

  }

}


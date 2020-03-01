/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.implementations.arrays;

import org.junit.Test;

import java.util.Arrays;
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class InterpolationSearchTest {

  private static final int BASE_SIZE = (1 << 22);

  @Test
  public void searchBigDenseIntArray() {
    BigInts buf = new BigInts(BASE_SIZE);
    for (int i = 0; i < 10000; i++) buf.set(i, i);
    buf.sort();
    for (int i = 0; i < 10000; i++)
      assertThat(InterpolationSearch.search(buf, i, 0, 9999), greaterThanOrEqualTo(0L));
  }

  @Test
  public void searchBigSparseIntArray() {
    BigInts buf = new BigInts(BASE_SIZE);
    for (int i = 0; i < 10000; i++) buf.set(i, i * 13);
    buf.sort();
    for (int i = 0; i < 10000; i++) {
      if (i % 13 == 0) assertThat(InterpolationSearch.search(buf, i, 0, 9999), greaterThanOrEqualTo(0L));
      else assertThat(InterpolationSearch.search(buf, i, 0, 9999), lessThan(0L));
    }
  }

  @Test
  public void searchBigVerySparse() {
    BigInts buf = new BigInts(BASE_SIZE);
    for (int i = 0; i < 10000; i++) buf.set(i, 0);
    buf.set(9998, 60);
    buf.set(9999, 100);
    assertThat(InterpolationSearch.search(buf, 60, 0, 9999), greaterThanOrEqualTo(0L));
    assertThat(InterpolationSearch.search(buf, 90, 0, 9999), lessThan(0L));
  }

  @Test
  public void searchDenseIntArray() {
    int[] arr = new int[BASE_SIZE];
    for (int i = 0; i < 10000; i++) arr[i] = i;
    Arrays.sort(arr, 0, 10000);
    for (int i = 0; i < 10000; i++)
      assertThat(InterpolationSearch.search(arr, i, 0, 9999), greaterThanOrEqualTo(0));
  }

  @Test
  public void searchSparseIntArray() {
    int[] arr = new int[BASE_SIZE];
    for (int i = 0; i < 10000; i++) arr[i] = i * 13;
    Arrays.sort(arr, 0, 10000);
    for (int i = 0; i < 10000; i++) {
      if (i % 13 == 0) assertThat(InterpolationSearch.search(arr, i, 0, 9999), greaterThanOrEqualTo(0));
      else assertThat(InterpolationSearch.search(arr, i, 0, 9999), lessThan(0));
    }
  }

  @Test
  public void searchVerySparse() {
    int[] arr = new int[BASE_SIZE];
    for (int i = 0; i < 10000; i++) arr[i] = 0;
    arr[9998] = 60;
    arr[9999] = 100;
    assertThat(InterpolationSearch.search(arr, 60, 0, 9999), greaterThanOrEqualTo(0));
    assertThat(InterpolationSearch.search(arr, 90, 0, 9999), lessThan(0));
  }

  @Test
  public void searchWithMultiplicities() {
    BigInts ints = new BigInts(BASE_SIZE);
    Random rand = new Random(1337);
    for (int i = 0; i < BASE_SIZE; i++) ints.set(i, rand.nextInt(1000));
    ints.sort();
    for (int i = 0; i < 1000; i++)
      assertThat(String.format("index=%d", i), InterpolationSearch.search(ints, i, 0, ints.size() - 1), greaterThanOrEqualTo(0L));
  }

  @Test
  public void searchPrimitive() {
    int[] data = new int[]{1, 2, 3, 5, 7, 11, 13, 17, 19, 23};
    for (int i = 0; i < 10; i++) {
      int idx = InterpolationSearch.search(data, data[i], 0, data.length - 1);
      assertThat(idx, is(i));
    }
  }

  @Test
  public void searchPrimitiveWithMultiplicities() {
    int[] arr = new int[BASE_SIZE];
    Random rand = new Random(1337);
    for (int i = 0; i < BASE_SIZE; i++) arr[i] = rand.nextInt(1000);
    Arrays.parallelSort(arr);
    for (int i = 0; i < 1000; i++) {
      int j = InterpolationSearch.search(arr, i, 0, arr.length - 1);
      assertThat(j, greaterThanOrEqualTo(0));
    }
  }


}
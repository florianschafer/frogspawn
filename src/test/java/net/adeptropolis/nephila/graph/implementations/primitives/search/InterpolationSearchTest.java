package net.adeptropolis.nephila.graph.implementations.primitives.search;

import net.adeptropolis.nephila.graph.implementations.primitives.Ints;
import net.adeptropolis.nephila.graph.implementations.primitives.arrays.ArrayInts;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;

public class InterpolationSearchTest {

  private static final int BASE_SIZE = (1 << 22);

  @Test
  public void searchBigDenseIntArray() {
    Ints buf = new ArrayInts(BASE_SIZE);
    for (int i = 0; i < 10000; i++) buf.set(i, i);
    buf.sort(10000);
    for (int i = 0; i < 10000; i++)
      Assert.assertThat(InterpolationSearch.search(buf, i, 0, 9999), greaterThanOrEqualTo(0L));
  }

  @Test
  public void searchBigSparseIntArray() {
    Ints buf = new ArrayInts(BASE_SIZE);
    for (int i = 0; i < 10000; i++) buf.set(i, i * 13);
    buf.sort(10000);
    for (int i = 0; i < 10000; i++) {
      if (i % 13 == 0) Assert.assertThat(InterpolationSearch.search(buf, i, 0, 9999), greaterThanOrEqualTo(0L));
      else Assert.assertThat(InterpolationSearch.search(buf, i, 0, 9999), lessThan(0L));
    }
  }

  @Test
  public void searchBigVerySparse() {
    Ints buf = new ArrayInts(BASE_SIZE);
    for (int i = 0; i < 10000; i++) buf.set(i, 0);
    buf.set(9998, 60);
    buf.set(9999, 100);
    Assert.assertThat(InterpolationSearch.search(buf, 60, 0, 9999), greaterThanOrEqualTo(0L));
    Assert.assertThat(InterpolationSearch.search(buf, 90, 0, 9999), lessThan(0L));
  }

  @Test
  public void searchDenseIntArray() {
    int[] arr = new int[BASE_SIZE];
    for (int i = 0; i < 10000; i++) arr[i] = i;
    Arrays.sort(arr, 0,10000);
    for (int i = 0; i < 10000; i++)
      Assert.assertThat(InterpolationSearch.search(arr, i, 0, 9999), greaterThanOrEqualTo(0));
  }

  @Test
  public void searchSparseIntArray() {
    int[] arr = new int[BASE_SIZE];
    for (int i = 0; i < 10000; i++) arr[i] = i *13;
    Arrays.sort(arr, 0,10000);
    for (int i = 0; i < 10000; i++) {
      if (i % 13 == 0) Assert.assertThat(InterpolationSearch.search(arr, i, 0, 9999), greaterThanOrEqualTo(0));
      else Assert.assertThat(InterpolationSearch.search(arr, i, 0, 9999), lessThan(0));
    }
  }

  @Test
  public void searchVerySparse() {
    int[] arr = new int[BASE_SIZE];
    for (int i = 0; i < 10000; i++) arr[i] = 0;
    arr[9998] = 60;
    arr[9999] = 100;
    Assert.assertThat(InterpolationSearch.search(arr, 60, 0, 9999), greaterThanOrEqualTo(0));
    Assert.assertThat(InterpolationSearch.search(arr, 90, 0, 9999), lessThan(0));
  }

}
package net.adeptropolis.nephila.graph.backend.primitives.sorting;

import net.adeptropolis.nephila.graph.backend.primitives.BigInts;
import net.adeptropolis.nephila.graph.backend.primitives.BigLongs;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class LongMergeSortTest {

  @Test
  public void mergeSort() {
    BigInts b = BigInts.of(77, 59, 14, 13, 19, 81, 89, 67, 32, 12);
    LongMergeSort.mergeSort(0, 10, b, b);
    assertThat(b, is(BigInts.of(12, 13, 14, 19, 32, 59, 67, 77, 81, 89)));
  }
}
package net.adeptropolis.nephila.graph.backend.arrays;

import java.util.Arrays;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

public class Helpers {

  public static void assertEquals(String name, long[] array, long... expected) {
    assertThat(String.format("%s length mismatch", name), array.length, is (expected.length));
    assertThat("Content mismatch", Arrays.compare(array, expected), is(0));
  }

  public static void assertEquals(String name, BigInts array, int... expected) {
    assertThat(String.format("%s length mismatch", name), array.size(), is((long)expected.length));
    for (int i = 0; i < array.size(); i++) {
      assertThat("Content mismatch", array.get(i), is(expected[i]));
    }
  }

  public static void assertEquals(String name, BigDoubles array, double... expected) {
    assertThat(String.format("%s length mismatch", name), array.size(), is ((long)expected.length));
    for (int i = 0; i < array.size(); i++) {
      assertThat("Content mismatch", array.get(i), closeTo(expected[i], 1E-9));
    }
  }

}

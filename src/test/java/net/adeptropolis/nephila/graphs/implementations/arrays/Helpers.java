/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.implementations.arrays;

import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class Helpers {

  public static void assertEquals(String name, long[] array, long... expected) {
    assertThat(String.format("%s length mismatch", name), array.length, is(expected.length));
    for (int i = 0; i < expected.length; i++) {
      assertThat("Content mismatch", array[i], is(expected[i]));
    }
  }

  public static void assertEquals(String name, BigInts array, int... expected) {
    assertThat(String.format("%s length mismatch", name), array.size(), is((long) expected.length));
    for (int i = 0; i < array.size(); i++) {
      assertThat("Content mismatch", array.get(i), is(expected[i]));
    }
  }

  public static void assertEquals(String name, BigDoubles array, double... expected) {
    assertThat(String.format("%s length mismatch", name), array.size(), is((long) expected.length));
    for (int i = 0; i < array.size(); i++) {
      assertThat("Content mismatch", array.get(i), closeTo(expected[i], 1E-9));
    }
  }

}

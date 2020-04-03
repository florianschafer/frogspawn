/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.implementations.arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

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

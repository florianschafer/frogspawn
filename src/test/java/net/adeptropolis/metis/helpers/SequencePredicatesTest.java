/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.helpers;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class SequencePredicatesTest {

  @Test
  public void intSingleElement() {
    verifyInt(0, 0, 0);
  }

  @Test
  public void intTwoElements() {
    verifyInt(0, 1, 0);
    verifyInt(0, 1, 1);
    verifyInt(1, 1, 1);
  }

  @Test
  public void intThreeElements() {
    verifyInt(0, 2, 2);
    verifyInt(1, 2, 2);
    verifyInt(2, 2, 2);
  }

  @Test
  public void intLowJustBelowHigh() {
    verifyInt(0, 9, 8);
    verifyInt(0, 10, 9);
  }

  @Test
  public void intLowJustBelowHighWithOffset() {
    verifyInt(8, 9, 8);
    verifyInt(9, 10, 9);
  }

  @Test
  public void objSingleElement() {
    verifyObj(0, 20, 0);
  }

  @Test
  public void objFindNearStepSizeBoundary() {
    verifyObj(0, 20, 19);
    verifyObj(0, 20, 20);
    verifyObj(0, 20, 21);
  }

  @Test
  public void objSequenceExhaustedBeforeFindingMatch() {
    Integer first = SequencePredicates.findFirst(0, 20,
            v -> (v >= 22) ? null :  v + 1, v -> v >= 25);
    assertNull(first);
  }

  @Test
  public void objSequenceExhaustedWithinInitialStepSize() {
    Integer first = SequencePredicates.findFirst(0, 20,
            v -> (v >= 15) ? null :  v + 1, v -> v >= 13);
    assertThat(first, is(13));
  }

  private void verifyInt(int low, int high, int threshold) {
    int first = SequencePredicates.findFirst(low, high, i -> i >= threshold);
    assertThat(first, is(threshold));
  }

  private void verifyObj(int initialValue, int stepSize, int threshold) {
    Integer first = SequencePredicates.findFirst(initialValue, stepSize, v -> v + 1, v -> v >= threshold);
    assertThat(first, is(threshold));
  }

}
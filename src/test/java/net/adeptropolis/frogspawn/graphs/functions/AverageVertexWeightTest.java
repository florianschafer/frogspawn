/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.functions;

import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class AverageVertexWeightTest extends GraphTestBase {

  @Test
  public void averageWeight() {
    Double avg = new AverageVertexWeight().apply(K43);
    assertThat(avg, closeTo(146.285, 1E-3));
  }

}
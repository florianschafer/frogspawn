/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.functions;

import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;

public class MedianVertexWeightTest extends GraphTestBase {

  @Test
  public void medianWeight() {
    Double median = new MedianVertexWeight().apply(K43);
    assertThat(median, closeTo(165.0, 1E-3));
  }


}
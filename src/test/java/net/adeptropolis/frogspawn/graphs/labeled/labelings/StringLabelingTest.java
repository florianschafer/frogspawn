/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled.labelings;

import org.junit.Test;

import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class StringLabelingTest {

  @Test
  public void emptyGraph() {
    StringLabeling labeling = new StringLabeling();
    assertThat(labeling.labels().count(), is(0L));
    assertThat(labeling.id("X"), is(-1));
    assertThat(labeling.label(42), nullValue());
  }

  @Test
  public void fewElements() {
    StringLabeling labeling = new StringLabeling();
    labeling.id("A");
    labeling.id("B");
    labeling.id("C");
    assertThat(labeling.label(0), is(("A")));
    assertThat(labeling.label(1), is(("B")));
    assertThat(labeling.label(2), is(("C")));
    assertThat(labeling.id("A"), is(0));
    assertThat(labeling.id("B"), is(1));
    assertThat(labeling.id("C"), is(2));
    assertThat(labeling.labels().collect(Collectors.toList()), contains("A", "B", "C"));
  }

  @Test
  public void commitAfterFirstLookup() {
    StringLabeling labeling = new StringLabeling();
    labeling.id("A");
    labeling.label(0);
    assertThat(labeling.id("B"), is(-1));
  }


}
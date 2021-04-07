/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import net.adeptropolis.frogspawn.graphs.labeled.labelings.DefaultLabeling;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class DefaultLabelingTest {

  @Test
  public void emptyGraph() {
    DefaultLabeling<String> labeling = new DefaultLabeling<>(String.class);
    assertThat(labeling.labels().count(), is(0L));
    assertThat(labeling.id("X"), is(-1));
    assertThat(labeling.label(42), nullValue());
  }

  @Test
  public void fewElements() {
    DefaultLabeling<String> labeling = new DefaultLabeling<>(String.class);
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
    DefaultLabeling<String> labeling = new DefaultLabeling<>(String.class);
    labeling.id("A");
    labeling.label(0);
    assertThat(labeling.id("B"), is(-1));
  }

}
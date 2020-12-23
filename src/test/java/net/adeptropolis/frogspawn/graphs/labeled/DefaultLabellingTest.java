/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import org.junit.Test;

import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class DefaultLabellingTest {

  @Test
  public void emptyGraph() {
    DefaultLabelling<String> labelling = new DefaultLabelling<>(String.class);
    assertThat(labelling.labels().count(), is(0L));
    assertThat(labelling.index("X"), is(-1));
    assertThat(labelling.label(42), nullValue());
  }

  @Test
  public void fewElements() {
    DefaultLabelling<String> labelling = new DefaultLabelling<>(String.class);
    labelling.index("A");
    labelling.index("B");
    labelling.index("C");
    assertThat(labelling.label(0), is(("A")));
    assertThat(labelling.label(1), is(("B")));
    assertThat(labelling.label(2), is(("C")));
    assertThat(labelling.index("A"), is(0));
    assertThat(labelling.index("B"), is(1));
    assertThat(labelling.index("C"), is(2));
    assertThat(labelling.labels().collect(Collectors.toList()), contains("A", "B", "C"));
  }

  @Test
  public void commitAfterFirstLookup() {
    DefaultLabelling<String> labelling = new DefaultLabelling<>(String.class);
    labelling.index("A");
    labelling.label(0);
    assertThat(labelling.index("B"), is(-1));
  }

}
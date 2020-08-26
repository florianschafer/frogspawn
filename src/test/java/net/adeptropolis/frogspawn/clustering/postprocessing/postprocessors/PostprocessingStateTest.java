/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PostprocessingStateTest {

  @Test
  public void merge() {
    PostprocessingState updated = new PostprocessingState()
            .update(new PostprocessingState(true, true))
            .update(new PostprocessingState(false, false));
    assertThat(updated.madeHierarchyChanges(), is(true));
  }

}
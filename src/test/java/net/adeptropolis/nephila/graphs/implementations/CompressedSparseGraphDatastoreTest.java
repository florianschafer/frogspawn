/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.implementations;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@Deprecated
public class CompressedSparseGraphDatastoreTest {

  @Test
  public void traverseEmptyMatrix() {
    CompressedSparseGraphDatastore storage = new DeprecatedCompressedSparseGraphBuilder().build();
    assertThat("Vertex count", storage.size(), is(0));
    assertThat("Edge count", storage.edgeCount(), is(0L));
  }

}
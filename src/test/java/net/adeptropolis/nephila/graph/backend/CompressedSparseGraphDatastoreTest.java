package net.adeptropolis.nephila.graph.backend;

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
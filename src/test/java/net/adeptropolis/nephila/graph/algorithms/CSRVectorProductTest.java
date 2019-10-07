package net.adeptropolis.nephila.graph.algorithms;

import net.adeptropolis.nephila.graph.backend.CompressedSparseGraphDatastore;
import net.adeptropolis.nephila.graph.backend.CompressedSparseGraphBuilder;
import net.adeptropolis.nephila.graph.backend.DeprecatedCompressedSparseGraphBuilder;
import net.adeptropolis.nephila.graph.backend.View;
import org.junit.Test;

import java.util.function.Consumer;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CSRVectorProductTest {

  @Test
  public void simpleMultiplication() {
    withStorage(mat -> {
      double[] res = new CSRVectorProduct(mat.defaultView()).multiply(new double[]{17, 19, 23});
      assertThat(res[0], is(206.0));
      assertThat(res[1], is(437.0));
      assertThat(res[2], is(593.0));
    });
  }

  private void withStorage(Consumer<CompressedSparseGraphDatastore> storageConsumer) {
    CompressedSparseGraphDatastore storage = new DeprecatedCompressedSparseGraphBuilder()
            .add(0, 0, 2)
            .add(0, 1, 3)
            .add(0, 2, 5)
            .add(1, 1, 7)
            .add(1, 2, 11)
            .add(2, 2, 13)
            .build();
    storageConsumer.accept(storage);
  }

  @Test
  public void subsetMultiplication() {
    withStorage(mat -> {
      View view = mat.view(new int[]{0, 2});
      double[] res = new CSRVectorProduct(view).multiply(new double[]{29, 31});
      assertThat(res[0], is(213.0));
      assertThat(res[1], is(548.0));
    });
  }

}
package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.backend.CSRStorage;
import net.adeptropolis.nephila.graph.backend.UndirectedCSRStorageBuilder;
import net.adeptropolis.nephila.graph.backend.View;
import org.junit.Test;

import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RowWeightsTest {

  @Test
  public void defaultWeights() {
    withDefaultMatrix(mat -> {
      RowWeights rowWeights = new RowWeights(mat.defaultView());
      double[] weights = rowWeights.get();
      assertThat(weights[0], is(5.0));
      assertThat(weights[1], is(14.0));
      assertThat(weights[2], is(8.0));
      assertThat(weights[3], is(7.0));
    });
  }

  private void withDefaultMatrix(Consumer<CSRStorage> storageConsumer) {
    CSRStorage storage = new UndirectedCSRStorageBuilder()
            .add(0, 1, 2)
            .add(0, 2, 3)
            .add(1, 2, 5)
            .add(1, 3, 7)
            .build();
    storageConsumer.accept(storage);
    storage.free();
  }

  @Test
  public void maskedColumsDoNotContribute() {
    withDefaultMatrix(mat -> {
      View view = mat.view(new int[]{0, 2, 3});
      RowWeights rowWeights = new RowWeights(view);
      double[] weights = rowWeights.get();
      assertThat(weights[0], is(3.0));
      assertThat(weights[1], is(3.0));
      assertThat(weights[2], is(0.0));
    });
  }

}
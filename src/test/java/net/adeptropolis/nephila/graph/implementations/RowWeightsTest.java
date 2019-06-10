package net.adeptropolis.nephila.graph.implementations;

import org.junit.Test;

import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RowWeightsTest {

  @Test
  public void defaultWeights() {
    withDefaultMatrix(view -> {
      RowWeights rowWeights = new RowWeights(view).update();
      double[] weights = rowWeights.get();
      assertThat(weights[0], is(5.0));
      assertThat(weights[1], is(14.0));
      assertThat(weights[2], is(8.0));
      assertThat(weights[3], is(7.0));
    });
  }


  @Test
  public void maskedColumsDoNotContribute() {
    withDefaultMatrix(view -> {
      view.set(new int[]{0, 2, 3});
      RowWeights rowWeights = new RowWeights(view).update();
      double[] weights = rowWeights.get();
      assertThat(weights[0], is(3.0));
      assertThat(weights[1], is(3.0));
      assertThat(weights[2], is(0.0));
    });
  }

  private void withDefaultMatrix(Consumer<CSRStorage.View> viewConsumer) {
    CSRStorage storage = new CSRStorageBuilder()
            .addSymmetric(0, 1, 2)
            .addSymmetric(0, 2, 3)
            .addSymmetric(1, 2, 5)
            .addSymmetric(1, 3, 7)
            .build();
    viewConsumer.accept(storage.defaultView());
    storage.free();
  }

}
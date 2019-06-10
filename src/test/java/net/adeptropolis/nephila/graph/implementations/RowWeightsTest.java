package net.adeptropolis.nephila.graph.implementations;

import org.junit.Test;

public class RowWeightsTest {

  @Test
  public void genericWeights() {




  }


  @Test
  public void maskedColumsDoNotContribute() {




  }

  @Test
  public void sizeFoo() {

  }


  private void foo() {
    CSRStorage storage = new CSRStorageBuilder()
            .add(0, 1, 2)
            .add(0, 2, 3)
            .add(1, 2, 5)
            .add(1, 3, 7)
            .build();
    CSRStorage.View view = storage.defaultView();
    RowWeights rowWeights = new RowWeights(view);


//    rowWeights.
    storage.free();
  }




}
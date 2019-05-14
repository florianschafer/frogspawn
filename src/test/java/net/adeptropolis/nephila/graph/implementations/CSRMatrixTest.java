package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.Buffers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CSRMatrixTest {

  @Test
  public void fullMatrix() {
    CSRMatrixBuilder b = new CSRMatrixBuilder();
    long indices = Buffers.allocInts(10000);
    long vec = Buffers.allocDoubles(10000);
    for (int i = 0; i < 10000; i++) {
      b.add(1, i, i + 1);
      Buffers.setInt(indices, i, i);
      Buffers.setDouble(vec, i, i + 3);
    }
    CSRMatrix mat = b.build();

    double p = mat.rowScalarProduct(1, vec, indices, 10000);
    assertThat(p, is(333483345000d));

    Buffers.free(vec);
    Buffers.free(indices);
    mat.free();
  }

//  private static void withBuilder(Function<CSRMatrixBuilder, CSRMatrixBuilder> builder,
//                                  Consumer<CSRMatrix> validator) {
//    CSRMatrix mat = builder.apply(new CSRMatrixBuilder()).build();
//    validator.accept(mat);
//    mat.free();
//  }

}
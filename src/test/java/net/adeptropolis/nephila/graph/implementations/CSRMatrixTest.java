package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.Buffers;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CSRMatrixTest {

  @Test
  public void fullRowScalarProduct() {
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

  @Test
  public void emptyIndexScalarProduct() {
    long indices = Buffers.allocInts(0);
    long vec = Buffers.allocDoubles(0);
    CSRMatrix mat = new CSRMatrixBuilder().add(0, 0, 17).build();
    double p = mat.rowScalarProduct(0, vec, indices, 0);
    assertThat(p, is(0d));
    Buffers.free(vec);
    Buffers.free(indices);
    mat.free();
  }

  @Test
  public void emptyRowScalarProduct() {
    long indices = Buffers.allocInts(3);
    Buffers.setInt(indices, 0, 0);
    Buffers.setInt(indices, 1, 2);
    Buffers.setInt(indices, 2, 3);
    long vec = Buffers.allocDoubles(3);
    Buffers.setDouble(vec, 0, 9);
    Buffers.setDouble(vec, 1, 11);
    Buffers.setDouble(vec, 2, 13);
    CSRMatrix mat = new CSRMatrixBuilder().add(1, 0, 17).build();
    double p = mat.rowScalarProduct(0, vec, indices, 3);
    assertThat(p, is(0d));
  }

  @Test
  public void scalarProductWithEntryOverhang() {
    long indices = Buffers.allocInts(3);
    Buffers.setInt(indices, 0, 0);
    Buffers.setInt(indices, 1, 2);
    Buffers.setInt(indices, 2, 3);
    long vec = Buffers.allocDoubles(3);
    Buffers.setDouble(vec, 0, 9);
    Buffers.setDouble(vec, 1, 11);
    Buffers.setDouble(vec, 2, 13);
    CSRMatrix mat = new CSRMatrixBuilder()
            .add(0, 0, 17)
            .add(0, 2, 19)
            .add(0, 4, 23)
            .add(0, 5, 29)
            .build();
    double p = mat.rowScalarProduct(0, vec, indices, 3);
    assertThat(p, is(9d * 17d + 11d * 19d));
    Buffers.free(vec);
    Buffers.free(indices);
    mat.free();
  }

  @Test
  public void scalarProductWithIndexOverhang() {
    long indices = Buffers.allocInts(4);
    Buffers.setInt(indices, 0, 0);
    Buffers.setInt(indices, 1, 2);
    Buffers.setInt(indices, 2, 3);
    Buffers.setInt(indices, 3, 4);
    long vec = Buffers.allocDoubles(4);
    Buffers.setDouble(vec, 0, 7);
    Buffers.setDouble(vec, 1, 11);
    Buffers.setDouble(vec, 2, 13);
    Buffers.setDouble(vec, 3, 19);
    CSRMatrix mat = new CSRMatrixBuilder()
            .add(0, 2, 23)
            .add(0, 4, 29)
            .build();
    double p = mat.rowScalarProduct(0, vec, indices, 4);
    assertThat(p, is(11d * 23d + 19d * 29d));
    Buffers.free(vec);
    Buffers.free(indices);
    mat.free();
  }

  @Test
  public void singleEntryScalarProductWithIndexOverhang() {
    long indices = Buffers.allocInts(2);
    Buffers.setInt(indices, 0, 0);
    Buffers.setInt(indices, 1, 2);
    long vec = Buffers.allocDoubles(2);
    Buffers.setDouble(vec, 0, 7);
    Buffers.setDouble(vec, 1, 11);
    CSRMatrix mat = new CSRMatrixBuilder().add(0, 2, 23).build();
    double p = mat.rowScalarProduct(0, vec, indices, 2);
    assertThat(p, is(11d * 23d));
    Buffers.free(vec);
    Buffers.free(indices);
    mat.free();
  }

  @Test
  public void singleIndexyScalarProductWithEntryOverhang() {
    long indices = Buffers.allocInts(1);
    Buffers.setInt(indices, 0, 0);
    long vec = Buffers.allocDoubles(4);
    Buffers.setDouble(vec, 0, 7);
    CSRMatrix mat = new CSRMatrixBuilder()
            .add(0, 0, 11)
            .add(0, 2, 23)
            .build();
    double p = mat.rowScalarProduct(0, vec, indices, 4);
    assertThat(p, is(7d * 11d));
    Buffers.free(vec);
    Buffers.free(indices);
    mat.free();
  }

  @Test
  public void multiplication() {

    long indices = Buffers.allocInts(4);
    Buffers.setInt(indices, 0, 0);
    Buffers.setInt(indices, 1, 2);
    Buffers.setInt(indices, 2, 3);
    Buffers.setInt(indices, 3, 4);

    long vec = Buffers.allocDoubles(4);
    Buffers.setDouble(vec, 0, 43);
    Buffers.setDouble(vec, 1, 47);
    Buffers.setDouble(vec, 2, 53);
    Buffers.setDouble(vec, 3, 59);

    long res = Buffers.allocDoubles(4);

    CSRMatrix mat = new CSRMatrixBuilder()
            .add(0, 0, 2)
            .add(0, 1, 3)
            .add(1, 0, 5)
            .add(1, 1, 7)
            .add(1, 2, 11)
            .add(2, 1, 13)
            .add(2, 2, 17)
            .add(2, 3, 19)
            .add(2, 4, 23)
            .add(4, 3, 37)
            .add(4, 4, 41)
            .build();

    mat.multiply(vec, indices, res, 4);
    assertThat(Buffers.getDouble(res, 0), is(2d * 43d));
    assertThat(Buffers.getDouble(res, 1), is(17d * 47d + 19d * 53 + 23d * 59));
    assertThat(Buffers.getDouble(res, 2), is(0d));
    assertThat(Buffers.getDouble(res, 3), is(37d * 53d + 41d * 59d));

    Buffers.free(res);
    Buffers.free(indices);
    Buffers.free(vec);
    mat.free();



  }

}
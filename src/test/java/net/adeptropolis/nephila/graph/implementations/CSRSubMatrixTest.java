package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.ArrayDoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.ArrayIntBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.DoubleBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.IntBuffer;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CSRSubMatrixTest {

  @Test
  public void fullRowScalarProduct() {
    CSRStorageBuilder b = new CSRStorageBuilder();
    IntBuffer indices = new ArrayIntBuffer(10000);
    DoubleBuffer vec = new ArrayDoubleBuffer(10000);
    for (int i = 0; i < 10000; i++) {
      b.add(1, i, i + 1);
      indices.set(i, i);
      vec.set(i, i + 3);
    }
    CSRStorage storage = b.build();
    CSRSubMatrix mat = new CSRSubMatrix(storage, indices);
    double p = mat.rowScalarProduct(1, vec);
    assertThat(p, is(333483345000d));
    vec.free();
    indices.free();
    mat.free();
    storage.free();
  }

  @Test
  public void emptyIndexScalarProduct() {
    IntBuffer indices = new ArrayIntBuffer(0);
    DoubleBuffer vec = new ArrayDoubleBuffer(0);
    CSRStorage storage = new CSRStorageBuilder().add(0, 0, 17).build();
    CSRSubMatrix mat = new CSRSubMatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec);
    assertThat(p, is(0d));
    vec.free();
    indices.free();
    mat.free();
    storage.free();
  }

  @Test
  public void emptyRowScalarProduct() {
    IntBuffer indices = new ArrayIntBuffer(3);
    indices.set(0, 0);
    indices.set(1, 2);
    indices.set(2, 3);
    DoubleBuffer vec = new ArrayDoubleBuffer(3);
    vec.set(0, 9);
    vec.set(1, 11);
    vec.set(2, 13);
    CSRStorage storage = new CSRStorageBuilder().add(1, 0, 17).build();
    CSRSubMatrix mat = new CSRSubMatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec);
    assertThat(p, is(0d));
    vec.free();
    indices.free();
    mat.free();
    storage.free();
  }

  @Test
  public void scalarProductWithEntryOverhang() {
    IntBuffer indices = new ArrayIntBuffer(3);
    indices.set(0, 0);
    indices.set(1, 2);
    indices.set(2, 3);
    DoubleBuffer vec = new ArrayDoubleBuffer(3);
    vec.set(0, 9);
    vec.set(1, 11);
    vec.set(2, 13);
    CSRStorage storage = new CSRStorageBuilder()
            .add(0, 0, 17)
            .add(0, 2, 19)
            .add(0, 4, 23)
            .add(0, 5, 29)
            .build();
    CSRSubMatrix mat = new CSRSubMatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec);
    assertThat(p, is(9d * 17d + 11d * 19d));
    vec.free();
    indices.free();
    mat.free();
    storage.free();
  }

  @Test
  public void scalarProductWithIndexOverhang() {
    IntBuffer indices = new ArrayIntBuffer(4);
    indices.set(0, 0);
    indices.set(1, 2);
    indices.set(2, 3);
    indices.set(3, 4);
    DoubleBuffer vec = new ArrayDoubleBuffer(4);
    vec.set(0, 7);
    vec.set(1, 11);
    vec.set(2, 13);
    vec.set(3, 19);
    CSRStorage storage = new CSRStorageBuilder()
            .add(0, 2, 23)
            .add(0, 4, 29)
            .build();
    CSRSubMatrix mat = new CSRSubMatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec);
    assertThat(p, is(11d * 23d + 19d * 29d));
    vec.free();
    indices.free();
    mat.free();
    storage.free();
  }

  @Test
  public void singleEntryScalarProductWithIndexOverhang() {
    IntBuffer indices = new ArrayIntBuffer(2);
    indices.set(0, 0);
    indices.set(1, 2);
    DoubleBuffer vec = new ArrayDoubleBuffer(2);
    vec.set(0, 7);
    vec.set(1, 11);
    CSRStorage storage = new CSRStorageBuilder().add(0, 2, 23).build();
    CSRSubMatrix mat = new CSRSubMatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec);
    assertThat(p, is(11d * 23d));
    vec.free();
    indices.free();
    mat.free();
    storage.free();
  }

  @Test
  public void singleIndexyScalarProductWithEntryOverhang() {
    IntBuffer indices = new ArrayIntBuffer(1);
    indices.set(0, 0);
    DoubleBuffer vec = new ArrayDoubleBuffer(4);
    vec.set(0, 7);
    CSRStorage storage = new CSRStorageBuilder()
            .add(0, 0, 11)
            .add(0, 2, 23)
            .build();
    CSRSubMatrix mat = new CSRSubMatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec);
    assertThat(p, is(7d * 11d));
    vec.free();
    indices.free();
    mat.free();
    storage.free();
  }


  @Test
  public void multiplication() {

    IntBuffer indices = new ArrayIntBuffer(4);

    indices.set(0, 0);
    indices.set(1, 2);
    indices.set(2, 3);
    indices.set(3, 4);

    DoubleBuffer vec = new ArrayDoubleBuffer(4);
    vec.set(0, 43);
    vec.set(1, 47);
    vec.set(2, 53);
    vec.set(3, 59);

    CSRStorage storage = new CSRStorageBuilder()
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
    CSRSubMatrix mat = new CSRSubMatrix(storage, indices);

    DoubleBuffer res = mat.multiply(vec, 16);
    assertThat(res.get(0), is(2d * 43d));
    assertThat(res.get(1), is(17d * 47d + 19d * 53 + 23d * 59));
    assertThat(res.get(2), is(0d));
    assertThat(res.get(3), is(37d * 53d + 41d * 59d));

    mat.free();
    indices.free();
    vec.free();
    storage.free();



  }

}
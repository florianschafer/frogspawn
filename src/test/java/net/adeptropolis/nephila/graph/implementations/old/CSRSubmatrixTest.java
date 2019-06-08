package net.adeptropolis.nephila.graph.implementations.old;

import net.adeptropolis.nephila.graph.implementations.CSRStorage;
import net.adeptropolis.nephila.graph.implementations.CSRStorageBuilder;
import net.adeptropolis.nephila.graph.implementations.old.CSRSubmatrix;
import net.adeptropolis.nephila.graph.implementations.old.Product;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CSRSubmatrixTest {

  private static final Product INNER_PROD = new CSRSubmatrix.DefaultProduct(null);

  @Test
  public void fullRowScalarProduct() {
    CSRStorageBuilder b = new CSRStorageBuilder();
    int[] indices = new int[10000];
    double[] vec = new double[10000];
    for (int i = 0; i < 10000; i++) {
      b.add(1, i, i + 1);
      indices[i] = i;
      vec[i] = i + 3;
    }
    CSRStorage storage = b.build();
    CSRSubmatrix mat = new CSRSubmatrix(storage, indices);
    double p = mat.rowScalarProduct(1, vec, INNER_PROD);
    assertThat(p, is(333483345000d));
    storage.free();
  }

  @Test
  public void emptyIndexScalarProduct() {
    int[] indices = new int[0];
    double[] vec = new double[0];
    CSRStorage storage = new CSRStorageBuilder().add(0, 0, 17).build();
    CSRSubmatrix mat = new CSRSubmatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec, INNER_PROD);
    assertThat(p, is(0d));
    storage.free();
  }

  @Test
  public void emptyRowScalarProduct() {
    int[] indices = {0, 2, 3};
    double[] vec = {9, 11, 13};
    CSRStorage storage = new CSRStorageBuilder().add(1, 0, 17).build();
    CSRSubmatrix mat = new CSRSubmatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec, INNER_PROD);
    assertThat(p, is(0d));
    storage.free();
  }

  @Test
  public void scalarProductWithEntryOverhang() {
    int[] indices = {0, 2, 3};
    double[] vec = {9, 11, 13};
    CSRStorage storage = new CSRStorageBuilder()
            .add(0, 0, 17)
            .add(0, 2, 19)
            .add(0, 4, 23)
            .add(0, 5, 29)
            .build();
    CSRSubmatrix mat = new CSRSubmatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec, INNER_PROD);
    assertThat(p, is(9d * 17d + 11d * 19d));
    storage.free();
  }

  @Test
  public void scalarProductWithIndexOverhang() {
    int[] indices = {0, 2, 3, 4};
    double[] vec = {7, 11, 13, 19};
    CSRStorage storage = new CSRStorageBuilder()
            .add(0, 2, 23)
            .add(0, 4, 29)
            .build();
    CSRSubmatrix mat = new CSRSubmatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec, INNER_PROD);
    assertThat(p, is(11d * 23d + 19d * 29d));
    storage.free();
  }

  @Test
  public void singleEntryScalarProductWithIndexOverhang() {
    int[] indices = {0, 2};
    double[] vec = {7, 11};
    CSRStorage storage = new CSRStorageBuilder().add(0, 2, 23).build();
    CSRSubmatrix mat = new CSRSubmatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec, INNER_PROD);
    assertThat(p, is(11d * 23d));
    storage.free();
  }

  @Test
  public void singleIndexyScalarProductWithEntryOverhang() {
    int[] indices = {0};
    double[] vec = {7};
    CSRStorage storage = new CSRStorageBuilder()
            .add(0, 0, 11)
            .add(0, 2, 23)
            .build();
    CSRSubmatrix mat = new CSRSubmatrix(storage, indices);
    double p = mat.rowScalarProduct(0, vec, INNER_PROD);
    assertThat(p, is(7d * 11d));
    storage.free();
  }


  @Test
  public void multiplication() {

    int[] indices = {0, 2, 3, 4};
    double[] vec = {43, 47, 53, 59};
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
    CSRSubmatrix mat = new CSRSubmatrix(storage, indices);

    double[] res = new double[4];
    mat.multiply(vec, res);
    assertThat(res[0], is(2d * 43d));
    assertThat(res[1], is(17d * 47d + 19d * 53 + 23d * 59));
    assertThat(res[2], is(0d));
    assertThat(res[3], is(37d * 53d + 41d * 59d));
    storage.free();

  }

}
package net.adeptropolis.nephila.graph.implementations;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class OffHeapCSRMatrixTest {

  @Test
  public void rowPointersEmptyHead() {
    OffHeapCSRMatrix matrix = new OffHeapCSRMatrix.Builder()
            .add(1, 0, 2.0)
            .add(1, 5, 3.0)
            .add(1, 7, 13.0)
            .build();
    assertThat("Row pointers match", getRowPointers(matrix), contains(0L, 0L, 3L));
    matrix.free();
  }


  @Test
  public void rowPointersSingleRow() {
    OffHeapCSRMatrix matrix = new OffHeapCSRMatrix.Builder()
            .add(0, 0, 2.0)
            .add(0, 5, 3.0)
            .add(0, 7, 13.0)
            .build();
    assertThat("Row pointers match", getRowPointers(matrix), contains(0L,3L));
    matrix.free();
  }

  @Test
  public void rowPointersMultipleEmptyRows() {
    OffHeapCSRMatrix matrix = new OffHeapCSRMatrix.Builder()
            .add(0, 0, 2.0)
            .add(0, 5, 3.0)
            .add(0, 7, 13.0)
            .add(3, 2, 17.0)
            .add(3, 4, 23.0)
            .add(3, 6, 29.0)
            .add(3, 8, 31.0)
            .build();
    assertThat("Row pointers match", getRowPointers(matrix), contains(0L, 3L, 3L, 3L, 7L));
    matrix.free();
  }

  @Test
  public void foo() {
//    TODO: Complete test, add one for empty head/tail
    HashMap<IndexPair, Double> entries = Maps.newHashMap();
    entries.put(IndexPair.of(0, 0), 2.0);
    entries.put(IndexPair.of(0, 5), 3.0);
    entries.put(IndexPair.of(0, 7), 13.0);
    entries.put(IndexPair.of(3, 2), 17.0);
    entries.put(IndexPair.of(3, 4), 23.0);
    entries.put(IndexPair.of(3, 6), 29.0);
    entries.put(IndexPair.of(3, 8), 31.0);
    OffHeapCSRMatrix.Builder builder = new OffHeapCSRMatrix.Builder();
    entries.forEach((idx, val) -> builder.add(idx.row, idx.col, val));
    OffHeapCSRMatrix matrix = builder.build();
    System.out.println(getRowPointers(matrix));
    for (int i = 0; i < 40; i++) {
      for (int j = 0; j < 40; j++) {
        double v = matrix.get(i, j);
        if (entries.containsKey(IndexPair.of(i, j))) {
          System.out.println("A");
          assertThat(v, is(entries.get(IndexPair.of(i, j))));
        } else {
          System.out.println("B");
          assertThat(v, is(0d));
        }
      }
    }

    System.out.println(matrix.get(3,8));
    //    assertThat("Row pointers match", getRowPointers(matrix), hasItems(0L, 3L, 3L, 3L, 7L));
    matrix.free();
  }



//  @Test
//  public void foo() {
//
//    LabeledTSVGraphSource source = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
//    OffHeapCSRMatrix.Builder builder = new OffHeapCSRMatrix.Builder();
//    source.edges().sequential().forEach(edge -> {
//      builder.add(edge.u, edge.v, edge.weight);
//      builder.add(edge.v, edge.u, edge.weight);
//    });
//    Stopwatch watch = Stopwatch.createStarted();
//    OffHeapCSRMatrix matrix = builder.build();
//    System.out.println("Took " + watch.elapsed().getSeconds());
//    System.out.println("Memory footprint is " + matrix.memoryFootprint());
//    matrix.free();
//
//
//
//  }

  private List<Long> getRowPointers(OffHeapCSRMatrix matrix) {
    return IntStream.range(0, matrix.numRows + 1)
            .mapToLong(matrix.rowPointers::get)
            .boxed()
            .collect(Collectors.toList());
  }

  private static class IndexPair {

    private final int row;
    private final int col;

    static IndexPair of(int row, int col) {
      return new IndexPair(row, col);
    }

    private IndexPair(int row, int col) {
      this.row = row;
      this.col = col;
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder()
              .append(row)
              .append(col)
              .hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      if (obj == null) { return false; }
      if (obj == this) { return true; }
      if (obj.getClass() != getClass()) {
        return false;
      }
      IndexPair rhs = (IndexPair) obj;
      return new EqualsBuilder()
              .append(row, rhs.row)
              .append(col, rhs.col)
              .isEquals();
    }
  }

}
package net.adeptropolis.nephila.graph.implementations;

import com.google.common.collect.ImmutableList;
import net.adeptropolis.nephila.graph.implementations.buffers.Buffers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CSRMatrixBuilderTest {

  @Test
  public void emptyMatrix() {
    withBuilder(Function.identity(), mat -> {
      assertThat(mat.getNumRows(), is(0));
      assertThat(mat.getNnz(), is(0L));
    });
  }

  @Test
  public void memoryFootprintReporting() {
    withBuilder(b -> b
            .add(0, 0, 1.0)
            .add(0, 1, 1.0), mat -> {
              assertThat(mat.getNnz(), is(2L));
              assertThat(mat.getNumRows(), is(1));
              assertThat(mat.memoryFootprint(), is("40 bytes"));
    });
  }

  @Test
  public void entrySorting() {
    withBuilder(b -> b
            .add(5, 3, 5.3)
            .add(2, 1, 2.1)
            .add(0, 0, 0.0)
            .add(0, 7, 0.7)
            .add(5, 0, 5.0)
            .add(0, 1, 0.1), mat -> {
      assertThat(mat.getNnz(), is(6L));
      assertThat(mat.getNumRows(), is(6));
    },
            ImmutableList.of(0L, 3L, 3L, 4L, 4L, 4L),
            ImmutableList.of(0, 1, 7, 1, 0, 3),
            ImmutableList.of(0.0, 0.1, 0.7, 2.1, 5.0, 5.3));
  }

  @Test
  public void trivialReduce() {
    withBuilder(b -> b
                    .add(0, 0, 1)
                    .add(0, 0, 3)
                    .add(0, 0, 5)
                    .add(0, 0, 7)
                    .add(0, 0, 11), mat -> {
              assertThat(mat.getNnz(), is(1L));
              assertThat(mat.getNumRows(), is(1));
            },
            ImmutableList.of(0L),
            ImmutableList.of(0),
            ImmutableList.of(27.0));
  }

  @Test
  public void reduceHead() {
    withBuilder(b -> b
                    .add(0, 0, 1)
                    .add(0, 0, 3)
                    .add(1, 1, 5)
                    .add(2, 2, 7)
                    .add(2, 2, 11)
                    .add(3, 3, 13), mat -> {
              assertThat(mat.getNnz(), is(4L));
              assertThat(mat.getNumRows(), is(4));
            },
            ImmutableList.of(0L, 1L, 2L, 3L),
            ImmutableList.of(0, 1, 2, 3),
            ImmutableList.of(4.0, 5.0, 18.0, 13.0));
  }


  @Test
  public void reduceTail() {
    withBuilder(b -> b
                    .add(0, 0, 1)
                    .add(1, 1, 3)
                    .add(2, 2, 5)
                    .add(2, 2, 7)
                    .add(3, 3, 11)
                    .add(3, 3, 13),mat -> {
              assertThat(mat.getNnz(), is(4L));
              assertThat(mat.getNumRows(), is(4));
            },
            ImmutableList.of(0L, 1L, 2L, 3L),
            ImmutableList.of(0, 1, 2, 3),
            ImmutableList.of(1.0, 3.0, 12.0, 24.0));
  }

  @Test
  public void emptyRows() {
    withBuilder(b -> b
                    .add(1, 1, 1)
                    .add(2, 2, 3)
                    .add(2, 2, 5)
                    .add(4, 4, 7),mat -> {
              assertThat(mat.getNnz(), is(3L));
              assertThat(mat.getNumRows(), is(5));
            },
            ImmutableList.of(0L, 0L, 1L, 2L, 2L),
            ImmutableList.of(1, 2, 4),
            ImmutableList.of(1.0, 8.0, 7.0));
  }

  @Test
  public void emptyFirstRow() {
    withBuilder(b -> b
                    .add(1, 1, 1)
                    .add(1, 2, 2)
                    .add(1, 3, 3),mat -> {
              assertThat(mat.getNnz(), is(3L));
              assertThat(mat.getNumRows(), is(2));
            },
            ImmutableList.of(0L, 0L),
            ImmutableList.of(1, 2, 3),
            ImmutableList.of(1.0, 2.0, 3.0));
  }

  private static void withBuilder(Function<CSRMatrixBuilder, CSRMatrixBuilder> builder,
                                  Consumer<CSRMatrix> validator) {
    CSRMatrix mat = builder.apply(new CSRMatrixBuilder()).build();
    validator.accept(mat);
    mat.free();
  }

  private static void withBuilder(Function<CSRMatrixBuilder, CSRMatrixBuilder> builder,
                                  Consumer<CSRMatrix> validator,
                                  List<Long> expectedRowPtrs,
                                  List<Integer> expectedColIndices,
                                  List<Double> expectedValues) {
    withBuilder(builder, mat -> {
      validator.accept(mat);
      long[] rowPtrs = Buffers.toLongArray(mat.rowPtrs, Math.toIntExact(mat.getNumRows()));
      int[] colIndices = Buffers.toIntArray(mat.colIndices, Math.toIntExact(mat.getNnz()));
      double[] values = Buffers.toDoubleArray(mat.values, Math.toIntExact(mat.getNnz()));
      assertThat("Last element in row pointers must be nnz", Buffers.getLong(mat.rowPtrs, mat.getNumRows()), is(mat.getNnz()));
      assertThat("Row pointers should match", Arrays.stream(rowPtrs).boxed().collect(Collectors.toList()), is(expectedRowPtrs));
      assertThat("Column indices should match", Arrays.stream(colIndices).boxed().collect(Collectors.toList()), is(expectedColIndices));
      assertThat("Values should match", Arrays.stream(values).boxed().collect(Collectors.toList()), is(expectedValues));
    });
  }


}
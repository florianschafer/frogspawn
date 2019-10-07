package net.adeptropolis.nephila.graph.backend;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CompressedSparseGraphBuilderTest {

  @Test
  public void emptyMatrix() {
    withBuilder(Function.identity(), storage -> {
      assertThat(storage.getSize(), is(0));
      assertThat(storage.getEdgeCount(), is(0L));
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
                    .add(0, 1, 0.1), storage -> {
              assertThat(storage.getEdgeCount(), is(11L));
              assertThat(storage.getSize(), is(8));
            },
            ImmutableList.of(0L, 4L, 6L, 7L, 8L, 8L, 10L, 10L),
            ImmutableList.of(0, 1, 5, 7, 0, 2, 1, 5, 0, 3, 0),
            ImmutableList.of(0.0, 0.1, 5.0, 0.7, 0.1, 2.1, 2.1, 5.3, 5.0, 5.3, 0.7));
  }

  @Test
  public void trivialReduce() {
    withBuilder(b -> b
                    .add(0, 0, 1)
                    .add(0, 0, 3)
                    .add(0, 0, 5)
                    .add(0, 0, 7)
                    .add(0, 0, 11), storage -> {
              assertThat(storage.getEdgeCount(), is(1L));
              assertThat(storage.getSize(), is(1));
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
                    .add(3, 3, 13), storage -> {
              assertThat(storage.getEdgeCount(), is(4L));
              assertThat(storage.getSize(), is(4));
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
                    .add(3, 3, 13), storage -> {
              assertThat(storage.getEdgeCount(), is(4L));
              assertThat(storage.getSize(), is(4));
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
                    .add(4, 4, 7), storage -> {
              assertThat(storage.getEdgeCount(), is(3L));
              assertThat(storage.getSize(), is(5));
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
                    .add(1, 3, 3), storage -> {
              assertThat(storage.getEdgeCount(), is(5L));
              assertThat(storage.getSize(), is(4));
            },
            ImmutableList.of(0L, 0L, 3L, 4L),
            ImmutableList.of(1, 2, 3, 1, 1),
            ImmutableList.of(1.0, 2.0, 3.0, 2.0, 3.0));
  }

  private static void withBuilder(Function<CompressedSparseGraphBuilder, CompressedSparseGraphBuilder> builder,
                                  Consumer<CompressedSparseGraphDatastore> validator) {
    CompressedSparseGraphDatastore storage = builder.apply(new CompressedSparseGraphBuilder()).buildDatastore();
    validator.accept(storage);
  }

  private static void withBuilder(Function<CompressedSparseGraphBuilder, CompressedSparseGraphBuilder> builder,
                                  Consumer<CompressedSparseGraphDatastore> validator,
                                  List<Long> expectedRowPtrs,
                                  List<Integer> expectedColIndices,
                                  List<Double> expectedValues) {
    withBuilder(builder, storage -> {
      validator.accept(storage);
      List<Long> rowPtrs = Lists.newArrayList();
      for (int i = 0; i < storage.getSize(); i++) rowPtrs.add(storage.pointers[i]);
      List<Integer> colIndices = Lists.newArrayList();
      for (int i = 0; i < storage.getEdgeCount(); i++) colIndices.add(storage.edges.get(i));
      List<Double> values = Lists.newArrayList();
      for (int i = 0; i < storage.getEdgeCount(); i++) values.add(storage.weights.get(i));
      assertThat("Last element in row pointers must be nnz", storage.pointers[storage.getSize()], is(storage.getEdgeCount()));
      assertThat("Row pointers should match", rowPtrs, is(expectedRowPtrs));
      assertThat("Column vertices should match", colIndices, is(expectedColIndices));
      assertThat("Values should match", values, is(expectedValues));
    });
  }

  private void foo() {

  }

}
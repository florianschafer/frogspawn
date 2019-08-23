package net.adeptropolis.nephila.graph.backend;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.function.BiConsumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

public class CSRStorageTest {

  @Test
  public void traverseEmptyMatrix() {
    CSRStorage storage = new CSRStorageBuilder().build();
    View view = storage.defaultView();
    CollectingEdgeVisitor visitor = new CollectingEdgeVisitor();
    view.traverseAdjacent(0, visitor);
    assertThat(visitor.entries, empty());
    storage.free();
  }

  @Test
  public void traverseEmptyRow() {
    withDefaultMatrix((mat, visitor) -> {
      mat.defaultView().traverseAdjacent(2, visitor);
      assertThat(visitor.entries, empty());
    });
  }

  private void withDefaultMatrix(BiConsumer<CSRStorage, CollectingEdgeVisitor> consumer) {
    CSRStorage storage = new CSRStorageBuilder()
            .add(1, 1, 2)
            .add(1, 2, 3)
            .add(1, 4, 7)
            .add(3, 3, 6)
            .add(3, 1, 5)
            .add(4, 5, 7)
            .add(5, 6, 9)
            .add(5, 7, 11)
            .build();
    consumer.accept(storage, new CollectingEdgeVisitor());
    storage.free();
  }

  @Test
  public void traverseRowWithNotAllColIndicesSelected() {
    withDefaultMatrix((mat, visitor) -> {
      mat.view(new int[]{1, 2}).traverseAdjacent(0, visitor);
      assertThat(visitor.entries, contains(
              Entry.of(0, 0, 2),
              Entry.of(0, 1, 3)));
    });
  }

  @Test
  public void traverseFullRow() {
    withDefaultMatrix((mat, visitor) -> {
      mat.defaultView().traverseAdjacent(1, visitor);
      assertThat(visitor.entries, contains(
              Entry.of(1, 1, 2),
              Entry.of(1, 2, 3),
              Entry.of(1, 4, 7)));
    });
  }

  @Test
  public void traverseRowByEntries() {
    withDefaultMatrix((mat, visitor) -> {
      mat.defaultView().traverseAdjacent(3, visitor);
      assertThat(visitor.entries, contains(
              Entry.of(3, 1, 5),
              Entry.of(3, 3, 6)));
    });
  }

  @Test
  public void traverseRowByIndices() {
    withDefaultMatrix((mat, visitor) -> {
      mat.view(new int[]{3}).traverseAdjacent(0, visitor);
      assertThat(visitor.entries, contains(Entry.of(0, 0, 6)));
    });
  }

  private static class Entry {

    private final int rowIdx;
    private final int colIdx;
    private final double value;

    private Entry(int rowIdx, int colIdx, double value) {
      this.rowIdx = rowIdx;
      this.colIdx = colIdx;
      this.value = value;
    }

    static Entry of(int rowIdx, int colIdx, double value) {
      return new Entry(rowIdx, colIdx, value);
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof Entry)) return false;
      Entry otherEntry = (Entry) other;
      return rowIdx == otherEntry.rowIdx
              && colIdx == otherEntry.colIdx
              && value == otherEntry.value;
    }

    @Override
    public String toString() {
      return String.format("(%d, %d, %f)", rowIdx, colIdx, value);
    }
  }

  private class CollectingEdgeVisitor implements EdgeVisitor {

    private final List<Entry> entries = Lists.newArrayList();

    @Override
    public void visit(int u, int v, double weight) {
      entries.add(Entry.of(u, v, weight));
    }

    @Override
    public void reset() {
      entries.clear();
    }

  }

}
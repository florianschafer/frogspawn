package net.adeptropolis.nephila.graph.implementations;

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
    CSRStorage.View view = storage.defaultView();
    CollectingEntryVisitor visitor = new CollectingEntryVisitor();
    view.traverseRow(0, visitor);
    assertThat(visitor.entries, empty());
    storage.free();
  }

  @Test
  public void traverseEmptyRow() {
      withDefaultView((view, visitor) -> {
        view.traverseRow(2, visitor);
        assertThat(visitor.entries, empty());
      });
  }

  @Test
  public void traverseRowWithNoColumnSelected() {
    withDefaultView((view, visitor) -> {
      view.set(new int[]{5});
      view.traverseRow(5, visitor);
      assertThat(visitor.entries, empty());
    });
  }

  @Test
  public void traverseRowWithNotAllColIndicesSelected() {
    withDefaultView((view, visitor) -> {
      view.set(new int[]{1, 2});
      view.traverseRow(0, visitor);
      assertThat(visitor.entries, contains(
              Entry.of(0, 0, 2),
              Entry.of(0, 1, 3)));
    });
  }

  @Test
  public void traverseFullRow() {
    withDefaultView((view, visitor) -> {
      view.traverseRow(1, visitor);
      assertThat(visitor.entries, contains(
              Entry.of(1, 1, 2),
              Entry.of(1, 2, 3),
              Entry.of(1, 4, 7)));
    });
  }

  @Test
  public void traverseRowByEntries() {
    withDefaultView((view, visitor) -> {
      view.traverseRow(3, visitor);
      assertThat(visitor.entries, contains(
              Entry.of(3, 1, 5),
              Entry.of(3, 3, 6)));
    });
  }

  @Test
  public void traverseRowByIndices() {
    withDefaultView((view, visitor) -> {
      view.set(new int[]{3});
      view.traverseRow(0, visitor);
      assertThat(visitor.entries, contains(Entry.of(0, 0, 6)));
    });
  }

  private void withDefaultView(BiConsumer<CSRStorage.View, CollectingEntryVisitor> viewConsumer) {
    CSRStorage storage = new CSRStorageBuilder()
            .add(1,1, 2)
            .add(1,2, 3)
            .add(1,4,7)
            .add(3,3, 6)
            .add(3,1, 5)
            .add(4,5,7)
            .add(5,6,9)
            .add(5,7,11)
            .build();
    CSRStorage.View view = storage.defaultView();
    viewConsumer.accept(view, new CollectingEntryVisitor());
    storage.free();
  }

  private class CollectingEntryVisitor implements EntryVisitor {

    private final List<Entry> entries = Lists.newArrayList();

    @Override
    public void visit(int rowIdx, int colIdx, double value) {
      entries.add(Entry.of(rowIdx, colIdx, value));
    }

    @Override
    public void reset() {
      entries.clear();
    }

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

}
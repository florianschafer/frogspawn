package net.adeptropolis.nephila.graph.implementations;

import net.adeptropolis.nephila.graph.implementations.buffers.IntBuffer;
import net.adeptropolis.nephila.graph.implementations.buffers.arrays.ArrayIntBuffer;

import java.util.Arrays;
import java.util.function.Consumer;

// TODO: stack might actually be the final index
// TODO: Refactor
// TODO: Share matrix stuff. MATRIX TRAVERSAL MAY BE FACTORED OUT AND JOINED WITH PRODUCT CLASSES

public class TarjanSCC {

  private final CSRSubmatrix adj;
  private final int[] stack;
  private int stackSize;
  private final int[] compIndices;
  private final int[] lowLinks;
  private final boolean[] onStack;
  private int idx;

  public TarjanSCC(CSRSubmatrix adj) {
    this.adj = adj;
    this.stack = new int[(int)adj.indices.size()];
    this.stackSize = 0;
    this.compIndices = new int[(int)adj.indices.size()];
    Arrays.fill(this.compIndices, -1);
    this.lowLinks = new int[(int)adj.indices.size()];
    Arrays.fill(this.lowLinks, -1);
    this.onStack = new boolean[(int)adj.indices.size()];
    Arrays.fill(this.onStack, false);

    this.idx = 0;
  }

  public void compute(Consumer<IntBuffer> componentConsumer) {
    for (int i = 0; i < adj.indices.size(); i++) {
      if (compIndices[i] == -1) {
        strongConnect(i, componentConsumer);
      }
    }
  }

  private void strongConnect(int i, Consumer<IntBuffer> componentConsumer) {

    compIndices[i] = idx;
    lowLinks[i] = idx;
    idx++;
    stack[stackSize++] = i;
    onStack[i] = true;

    traverseNeighbors(i, componentConsumer);

    if (lowLinks[i] == compIndices[i]) {
      IntBuffer comp = new ArrayIntBuffer(adj.indices.size());
      int compSize = 0;
      while (true) {
        int w = stack[--stackSize];
        onStack[w] = false;
        comp.set(compSize++, w);
        if (w == i) break;
      }
      comp.resize(compSize);
      componentConsumer.accept(comp);
    }
  }

  private void traverseNeighbors(final int row, Consumer<IntBuffer> componentConsumer) {
    if (adj.indices.size() == 0) return;
    int origRow = adj.indices.get(row);
    long low = adj.data.getRowPtrs()[origRow];
    long high = adj.data.getRowPtrs()[origRow + 1];
    if (low == high) return; // Empty row

    int col;
    long retrievedIdx;
    long secPtr;

    if (adj.indices.size() > high - low) {
      secPtr = 0L;
      for (long ptr = low; ptr < high; ptr++) {
        col = adj.data.getColIndices().get(ptr);
        retrievedIdx = adj.indices.searchSorted(col, secPtr, adj.indices.size() - 1);
        if (retrievedIdx >= 0) {
          update(row, (int) retrievedIdx, componentConsumer);
          secPtr = retrievedIdx + 1;
        }
        if (secPtr >= adj.indices.size()) break;
      }
    } else {
      secPtr = low;
      for (long ptr = 0; ptr < adj.indices.size(); ptr++) {
        col = adj.indices.get(ptr);
        retrievedIdx = adj.data.getColIndices().searchSorted(col, secPtr, high);
        if (retrievedIdx >= 0 && retrievedIdx < high) {
          update(row, (int) ptr, componentConsumer);
          secPtr = retrievedIdx + 1;
        }
        if (secPtr >= high) break;
      }
    }
  }

  private void update(int v, int w, Consumer<IntBuffer> componentConsumer) {
    if (compIndices[w] == -1) {
      strongConnect(w, componentConsumer);
      lowLinks[v] = Math.min(lowLinks[v], lowLinks[w]);
    } else if (onStack[w]) {
      lowLinks[v] = Math.min(lowLinks[v], compIndices[w]);
    }
  }


}

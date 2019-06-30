package net.adeptropolis.nephila.graph.implementations;

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;

import java.util.Arrays;
import java.util.function.Consumer;

// TODO: This one might need some more optimization
// TODO: Prealloc sets to some sensible size
// TODO: IntRBTreeSets are only chosen because of faster clear(). Check.

public class ConnectedComponents {

  private final CSRStorage.View view;
  private final CCVisitor visitor;
  private IntLinkedOpenHashSet globalQueue;
  private IntRBTreeSet ccQueue;
  private IntRBTreeSet currentCC;

  public ConnectedComponents(CSRStorage.View view) {
    this.view = view;
    this.visitor = new CCVisitor();
    this.globalQueue = new IntLinkedOpenHashSet();
    this.ccQueue = new IntRBTreeSet();
    this.currentCC = new IntRBTreeSet();
  }

  public void find(Consumer<CSRStorage.View> componentConsumer) {

    globalQueue.clear();
    for (int i = 0; i < view.size(); i++) globalQueue.add(i);

    while (!globalQueue.isEmpty()) {
      visitor.reset();
      int i = globalQueue.removeFirstInt();
      ccQueue.add(i);
      while (!ccQueue.isEmpty()) {
        int j = ccQueue.firstInt();
        ccQueue.remove(j);
        currentCC.add(j);
        view.traverseRow(j, visitor);
      }

      finalizeComponent(componentConsumer);

      globalQueue.removeAll(currentCC);
    }

  }

  private void finalizeComponent(Consumer<CSRStorage.View> componentConsumer) {
    int[] componentIndices = currentCC.toIntArray();
    for (int j = 0; j < componentIndices.length; j++)
      componentIndices[j] = view.get(componentIndices[j]); // Map view indices to actual matrix indices
    Arrays.parallelSort(componentIndices);
//    System.out.println("Remaining: " + globalQueue.size());
    componentConsumer.accept(view.subview(componentIndices));
  }

  private class CCVisitor implements EntryVisitor {

    @Override
    public void visit(int rowIdx, int colIdx, double value) {
      if (!ccQueue.contains(colIdx) && !currentCC.contains(colIdx)) ccQueue.add(colIdx);
    }

    @Override
    public void reset() {
      currentCC.clear();
      ccQueue.clear();
    }
  }

}

package net.adeptropolis.nephila.graph.implementations;

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

// TODO: Prealloc sets to some sensible size

public class ConnectedComponents {

  private final CSRStorage.View view;
  private final CCVisitor visitor;
  private final IntLinkedOpenHashSet globalQueue;
  private final IntLinkedOpenHashSet ccQueue;
  private final IntOpenHashSet currentCC;

  public ConnectedComponents(CSRStorage.View view) {
    this.view = view;
    this.visitor = new CCVisitor();
    this.globalQueue = new IntLinkedOpenHashSet();
    this.ccQueue = new IntLinkedOpenHashSet();
    this.currentCC = new IntOpenHashSet();
  }

  public synchronized void foo() {

    globalQueue.clear();
    for (int i = 0; i < view.size(); i++) globalQueue.add(i);

    while (!globalQueue.isEmpty()) {
      visitor.reset();
      int i = globalQueue.removeFirstInt();
      ccQueue.add(i);
      currentCC.add(i);
      while (!ccQueue.isEmpty()) {
        int j = ccQueue.removeFirstInt();
        currentCC.add(j);
        view.traverseRow(j, visitor);
      }
      globalQueue.removeAll(currentCC);
    }

  }

  private class CCVisitor implements EntryVisitor {

    @Override
    public void visit(int rowIdx, int colIdx, double value) {
      if (!ccQueue.contains(colIdx) && !currentCC.contains(colIdx)) {
        ccQueue.add(colIdx);
        currentCC.add(colIdx);
      }
    }

    @Override
    public void reset() {
      currentCC.clear();
      ccQueue.clear();
    }
  }

}

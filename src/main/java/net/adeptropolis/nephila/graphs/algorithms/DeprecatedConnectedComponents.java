package net.adeptropolis.nephila.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.nephila.graphs.EdgeConsumer;
import net.adeptropolis.nephila.graphs.implementations.View;

import java.util.Arrays;
import java.util.function.Consumer;

// TODO: This one might need some more optimization
// TODO: Prealloc sets to some sensible size
// TODO: IntRBTreeSets are only chosen because of faster clear(). Check.

@Deprecated
public class DeprecatedConnectedComponents {

  private final View view;
  private final CCConsumer visitor;
  private IntLinkedOpenHashSet globalQueue;
  private IntRBTreeSet ccQueue;
  private IntRBTreeSet currentCC;

  public DeprecatedConnectedComponents(View view) {
    this.view = view;
    this.visitor = new CCConsumer();
    this.globalQueue = new IntLinkedOpenHashSet();
    this.ccQueue = new IntRBTreeSet();
    this.currentCC = new IntRBTreeSet();
  }

  public void find(Consumer<View> componentConsumer) {

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
        view.traverseAdjacent(j, visitor);
      }

      finalizeComponent(componentConsumer);

      globalQueue.removeAll(currentCC);
    }

  }

  private void finalizeComponent(Consumer<View> componentConsumer) {
    int[] componentIndices = currentCC.toIntArray();
    for (int j = 0; j < componentIndices.length; j++)
      componentIndices[j] = view.getVertex(componentIndices[j]); // Map view indices to actual matrix indices
    Arrays.parallelSort(componentIndices);
    componentConsumer.accept(view.subview(componentIndices));
  }

  private class CCConsumer implements EdgeConsumer {

    @Override
    public void accept(int u, int v, double weight) {
      if (!ccQueue.contains(v) && !currentCC.contains(v)) ccQueue.add(v);
    }

    //    @Override
    public void reset() {
      currentCC.clear();
      ccQueue.clear();
    }
  }

}

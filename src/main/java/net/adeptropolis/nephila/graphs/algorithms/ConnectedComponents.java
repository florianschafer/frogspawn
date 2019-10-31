package net.adeptropolis.nephila.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import net.adeptropolis.nephila.graphs.EdgeConsumer;
import net.adeptropolis.nephila.graphs.Graph;

import java.util.function.Consumer;

public class ConnectedComponents implements EdgeConsumer {

  private final Graph graph;
  private IntLinkedOpenHashSet remaining;
  private IntLinkedOpenHashSet componentQueue;
  private IntLinkedOpenHashSet component;

  public ConnectedComponents(Graph graph) {
    this.graph = graph;
    this.remaining = new IntLinkedOpenHashSet();
    this.componentQueue = new IntLinkedOpenHashSet();
    this.component = new IntLinkedOpenHashSet();
  }

  public static void find(Graph graph, Consumer<Graph> consumer) {
    new ConnectedComponents(graph).find(consumer);
  }

  public void find(Consumer<Graph> consumer) {
    remaining.clear();
    for (int i = 0; i < graph.size(); i++) remaining.add(i);
    while (!remaining.isEmpty()) {
      int i = remaining.removeFirstInt();
      processComponent(i);
      Graph subgraph = graph.locallyInducedSubgraph(component.iterator());
      consumer.accept(subgraph);
      remaining.removeAll(component);
    }
  }

  private void processComponent(int i) {
    component.clear();
    componentQueue.clear();
    componentQueue.add(i);
    while (!componentQueue.isEmpty()) {
      int j = componentQueue.removeFirstInt();
      component.add(j);
      graph.traverse(j, this);
    }
  }

  @Override
  public void accept(int u, int v, double weight) {
    if (!componentQueue.contains(v) && !component.contains(v)) componentQueue.add(v);
  }

}

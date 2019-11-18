package net.adeptropolis.nephila.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.nephila.graphs.EdgeConsumer;
import net.adeptropolis.nephila.graphs.Graph;

import java.util.function.Consumer;

/**
 * <p>Compute the connected components of a graph using DFS</p>
 */

public class ConnectedComponents implements EdgeConsumer {

  private final Graph graph;
  private IntLinkedOpenHashSet remaining;
  private IntLinkedOpenHashSet componentQueue;
  private IntOpenHashSet component;

  /**
   * Create a new ConnectedComponents instance
   *
   * @param graph The input graph
   */

  public ConnectedComponents(Graph graph) {
    this.graph = graph;
    this.remaining = new IntLinkedOpenHashSet();
    this.componentQueue = new IntLinkedOpenHashSet();
    this.component = new IntOpenHashSet();
  }

  /**
   * <p>Convenience access for find(..) below</p>
   * <p>Find the connected components of a graph</p>
   *
   * @param graph    The input graph
   * @param consumer A consumer for the connected subgraphs
   */

  public static void find(Graph graph, Consumer<Graph> consumer) {
    new ConnectedComponents(graph).find(consumer);
  }

  /**
   * <p>Find all connected components of the graph</p>
   *
   * @param consumer A consumer for the connected subgraphs
   */

  public void find(Consumer<Graph> consumer) {
    remaining.clear();
    for (int i = 0; i < graph.size(); i++) remaining.add(i);
    while (!remaining.isEmpty()) {
      int i = remaining.removeFirstInt();
      processComponent(i);
      Graph subgraph = graph.localInducedSubgraph(component.iterator());
      consumer.accept(subgraph);
      remaining.removeAll(component);
    }
  }

  /**
   * Process the current connected component
   *
   * @param i First member vertex of the component
   */

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

  /**
   * Internal: Callback for graph traversal
   *
   * @param u      Left vertex
   * @param v      Right vertex
   * @param weight Edge weight
   */

  @Override
  public void accept(int u, int v, double weight) {
    if (!componentQueue.contains(v) && !component.contains(v)) componentQueue.add(v);
  }

}

/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.traversal.EdgeConsumer;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * <p>Compute the connected components of a graph using DFS</p>
 */

public class ConnectedComponents implements EdgeConsumer {

  private static final Logger LOG = LoggerFactory.getLogger(ConnectedComponents.class.getSimpleName());

  private final Graph graph;
  private final IntLinkedOpenHashSet remaining;
  private final IntLinkedOpenHashSet componentQueue;
  private IntOpenHashSet component;

  /**
   * Create a new ConnectedComponents instance
   *
   * @param graph The input graph
   */

  private ConnectedComponents(Graph graph) {
    this.graph = graph;
    this.remaining = new IntLinkedOpenHashSet();
    this.componentQueue = new IntLinkedOpenHashSet();
    this.component = new IntOpenHashSet();
  }

  /**
   * <p>Convenience access for find(..)</p>
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

  private void find(Consumer<Graph> consumer) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    remaining.clear();
    for (int i = 0; i < graph.order(); i++) remaining.add(i);
    int comps = 0;
    while (!remaining.isEmpty()) {
      int i = remaining.removeFirstInt();
      processComponent(i);
      Graph subgraph = graph.localInducedSubgraph(component.iterator());
      consumer.accept(subgraph);
      comps++;
      remaining.removeAll(component);
    }
    stopWatch.stop();
    LOG.trace("Isolated {} connected components in {}", comps, stopWatch);
  }

  /**
   * Process the current connected component
   *
   * @param i First member vertex of the component
   */

  private void processComponent(int i) {
    /*
    component.clear();
    Unfortunately the line below is way faster than any clear() on a suitable data structure. Let the GC have fun with it.
     */
    component = new IntOpenHashSet();
    componentQueue.clear();
    componentQueue.add(i);
    while (!componentQueue.isEmpty()) {
      int j = componentQueue.removeFirstInt();
      component.add(j);
      graph.traverseIncidentEdges(j, this);
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

/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntLinkedOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * <p>Compute the connected components of a graph using DFS</p>
 */

public class ConnectedComponents {

  private static final Logger LOG = LoggerFactory.getLogger(ConnectedComponents.class.getSimpleName());

  private final Graph graph;

  /**
   * Create a new ConnectedComponents instance
   *
   * @param graph The input graph
   */

  private ConnectedComponents(Graph graph) {
    this.graph = graph;
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
    IntLinkedOpenHashSet remaining = new IntLinkedOpenHashSet();
    for (int i = 0; i < graph.order(); i++) remaining.add(i);
    int comps = 0;
    while (!remaining.isEmpty()) {
      int i = remaining.removeFirstInt();
      IntOpenHashSet component = processComponent(i);
      Graph subgraph = graph.localSubgraph(component.iterator());
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
   * @return List of vertices that belong to the same connected component that i does
   */

  private IntOpenHashSet processComponent(int i) {
    IntOpenHashSet component = new IntOpenHashSet();
    IntLinkedOpenHashSet queue = new IntLinkedOpenHashSet();
    queue.add(i);
    while (!queue.isEmpty()) {
      int j = queue.removeFirstInt();
      component.add(j);
      graph.traverseIncidentEdges(j, (u, v, weight) -> {
        if (!queue.contains(v) && !component.contains(v)) {
          queue.add(v);
        }
      }, TraversalMode.DEFAULT);
    }
    return component;
  }

}

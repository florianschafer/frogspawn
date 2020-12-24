/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;


import net.adeptropolis.frogspawn.graphs.labeled.labelings.DefaultLabeling;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: Test
public class GraphSupplementer {

  private static final Logger LOG = LoggerFactory.getLogger(GraphSupplementer.class.getSimpleName());

  public static <V extends Serializable> LabeledGraph<V> extend(LabeledGraph<V> graph, LabeledGraph<V> supplement, int maxDist, double supplementBoost, Class<V> clazz) {
    Set<V> vertices = selectVertices(graph, supplement, maxDist);
    LabeledGraphBuilder<V> builder = new LabeledGraphBuilder<>(new DefaultLabeling<>(clazz));
    double boostFactor = supplementBoost * averageWeight(graph) / averageWeight(supplement);
    addEdges(graph, vertices, builder, 1);
    addEdges(supplement, vertices, builder, boostFactor);
    LabeledGraph<V> result = builder.build();
    LOG.debug("Merged two graphs {} x {} => {}, edges {} => {}",  graph.getGraph().order(), supplement.getGraph().order(), result.getGraph().order(), graph.getGraph().size(), result.getGraph().size());
    return result;
  }

  private static <V extends Serializable> double averageWeight(LabeledGraph<V> graph) {
    return graph.getGraph().totalWeight() / graph.getGraph().size();
  }

  private static <V extends Serializable> void addEdges(LabeledGraph<V> graph, Set<V> vertices, LabeledGraphBuilder<V> builder, double boostFactor) {
    graph.traverse((u, v, weight) -> {
      if (vertices.contains(u) && vertices.contains(v)) {
        builder.add(u, v, boostFactor * weight);
      }
    }, TraversalMode.LOWER_TRIANGULAR);
  }

  private static <V extends Serializable> Set<V> selectVertices(LabeledGraph<V> graph, LabeledGraph<V> supplement, int maxDist) {
    Set<V> selected = graph.labels().collect(Collectors.toSet());
    for (int i = 1; i <= maxDist; i++) {
      selected.addAll(grow(supplement, selected));
    }
    return selected;
  }

  private static <V extends Serializable> Set<V> grow(LabeledGraph<V> supplement, Set<V> selected) {
    Set<V> additions = new HashSet<>();
    supplement.traverse((u, v, weight) -> {
      if (selected.contains(u) && !selected.contains(v)) {
        additions.add(v);
      }
      if (selected.contains(v) && !selected.contains(u)) {
        additions.add(u);
      }
    }, TraversalMode.LOWER_TRIANGULAR);
    return additions;
  }

}

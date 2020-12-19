/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;


import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GraphMerger {

  public static <V extends Serializable> LabeledGraph<V> extend(LabeledGraph<V> graph, LabeledGraph<V> supplement, int maxDist, int minDegree) {
    Set<V> vertices = ensureDegrees(graph, supplement, selectVertices(graph, supplement, maxDist), minDegree);
    LabeledGraphBuilder<V> builder = new LabeledGraphBuilder<>(graph.getLabelClass());
    addEdges(graph, vertices, builder);
    addEdges(supplement, vertices, builder);
    return builder.build();
  }

  private static <V extends Serializable> void addEdges(LabeledGraph<V> graph, Set<V> vertices, LabeledGraphBuilder<V> builder) {
    graph.traverse((u, v, weight) -> {
      if (vertices.contains(u) && vertices.contains(v)) {
        builder.add(u, v, weight);
      }
    }, TraversalMode.LOWER_TRIANGULAR);
  }


  private static <V extends Serializable> Set<V> ensureDegrees(LabeledGraph<V> graph, LabeledGraph<V> supplement, Set<V> vertices, int minDegree) {
    while (true) {
      DegreeCounter<V> degrees = degrees(graph, supplement, vertices);
      List<V> rejects = vertices.stream().filter(v -> degrees.get(v) < minDegree).collect(Collectors.toList());
      if (rejects.isEmpty()) {
        return vertices;
      }
      vertices.removeAll(rejects);
    }
  }

  private static <V extends Serializable> DegreeCounter<V> degrees(LabeledGraph<V> graph, LabeledGraph<V> supplement, Set<V> vertices) {
    DegreeCounter<V> degrees = new DegreeCounter<>();
    vertices.forEach(label -> {
      graph.traverseIncidentEdges(label, degrees, TraversalMode.DEFAULT);
      supplement.traverseIncidentEdges(label, degrees, TraversalMode.DEFAULT);
    });
    return degrees;
  }

  private static <V extends Serializable> Set<V> selectVertices(LabeledGraph<V> graph, LabeledGraph<V> supplement, int maxDist) {
    Set<V> selected = new HashSet<>(Arrays.asList(graph.getLabels()));
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

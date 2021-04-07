/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import com.google.common.collect.Lists;
import net.adeptropolis.frogspawn.graphs.functions.GraphFunction;
import net.adeptropolis.frogspawn.graphs.labeled.labelings.Labeling;
import net.adeptropolis.frogspawn.graphs.traversal.TraversalMode;

import java.io.Serializable;
import java.util.List;

/**
 * Provides all functionality to merge multiple labeled graphs into one. Edge weights are normalized and boosted graph-wise.
 *
 * @param <V> Label type
 */

public class LabeledGraphMerger<V extends Serializable> {

  private final List<BoostedGraph<V>> graphs;
  private final GraphFunction<Double> baseWeightFunction;
  private final Labeling<V> labeling;

  /**
   * Constructor
   *
   * @param baseWeightFunction Function to determine the base
   * @param labeling           New labeling instance for the merged graph
   */

  public LabeledGraphMerger(GraphFunction<Double> baseWeightFunction, Labeling<V> labeling) {
    this.baseWeightFunction = baseWeightFunction;
    this.labeling = labeling;
    this.graphs = Lists.newArrayList();
  }

  /**
   * Add a graph to the merge set
   *
   * @param graph Labeled graph
   * @param boost Edge weight boost for this graph
   */

  public void add(LabeledGraph<V> graph, double boost) {
    graphs.add(new BoostedGraph<>(graph, boost));
  }

  /**
   * Merge all graphs
   *
   * @return Merged graph
   */

  public LabeledGraph<V> merge() {
    LabeledGraphBuilder<V> builder = new LabeledGraphBuilder<>(labeling);
    for (BoostedGraph<V> graph : graphs) {
      mergeSingleGraph(graph, builder);
    }
    return builder.build();
  }

  /**
   * Add a single graph's edges to the merge set
   *
   * @param graph   Graph
   * @param builder Graph builder
   */

  private void mergeSingleGraph(BoostedGraph<V> graph, LabeledGraphBuilder<V> builder) {
    double baseWeight = baseWeightFunction.apply(graph.getLabeledGraph().getGraph());
    double scaling = graph.getBoost() / baseWeight;
    graph.getLabeledGraph().traverse((u, v, weight) -> builder.add(u, v, weight * scaling), TraversalMode.LOWER_TRIANGULAR);
  }

  /**
   * Helper class for storing graphs with associated boosts
   *
   * @param <V> Label type
   */

  private static class BoostedGraph<V extends Serializable> {

    private final LabeledGraph<V> graph;
    private final double boost;

    /**
     * Constructor
     *
     * @param graph Graph
     * @param boost Graph boost
     */

    private BoostedGraph(LabeledGraph<V> graph, double boost) {
      this.graph = graph;
      this.boost = boost;
    }

    /**
     * @return Graph
     */

    public LabeledGraph<V> getLabeledGraph() {
      return graph;
    }

    /**
     * @return Boost
     */

    public double getBoost() {
      return boost;
    }
  }

}

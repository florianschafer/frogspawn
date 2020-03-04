/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering;

import net.adeptropolis.metis.graphs.Graph;

/**
 * Protocluster: A cluster candidate
 */

public class Protocluster {

  private final Graph graph;
  private Cluster cluster;
  private GraphType graphType;

  /**
   * Constructor
   *
   * @param graph     A graph, i.e. the cluster candidate
   * @param graphType Type of the graph. Either <code>ROOT</code>, <code>COMPONENT</code> or <code>SPECTRAL</code>
   * @param cluster   Parent cluster
   */

  Protocluster(Graph graph, GraphType graphType, Cluster cluster) {
    this.graph = graph;
    this.graphType = graphType;
    this.cluster = cluster;
  }

  /**
   * @return The cluster candidate graph
   */

  public Graph getGraph() {
    return graph;
  }

  /**
   * @return Type of the cluster candidate graph
   */

  GraphType getGraphType() {
    return graphType;
  }

  /**
   * Set the cluster candidate graph to type <code>COMPONENT</code>
   */

  void setGraphTypeConnectedComponent() {
    this.graphType = GraphType.COMPONENT;
  }

  /**
   * @return Parent cluster
   */

  public Cluster getCluster() {
    return cluster;
  }

  /**
   * Set the parent cluster
   *
   * @param cluster Parent cluster
   */

  public void setCluster(Cluster cluster) {
    this.cluster = cluster;
  }

  /**
   * Type of the cluster candidate graph. Possible values:
   * <ul>
   *   <li><code>ROOT</code></li>: Root graph
   *   <li><code>COMPONENT</code></li>: Connected component
   *   <li><code>SPECTRAL</code></li>: Spectral partition
   * </ul>
   */

  public enum GraphType {
    ROOT, COMPONENT, SPECTRAL
  }

}

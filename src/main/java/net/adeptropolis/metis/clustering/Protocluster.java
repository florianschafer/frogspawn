/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering;

import net.adeptropolis.metis.graphs.Graph;

public class Protocluster {

  private final Graph graph;
  private Cluster cluster;
  private GraphType graphType;

  Protocluster(Graph graph, GraphType graphType, Cluster cluster) {
    this.graph = graph;
    this.graphType = graphType;
    this.cluster = cluster;
  }

  public Graph getGraph() {
    return graph;
  }

  GraphType getGraphType() {
    return graphType;
  }

  void setGraphTypeConnectedComponent() {
    this.graphType = GraphType.COMPONENT;
  }

  public Cluster getCluster() {
    return cluster;
  }

  public void setCluster(Cluster cluster) {
    this.cluster = cluster;
  }

  public enum GraphType {
    ROOT, COMPONENT, SPECTRAL
  }

}

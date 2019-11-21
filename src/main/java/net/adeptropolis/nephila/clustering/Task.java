package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.Graph;

public class Task {

  private final Graph graph;
  private final Cluster cluster;
  private GraphType graphType;

  public Task(Graph graph, GraphType graphType, Cluster cluster) {
    this.graph = graph;
    this.graphType = graphType;
    this.cluster = cluster;
  }

  public Graph getGraph() {
    return graph;
  }

  public GraphType getGraphType() {
    return graphType;
  }

  public void setGraphType(GraphType graphType) {
    this.graphType = graphType;
  }

  public Cluster getCluster() {
    return cluster;
  }

  enum GraphType {
    ROOT, COMPONENT, SPECTRAL
  }

}

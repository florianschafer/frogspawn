package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.Graph;

public class Protocluster {

  private final Graph graph;
  private Cluster cluster;
  private GraphType graphType;

  public Protocluster(Graph graph, GraphType graphType, Cluster cluster) {
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

  public void setCluster(Cluster cluster) {
    this.cluster = cluster;
  }

  public enum GraphType {
    ROOT, COMPONENT, SPECTRAL
  }

}

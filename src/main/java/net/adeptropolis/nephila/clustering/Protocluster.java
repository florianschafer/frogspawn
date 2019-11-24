package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.Graph;

public class Protocluster {

  private final Graph graph;
  private final Cluster parent;
  private GraphType graphType;

  public Protocluster(Graph graph, GraphType graphType, Cluster parent) {
    this.graph = graph;
    this.graphType = graphType;
    this.parent = parent;
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

  public Cluster getParent() {
    return parent;
  }

  enum GraphType {
    ROOT, COMPONENT, SPECTRAL
  }

}

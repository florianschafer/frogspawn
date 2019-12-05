package net.adeptropolis.nephila.clustering.postprocessing;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.Consistency;
import net.adeptropolis.nephila.graphs.Graph;

// TODO: Think about the fact that the removed vertices may not necessarily stem from the specific cluster (s. aggregate)

public class ConsistencyPostprocessor implements Postprocessor {

  private final Consistency consistency;
  private final Graph graph;

  public ConsistencyPostprocessor(Consistency consistency, Graph graph) {
    this.consistency = consistency;
    this.graph = graph;
  }

  @Override
  public boolean apply(Cluster cluster) {
    TODO: Blindly reusing consistency is wrong! Think of the vertices further down below!
    This might lead to duplicate vertices over multiple clusters!
    Cluster parent = cluster.getParent();
    if (parent == null) {
      return false;
    }
    Graph clusterGraph = cluster.aggregateGraph(this.graph);
    Graph consistentGraph = consistency.ensure(parent, clusterGraph);
    if (consistentGraph == null) {
      assignChildrenToParent(cluster, parent);
      return true;
    } else if (consistentGraph.size() == clusterGraph.size()) {
      return false;
    }
    IntArrayList newRemainder = extractRemainder(cluster, consistentGraph);
    cluster.setRemainder(newRemainder);
    return true;
  }

  private IntArrayList extractRemainder(Cluster cluster, Graph consistentGraph) {
    IntArrayList newRemainder = new IntArrayList();
    for (int v : cluster.getRemainder()) {
      if (consistentGraph.localVertexId(v) >= 0) {
        newRemainder.add(v);
      }
    }
    return newRemainder;
  }

  private void assignChildrenToParent(Cluster cluster, Cluster parent) {
    parent.getChildren().remove(cluster);
    parent.addChildren(cluster.getChildren());
    for (Cluster child : cluster.getChildren()) {
      child.setParent(parent);
    }
  }

}

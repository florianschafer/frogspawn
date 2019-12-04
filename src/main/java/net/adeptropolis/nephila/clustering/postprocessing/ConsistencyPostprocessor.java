package net.adeptropolis.nephila.clustering.postprocessing;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.Consistency;
import net.adeptropolis.nephila.graphs.Graph;

public class ConsistencyPostprocessor implements Postprocessor {

  private final Consistency consistency;
  private final int minClusterSize;
  private final Graph graph;

  public ConsistencyPostprocessor(Consistency consistency, int minClusterSize, Graph graph) {
    this.consistency = consistency;
    this.minClusterSize = minClusterSize;
    this.graph = graph;
  }

  @Override
  public boolean apply(Cluster cluster) {
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

    IntArrayList newRemainder = redistributeRemainder(cluster, parent, consistentGraph);

    finalizeCluster(cluster, parent, newRemainder);

    // TODO: Think about the fact that the removed vertices may not necessarily stem from the specific cluster (s. aggregate)
    // TODO: When testing, remember to also check the consistentGraph == null case where child and parents need to be set accordingly

    return true;
  }

  private void finalizeCluster(Cluster cluster, Cluster parent, IntArrayList newRemainder) {
    if (newRemainder.size() >= minClusterSize) {
      cluster.setRemainder(newRemainder);
    } else {
      parent.addToRemainder(newRemainder.iterator());
      assignChildrenToParent(cluster, parent);
    }
  }

  private IntArrayList redistributeRemainder(Cluster cluster, Cluster parent, Graph consistentGraph) {
    IntArrayList newRemainder = new IntArrayList();
    for (int v : cluster.getRemainder()) {
      if (consistentGraph.localVertexId(v) >=0) {
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

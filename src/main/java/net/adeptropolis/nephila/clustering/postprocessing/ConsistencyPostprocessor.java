package net.adeptropolis.nephila.clustering.postprocessing;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;

// TODO: Think about the fact that the removed vertices may not necessarily stem from the specific cluster (s. aggregate)

public class ConsistencyPostprocessor implements Postprocessor {

  private final Graph graph;
  private final int minClusterSize;
  private final double minClusterLikelihood;

  public ConsistencyPostprocessor(Graph graph, int minClusterSize, double minClusterLikelihood) {
    this.graph = graph;
    this.minClusterSize = minClusterSize;
    this.minClusterLikelihood = minClusterLikelihood;
  }

  @Override
  public boolean apply(Cluster cluster) {
    Cluster parent = cluster.getParent();
    if (parent == null) {
      return false;
    }
    IntRBTreeSet clusterVertices = new IntRBTreeSet(cluster.getRemainder());
    Graph clusterGraph = cluster.aggregateGraph(graph);
    IntRBTreeSet survivors = initSurvivors(clusterGraph);
    for (Graph subgraph = clusterGraph; true; subgraph = inducedSubgraph(survivors)) {
      int prevSize = clusterVertices.size();
      double[] likelihoods = subgraph.relativeWeights(graph);
      VertexIterator it = subgraph.vertexIterator();
      while (it.hasNext()) {
        if (likelihoods[it.localId()] < minClusterLikelihood) {
          if (clusterVertices.contains(it.globalId())) {
            parent.addToRemainder(it.globalId());
            clusterVertices.remove(it.globalId());
          }
          survivors.remove(it.globalId());
        }
      }
      if (clusterVertices.size() < minClusterSize) {
        parent.addToRemainder(clusterVertices.iterator());
        assignChildrenToParent(cluster, parent);
        return true;
      } else if (clusterVertices.size() == prevSize) {
        break;
      }
    }
    if (clusterVertices.size() == cluster.getRemainder().size()) {
      return false;
    } else {
      cluster.setRemainder(new IntArrayList(clusterVertices));
      return true;
    }
  }

  private Graph inducedSubgraph(IntRBTreeSet survivors) {
    return graph.inducedSubgraph(survivors.iterator());
  }

  private IntRBTreeSet initSurvivors(Graph candidate) {
    IntRBTreeSet remainingVertices = new IntRBTreeSet();
    VertexIterator vertexIt = candidate.vertexIterator();
    while (vertexIt.hasNext()) {
      remainingVertices.add(vertexIt.globalId());
    }
    return remainingVertices;
  }

  private void assignChildrenToParent(Cluster cluster, Cluster parent) {
    parent.getChildren().remove(cluster);
    parent.addChildren(cluster.getChildren());
    for (Cluster child : cluster.getChildren()) {
      child.setParent(parent);
    }
  }

}

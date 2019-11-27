package net.adeptropolis.nephila.clustering.shapers;

import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ClusteringSettings;
import net.adeptropolis.nephila.graphs.Graph;

public class AncestorSimilarityShaper implements Shaper {

  private final double minAncestorOverlap;
  private final Graph rootGraph;

  public AncestorSimilarityShaper(ClusteringSettings settings, Graph rootGraph) {
    minAncestorOverlap = settings.getMinAncestorOverlap();
    this.rootGraph = rootGraph;
  }

  @Override
  public boolean imposeStructure(Cluster cluster) {

    Cluster ancestor = cluster.getParent();
    if (ancestor == null) {
      return false;
    }

//    while ( ancestorOverlap(branch, ancestor) < minParentOverlap) {
//      if (ancestor.getParent() == null) break;
//      ancestor = ancestor.getParent();
//    }
//
//    if (ancestor != cluster.getParent()) {
//      cluster.getParent().getChildren().remove(cluster);
//      cluster.setParent(ancestor);
//      ancestor.getChildren().add(cluster);
//    }

    return false;
  }

}

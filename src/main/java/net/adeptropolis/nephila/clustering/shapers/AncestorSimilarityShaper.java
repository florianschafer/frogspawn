package net.adeptropolis.nephila.clustering.shapers;

import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ClusteringSettings;
import net.adeptropolis.nephila.clustering.Protocluster;

public class AncestorSimilarityShaper implements Shaper {

  private final double minAncestorOverlap;

  public AncestorSimilarityShaper(ClusteringSettings settings) {
    minAncestorOverlap = settings.getMinAncestorOverlap();
  }

  @Override
  public boolean imposeStructure(Protocluster protocluster) {

//    Cluster cluster = protocluster.getCluster();
//    Cluster ancestor = cluster.getParent();
//    if (ancestor == null) {
//      return false;
//    }
//
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

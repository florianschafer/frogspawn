package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ClusteringSettings;

public class SingletonCollapsingPostprocessor implements Postprocessor {

  private final boolean collapseSingletons;

  public SingletonCollapsingPostprocessor(ClusteringSettings settings) {
    this.collapseSingletons = settings.getCollapseSingletons();
  }

  @Override
  public boolean apply(Cluster cluster) {
    Cluster parent = cluster.getParent();
    if (collapseSingletons && parent != null && parent.getChildren().size() == 1) {
      parent.addToRemainder(cluster.getRemainder().iterator());
      parent.getChildren().remove(cluster);
      parent.addChildren(cluster.getChildren());
      for (Cluster child : cluster.getChildren()) {
        child.setParent(parent);
      }
      return true;
    } else {
      return false;
    }
  }
}

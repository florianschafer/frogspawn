package net.adeptropolis.nephila.clustering;

public class SingletonCollapsingShaper implements Shaper {

  private final boolean collapseSingletons;

  public SingletonCollapsingShaper(ClusteringSettings settings) {
    this.collapseSingletons = settings.getCollapseSingletons();
  }

  @Override
  public Protocluster imposeStructure(Protocluster protocluster) {
    if (collapseSingletons) {
      Cluster cluster = protocluster.getCluster();
      Cluster parent = cluster.getParent();
      if (parent != null && parent.getChildren().size() == 1) {
        parent.addToRemainder(cluster.getRemainder().iterator());
        parent.getChildren().remove(cluster);
        protocluster.setCluster(parent);
      }
    }
    return protocluster;
  }
}

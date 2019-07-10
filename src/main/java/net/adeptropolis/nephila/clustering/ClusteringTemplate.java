package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graph.implementations.CSRStorage;
import net.adeptropolis.nephila.graph.implementations.RowWeights;

public class ClusteringTemplate {

  private final CSRStorage.View rootView;
  private final double[] rootWeights;

  public ClusteringTemplate(CSRStorage graph) {
    this.rootView = graph.defaultView();
    this.rootWeights = new RowWeights(this.rootView).get();
  }

  public double[] computeVertexConsistencies(CSRStorage.View partition) {
    double[] partitionWeights = new RowWeights(partition).get();
    double[] cuts = new double[partition.size()];
    for (int i = 0; i < partition.size(); i++) {
      double weightRelToRoot = rootWeights[rootView.getIndex(partition.get(i))];
      double weightRelToChild = partitionWeights[i];
      cuts[i] = (weightRelToRoot > 0) ? weightRelToChild / weightRelToRoot : 0;
    }
    return cuts;
  }

  public CSRStorage.View getRootView() {
    return rootView;
  }
}

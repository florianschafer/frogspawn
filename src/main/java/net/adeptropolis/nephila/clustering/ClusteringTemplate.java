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

  public double[] computeVertexScores(CSRStorage.View partition) {
    double[] partitonWeights = new RowWeights(partition).get();
    double[] scores = new double[partition.size()];
    for (int i = 0; i < partition.size(); i++) {
      double weightRelToParent = rootWeights[rootView.getIndex(partition.get(i))];
      double weightRelToChild = partitonWeights[i];
      scores[i] = (weightRelToParent > 0) ? weightRelToChild / weightRelToParent : 0;
    }
    return scores;
  }

}

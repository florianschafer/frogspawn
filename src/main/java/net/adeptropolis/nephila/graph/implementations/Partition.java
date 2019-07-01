package net.adeptropolis.nephila.graph.implementations;

// TODO/NOTE: Parent weights are actually already available in normalized Laplacian
public class Partition {

  private final CSRStorage.View view;
  private final double consistency;
  private final double[] scores;

  private Partition(CSRStorage.View view, double consistency, double[] scores) {
    this.view = view;
    this.consistency = consistency;
    this.scores = scores;
  }

  public static Partition of(CSRStorage.View child, CSRStorage.View parent) {
    double[] childWeights = new RowWeights(child).get();
    double[] parentWeights = new RowWeights(parent).get();

    double[] scores = new double[child.size()];
    double parentSubsetWeight = 0;
    for (int i = 0; i < child.size(); i++) {
      double weightRelToParent = parentWeights[parent.getIndex(child.get(i))];
      double weightRelToChild = childWeights[i];
      scores[i] = (weightRelToParent > 0) ? weightRelToChild / weightRelToParent : 0;
      parentSubsetWeight += weightRelToParent;
    }

    double consistency = (parentSubsetWeight > 0) ? sum(childWeights) / parentSubsetWeight : 0;
    return new Partition(child, consistency, scores);
  }


  private static double sum(double[] arr) {
    double s = 0;
    for (double v : arr) s += v;
    return s;
  }


  public CSRStorage.View getView() {
    return view;
  }

  public double getConsistency() {
    return consistency;
  }

  public double[] getScores() {
    return scores;
  }
}

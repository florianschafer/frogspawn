package net.adeptropolis.nephila.clustering;

public class ClusterMetrics {

  public int[] getSortedVertices() {
    return sortedVertices;
  }

  public double[] getScores() {
    return scores;
  }

  private final int[] sortedVertices;
  private final double[] scores;

  public ClusterMetrics(int[] sortedVertices, double[] scores) {
    this.sortedVertices = sortedVertices;
    this.scores = scores;
  }
}

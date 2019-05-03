package net.adeptropolis.nephila.graph;

public class Edge {

  public int u;
  public int v;
  public double weight;

  public Edge(int u, int v, double weight) {
    this.u = u;
    this.v = v;
    this.weight = weight;
  }

  @Override
  public String toString() {
    return String.format("[%d,%d:%f]", u, v, weight);
  }
}

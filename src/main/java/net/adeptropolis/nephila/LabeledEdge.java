package net.adeptropolis.nephila;

public class LabeledEdge<T> {

  public T u;
  public T v;
  public double weight;

  public LabeledEdge(T u, T v, double weight) {
    this.u = u;
    this.v = v;
    this.weight = weight;
  }

}

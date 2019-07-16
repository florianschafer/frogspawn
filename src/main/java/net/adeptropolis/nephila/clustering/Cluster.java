package net.adeptropolis.nephila.clustering;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.nephila.graph.implementations.CSRStorage;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Cluster {

  private final IntArrayList remainder;


  private Cluster parent;

  private List<Cluster> children;

  public Cluster(Cluster parent) {
    this.parent = parent;
    this.remainder = new IntArrayList();
    this.children = Lists.newArrayList();
    if (parent != null) parent.children.add(this);
  }

  void addToRemainder(CSRStorage.View view) {
    for (int v : view.getIndices()) addToRemainder(v);
  }

  void addToRemainder(int v) {
    remainder.add(v);
  }

  public void addToRemainder(IntArrayList vertices) {
    remainder.addAll(vertices);
  }

  public void traverseGraphEdges(BiConsumer<Cluster, Cluster> edgeConsumer) {
    for (Cluster child: children) {
      edgeConsumer.accept(this, child);
      child.traverseGraphEdges(edgeConsumer);
    }
  }

  public void traverseSubclusters(Consumer<Cluster> consumer) {
    consumer.accept(this);
    for (Cluster child: children) child.traverseSubclusters(consumer);
  }

  public IntArrayList aggregateVertices() {
    IntArrayList vertices = new IntArrayList();
    traverseSubclusters(cluster -> vertices.addAll(cluster.remainder));
    return vertices;
  }

  public String id() {
    // TODO: Find a more sensible id (e.g. cluster coordinates)
    return String.valueOf(Math.abs(this.hashCode()));
  }

  public Cluster getParent() {
    return parent;
  }

  public void setParent(Cluster parent) {
    this.parent = parent;
  }

  public List<Cluster> getChildren() {
    return children;
  }

  public void setChildren(List<Cluster> children) {
    this.children = children;
  }

  public IntArrayList getRemainder() {
    return remainder;
  }

}

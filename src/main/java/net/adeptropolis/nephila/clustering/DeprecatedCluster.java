package net.adeptropolis.nephila.clustering;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.nephila.graphs.implementations.View;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


// TODO: Implement proper equals, hash!

@Deprecated
public class DeprecatedCluster {

  private final IntArrayList remainder;

  private DeprecatedCluster parent;

  private Set<DeprecatedCluster> children;

  public DeprecatedCluster(DeprecatedCluster parent) {
    this.parent = parent;
    this.remainder = new IntArrayList();
    this.children = Sets.newHashSet();
    if (parent != null) parent.children.add(this);
  }

  void addToRemainder(View view) {
    for (int v : view.getVertices()) addToRemainder(v);
  }

  void addToRemainder(int v) {
    remainder.add(v);
  }

  public void addToRemainder(IntArrayList vertices) {
    remainder.addAll(vertices);
  }

  public void traverseGraphEdges(BiConsumer<DeprecatedCluster, DeprecatedCluster> edgeConsumer) {
    for (DeprecatedCluster child : children) {
      edgeConsumer.accept(this, child);
      child.traverseGraphEdges(edgeConsumer);
    }
  }

  public void traverseSubclusters(Consumer<DeprecatedCluster> consumer) {
    consumer.accept(this);
    for (DeprecatedCluster child : children) child.traverseSubclusters(consumer);
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

  public DeprecatedCluster getParent() {
    return parent;
  }

  public void setParent(DeprecatedCluster parent) {
    this.parent = parent;
  }

  public Set<DeprecatedCluster> getChildren() {
    return children;
  }

  public IntArrayList getRemainder() {
    return remainder;
  }

  public int depth() {
    int depth = 0;
    DeprecatedCluster cursor = this;
    while (cursor.parent != null) {
      depth += 1;
      cursor = cursor.parent;
    }
    return depth;
  }

}

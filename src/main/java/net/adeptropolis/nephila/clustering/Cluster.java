package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

  private final Cluster parent;
  private final List<Cluster> children;

  private final IntArrayList remainder;

  public Cluster(Cluster parent) {
    this.parent = parent;
    this.children = new ArrayList<>();
    this.remainder = new IntArrayList();
    if (parent != null) parent.children.add(this);
  }

  public IntArrayList getRemainder() {
    return remainder;
  }

  public void addToRemainder(int globalId) {
    remainder.add(globalId);
  }

  public void addToRemainder(IntIterator it) {
    while (it.hasNext()) {
      addToRemainder(it.nextInt());
    }
  }

  public void addToRemainder(Graph graph) {
    remainder.ensureCapacity(remainder.size() + graph.size());
    VertexIterator vertexIterator = graph.vertexIterator();
    while (vertexIterator.hasNext()) {
      remainder.add(vertexIterator.globalId());
    }
  }

}

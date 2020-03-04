/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.VertexIterator;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A cluster
 * <p>From a user perspective, instances of this class hold the remainder (i.e. those vertices that couldn't be
 * clustered any further according to some consistency metric) and a set of child clusters.</p>
 * <p>From an internal-use point of view, this class also provides all methods necessary to manipulate the cluster hierarchy.</p>
 */

// TODO: This class is by far the most messy part of the whole project. Find a way to unravel all the mixed concerns here!
public class Cluster {

  private final Set<Cluster> children;
  private Cluster parent;
  private IntArrayList remainder;

  /**
   * Constructor
   *
   * @param parent Parent cluster. This instance is implicitly added to its children.
   */

  public Cluster(Cluster parent) {
    this.parent = parent;
    this.children = new HashSet<>();
    this.remainder = new IntArrayList();
    if (parent != null) parent.children.add(this);
  }

  /**
   * Assimilate a child cluster, i.e. remove it and take ownership of its children.
   * Optionally merge its remainder into one's own.
   *
   * @param child               Child cluster
   * @param assimilateRemainder Whether to merge the child's vertices into this cluster's remainder.
   */

  // TODO: Check whether assimilateRemainder == false is the reason why some vertices are missing
  public void assimilateChild(Cluster child, boolean assimilateRemainder) {
    children.remove(child);
    for (Cluster grandchild : child.children) {
      grandchild.parent = this;
    }
    children.addAll(child.getChildren());
    if (assimilateRemainder) {
      remainder.addAll(child.getRemainder());
    }
  }

  /**
   * Annex a descendant cluster, i.e. make this cluster its parent
   *
   * @param cluster Cluster
   */

  public void annex(Cluster cluster) {
    if (cluster.parent.children.remove(cluster)) {
      cluster.parent = this;
      children.add(cluster);
    }
  }

  /**
   * @return Remainder of this cluster
   */

  public IntArrayList getRemainder() {
    return remainder;
  }

  /**
   * Set the remainder of this cluster
   *
   * @param remainder Remainder
   */

  public void setRemainder(IntArrayList remainder) {
    this.remainder = remainder;
  }

  /**
   * @return Child clusters
   */

  public Set<Cluster> getChildren() {
    return children;
  }

  /**
   * @return Parent cluster
   */

  public Cluster getParent() {
    return parent;
  }

  /**
   * Add a vertex to this cluster's remainder
   *
   * @param globalId Global vertex id
   */

  public void addToRemainder(int globalId) {
    remainder.add(globalId);
  }

  /**
   * Add multiple vertices to this cluster's remainder
   *
   * @param it Iterator of global vertex ids
   */

  public void addToRemainder(IntIterator it) {
    while (it.hasNext()) {
      addToRemainder(it.nextInt());
    }
  }

  /**
   * Add all vertices from a graph to this cluster's remainder
   *
   * @param graph Graph
   */

  void addToRemainder(Graph graph) {
    remainder.ensureCapacity(remainder.size() + graph.order());
    VertexIterator vertexIterator = graph.vertexIterator();
    while (vertexIterator.hasNext()) {
      remainder.add(vertexIterator.globalId());
    }
  }

  /**
   * Traverse all descendants of this cluster
   *
   * @param consumer Cluster consumer
   */

  public void traverse(Consumer<Cluster> consumer) {
    consumer.accept(this);
    for (Cluster child : children) {
      child.traverse(consumer);
    }
  }

  /**
   * Aggregate vertices from all descendants of this cluster
   *
   * @return List of global vertex ids
   */

  public IntArrayList aggregateVertices() {
    IntArrayList vertices = new IntArrayList();
    traverse(cluster -> vertices.addAll(cluster.remainder));
    return vertices;
  }

  /**
   * Depth of this cluster within the overall hierarchy
   *
   * @return Depth
   */

  public int depth() {
    int depth = 0;
    Cluster ptr = this;
    while (ptr.getParent() != null) {
      depth++;
      ptr = ptr.getParent();
    }
    return depth;
  }

  /**
   * @return The root cluster
   */

  // TODO: Check performance impact
  public Cluster root() {
    Cluster ptr = this;
    while (ptr.getParent() != null) {
      ptr = ptr.getParent();
    }
    return ptr;
  }

  /**
   * @param rootGraph Root graph
   * @return A new subgraph from this cluster's vertices and that of all its descendants
   */

  public Graph aggregateGraph(Graph rootGraph) {
    return rootGraph.inducedSubgraph(aggregateVertices().iterator());
  }

  /**
   * @param rootGraph Root graph
   * @return A new subgraph only consisting of this cluster's remainder vertices
   */

  public Graph remainderGraph(Graph rootGraph) {
    return rootGraph.inducedSubgraph(remainder.iterator());
  }

  /**
   * @return A list of all descendant clusters
   */

  public Set<Cluster> aggregateClusters() {
    Set<Cluster> clusters = new HashSet<>();
    traverse(clusters::add);
    return clusters;
  }

  /**
   * Equals
   *
   * <p>Please note that due to the fact that clusters may be wildly modified in the process and still two clusters should only
   * be regarded as equal if they refer to the same instance. The call below is just there as a reminder of this fact.</p>
   *
   * @param obj The reference object with which to compare.
   * @return True if this object is the same as the obj argument; false otherwise.
   */

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /**
   * Hash Code
   *
   * <p>Please note that due to the fact that clusters may be wildly modified in the process and still two clusters should only
   * yield the same hash code if they refer to the same instance. The call below is just there as a reminder of this fact.</p>
   *
   * @return A hash code value for this object.
   */

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}

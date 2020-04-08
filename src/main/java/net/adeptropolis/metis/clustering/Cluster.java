/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.clustering;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.VertexIterator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * A cluster
 * <p>From a user perspective, instances of this class hold the remainder (i.e. those vertices that couldn't be
 * clustered any further according to some consistency metric) and a set of child clusters.</p>
 * <p>From an internal-use point of view, this class also provides all methods necessary to manipulate the cluster hierarchy.</p>
 */

// TODO: This class is by far the most messy part of the whole project. Find a way to unravel all the mixed concerns here
//  and expose much less internal methods!
public class Cluster {

  private static final AtomicInteger CURR_ID = new AtomicInteger(Integer.MIN_VALUE);
  private static final Comparator<Cluster> DETERMINISTIC_CHILD_COMPARATOR = Comparator.comparingInt(Cluster::getId);
  private static final long HASH_MULTIPLIER_PRIME = 8388581L;
  private static final long HASH_MOD_PRIME = 2147483647L;

  private final SortedSet<Cluster> children;
  private final int id;
  private final Root root;
  private Cluster parent;
  private IntArrayList remainder;

  /**
   * Root cluster constructor
   *
   * @param rootGraph Root graph for the whole clustering run
   */

  public Cluster(Graph rootGraph) {
    this.root = new Root(this, rootGraph);
    this.parent = null;
    this.children = new TreeSet<>(DETERMINISTIC_CHILD_COMPARATOR);
    this.remainder = new IntArrayList();
    this.id = CURR_ID.getAndIncrement();
  }

  /**
   * Standard constructor
   *
   * @param parent Parent cluster. This instance is implicitly added to its children.
   */

  public Cluster(Cluster parent) {
    this.parent = parent;
    this.parent.children.add(this);
    this.root = parent.root;
    this.children = new TreeSet<>(DETERMINISTIC_CHILD_COMPARATOR);
    this.remainder = new IntArrayList();
    this.id = CURR_ID.getAndIncrement();
  }

  /**
   * Assimilate a child cluster, i.e. remove it and take ownership of its children.
   * Optionally merge its remainder into one's own.
   *
   * @param child               Child cluster
   * @param assimilateRemainder Whether to merge the child's vertices into this cluster's remainder.
   */

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

  public Cluster root() {
    return root.cluster;
  }

  /**
   * @return The root graph
   */

  public Graph rootGraph() {
    return root.graph;
  }

  /**
   * @return A new subgraph from this cluster's vertices and that of all its descendants
   */

  public Graph aggregateGraph() {
    return root.graph.inducedSubgraph(aggregateVertices().iterator());
  }

  /**
   * @return A new subgraph only consisting of this cluster's remainder vertices
   */

  public Graph remainderGraph() {
    return root.graph.inducedSubgraph(remainder.iterator());
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
   * @return Cluster id used for stable child sorting (determinism)
   */

  public int getId() {
    return id;
  }

  /**
   * Equals
   *
   * <p>Note: Cluster equality refers to identical instances instead of the content of its members.
   * Thus, two clusters are considered equal if and only if they have the same id.</p>
   *
   * @param obj The reference object to compare against.
   * @return True if this object is the same as the obj argument; false otherwise.
   */

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Cluster)) return false;
    Cluster other = (Cluster) obj;
    return other.getId() == getId();
  }

  /**
   * Hash Code
   *
   * <p>Note: With the cluster id, there is already a unique integer value representing a particular cluster instance.
   * This hash function merely spreads out the ids over the full int range.</p>
   *
   * @return A hash code value for this object.
   */

  @Override
  public int hashCode() {
    return (int) ((HASH_MULTIPLIER_PRIME * getId()) % HASH_MOD_PRIME);
  }

  /**
   * Simple storage class for the root cluster and graph
   */

  private static class Root {

    private final Cluster cluster;
    private final Graph graph;

    private Root(Cluster cluster, Graph graph) {
      this.cluster = cluster;
      this.graph = graph;
    }

  }

}

/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ClusterTest {

  @Test
  public void returnChildren() {
    Cluster root = new Cluster(null);
    Cluster child1 = new Cluster(root);
    Cluster child2 = new Cluster(root);
    assertThat(root.getChildren().size(), is(2));
    assertThat(root.getChildren(), containsInAnyOrder(child1, child2));
  }

  @Test
  public void returnParent() {
    Cluster root = new Cluster(null);
    Cluster child = new Cluster(root);
    assertThat(child.getParent(), is(root));
  }

  @Test
  public void addSingleVerticesToRemainder() {
    Cluster cluster = new Cluster(null);
    cluster.addToRemainder(42);
    assertThat(cluster.getRemainder(), is(new IntArrayList(IntIterators.wrap(new int[]{42}))));
  }

  @Test
  public void growRemainderUsingIterator() {
    Cluster cluster = new Cluster(null);
    cluster.addToRemainder(IntIterators.wrap(new int[]{271, 314}));
    assertThat(cluster.getRemainder(), is(new IntArrayList(IntIterators.wrap(new int[]{271, 314}))));
  }

  @Test
  public void addGraphToRemainder() {
    Cluster cluster = new Cluster(null);
    Graph graph = new CompressedSparseGraphBuilder()
            .add(42, 73, 3)
            .build()
            .inducedSubgraph(IntIterators.wrap(new int[]{42, 73}));
    cluster.addToRemainder(graph);
    assertThat(cluster.getRemainder(), is(new IntArrayList(IntIterators.wrap(new int[]{42, 73}))));
  }

  @Test
  public void traverse() {
    Cluster root = new Cluster(null);
    Cluster child1 = new Cluster(root);
    Cluster child2 = new Cluster(root);
    Cluster child11 = new Cluster(child1);
    Cluster child12 = new Cluster(child1);
    Cluster child21 = new Cluster(child2);
    Cluster child22 = new Cluster(child2);
    Set<Cluster> visited = new HashSet<>();
    root.traverse(visited::add);
    assertThat(visited, is(ImmutableSet.of(root, child1, child2, child11, child12, child21, child22)));
  }

  @Test
  public void aggregateVertices() {
    Cluster root = new Cluster(null);
    root.addToRemainder(42);
    Cluster child1 = new Cluster(root);
    child1.addToRemainder(314);
    Cluster child2 = new Cluster(root);
    child2.addToRemainder(271);
    IntArrayList vertices = root.aggregateVertices();
    vertices.sort(Comparator.comparingInt(x -> x));
    assertThat(vertices, is(new IntArrayList(IntIterators.wrap(new int[]{42, 271, 314}))));

  }


}
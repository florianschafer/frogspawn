/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.implementations.SparseGraphBuilder;
import net.adeptropolis.frogspawn.graphs.labeled.LabeledGraph;
import net.adeptropolis.frogspawn.graphs.labeled.LabeledGraphBuilder;
import net.adeptropolis.frogspawn.graphs.labeled.labelings.DefaultLabeling;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static it.unimi.dsi.fastutil.ints.IntComparators.NATURAL_COMPARATOR;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ClusterTest {

  @Test
  public void returnChildren() {
    Cluster root = new Cluster((Graph) null);
    Cluster child1 = new Cluster(root);
    Cluster child2 = new Cluster(root);
    assertThat(root.getChildren().size(), is(2));
    assertThat(root.getChildren(), containsInAnyOrder(child1, child2));
  }

  @Test
  public void returnParent() {
    Cluster root = new Cluster((Graph) null);
    Cluster child = new Cluster(root);
    assertThat(child.getParent(), is(root));
  }

  @Test
  public void addSingleVerticesToRemainder() {
    Cluster cluster = new Cluster((Graph) null);
    cluster.addToRemainder(42);
    assertThat(cluster.getRemainder(), is(new IntArrayList(IntIterators.wrap(new int[]{42}))));
  }

  @Test
  public void growRemainderUsingIterator() {
    Cluster cluster = new Cluster((Graph) null);
    cluster.addToRemainder(IntIterators.wrap(new int[]{271, 314}));
    assertThat(cluster.getRemainder(), is(new IntArrayList(IntIterators.wrap(new int[]{271, 314}))));
  }

  @Test
  public void addGraphToRemainder() {
    Cluster cluster = new Cluster((Graph) null);
    Graph graph = new SparseGraphBuilder()
            .add(42, 73, 3)
            .build()
            .subgraph(IntIterators.wrap(new int[]{42, 73}));
    cluster.addToRemainder(graph);
    assertThat(cluster.getRemainder(), is(new IntArrayList(IntIterators.wrap(new int[]{42, 73}))));
  }

  @Test
  public void traverse() {
    Cluster root = new Cluster((Graph) null);
    Cluster child1 = new Cluster(root);
    Cluster child2 = new Cluster(root);
    Cluster child11 = new Cluster(child1);
    Cluster child12 = new Cluster(child1);
    Cluster child21 = new Cluster(child2);
    Cluster child22 = new Cluster(child2);
    Set<Cluster> visited = new HashSet<>();
    root.traverse(visited::add);
    assertThat(visited, is(new HashSet<>(Arrays.asList(root, child1, child2, child11, child12, child21, child22))));
  }

  @Test
  public void aggregateVertices() {
    Cluster root = new Cluster((Graph) null);
    root.addToRemainder(42);
    Cluster child1 = new Cluster(root);
    child1.addToRemainder(314);
    Cluster child2 = new Cluster(root);
    child2.addToRemainder(271);
    IntArrayList vertices = root.aggregateVertices();
    vertices.sort(NATURAL_COMPARATOR);
    assertThat(vertices, is(new IntArrayList(IntIterators.wrap(new int[]{42, 271, 314}))));
  }

  @Test
  public void depth() {
    Cluster root = new Cluster((Graph) null);
    Cluster child1 = new Cluster(root);
    Cluster child11 = new Cluster(child1);
    Cluster child12 = new Cluster(child1);
    Cluster child2 = new Cluster(root);
    Cluster child21 = new Cluster(child2);
    Cluster child22 = new Cluster(child2);
    Cluster child221 = new Cluster(child22);
    assertThat(root.depth(), is(0));
    assertThat(child1.depth(), is(1));
    assertThat(child11.depth(), is(2));
    assertThat(child12.depth(), is(2));
    assertThat(child2.depth(), is(1));
    assertThat(child21.depth(), is(2));
    assertThat(child22.depth(), is(2));
    assertThat(child221.depth(), is(3));
  }

  @Test
  public void root() {
    Cluster root = new Cluster((Graph) null);
    Cluster child2 = new Cluster(root);
    Cluster child22 = new Cluster(child2);
    Cluster child221 = new Cluster(child22);
    assertThat(child221.root(), is(root));
  }

  @Test
  public void aggregateClusters() {
    Cluster root = new Cluster((Graph) null);
    Cluster child1 = new Cluster(root);
    Cluster child2 = new Cluster(root);
    assertThat(root.aggregateClusters(), is(new HashSet<>(Arrays.asList(root, child1, child2))));
  }

  @Test
  public void equality() {
    Cluster cluster1 = new Cluster((Graph) null);
    Cluster cluster2 = new Cluster(cluster1);
    assertThat(cluster1, is(cluster1));
    assertThat(cluster2, is(cluster2));
    assertThat(cluster1, not(is(cluster2)));
    assertThat(cluster2, not(is(cluster1)));
  }

  @Test
  public void hashes() {
    Cluster cluster1 = new Cluster((Graph) null);
    Cluster cluster2 = new Cluster(cluster1);
    int hash1 = cluster1.hashCode();
    int hash2 = cluster2.hashCode();
    assertThat(hash1, is(hash1));
    assertThat(hash2, is(hash2));
    assertThat(hash1, not(is(hash2)));
    assertThat(hash2, not(is(hash1)));
  }

  @Test
  public void remainderLabels() {
    LabeledGraph<String> graph = new LabeledGraphBuilder<>(new DefaultLabeling<>(String.class))
            .add("A", "B", 3)
            .add("B", "C", 4)
            .build();
    Cluster root = new Cluster(graph.getGraph());
    root.addToRemainder(IntIterators.wrap(new int[]{1, 2}));
    assertThat(root.remainderLabels(graph.getLabeling()).collect(Collectors.joining(", ")), is("B, C"));
  }

}
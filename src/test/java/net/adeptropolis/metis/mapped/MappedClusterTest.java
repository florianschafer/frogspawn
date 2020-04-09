/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.mapped;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.consistency.ConsistencyMetric;
import net.adeptropolis.metis.clustering.consistency.RelativeWeightConsistencyMetric;
import net.adeptropolis.metis.digest.ClusterDigester;
import net.adeptropolis.metis.graphs.Graph;
import net.adeptropolis.metis.graphs.GraphTestBase;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;

public class MappedClusterTest extends GraphTestBase {

  private Cluster root;
  private Cluster c1;
  private Cluster c2;
  private Cluster c21;
  private Cluster c22;

  private MappedCluster<String, String> mappedRoot;

  @Before
  public void setup() {
    Graph graph = completeGraph(10);
    root = new Cluster(graph);
    root.setRemainder(IntArrayList.wrap(new int[]{ 0, 1 }));
    c1 = new Cluster(root);
    c1.setRemainder(IntArrayList.wrap(new int[]{ 2, 3 }));
    c2 = new Cluster(root);
    c2.setRemainder(IntArrayList.wrap(new int[]{ 4, 5 }));
    c21 = new Cluster(c2);
    c21.setRemainder(IntArrayList.wrap(new int[]{ 6, 7 }));
    c22 = new Cluster(c2);
    c22.setRemainder(IntArrayList.wrap(new int[]{ 8, 9 }));
    String[] labels = IntStream.range(0, 10).mapToObj(String::valueOf).toArray(String[]::new);
    ConsistencyMetric metric = new RelativeWeightConsistencyMetric();
    ClusterDigester digester = new ClusterDigester(metric, 0, false, ((vertexId, weight, score) -> -vertexId));
    ClusterMemberMapper<String, String> mapper = (String label, double weight, double score) -> String.format("[%s]", label);
    mappedRoot = MappedCluster.mapTree(root, labels, digester, mapper);
  }

  @Test
  public void members() {
    verifyMembers(mappedRoot, "[0],[1]");
  }

  @Test
  public void children() {
    List<MappedCluster<String, String>> children = mappedRoot.getChildren().collect(Collectors.toList());
    assertThat(children.size(), is(2));
    verifyMembers(children.get(0), "[2],[3]");
    assertThat(children.get(0).getChildren().count(), is (0L));
    assertThat(children.get(0).getParent(), is(mappedRoot));
    verifyMembers(children.get(1), "[4],[5]");
    assertThat(children.get(1).getChildren().count(), is (2L));
    assertThat(children.get(1).getParent(), is(mappedRoot));
  }

  @Test
  public void grandchildren() {
    verifyMembers(mappedRoot, "[0],[1]");
    List<MappedCluster<String, String>> children = mappedRoot.getChildren().collect(Collectors.toList());
    MappedCluster<String, String> child = children.get(1);
    List<MappedCluster<String, String>> grandchildren = child.getChildren().collect(Collectors.toList());
    assertThat(grandchildren.size(), is(2));
    verifyMembers(grandchildren.get(0), "[6],[7]");
    assertThat(grandchildren.get(0).getChildren().count(), is (0L));
    assertThat(grandchildren.get(0).getParent(), is(child));
    verifyMembers(grandchildren.get(1), "[8],[9]");
    assertThat(grandchildren.get(1).getChildren().count(), is (0L));
    assertThat(grandchildren.get(1).getParent(), is(child));
  }

  @Test
  public void traversal() {
    List<MappedCluster<String, String>> collected = Lists.newArrayList();
    mappedRoot.traverse(collected::add);
    String fingerprint = collected.stream()
            .map(cluster -> cluster.getMembers().collect(Collectors.joining("|")))
            .collect(Collectors.joining(","));
    assertThat(fingerprint, is("[0]|[1],[2]|[3],[4]|[5],[6]|[7],[8]|[9]"));
  }

  @Test
  public void id() {
    assertThat(mappedRoot.getId(), is(root.getId()));
    List<MappedCluster<String, String>> children = mappedRoot.getChildren().collect(Collectors.toList());
    assertThat(children.get(0).getId(), is(c1.getId()));
    assertThat(children.get(1).getId(), is(c2.getId()));
  }

  @Test
  public void verifyEquals() {
    assertThat(mappedRoot, is(mappedRoot));
    List<MappedCluster<String, String>> children = mappedRoot.getChildren().collect(Collectors.toList());
    assertThat(mappedRoot, not(is(children.get(0))));
    assertThat(mappedRoot, not(is(children.get(1))));
    assertThat(children.get(0), not(is(children.get(1))));
    assertThat(children.get(0), is(children.get(0)));
    assertThat(children.get(1), is(children.get(1)));
  }

  @Test
  public void hash() {
    assertThat(mappedRoot.hashCode(), is(mappedRoot.hashCode()));
    List<MappedCluster<String, String>> children = mappedRoot.getChildren().collect(Collectors.toList());
    assertThat(mappedRoot.hashCode(), not(is(children.get(0).hashCode())));
    assertThat(mappedRoot.hashCode(), not(is(children.get(1).hashCode())));
    assertThat(children.get(0).hashCode(), not(is(children.get(1).hashCode())));
    assertThat(children.get(0).hashCode(), is(children.get(0).hashCode()));
    assertThat(children.get(1).hashCode(), is(children.get(1).hashCode()));
  }

  private void verifyMembers(MappedCluster<String, String> cluster, String expected) {
    String members = cluster.getMembers().collect(Collectors.joining(","));
    assertThat(members, is(expected));
  }

}
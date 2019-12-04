package net.adeptropolis.nephila.clustering.postprocessing;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.Consistency;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

public class ConsistencyPostprocessorTest {

  private Graph graph;
  private ConsistencyPostprocessor consistencyPostprocessor;

  private Cluster c0;
  private Cluster c1;
  private Cluster c2;
  private Cluster c3;
  private Cluster c4;
  private Cluster c5;
  private Cluster c678;
  private Cluster c9;

  @Before
  public void setUp() {
    graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 1)
            .add(0, 2, 1)
            .add(2, 3, 1)
            .add(2, 4, 1)
            .add(4, 5, 1)
            .add(4, 6, 0.6)
            .add(4, 7, 1)
            .add(4, 8, 1)
            .add(6, 7, 1)
            .add(6, 0, 1)
            .add(6, 1, 1)
            .add(6, 2, 1)
            .add(6, 3, 1)
            .add(6, 5, 1)
            .add(6, 7, 1)
            .add(7, 0, 1)
            .add(7, 1, 1)
            .add(7, 2, 1)
            .add(7, 3, 1)
            .add(7, 5, 1)
            .add(8, 7, 10)
            .build();
    c0 = new Cluster(null);
    c0.addToRemainder(IntIterators.wrap(new int[]{0}));
    c1 = new Cluster(c0);
    c1.addToRemainder(IntIterators.wrap(new int[]{1}));
    c2 = new Cluster(c0);
    c2.addToRemainder(IntIterators.wrap(new int[]{2}));
    c3 = new Cluster(c2);
    c3.addToRemainder(IntIterators.wrap(new int[]{3}));
    c4 = new Cluster(c2);
    c4.addToRemainder(IntIterators.wrap(new int[]{4}));
    c5 = new Cluster(c4);
    c5.addToRemainder(IntIterators.wrap(new int[]{5}));
    c678 = new Cluster(c4);
    c678.addToRemainder(IntIterators.wrap(new int[]{6, 7, 8}));
    c9 = new Cluster(c678);
  }

  @Test
  public void ignoreRootCluster() {
    Consistency consistency = new Consistency(graph, 10, 0.5);
    consistencyPostprocessor = new ConsistencyPostprocessor(consistency, graph);
    assertFalse(consistencyPostprocessor.apply(c0));
  }

  @Test
  public void allVerticesAreInconsistent() {
    Consistency consistency = new Consistency(graph, 10000, 1.0);
    consistencyPostprocessor = new ConsistencyPostprocessor(consistency, graph);
    assertTrue(consistencyPostprocessor.apply(c678));
    assertThat(c4.getChildren(), is(ImmutableSet.of(c5, c9)));
    assertThat(c5.getParent(), is(c4));
    assertThat(c4.getRemainder(), containsInAnyOrder(4, 6, 7, 8));
  }

  @Test
  public void allVerticesAreConsistent() {
    Consistency consistency = new Consistency(graph, 1, 0.0);
    consistencyPostprocessor = new ConsistencyPostprocessor(consistency, graph);
    assertFalse(consistencyPostprocessor.apply(c678));
    assertThat(c4.getChildren(), is(ImmutableSet.of(c5, c678)));
    assertThat(c4.getRemainder(), containsInAnyOrder(4));
  }

  @Test
  public void someVerticesAreConsistent() {
    Consistency consistency = new Consistency(graph, 1, 0.27);
    consistencyPostprocessor = new ConsistencyPostprocessor(consistency, graph);
    assertTrue(consistencyPostprocessor.apply(c678));
    assertThat(c4.getChildren(), is(ImmutableSet.of(c5, c678)));
    assertThat(c4.getRemainder(), containsInAnyOrder(4, 6));
    assertThat(c678.getRemainder(), containsInAnyOrder(7, 8));
  }

  @Test
  public void numberOfConsistentVerticesBelowMinClusterSize() {
    Consistency consistency = new Consistency(graph, 3, 0.27);
    consistencyPostprocessor = new ConsistencyPostprocessor(consistency, graph);
    assertTrue(consistencyPostprocessor.apply(c678));
    assertThat(c4.getChildren(), is(ImmutableSet.of(c5, c9)));
    assertThat(c4.getRemainder(), containsInAnyOrder(4, 6, 7, 8));
  }







}
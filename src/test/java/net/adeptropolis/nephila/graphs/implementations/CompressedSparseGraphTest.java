package net.adeptropolis.nephila.graphs.implementations;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.nephila.graphs.Edge;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.GraphTestBase;
import net.adeptropolis.nephila.graphs.VertexIterator;
import org.junit.Test;

import static net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph.builder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CompressedSparseGraphTest extends GraphTestBase {

  private static CompressedSparseGraph defaultGraph = builder()
          .add(0, 1, 2)
          .add(1, 2, 3)
          .add(4, 9, 5)
          .add(4, 10, 7)
          .add(4, 11, 11)
          .build();

  @Test
  public void size() {
    assertThat(defaultGraph.size(), is(12));
  }

  @Test
  public void vertices() {
    VertexIterator it = defaultGraph.vertices();
    IntArrayList localIds = new IntArrayList();
    IntArrayList globalIds = new IntArrayList();
    while (it.proceed()) {
      localIds.add(it.localId());
      globalIds.add(it.globalId());
    }
    assertThat(localIds, contains(0, 1, 2, 3, 4 ,5, 6, 7, 8, 9, 10, 11));
    assertThat(globalIds, contains(0, 1, 2, 3, 4 ,5, 6, 7, 8, 9, 10, 11));
  }

  @Test
  public void emptyGraph() {
    CompressedSparseGraph graph = builder().build();
    assertThat(graph.size(), is(0));
    graph.traverse(consumer);
    assertThat(consumer.getEdges().size(), is(0));
  }

  @Test
  public void fullTraversal() {
    CompressedSparseGraph graph = builder()
            .add(0, 1, 2)
            .add(1, 3, 3)
            .add(6, 4, 5)
            .build();
    graph.traverse(consumer);
    assertThat(consumer.getEdges(), containsInAnyOrder(
            Edge.of(0, 1, 2),
            Edge.of(1, 0, 2),
            Edge.of(1, 3, 3),
            Edge.of(3, 1, 3),
            Edge.of(6, 4, 5),
            Edge.of(4, 6, 5)));
  }

  @Test
  public void indexMapping() {
    for (int i = 0; i < defaultGraph.size(); i++) {
      assertThat(defaultGraph.globalVertexId(i), is(i));
      assertThat(defaultGraph.localVertexId(i), is(i));

    }
  }

  @Test
  public void traverseById() {
    defaultGraph.traverse (defaultGraph.localVertexId(4), consumer);
    assertThat(consumer.getEdges(), containsInAnyOrder(
            Edge.of(4, 9, 5),
            Edge.of(4, 10, 7),
            Edge.of(4, 11, 11)));
  }

  @Test
  public void traverseNonExistentId() {
    defaultGraph.traverse(-1, consumer);
    assertThat(consumer.getEdges().size(), is(0));
  }

  @Test
  public void subgraph() {
    Graph subgraph = defaultGraph.inducedSubgraph(IntIterators.wrap(new int[]{4, 11}));
    assertThat(subgraph.size(), is(2));
  }

}
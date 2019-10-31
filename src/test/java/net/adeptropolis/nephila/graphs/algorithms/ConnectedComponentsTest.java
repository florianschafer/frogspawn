package net.adeptropolis.nephila.graphs.algorithms;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.GraphTestBase;
import net.adeptropolis.nephila.graphs.VertexIterator;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

public class ConnectedComponentsTest extends GraphTestBase {

  @Test
  public void fullyConnectedGraph() {
    Graph graph = butterflyGraph();
    List<List<Integer>> subgraphs = getSubgraphs(graph);
    assertThat(subgraphs, hasSize(1));
    assertThat(subgraphs.get(0), contains(0, 1, 2, 3, 4, 5, 6));
  }

  @Test
  public void removingNonBridgePreservesComponent() {
    Graph graph = butterflyGraph().inducedSubgraph(IntIterators.wrap(new int[]{0, 2, 3, 4, 5, 6}));
    List<List<Integer>> subgraphs = getSubgraphs(graph);
    assertThat(subgraphs, hasSize(1));
    assertThat(subgraphs.get(0), contains(0, 2, 3, 4, 5, 6));
  }

  @Test
  public void splitAtMiddleHub() {
    Graph graph = butterflyGraph().inducedSubgraph(IntIterators.wrap(new int[]{1, 2, 3, 4, 5, 6}));
    List<List<Integer>> subgraphs = getSubgraphs(graph);
    assertThat(subgraphs, hasSize(2));
    assertThat(subgraphs.get(0), contains(1, 2, 3));
    assertThat(subgraphs.get(1), contains(4, 5, 6));
  }

  @Test
  public void splitMultiple() {
    Graph graph = butterflyGraph().inducedSubgraph(IntIterators.wrap(new int[]{1, 3, 4, 5, 6}));
    List<List<Integer>> subgraphs = getSubgraphs(graph);
    assertThat(subgraphs, hasSize(3));
    assertThat(subgraphs.get(0), contains(1));
    assertThat(subgraphs.get(1), contains(3));
    assertThat(subgraphs.get(2), contains(4, 5, 6));
  }

  private List<List<Integer>> getSubgraphs(Graph graph) {
    SubgraphCollectingConsumer consumer = new SubgraphCollectingConsumer();
    ConnectedComponents.find(graph, consumer);
    return consumer.subgraphVertices();
  }


  static class SubgraphCollectingConsumer implements Consumer<Graph> {

    public List<List<Integer>> subgraphVertices() {
      return graphs.stream().sorted(Comparator.comparingInt(Graph::size).thenComparingInt(x -> {
        VertexIterator vertices = x.vertices();
        vertices.proceed();
        return vertices.globalId();
      })).map(graph -> {
        List<Integer> vertices = Lists.newArrayList();
        VertexIterator iterator = graph.vertices();
        while (iterator.proceed()) {
          vertices.add(iterator.globalId());
        }
        return vertices;
      }).collect(Collectors.toList());
    }

    private final List<Graph> graphs;

    SubgraphCollectingConsumer() {
      graphs = Lists.newArrayList();
    }

    @Override
    public void accept(Graph graph) {
      graphs.add(graph);
    }
  }


}
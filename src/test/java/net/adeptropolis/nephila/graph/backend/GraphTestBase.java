package net.adeptropolis.nephila.graph.backend;

import net.adeptropolis.nephila.graph.Edge;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

public class GraphTestBase {

  protected CollectingEdgeConsumer consumer = new CollectingEdgeConsumer();;

  @Before
  public void init() {
    consumer.reset();
  }

  protected class CollectingEdgeConsumer implements EdgeConsumer {

    List<Edge> edges = new ArrayList<>();

    @Override
    public void accept(int u, int v, double weight) {
      edges.add(Edge.of(u, v, weight));
    }

    @Override
    public void reset() {
      edges.clear();
    }
  }

}

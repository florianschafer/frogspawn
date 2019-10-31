package net.adeptropolis.nephila.graphs;

import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class GraphTestBase {

  protected CollectingEdgeConsumer consumer = new CollectingEdgeConsumer();
  private FingerprintingEdgeConsumer fingerprintingConsumer = new FingerprintingEdgeConsumer();

  @Before
  public void init() {
    consumer.reset();
    fingerprintingConsumer.reset();
  }

  Graph bandedGraph(int n, int k) {
    CompressedSparseGraphBuilder builder = CompressedSparseGraph.builder();
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < Math.min(i + k, n); j++) {
        builder.add(i, j, 2 * i + 3 * j);
      }
    }
    return builder.build();
  }

  protected Graph butterflyGraph() {
    return new CompressedSparseGraphBuilder()
            .add(0, 1, 1)
            .add(0, 2, 1)
            .add(1, 2, 1)
            .add(2, 3, 1)
            .add(0, 4, 1)
            .add(0, 5, 1)
            .add(4, 5, 1)
            .add(5, 6, 1)
            .build();
  }

  long bandedGraphFingerprint(int n, int k) {
    long fp = 0;
    for (long i = 0; i < n; i++) {
      for (long j = i + 1; j < Math.min(i + k, n); j++) {
        long weight = 2 * i + 3 * j;
        fp += ((i * weight) % (j + 10));
        fp += ((j * weight) % (i + 10));
      }
    }
    return fp;
  }

  long traverseFingerprint(Graph graph) {
    EdgeOps.traverse(graph, fingerprintingConsumer);
    return fingerprintingConsumer.getFingerprint();
  }

  protected class CollectingEdgeConsumer implements EdgeConsumer {

    private List<Edge> edges = new ArrayList<>();

    @Override
    public void accept(int u, int v, double weight) {
      synchronized (edges) {
        edges.add(Edge.of(u, v, weight));
      }
    }

    public void reset() {
      edges.clear();
    }

    public List<Edge> getEdges() {
      return edges;
    }

  }

  class FingerprintingEdgeConsumer implements EdgeConsumer {

    private final AtomicLong fingerprint;

    FingerprintingEdgeConsumer() {
      fingerprint = new AtomicLong();
    }

    @Override
    public void accept(int u, int v, double weight) {
      fingerprint.addAndGet((long) ((u * weight) % (v + 10)));
    }

    public void reset() {
      fingerprint.set(0);
    }

    long getFingerprint() {
      return fingerprint.get();
    }

  }


}

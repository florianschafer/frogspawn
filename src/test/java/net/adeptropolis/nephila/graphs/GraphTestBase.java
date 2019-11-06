package net.adeptropolis.nephila.graphs;

import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class GraphTestBase {

    /*
    17866   13573   14616   14119   11685   11997   11950   11850   12138   15711
    13573   15921   13928   10683   10537   13017   13103   11201   12481   13251
    14616   13928   15355   11810   11343   12353   14209   10488   12119   14730
    14119   10683   11810   15171   12122    8889    9614   10414    9832   13177
    11685   10537   11343   12122   11751    8678   10064    9407    9538   11418
    11997   13017   12353    8889    8678   12179   12038    9032   10501   12332
    11950   13103   14209    9614   10064   12038   15627   10313   11600   14138
    11850   11201   10488   10414    9407    9032   10313   11011    9946   11200
    12138   12481   12119    9832    9538   10501   11600    9946   11297   12258
    15711   13251   14730   13177   11418   12332   14138   11200   12258   16689
  */
  protected static final CompressedSparseGraph SOME_10_GRAPH = new CompressedSparseGraphBuilder()
          .add(0, 0, 17866).add(0, 1, 13573).add(0, 2, 14616).add(0, 3, 14119)
          .add(0, 4, 11685).add(0, 5, 11997).add(0, 6, 11950).add(0, 7, 11850)
          .add(0, 8, 12138).add(0, 9, 15711)
          .add(1, 1, 15921).add(1, 2, 13928).add(1, 3, 10683).add(1, 4, 10537)
          .add(1, 5, 13017).add(1, 6, 13103).add(1, 7, 11201).add(1, 8, 12481)
          .add(1, 9, 13251)
          .add(2, 2, 15355).add(2, 3, 11810).add(2, 4, 11343).add(2, 5, 12353)
          .add(2, 6, 14209).add(2, 7, 10488).add(2, 8, 12119).add(2, 9, 14730)
          .add(3, 3, 15171).add(3, 4, 12122).add(3, 5, 8889).add(3, 6, 9614)
          .add(3, 7, 10414).add(3, 8, 9832).add(3, 9, 13177)
          .add(4, 4, 11751).add(4, 5, 8678).add(4, 6, 10064).add(4, 7, 9407)
          .add(4, 8, 9538).add(4, 9, 11418)
          .add(5, 5, 12179).add(5, 6, 12038).add(5, 7, 9032).add(5, 8, 10501)
          .add(5, 9, 12332)
          .add(6, 6, 15627).add(6, 7, 10313).add(6, 8, 11600).add(6, 9, 14138)
          .add(7, 7, 11011).add(7, 8, 9946).add(7, 9, 11200)
          .add(8, 8, 11297).add(8, 9, 12258)
          .add(9, 9, 16689)
          .build();

  protected final Graph EIGEN_REF_GRAPH = new CompressedSparseGraphBuilder()
          .add(0, 1, 1)
          .add(0, 4, 1)
          .add(1, 2, 1)
          .add(1, 3, 1)
          .add(1, 4, 1)
          .add(2, 5, 1)
          .add(3, 4, 1)
          .add(4, 5, 1)
          .build();

  protected final Graph SOME_BIPARTITE_GRAPH = new CompressedSparseGraphBuilder()
          .add(0, 1, 2)
          .add(0, 3, 3)
          .add(0, 4, 11)
          .add(1, 2, 5)
          .add(2, 3, 7)
          .add(2, 5, 13)
          .build();
  protected final Graph K43 = new CompressedSparseGraphBuilder()
          .add(0, 1, 5)
          .add(0, 3, 11)
          .add(0, 5, 17)
          .add(1, 2, 23)
          .add(1, 4, 31)
          .add(1, 6, 41)
          .add(2, 3, 43)
          .add(2, 5, 53)
          .add(3, 4, 61)
          .add(3, 6, 71)
          .add(4, 5, 73)
          .add(5, 6, 83)
          .build();
  protected final Graph K12 = new CompressedSparseGraphBuilder()
          .add(0, 1, 2)
          .add(0, 2, 3)
          .build();
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

package net.adeptropolis.nephila.graph.backend;

import com.google.common.util.concurrent.AtomicDouble;
import net.adeptropolis.nephila.graph.Edge;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.hamcrest.Matchers.is;

public class ParallelEdgeTraversalTest {

    @Test
    public void graph() {
      CompressedSparseGraph graph = createGraph(120000);
      System.out.println("Built graph");
      while (true) {
        CollectingConsumer consumer = new CollectingConsumer();
        graph.traverse(consumer);
        System.out.println(consumer.getEdges().size());
      }

    }

    private CompressedSparseGraph createGraph(int n) {
      CompressedSparseGraphBuilder builder = CompressedSparseGraph.builder();
      for (int i = 0; i < n; i++) {
        for (int j = i+1; j < Math.min(i + 50, n); j++) {
          builder.add(i, j, 2 * i + 3 * j);
        }
      }
      return builder.build();
    }


  /********************************************************************************************************
   * S. below for the original tests
   */

  @Test
  public void traversalVisitsAllEntries() {
    withLargeDenseMatrix(mat -> {
      FingerprintingConsumer visitor = new FingerprintingConsumer();
      mat.defaultView().traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(582167083500d));
    });
  }

  private void withLargeDenseMatrix(Consumer<CompressedSparseGraphDatastore> storageConsumer) {
    DeprecatedCompressedSparseGraphBuilder builder = new DeprecatedCompressedSparseGraphBuilder();
    for (int i = 0; i < 1000; i++) {
      for (int j = i + 1; j < 1000; j++) {
        builder.add(i, j, i + j);
      }
    }
    CompressedSparseGraphDatastore storage = builder.build();
    storageConsumer.accept(storage);
  }

  @Test
  public void traversalIgnoresNonSelectedEntries() {
    withLargeDenseMatrix(mat -> {
      FingerprintingConsumer visitor = new FingerprintingConsumer();
      View view = mat.view(indicesWithSize(999));
      view.traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(579840743502d));
    });
  }

  private int[] indicesWithSize(int size) {
    int[] indices = new int[size];
    for (int i = 0; i < size; i++) indices[i] = i;
    return indices;
  }

  @Test
  public void traversalAllowsReuse() {
    withLargeDenseMatrix(mat -> {
      FingerprintingConsumer visitor = new FingerprintingConsumer();
      View view = mat.view(indicesWithSize(999));
      view.traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(579840743502d));
      view = mat.view(indicesWithSize(998));
      view.traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(577521382520d));
    });
  }

  class FingerprintingConsumer implements EdgeConsumer {

    private final AtomicDouble fingerprint;

    FingerprintingConsumer() {
      fingerprint = new AtomicDouble();
    }

    double getFingerprint() {
      return fingerprint.get();
    }

    @Override
    public void accept(int u, int v, double weight) {
      burnCycles();
      fingerprint.addAndGet(u * weight + v);
    }

    private void burnCycles() {
      double sum = 0;
      for (int i = 0; i < 5000; i++) sum += Math.sqrt(i);
      if (Math.round(sum) % 12345 == 0) System.out.println("Ignore this");
    }

    @Override
    public void reset() {
      fingerprint.set(0);
    }
  }

  class CollectingConsumer implements EdgeConsumer {

    private final Set<Edge> edges;

    CollectingConsumer() {
      edges = new HashSet<>();
    }

    @Override
    public void accept(int u, int v, double weight) {
      edges.add(Edge.of(u, v, weight));
    }

    @Override
    public void reset() {
      edges.clear();
    }

    public Set<Edge> getEdges() {
      return edges;
    }
  }

}
package net.adeptropolis.nephila.graph.backend;

import com.google.common.util.concurrent.AtomicDouble;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.function.Consumer;

import static org.hamcrest.Matchers.is;

public class ParallelEdgeTraversalTest {

  @Test
  public void traversalVisitsAllEntries() {
    withLargeDenseMatrix(mat -> {
      FingerprintingVisitor visitor = new FingerprintingVisitor();
      mat.defaultView().traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(582167083500d));
    });
  }

  private void withLargeDenseMatrix(Consumer<CSRStorage> storageConsumer) {
    UndirectedCSRStorageBuilder builder = new UndirectedCSRStorageBuilder();
    for (int i = 0; i < 1000; i++) {
      for (int j = i + 1; j < 1000; j++) {
        builder.add(i, j, i + j);
      }
    }
    CSRStorage storage = builder.build();
    storageConsumer.accept(storage);
    storage.free();
  }

  @Test
  public void traversalIgnoresNonSelectedEntries() {
    withLargeDenseMatrix(mat -> {
      FingerprintingVisitor visitor = new FingerprintingVisitor();
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
      FingerprintingVisitor visitor = new FingerprintingVisitor();
      View view = mat.view(indicesWithSize(999));
      view.traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(579840743502d));
      view = mat.view(indicesWithSize(998));
      view.traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(577521382520d));
    });
  }

  class FingerprintingVisitor implements EdgeVisitor {

    private final AtomicDouble fingerprint;

    FingerprintingVisitor() {
      fingerprint = new AtomicDouble();
    }

    double getFingerprint() {
      return fingerprint.get();
    }

    @Override
    public void visit(int u, int v, double weight) {
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

}
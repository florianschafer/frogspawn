package net.adeptropolis.nephila.graph.implementations;

import com.google.common.util.concurrent.AtomicDouble;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.function.Consumer;

import static org.hamcrest.Matchers.is;

public class CSRViewTraversalTest {

  @Test
  public void traversalVisitsAllEntries() {
    withLargeDenseMatrix(view -> {
      FingerprintingVisitor visitor = new FingerprintingVisitor();
      CSRViewTraversal viewTraversal = new CSRViewTraversal(view);
      viewTraversal.traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(582167083500d));
      viewTraversal.cleanup();
    });
  }

  @Test
  public void traversalIgnoresNonSelectedEntries() {
    withLargeDenseMatrix(view -> {
      FingerprintingVisitor visitor = new FingerprintingVisitor();
      CSRViewTraversal viewTraversal = new CSRViewTraversal(view);
      view.indicesSize = 999;
      viewTraversal.traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(579840743502d));
      viewTraversal.cleanup();
    });
  }

  @Test
  public void traversalAllowsReuse() {
    withLargeDenseMatrix(view -> {
      FingerprintingVisitor visitor = new FingerprintingVisitor();
      CSRViewTraversal viewTraversal = new CSRViewTraversal(view);
      view.indicesSize = 999;
      viewTraversal.traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(579840743502d));
      view.indicesSize = 998;
      viewTraversal.traverse(visitor);
      MatcherAssert.assertThat(visitor.getFingerprint(), is(577521382520d));
      viewTraversal.cleanup();
    });
  }


  private void withLargeDenseMatrix(Consumer<CSRStorage.View> viewConsumer) {
    CSRStorageBuilder builder = new CSRStorageBuilder();
    for (int i = 0; i < 1000; i++) {
      for (int j = i + 1; j < 1000; j++) {
        builder.addSymmetric(i, j, i + j);
      }
    }
    CSRStorage storage = builder.build();
    viewConsumer.accept(storage.view());
    storage.free();
  }

  class FingerprintingVisitor implements EntryVisitor {

    private final AtomicDouble fingerprint;

    FingerprintingVisitor() {
      fingerprint = new AtomicDouble();
    }

    double getFingerprint() {
      return fingerprint.get();
    }

    @Override
    public void visit(int rowIdx, int colIdx, double value) {
      burnCycles();
      fingerprint.addAndGet(rowIdx * value + colIdx);
    }

    @Override
    public void reset() {
      fingerprint.set(0);
    }

    private void burnCycles() {
      double sum = 0;
      for (int i = 0; i < 5000; i++) sum += Math.sqrt(i);
      if (Math.round(sum) % 12345 == 0) System.out.println("Ignore this");
    }
  }

}
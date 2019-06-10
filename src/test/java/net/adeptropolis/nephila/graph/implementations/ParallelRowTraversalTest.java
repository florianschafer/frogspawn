package net.adeptropolis.nephila.graph.implementations;

import com.google.common.util.concurrent.AtomicDouble;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.util.function.Consumer;

import static org.hamcrest.Matchers.is;

public class ParallelRowTraversalTest {

  @Test
  public void traversalVisitsAllEntries() {
    withLargeDenseMatrix(view -> {
      FingerprintingTraversal traversal = new FingerprintingTraversal();
      ParallelRowTraversal rowTraversal = new ParallelRowTraversal(traversal, view);
      rowTraversal.traverseRows();
      MatcherAssert.assertThat(traversal.getFingerprint(), is(582167083500d));
      rowTraversal.cleanup();
    });
  }

  @Test
  public void traversalIgnoresNonSelectedEntries() {
    withLargeDenseMatrix(view -> {
      FingerprintingTraversal traversal = new FingerprintingTraversal();
      ParallelRowTraversal rowTraversal = new ParallelRowTraversal(traversal, view);
      view.indicesSize = 999;
      rowTraversal.traverseRows();
      MatcherAssert.assertThat(traversal.getFingerprint(), is(579840743502d));
      rowTraversal.cleanup();
    });
  }

  @Test
  public void traversalAllowsReuse() {
    withLargeDenseMatrix(view -> {
      FingerprintingTraversal traversal = new FingerprintingTraversal();
      ParallelRowTraversal rowTraversal = new ParallelRowTraversal(traversal, view);
      view.indicesSize = 999;
      rowTraversal.traverseRows();
      MatcherAssert.assertThat(traversal.getFingerprint(), is(579840743502d));
      view.indicesSize = 998;
      rowTraversal.traverseRows();
      MatcherAssert.assertThat(traversal.getFingerprint(), is(577521382520d));
      rowTraversal.cleanup();
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

  class FingerprintingTraversal implements RowTraversal {

    private final AtomicDouble fingerprint;

    FingerprintingTraversal() {
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
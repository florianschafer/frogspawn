package net.adeptropolis.nephila.graph.implementations;

import java.util.Arrays;
import java.util.function.Consumer;

// TODO: Might need some optimizations / love

public class SpectralBipartitioner {

  private static final int MIN_ITERATIONS = 15;
  private static final int MAX_ITERATIONS = 10000;

  private final CSRStorage.View view;
  private final double maxAlternations;

  public SpectralBipartitioner(CSRStorage.View view, double maxAlternations) {
    this.view = view;
    this.maxAlternations = maxAlternations;
  }

  public void partition(Consumer<CSRStorage.View> partitionConsumer) {
    BipartiteSSNLSolver solver = new BipartiteSSNLSolver(view);
    double[] v2 = solver.approxV2Signatures(maxAlternations, MIN_ITERATIONS, MAX_ITERATIONS);
    partitionConsumer.accept(extractSubview(v2, x -> x >= 0));
    partitionConsumer.accept(extractSubview(v2, x -> x < 0));
  }

  private CSRStorage.View extractSubview(double[] v2, DoubleToBooleanFunction selector) {
    int viewSize = 0;
    for (double v : v2) if (selector.apply(v)) viewSize++;
    int[] partition = new int[viewSize];
    int partitionIdx = 0;
    for (int i = 0; i < v2.length; i++) if (selector.apply(v2[i])) partition[partitionIdx++] = view.get(i);
    Arrays.parallelSort(partition);
    return view.subview(partition);
  }

  @FunctionalInterface
  public interface DoubleToBooleanFunction {
    boolean apply(double var);
  }

}

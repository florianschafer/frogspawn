/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.algorithms;

import net.adeptropolis.nephila.graphs.implementations.View;

import java.util.Arrays;
import java.util.function.Consumer;

// TODO: Might need some optimizations / love

@Deprecated
public class DeprecatedSpectralBipartitioner {

  private static final int MIN_ITERATIONS = 15;
  private static final int MAX_ITERATIONS = 10000;

  private final View view;
  private final double maxAlternations;

  public DeprecatedSpectralBipartitioner(View view, double maxAlternations) {
    this.view = view;
    this.maxAlternations = maxAlternations;
  }

  public void partition(Consumer<View> partitionConsumer) {
    BipartiteSSNLSolver solver = new BipartiteSSNLSolver(view);
    double[] v2 = solver.approxV2Signatures(maxAlternations, MIN_ITERATIONS, MAX_ITERATIONS);
    partitionConsumer.accept(extractSubview(v2, x -> x >= 0));
    partitionConsumer.accept(extractSubview(v2, x -> x < 0));
  }

  private View extractSubview(double[] v2, DoubleToBooleanFunction selector) {
    int viewSize = 0;
    for (double v : v2) if (selector.apply(v)) viewSize++;
    int[] partition = new int[viewSize];
    int partitionIdx = 0;
    for (int i = 0; i < v2.length; i++) if (selector.apply(v2[i])) partition[partitionIdx++] = view.getVertex(i);
    Arrays.parallelSort(partition);
    return view.subview(partition);
  }

  @FunctionalInterface
  public interface DoubleToBooleanFunction {
    boolean apply(double var);
  }

}

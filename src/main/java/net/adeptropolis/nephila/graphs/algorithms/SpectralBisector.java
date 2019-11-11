package net.adeptropolis.nephila.graphs.algorithms;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.ConstantInitialVectors;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.ConvergenceCriterion;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.PowerIteration;
import net.adeptropolis.nephila.graphs.operators.SSNLOperator;

import java.util.function.Consumer;

public class SpectralBisector {

  private static final double AMBIGUOUS_THRESHOLD = 1E-8;

  private final Graph graph;
  private final ConvergenceCriterion convergenceCriterion;

  public SpectralBisector(Graph graph, ConvergenceCriterion convergenceCriterion) {
    this.graph = graph;
    this.convergenceCriterion = convergenceCriterion;
  }
  public IntIterator bisect(Consumer<Graph> consumer, int maxIterations) {
    SSNLOperator ssnl = new SSNLOperator(graph);
    double[] iv = ConstantInitialVectors.generate(graph.size());
    double[] v2 = PowerIteration.apply(ssnl, convergenceCriterion, iv, maxIterations);

    if (v2 == null) {
      return null;
    }

    TODO:  Finish / fix that shit
            also check whether it really makes sense to use iterators in the subgraph constructors

    IntArrayList rejects = new IntArrayList();
    int lSize = 0;
    int rSize = 0;
    for (int i = 0; i < graph.size(); i++) {
      if (Math.abs(v2[i]) < AMBIGUOUS_THRESHOLD) {
        rejects.add(i);
      } else if (v2[i] > 0) {
        lSize++;
      } else if (v2[i] < 0) {
        rSize++;
      }
    }
    Preconditions.checkState(lSize + rSize + rejects.size() == graph.size());

    int[] lVertices = new int[lSize];
    int[] rVertices = new int[lSize];

    for (int i = 0; i < graph.size(); i++) {
      if (Math.abs(v2[i]) < AMBIGUOUS_THRESHOLD) {
        rejects.add(i);
      } else if (v2[i] >= 0) {
        lSize++;
      } else {
        rSize++;
      }
    }





//    partitionConsumer.accept(extractSubview(v2, x -> x >= 0));
//    partitionConsumer.accept(extractSubview(v2, x -> x < 0));
    return rejects.iterator();
  }

//  private View extractSubview(double[] v2, DoubleToBooleanFunction selector) {
//    int viewSize = 0;
//    for (double v : v2) if (selector.apply(v)) viewSize++;
//    int[] partition = new int[viewSize];
//    int partitionIdx = 0;
//    for (int i = 0; i < v2.length; i++) if (selector.apply(v2[i])) partition[partitionIdx++] = view.getVertex(i);
//    Arrays.parallelSort(partition);
//    return view.subview(partition);
//  }

  private static class FooIterator implements IntIterator {

    private final double[] v2;
    private int u;

    private FooIterator(double[] v2) {
      this.v2 = v2;
      this.u = 0;
    }

    @Override
    public int nextInt() {
      return 0;
    }

    @Override
    public boolean hasNext() {
      for (; u < v2.length; u++) {
        if

      }
      return false;

      if (Math.abs(v2[i]) < AMBIGUOUS_THRESHOLD) {
        rejects.add(i);
      } else if (v2[i] >= 0) {
        lSize++;
      } else {
        rSize++;
      }

      return false;
    }
  }

}

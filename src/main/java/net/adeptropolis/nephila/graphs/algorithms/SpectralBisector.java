package net.adeptropolis.nephila.graphs.algorithms;

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

    IntArrayList rejects = new IntArrayList();
    IntArrayList left = new IntArrayList();
    IntArrayList right = new IntArrayList();

    for (int i = 0; i < graph.size(); i++) {
      if (Math.abs(v2[i]) < AMBIGUOUS_THRESHOLD) {
        rejects.add(i);
      } else if (v2[i] > 0) {
        right.add(i);
      } else if (v2[i] < 0) {
        left.add(i);
      }
    }

    consumer.accept(graph.localInducedSubgraph(left.iterator()));
    consumer.accept(graph.localInducedSubgraph(right.iterator()));

    return rejects.iterator();
  }

}

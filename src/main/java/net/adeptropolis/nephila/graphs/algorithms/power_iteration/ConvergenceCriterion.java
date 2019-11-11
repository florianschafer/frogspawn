package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

@FunctionalInterface
public
interface ConvergenceCriterion {

  boolean satisfied(double[] previous, double[] current, int iterations);

}

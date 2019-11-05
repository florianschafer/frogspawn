package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

@FunctionalInterface
interface IterationTerminator {

  boolean terminate(double[] prev, double[] current);

}

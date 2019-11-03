package net.adeptropolis.nephila.graphs.operators;


/**
 * <p>A linear graph operator</p>
 */

@FunctionalInterface
public interface LinearGraphOperator {

  /**
   *
   * @param argument A vertex-indexed vector
   * @return Apply the given operator to the argument vector and return the result.
   */

  double[] apply(double[] argument);

}

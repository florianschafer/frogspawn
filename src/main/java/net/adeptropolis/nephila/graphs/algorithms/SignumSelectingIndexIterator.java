package net.adeptropolis.nephila.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntIterator;

/**
 * <p>An iterator for the indices of either the non-negative or negative entries of a given vector</p>
 */

class SignumSelectingIndexIterator implements IntIterator {

  private final double[] v;
  private final int selectSignum;
  private int idx;
  private int next;

  /**
   * Constructor
   * @param v A vector
   * @param selectSignum Select the indices of either all non-negative entries (selectSignum >= 0) or those of all negative negative ones (selectSignum < 0)
   */

  SignumSelectingIndexIterator(double[] v, int selectSignum) {
    this.v = v;
    this.selectSignum = selectSignum;
    this.idx = 0;
  }

  /**
   *
   * @return The next index matching the given signum
   */

  @Override
  public int nextInt() {
    return next;
  }

  /**
   *
   * @return Only true if further indices are available.
   */

  @Override
  public boolean hasNext() {
    for (int i = idx; i < v.length; i++) {
      if ((selectSignum >= 0 && v[i] >= 0) || (selectSignum < 0 && v[i] < 0)) {
        next = i;
        idx = i + 1;
        return true;
      }
    }
    return false;
  }
}

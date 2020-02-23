/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntIterator;

import java.util.function.IntPredicate;

/**
 * <p>An iterator for the indices of either the non-negative or negative entries of a given vector</p>
 */

public class SignumSelectingIndexIterator implements IntIterator {

  private final double[] v;
  private final int selectSignum;
  private final IntPredicate customPredicate;
  private int idx;
  private int next;

  /**
   * Constructor
   *
   * @param v               A vector
   * @param selectSignum    Select the indices of either all non-negative entries (selectSignum ≥ 0) or those of all negative negative ones (selectSignum &lt; 0)
   * @param customPredicate Optional: Provide an additional predicate for which entries to select. The logic is <code>customPredicate || ...<code/>. May be <code>null</code>.
   */

  public SignumSelectingIndexIterator(double[] v, int selectSignum, IntPredicate customPredicate) {
    this.v = v;
    this.selectSignum = selectSignum;
    this.customPredicate = customPredicate;
    this.idx = 0;
  }

  /**
   * @return The next index matching the given signum
   */

  @Override
  public int nextInt() {
    return next;
  }

  /**
   * @return Only true if further indices are available.
   */

  @Override
  public boolean hasNext() {
    for (int i = idx; i < v.length; i++) {
      if ((customPredicate != null && customPredicate.test(i)) || (selectSignum >= 0 && v[i] >= 0) || (selectSignum < 0 && v[i] < 0)) {
        next = i;
        idx = i + 1;
        return true;
      }
    }
    return false;
  }

}

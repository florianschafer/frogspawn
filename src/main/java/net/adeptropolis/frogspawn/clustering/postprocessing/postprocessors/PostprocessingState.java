/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

/**
 * Simple storage class for the state of a postprocessor
 *
 */

public class PostprocessingState {

  private boolean madeHierarchyChanges;
  private boolean forceQualityGuard;

  /**
   * Constructor
   *
   * @param madeHierarchyChanges Whether the postprocessor made changes to the cluster hierarchy
   * @param forceQualityGuard Whether to always force running the quality guard afterwards, regardless of what <code>madeHierarchyChanges</code> says.
   */

  public PostprocessingState(boolean madeHierarchyChanges, boolean forceQualityGuard) {
    this.madeHierarchyChanges = madeHierarchyChanges;
    this.forceQualityGuard = forceQualityGuard;
  }

  /**
   * Constructor
   *
   * @param madeHierarchyChanges Whether the postprocessor made changes to the cluster hierarchy
   */

  public PostprocessingState(boolean madeHierarchyChanges) {
    this(madeHierarchyChanges, false);
  }

  /**
   * Default Constructor
   */

  public PostprocessingState() {
    this(false, false);
  }

  /**
   * @return Whether there were any changes to the hierarchy
   */

  public boolean madeHierarchyChanges() {
    return madeHierarchyChanges;
  }

  /**
   * @return Whether running the quality guard should be enforced
   */

  public boolean forceQualityGuard() {
    return forceQualityGuard;
  }

  /**
   * Update this state by <code>OR</code>ing all members with another instance of this class
   * @param other Other instance of this class
   * @return this
   */

  public PostprocessingState update(PostprocessingState other) {
    madeHierarchyChanges |= other.madeHierarchyChanges;
    forceQualityGuard |= other.forceQualityGuard;
    return this;
  }

}

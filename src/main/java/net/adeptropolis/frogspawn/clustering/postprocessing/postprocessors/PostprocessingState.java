/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors;

import java.util.Objects;

/**
 * Simple storage class for the state of a postprocessor
 */

public class PostprocessingState {

  public static final PostprocessingState CHANGED = new PostprocessingState(true);
  public static final PostprocessingState UNCHANGED = new PostprocessingState(false);
  public static final PostprocessingState UNCHANGED_WITH_GUARD = new PostprocessingState(true);

  private boolean madeHierarchyChanges;
  private boolean forceQualityGuard;

  /**
   * Constructor
   *
   * @param madeHierarchyChanges Whether the postprocessor made changes to the cluster hierarchy
   * @param forceQualityGuard    Whether to always force running the quality guard afterwards, regardless of what <code>madeHierarchyChanges</code> says.
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
   *
   * @param other Other instance of this class
   * @return this
   */

  public PostprocessingState update(PostprocessingState other) {
    madeHierarchyChanges |= other.madeHierarchyChanges;
    forceQualityGuard |= other.forceQualityGuard;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    PostprocessingState state = (PostprocessingState) o;
    return madeHierarchyChanges == state.madeHierarchyChanges &&
            forceQualityGuard == state.forceQualityGuard;
  }

  @Override
  public int hashCode() {
    return Objects.hash(madeHierarchyChanges, forceQualityGuard);
  }
}

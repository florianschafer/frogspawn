/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.postprocessing.postprocessors.PostprocessingState;

/**
 * Cluster hierarchy postprocessor
 * <p>Note: All postprocessors must be stateless!</p>
 */

public interface Postprocessor {

  /**
   * Impose a particular structure upon the current cluster or change its place in the cluster tree
   *
   * @param cluster A cluster. Not necessarily root.
   * @return Post-postprocessing state
   */

  PostprocessingState apply(Cluster cluster);

  /**
   * Return the designated cluster tree traversal mode that this postprocessor is using. Possible values:
   * <ul>
   *   <li>LOCAL_BOTTOM_TO_TOP: This postprocessor only operates locally on a single cluster. The full cluster tree
   *   traversal is handled externally in a bottom-to-top fashion</li>
   *   <li>GLOBAL_CUSTOM: This postprocessor applies a custom cluster tree traversal. In that case, the
   *   argument to <code>apply</code> should always be the root cluster</li>
   * </ul>
   *
   * @return Either <code>LOCAL_BOTTOM_TO_TOP</code> or <code>GLOBAL_CUSTOM</code>
   */

  TreeTraversalMode traversalMode();

  /**
   * Define whether this postprocessor potentially affects the vertex affinity structure of a cluster
   *
   * @return <code>true</code> if this postprocessor modifies the cluster vertices. <code>false</code> otherwise.
   */

  // TODO: It might help to intruduce a second return flag to all postprocessors that signals
  // whether the cluster hierarchy has actually changed or whether just some child\
  // nodes were assimilated by their parents, which doesn't negatively affect affinity
  boolean compromisesVertexAffinity();

  /**
   * Define whether this postprocessor should be run in idempotency mode. That is, if this flag is set to
   * true, the postprocessor will be re-applied until there are no further changes to the cluster
   * hierarchy
   *
   * @return <code>true</code> if the postprocessor should be run in idempotency mode. Otherwise <code>false</code>.
   */

  boolean requiresIdempotency();

}

/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

public enum SingletonMode {

  /**
   * Leafe singleton clusters unchanged
   */

  NONE,

  /**
   * Redistribute singleton clusters to their grandparents
   */

  REDISTRIBUTE,

  /**
   * Let the parent cluster assimilate both the remainder and children of singletons
   */

  ASSIMILATE

}

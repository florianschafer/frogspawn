/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

public enum TreeTraversalMode {

    /**
     * Built-in mode for traversing the full cluster tree bottom-to-top.
     * Use for postprocessors that operate locally and require this
     * traversal direction
     */

    LOCAL_BOTTOM_TO_TOP,

    /**
     * Signifies that the postprocessor is responsible for traversing
     * the cluster tree.
     */

    GLOBAL_CUSTOM

}

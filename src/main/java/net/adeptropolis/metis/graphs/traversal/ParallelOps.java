/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.traversal;

import net.adeptropolis.metis.graphs.Graph;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Base class for parallel graph traversal operations
 */

public abstract class ParallelOps {

  /**
   * Minimum number of vertices required to allow for parallel traversal (as opposed to single-threaded)
   */
  static final int PARALLELIZATION_THRESHOLD = 1000;

  static final int THREAD_POOL_SIZE = 2 * Runtime.getRuntime().availableProcessors();
  static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
          THREAD_POOL_SIZE, THREAD_POOL_SIZE, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

  protected final Graph graph;
  protected final int slice;
  final CountDownLatch latch;

  /**
   * Constructor
   *
   * @param graph The graph that whose edge are to be traversed
   * @param slice Graph slice processed by this thread
   * @param latch CountDown latch
   */

  ParallelOps(Graph graph, int slice, CountDownLatch latch) {
    this.graph = graph;
    this.slice = slice;
    this.latch = latch;
  }

}

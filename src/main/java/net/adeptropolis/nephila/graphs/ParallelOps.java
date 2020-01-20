/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class ParallelOps {

  protected static final int PARALLELIZATION_THRESHOLD = 1000;
  protected static final int THREAD_POOL_SIZE = 2 * Runtime.getRuntime().availableProcessors();
  protected static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
          THREAD_POOL_SIZE, THREAD_POOL_SIZE, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<>());

  protected final Graph graph;
  protected final int slice;
  protected final CountDownLatch latch;

  protected ParallelOps(Graph graph, int slice, CountDownLatch latch) {
    this.graph = graph;
    this.slice = slice;
    this.latch = latch;
  }

}

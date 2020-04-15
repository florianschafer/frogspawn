/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.graphs.traversal;

import net.adeptropolis.metis.graphs.Graph;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base class for parallel graph traversal operations
 */

abstract class ParallelOps {

  /**
   * Minimum number of vertices required to allow for parallel traversal (as opposed to single-threaded)
   */
  static final int PARALLELIZATION_THRESHOLD = 128;

  static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
  static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
          THREAD_POOL_SIZE, THREAD_POOL_SIZE, Long.MAX_VALUE, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new DaemonThreadOps());

  final Graph graph;
  final int slice;
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

  static class DaemonThreadOps implements ThreadFactory, UncaughtExceptionHandler {

    private final AtomicInteger threadId;

    DaemonThreadOps() {
      threadId = new AtomicInteger();
    }

    @Override
    @SuppressWarnings("NullableProblems")
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, String.format("worker-thread-%d", threadId.getAndIncrement()));
      thread.setDaemon(true);
      thread.setPriority(5);
      thread.setUncaughtExceptionHandler(this);
      return thread;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
      throw new ParallelOpsException(throwable);
    }
  }

}

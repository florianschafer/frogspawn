package net.adeptropolis.nephila.graph.backend;

import net.adeptropolis.nephila.graph.Graph;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


// TODO: Improve / optimize threading, object churn and runnable re-use

public class ParallelEdgeTraversal implements Runnable, Thread.UncaughtExceptionHandler {

  private static final ParallelEdgeTraversal defaultEdgeTraversal = new ParallelEdgeTraversal();

  private static final int THREAD_BATCH_SIZE = 64;
  private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
  private static final AtomicInteger RUNNING = new AtomicInteger(THREAD_POOL_SIZE);


  private AtomicInteger workPtr;
  @Deprecated private View view;
  private Graph graph;
  private EdgeConsumer consumer;
  //  private boolean running;
  private boolean threadsRunning;

  private final AtomicBoolean energize = new AtomicBoolean();
  private final Object finishedLock = new Object();


  private ParallelEdgeTraversal() {
    this.workPtr = new AtomicInteger();
    energize.set(false);
  }

  private void ensureThreads() {
    if (threadsRunning) {
      return;
    }
    for (int k = 0; k < THREAD_POOL_SIZE; k++) {
      Thread thread = new Thread(this);
      thread.setDaemon(true);
      thread.setPriority(Thread.NORM_PRIORITY);
      thread.setUncaughtExceptionHandler(this);
      thread.start();
    }
    try {
      Thread.sleep(100);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    threadsRunning = true;
  }

  private void start() {
    ensureThreads();

    energize.set(true);
    synchronized (energize) {
      energize.notifyAll();
    }
//    try {
//      Thread.sleep(100);
//    } catch (InterruptedException e) {
//      throw new RuntimeException(e);
//    }
    while (RUNNING.get() > 0) {
      try {
        synchronized (finishedLock) {
          finishedLock.wait();
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    energize.set(false);
  }

  @Override
  public void run() {
    while (true) {
      synchronized (energize) {
        if (!energize.get()) {
          try {
            Thread.sleep(5);
          } catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
          continue;
        }
        RUNNING.decrementAndGet();
        try {
          energize.wait();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      RUNNING.incrementAndGet();
      if (graph != null) {
        runGraph();
      } else if(view != null) {
        runView();
      }
      synchronized (finishedLock) {
        finishedLock.notify();
      }
    }

  }

  public static void traverse(Graph graph, EdgeConsumer consumer) {
    defaultEdgeTraversal.workPtr.set(0);
    defaultEdgeTraversal.graph = graph;
    defaultEdgeTraversal.view = null;
    defaultEdgeTraversal.consumer = consumer;
    defaultEdgeTraversal.start();
  }

  @Deprecated
  public static void traverse(View view, EdgeConsumer consumer) {
    defaultEdgeTraversal.workPtr.set(0);
    defaultEdgeTraversal.graph = null;
    defaultEdgeTraversal.view = view;
    defaultEdgeTraversal.consumer = consumer;
    defaultEdgeTraversal.start();
  }

  @Deprecated
  private void runView() {
    int i;
    while ((i = workPtr.getAndAdd(THREAD_BATCH_SIZE)) < view.size()) {
      for (int j = i; j < Math.min(i + THREAD_BATCH_SIZE, view.size()); j++) {
        view.traverseAdjacent(j, consumer);
      }
    }
  }

  private void runGraph() {
    int i;
    while ((i = workPtr.getAndAdd(THREAD_BATCH_SIZE)) < graph.size()) {
      for (int j = i; j < Math.min(i + THREAD_BATCH_SIZE, graph.size()); j++) {
        graph.traverse(j, consumer);
      }
    }
  }

  @Override
  public void uncaughtException(Thread thread, Throwable throwable) {
    throw new RuntimeException("Error in " + thread, throwable);
  }

}

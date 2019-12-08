/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.algorithms.ConnectedComponents;
import net.adeptropolis.nephila.graphs.algorithms.SpectralBisector;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.PowerIteration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.PriorityQueue;

public class RecursiveClustering {

  private static final Logger LOG = LoggerFactory.getLogger(RecursiveClustering.class.getSimpleName());

  private final Graph graph;
  private final ClusteringSettings settings;
  private final SpectralBisector bisector;
  private final PriorityQueue<Protocluster> queue;

  public RecursiveClustering(Graph graph, ClusteringSettings settings) {
    this.graph = graph;
    this.settings = settings;
    this.bisector = new SpectralBisector(settings.getConvergenceCriterion());
    this.queue = new PriorityQueue<>(Comparator.comparingInt(protocluster -> protocluster.getGraph().size()));
  }

  public Cluster run() {
    Cluster root = new Cluster(null);
    Protocluster initialProtocluster = new Protocluster(graph, Protocluster.GraphType.ROOT, root);
    queue.add(initialProtocluster);
    processQueue();
    return root;
  }

  private void processQueue() {
    while (!queue.isEmpty()) {
      // TODO: Account for pre-recursion structure somewhere here
      Protocluster protocluster = queue.poll();
      if (protocluster.getGraphType() == Protocluster.GraphType.COMPONENT) {
        bisect(protocluster);
      } else {
        decomposeComponents(protocluster);
      }
    }
  }

  /**
   * Bisect the protocluster's graph such that the normalized cut is minimized. Possible scenarios for each partition:
   * <ol>
   *   <li>The partition is either smaller than the allowed minimum cluster size or comprises the full
   *   input graph (which hits an error) -> Add its vertices to the cluster's remainder and terminate</li>
   *   <li>
   *     Ensure consistency of the resulting subgraph vertices and add all non-compliant vertices to the cluster reminder.
   *     For the remaining subgraph, one of two conditions may apply:
   *     <ol>
   *       <li>The remaining consistent subgraph is smaller than the allowed minimum cluster
   *       size -> Add its vertices to the cluster's remainder and terminate</li>
   *       <li>Else: Create a new protocluster with graph type <code>SPECTRAL</code> and add it to the queue.</li>
   *     </ol>
   *   </li>
   * </ol>
   *
   * @param protocluster A protocluster
   */

  private void bisect(Protocluster protocluster) {
    try {
      bisector.bisect(protocluster.getGraph(), settings.getMaxIterations(), partition -> {
        if (partition.size() < settings.getMinClusterSize() || partition.size() == protocluster.getGraph().size()) {
          protocluster.getCluster().addToRemainder(partition);
        } else {
//          View consistentSubgraph = ensureConsistency(branch, partition);
//          if (consistentSubgraph.size() < minPartitionSize) {
//            branch.cluster.addToRemainder(consistentSubgraph);
//          } else {
//            enqueueTask(Task.GraphType.SPECTRAL, task.getCluster(), consistentSubgraph);
//          }
        }
      });
    } catch (PowerIteration.MaxIterationsExceededException e) {
      LOG.warn("Exceeded maximum number of iterations ({}). Not clustering any further.", settings.getMaxIterations());
    }
  }

  /**
   * Decompose the current protocluster's graph into its connected components. There are 3 possible scenarios:
   * <ol>
   *   <li>The input graph was already fully connected -> label the protocluster's graph as connected component and re-add it to the queue</li>
   *   <li>The connected component is smaller than the minimum cluster size -> Add its vertices to the cluster's reminder and terminate</li>
   *   <li>Else -> Label the component as connected and add a proper new protocluster to the queue</li>
   * </ol>
   *
   * @param protocluster A protocluster
   */

  private void decomposeComponents(Protocluster protocluster) {
    ConnectedComponents.find(protocluster.getGraph(), component -> {
      if (component.size() == protocluster.getGraph().size()) {
        protocluster.setGraphType(Protocluster.GraphType.COMPONENT);
        queue.add(protocluster);
      } else if (component.size() < settings.getMinClusterSize()) {
        protocluster.getCluster().addToRemainder(component);
      } else {
        enqueueProtocluster(Protocluster.GraphType.COMPONENT, protocluster.getCluster(), component);
      }
    });
  }

  private void enqueueProtocluster(Protocluster.GraphType graphType, Cluster parent, Graph subgraph) {
    Cluster childCluster = new Cluster(parent);
    Protocluster protocluster = new Protocluster(subgraph, graphType, childCluster);
    // TODO: Add post recursion structure here!
    queue.add(protocluster);
  }

}

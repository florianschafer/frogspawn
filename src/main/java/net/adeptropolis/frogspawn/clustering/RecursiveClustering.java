/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering;

import com.google.common.base.Preconditions;
import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.clustering.affiliation.AffiliationGuard;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.algorithms.ConnectedComponents;
import net.adeptropolis.frogspawn.graphs.algorithms.SpectralBisector;
import net.adeptropolis.frogspawn.graphs.algorithms.power_iteration.PowerIterationException;
import net.adeptropolis.frogspawn.graphs.algorithms.power_iteration.RandomInitialVectorsSource;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <p>Recursive clustering</p>
 * <p>Take a given graph and return a hierarchy of clusters</p>
 */

public class RecursiveClustering {

  private static final Logger LOG = LoggerFactory.getLogger(RecursiveClustering.class.getSimpleName());

  private final Graph graph;
  private final ClusteringSettings settings;
  private final SpectralBisector bisector;
  private final AffiliationGuard affiliationGuard;
  private final RandomInitialVectorsSource ivSource;

  // NOTE: By construction, this type of queue induces the top-town ordering required for determinism
  // and ensures the correct behaviour of vertex affiliation guards
  private final ConcurrentLinkedQueue<Protocluster> queue;

  /**
   * Constructor
   *
   * @param graph    Input graph
   * @param settings Clustering settings
   */

  private RecursiveClustering(Graph graph, ClusteringSettings settings) {
    this.graph = graph;
    this.settings = settings;
    this.bisector = new SpectralBisector(settings);
    this.queue = new ConcurrentLinkedQueue<>();
    this.affiliationGuard = new AffiliationGuard(settings.getAffiliationMetric(),
            graph, settings.getMinClusterSize(), settings.getMinAffiliation());
    this.ivSource = new RandomInitialVectorsSource(settings.getRandomSeed());
  }

  public static Cluster run(Graph graph, ClusteringSettings settings) {
    return new RecursiveClustering(graph, settings).run();
  }

  /**
   * Run the recursive clustering
   *
   * @return Root cluster of the generated cluster hierarchy
   */

  public Cluster run() {
    LOG.info("Starting recursive clustering of {} vertices using settings: {}", graph.order(), settings);
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    Cluster root = new Cluster(graph);
    Protocluster initialProtocluster = new Protocluster(graph, Protocluster.GraphType.ROOT, root);
    queue.add(initialProtocluster);
    processQueue();
    stopWatch.stop();
    LOG.info("Finished clustering {} vertices after {}", graph.order(), stopWatch);
    return root;
  }

  /**
   * Process all elements of the recursive clustering task queue until it is exhausted.
   * Depending on type of the protocluster, it is either decomposed into its connected components or subject to
   * spectral bisection.
   */

  private void processQueue() {
    while (!queue.isEmpty()) {
      Protocluster protocluster = queue.poll();
      if (protocluster.getGraphType() == Protocluster.GraphType.COMPONENT) {
        bisect(protocluster);
      } else {
        decomposeComponents(protocluster);
      }
    }
  }

  /**
   * Bisect the protocluster's graph such that the normalized cut is minimized.
   *
   * @param protocluster A protocluster
   */

  private void bisect(Protocluster protocluster) {
    try {
      bisector.bisect(protocluster.getGraph(), settings.getMaxIterations(), ivSource, partition -> processPartition(protocluster, partition));
    } catch (PowerIterationException e) {
      if (protocluster.getGraph().size() >= settings.getMinClusterSize()) {
        addTerminalChild(protocluster, protocluster.getGraph());
      } else {
        protocluster.getCluster().addToRemainder(protocluster.getGraph());
      }
      LOG.debug(String.format("%s. Not clustering any further.", e.getMessage()));
    }
  }

  /**
   * Process a partition coming out of the spectral bisection step. Possible scenarios:
   * <ol>
   *   <li>The partition is either smaller than the allowed minimum cluster size or comprises the full
   *   input graph (which hints an iteration excess or an error). In that case, add its vertices to the cluster's remainder
   *   and terminate</li>
   *   <li>
   *     Ensure affiliation of the resulting subgraph vertices and add all non-compliant vertices to the cluster remainder.
   *     For the remaining subgraph, one of two conditions may apply:
   *     <ol>
   *       <li>The remaining subgraph is smaller than the allowed minimum cluster
   *       size → Add its vertices to the cluster's remainder and terminate</li>
   *       <li>Else: Create a new protocluster with graph type <code>SPECTRAL</code> and add it to the queue
   *       (unless it's exactly as large as the minimum cluster size, in which case a new child cluster is created).</li>
   *     </ol>
   *   </li>
   * </ol>
   *
   * @param protocluster Current protocluster
   * @param partition    Cluster candidate partition
   */

  private void processPartition(Protocluster protocluster, Graph partition) {
    if (partition.order() < settings.getMinClusterSize() || partition.order() == protocluster.getGraph().order()) {
      protocluster.getCluster().addToRemainder(partition);
    } else {
      Graph guaranteedAffiliationSubgraph = affiliationGuard.ensure(protocluster.getCluster(), partition);
      if (guaranteedAffiliationSubgraph != null) {
        processGuaranteedAffiliationSubgraph(protocluster, guaranteedAffiliationSubgraph);
      }
    }
  }

  /**
   * <p>Process a subgraph returned by a affiliation guard.</p>
   * <p>If the graph is larger than the minimum cluster size, put it back into the processing queue.
   * Otherwise, create a child cluster from its vertices and terminate here.</p>
   *
   * @param protocluster                  Protocluster
   * @param guaranteedAffiliationSubgraph Subgraph whose vertices fulfil the min affiliation metric wrt. to the graph
   */

  private void processGuaranteedAffiliationSubgraph(Protocluster protocluster, Graph guaranteedAffiliationSubgraph) {
    if (guaranteedAffiliationSubgraph.size() > settings.getMinClusterSize()) {
      enqueueProtocluster(Protocluster.GraphType.SPECTRAL, protocluster.getCluster(), guaranteedAffiliationSubgraph);
    } else {
      Preconditions.checkState(guaranteedAffiliationSubgraph.size() == settings.getMinClusterSize());
      addTerminalChild(protocluster, guaranteedAffiliationSubgraph);
    }
  }

  /**
   * Create a new child to the current (proto)cluster and add a graph to its remainder
   *
   * @param protocluster The current protocluster
   * @param graph        A graph
   */

  private void addTerminalChild(Protocluster protocluster, Graph graph) {
    Cluster child = new Cluster(protocluster.getCluster());
    child.addToRemainder(graph);
  }

  /**
   * Decompose the current protocluster's graph into its connected components. There are 4 possible scenarios:
   * <ol>
   *   <li>The input graph was already fully connected. Label the protocluster's graph as connected component and re-add it to the queue</li>
   *   <li>The connected component is smaller than the minimum cluster size. In that case, add its vertices to the cluster's remainder and terminate</li>
   *   <li>The connected component order is exactly the minimum cluster size. Add a new child with the connected component vertices</li>
   *   <li>Else label the component as connected and add a proper new protocluster to the queue</li>
   * </ol>
   *
   * @param protocluster A protocluster
   */

  private void decomposeComponents(Protocluster protocluster) {
    ConnectedComponents.find(protocluster.getGraph(), component -> {
      if (component.order() == protocluster.getGraph().order()) {
        protocluster.setGraphTypeConnectedComponent();
        queue.add(protocluster);
      } else if (component.order() < settings.getMinClusterSize()) {
        protocluster.getCluster().addToRemainder(component);
      } else if (component.order() == settings.getMinClusterSize()) {
        addTerminalChild(protocluster, component);
      } else if (component.order() > settings.getMinClusterSize()) {
        enqueueProtocluster(Protocluster.GraphType.COMPONENT, protocluster.getCluster(), component);
      }
    });
  }

  /**
   * Insert a new protocluster into the queue
   *
   * @param graphType Type of the graph for the new protocluster
   * @param parent    Parent cluster
   * @param subgraph  Protocluster graph
   */

  private void enqueueProtocluster(Protocluster.GraphType graphType, Cluster parent, Graph subgraph) {
    Cluster childCluster = new Cluster(parent);
    Protocluster protocluster = new Protocluster(subgraph, graphType, childCluster);
    queue.add(protocluster);
  }

}

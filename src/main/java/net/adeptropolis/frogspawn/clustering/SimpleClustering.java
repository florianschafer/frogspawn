/*
 * Copyright (c) Florian Schaefer 2021.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering;

import lombok.Value;
import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.digest.ClusterDigester;
import net.adeptropolis.frogspawn.graphs.labeled.LabeledGraph;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides a somewhat simpler interface to {@link RecursiveClustering}, automatically taking
 * care of digest generation and mapping. You may prefer to use this instead whenever the output
 * should be a nested hierarchy of (scored) vertex labels and the graph isn't so large that storing
 * all results in memory becomes an issue.
 *
 * @param <L> Label type
 */

public class SimpleClustering<L> {

  private final LabeledGraph<L> graph;
  private final ClusteringSettings settings;
  private final ClusterDigester digester;

  /**
   * Perform clustering on a labeled graph
   * @param graph Labeled input graph
   * @param settings Clustering settings
   * @param <L> Label type
   * @return Hierarchy of {@link OutputCluster} instances
   */

  public static <L> OutputCluster<L> cluster(LabeledGraph<L> graph, ClusteringSettings settings) {
    return new SimpleClustering<>(graph, settings).cluster();
  }

  /**
   * Constructor
   * @param graph Labeled input graph
   * @param settings Clustering settings
   */

  private SimpleClustering(LabeledGraph<L> graph, ClusteringSettings settings) {
    this.graph = graph;
    this.settings = settings;
    this.digester = new ClusterDigester(settings);
  }

  /**
   * Perform clustering on a labeled graph (private implementation)
   * @return Hierarchy of {@link OutputCluster} instances
   */

  private OutputCluster<L> cluster() {
    Cluster root = RecursiveClustering.run(graph.getGraph(), settings);
    return collectOutput(root);
  }

  /**
   * Recurse the cluster hierarchy and collect output
   * @param cluster A cluster
   * @return New output cluster
   */

  private OutputCluster<L> collectOutput(Cluster cluster) {
    List<ClusterMember<L>> members = collectMembers(cluster);
    List<OutputCluster<L>> children = collectChildren(cluster);
    return new OutputCluster<>(children, members);
  }

  /**
   * Recursively collect all children of a cluster
   * @param cluster A cluster
   * @return List of output clusters
   */

  private List<OutputCluster<L>> collectChildren(Cluster cluster) {
    return cluster.getChildren()
            .stream()
            .map(this::collectOutput)
            .collect(Collectors.toList());
  }

  /**
   * Digest and collect all members of a cluster
   * @param cluster A cluster
   * @return List of cluster members
   */

  private List<ClusterMember<L>> collectMembers(Cluster cluster) {
    return digester.digest(cluster)
            .map(ClusterMember::new, graph.getLabeling())
            .collect(Collectors.toList());
  }

  /**
   * Simple storage class for cluster vertices (labels) along with their affiliation scores
   * and intra-cluster weights
   * @param <L> Label type
   */

  @Value
  public static class ClusterMember<L> {

    L label;
    double weight;
    double affiliationScore;

  }

  /**
   * Simple storage class for nested output clusters
   * @param <L> Label type
   */

  @Value
  public static class OutputCluster<L> {

    List<OutputCluster<L>> children;
    List<ClusterMember<L>> members;

  }


}
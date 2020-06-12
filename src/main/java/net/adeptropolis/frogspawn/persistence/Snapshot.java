/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.persistence;

import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.graphs.labeled.LabeledGraph;

import java.io.File;
import java.io.Serializable;

/**
 * Create and load snapshots of labeled clustering states
 *
 * @param <V> Label type
 */

public class Snapshot<V extends Serializable> implements Serializable {

  static final long serialVersionUID = 322198804270556255L;

  private final Cluster root;
  private final LabeledGraph<V> graph;

  /**
   * Private constructor
   *
   * @param root  Root cluster
   * @param graph Root graph
   */

  private Snapshot(Cluster root, LabeledGraph<V> graph) {
    this.root = root;
    this.graph = graph;
  }

  /**
   * Save a snapshot to file
   *
   * @param file  Output file
   * @param root  Root cluster
   * @param graph Root graph
   * @param <V>   Graph label type
   */

  public static <V extends Serializable> void save(File file, Cluster root, LabeledGraph<V> graph) {
    new Snapshot<>(root, graph).save(file);
  }

  /**
   * Load a snapshot from file
   *
   * @param file Snapshot file
   * @param <V>  Graph label type
   * @return Restored snapshot
   */

  public static <V extends Serializable> Snapshot<V> load(File file) {
    return Serialization.load(file);
  }

  /**
   * Save this snapshot to file
   *
   * @param file Output file
   */

  private void save(File file) {
    Serialization.save(this, file);
  }

  /**
   * @return Root cluster
   */

  public Cluster getRoot() {
    return root;
  }

  /**
   * @return Root graph
   */

  public LabeledGraph<V> getGraph() {
    return graph;
  }

}

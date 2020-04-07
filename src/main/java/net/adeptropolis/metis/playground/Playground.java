/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.playground;

import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.RecursiveClustering;
import net.adeptropolis.metis.digest.ClusterDigester;
import net.adeptropolis.metis.graphs.labeled.LabeledGraph;
import net.adeptropolis.metis.graphs.labeled.LabeledGraphSource;
import net.adeptropolis.metis.sinks.Sink;
import net.adeptropolis.metis.sinks.TextSink;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static net.adeptropolis.metis.digest.ClusterDigester.DESCENDING_COMBINED;

/**
 * !!!
 * Please ignore this class. It's only being used for manual testing and will go away soon
 * !!!
 */

public class Playground {

  private static final Path LARGE_GRAPH = Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv");
  private static final Path MEDIUM_GRAPH = Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.10M.tsv");
  private static final Path SMALL_GRAPH = Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.small.tsv");

  public static void main(String[] args) throws IOException {
    new Playground().standardClustering();
  }

  private void standardClustering() throws IOException {
    LabeledGraph<String> labeledGraph = LabeledGraphSource.fromTSV(Files.lines(SMALL_GRAPH));
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinVertexConsistency(0.05)
            .withMinparentOverlap(0.65)
            .build();
    Cluster root = new RecursiveClustering(labeledGraph.getGraph(), settings).run();
    ClusterDigester digester = new ClusterDigester(settings.getConsistencyMetric(), 1000, false, DESCENDING_COMBINED.apply(1.75));
    Sink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters15.txt"), digester, labeledGraph.getLabels());
    textSink.consume(root);

  }

}

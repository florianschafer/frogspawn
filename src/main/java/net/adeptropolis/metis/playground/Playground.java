/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.playground;

import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.RecursiveClustering;
import net.adeptropolis.metis.digest.ClusterDigester;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import net.adeptropolis.metis.sinks.Sink;
import net.adeptropolis.metis.sinks.TextSink;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import static net.adeptropolis.metis.digest.ClusterDigester.DESCENDING_COMBINED;

/**
 * !!!
 * Please ignore this class. It's only being used for manual testing and will go away soon
 * !!!
 */

public class Playground {

  public static void main(String[] args) throws FileNotFoundException {
    new Playground().standardClustering();
  }

  private void standardClustering() throws FileNotFoundException {
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.10M.tsv"));
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.small.tsv"));
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinVertexConsistency(0.05)
            .withMinparentOverlap(0.65)
            .build();
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    CompressedSparseGraph graph = builder.build();
    Cluster root = new RecursiveClustering(graph, settings).run();
//    ClusterDigester digester = new ClusterDigester(settings.getConsistencyMetric(), 1000, false, DESCENDING_WEIGHTS);
    ClusterDigester digester = new ClusterDigester(settings.getConsistencyMetric(), 1000, false, DESCENDING_COMBINED.apply(1.75));
    Sink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters15.txt"), digester, g.inverseLabels());
//    LeafTextSink textSink = new LeafTextSink(Paths.get("/home/florian/tmp/clusters16.txt"), digester, g.inverseLabels());
    textSink.consume(root);

  }

}

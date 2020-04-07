/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.RecursiveClustering;
import net.adeptropolis.metis.digest.ClusterDigester;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import net.adeptropolis.metis.sinks.TextSink;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

import static net.adeptropolis.metis.digest.ClusterDigester.DESCENDING_COMBINED;
import static net.adeptropolis.metis.digest.ClusterDigester.DESCENDING_WEIGHTS;

/**
 * !!!
 * Please ignore this class. It's only being used for manual testing and will go away soon
 * !!!
 */

@Ignore("Manual testing")
public class Playground {

  @Test
  public void standardClustering() throws FileNotFoundException {
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.10M.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.small.tsv"));
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinVertexConsistency(0.05)
            .build();
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    CompressedSparseGraph graph = builder.build();
    Cluster root = new RecursiveClustering(graph, settings).run();
//    ClusterDigester digester = new ClusterDigester(settings.getConsistencyMetric(), 1000, false, DESCENDING_WEIGHTS);
    ClusterDigester digester = new ClusterDigester(settings.getConsistencyMetric(), 1000, false, DESCENDING_COMBINED.apply(1.5));
    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters14.txt"), digester, g.inverseLabels());
//    LeafTextSink textSink = new LeafTextSink(Paths.get("/home/florian/tmp/clusters16.txt"), digester, g.inverseLabels());
    textSink.consume(root);
  }


}
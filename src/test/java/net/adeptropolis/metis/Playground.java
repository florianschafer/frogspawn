/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.RecursiveClustering;
import net.adeptropolis.metis.digest.ClusterDigester;
import net.adeptropolis.metis.digest.TopWeightsRemainderClusterDigester;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import net.adeptropolis.metis.sinks.TextSink;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

@Ignore("Manual testing")
public class Playground {

  @Test
  public void siemens() throws FileNotFoundException {
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/tmp/siemens.tsv"));
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinVertexConsistency(0.4)
            .withMinparentOverlap(0.7)
            .withMinClusterSize(10)
            .build();
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    CompressedSparseGraph graph = builder.build();
    Cluster root = new RecursiveClustering(graph, settings).run();
    ClusterDigester digester = new TopWeightsRemainderClusterDigester(settings.getConsistencyMetric(), 200);
    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/siemens_results.txt"), digester, g.inverseLabels());
    textSink.consume(root);
  }

  @Test
  public void standardClustering() throws FileNotFoundException {
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.10M.tsv"));
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.small.tsv"));
    ClusteringSettings settings = ClusteringSettings.builder()
//            .withTrailSize(18)
//            .withConvergenceThreshold(0.9)
            .withMinVertexConsistency(0.05)
//            .withMinparentOverlap(0.15)
//            .withMinparentOverlap(0.55)
//            .withParentSearchStepSize(40)
            .build();
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    CompressedSparseGraph graph = builder.build();
    Cluster root = new RecursiveClustering(graph, settings).run();
//    ClusterDigester digester = new TopWeightsAggregateClusterDigester(1000, graph);
    ClusterDigester digester = new TopWeightsRemainderClusterDigester(settings.getConsistencyMetric(), 1000);
    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters14.txt"), digester, g.inverseLabels());
//    LeafTextSink textSink = new LeafTextSink(Paths.get("/home/florian/tmp/clusters16.txt"), digester, g.inverseLabels());
    textSink.consume(root);
  }


}
/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis;

import net.adeptropolis.metis.clustering.*;
import net.adeptropolis.metis.clustering.labeling.Labeling;
import net.adeptropolis.metis.clustering.labeling.TopWeightsRemainderLabeling;
import net.adeptropolis.metis.clustering.sinks.TextSink;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.metis.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.nio.file.Paths;

@Ignore("Manual testing")
public class Playground {

  @Test
  public void remainderClustering() throws FileNotFoundException {
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv"));
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinClusterSize(25)
            .withMinClusterLikelihood(0.025)
            .withMinAncestorOverlap(0.3)
            .build();
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    CompressedSparseGraph graph = builder.build();
    Cluster root = new RecursiveClusterSieve(graph, settings).run();
    Labeling labeling = new TopWeightsRemainderLabeling(150, graph);
    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters4.txt"), labeling, g.inverseLabels());
    textSink.consume(root);
  }

  @Test
  public void standardClustering() throws FileNotFoundException {
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv"));
    ClusteringSettings settings = ClusteringSettings.builder().build();
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    CompressedSparseGraph graph = builder.build();
    Cluster root = new RecursiveClusterSieve(graph, settings).run();
    Labeling labeling = new TopWeightsRemainderLabeling(25, graph);
    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters6.txt"), labeling, g.inverseLabels());
//    Sink textSink = new LeafTextSink(Paths.get("/home/florian/tmp/clusters.txt"), labeling, g.inverseLabels());
    textSink.consume(root);

  }

}
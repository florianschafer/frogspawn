/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.nephila;

import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ConsistencyMetric;
import net.adeptropolis.nephila.clustering.RelativeWeightConsistencyMetric;
import net.adeptropolis.nephila.clustering.labeling.Labeling;
import net.adeptropolis.nephila.clustering.labeling.TopWeightsRemainderLabeling;
import net.adeptropolis.nephila.clustering.sinks.TextSink;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Ignore
public class Playground {

  private Logger LOG = LoggerFactory.getLogger(Playground.class);
    
  @Test
  public void remainderClustering() throws FileNotFoundException {
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv"));
//    net.adeptropolis.nephila.LabeledTSVGraphSource g = new net.adeptropolis.nephila.LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.50.tsv"));
    ClusteringSettings settings = new ClusteringSettings(25, 0.025, 0.3, 25, 0.95, 10000);
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    CompressedSparseGraph graph = builder.build();
    ConsistencyMetric metric = new RelativeWeightConsistencyMetric();
    Cluster root = Clustering.run(graph, metric, settings);
    Labeling labeling = new TopWeightsRemainderLabeling(150, graph);
    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters4.txt"), labeling, g.inverseLabels());
    textSink.consume(root);
  }

  @Test
  public void standardClustering() throws FileNotFoundException {

//    net.adeptropolis.nephila.LabeledTSVGraphSource g = new net.adeptropolis.nephila.LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
//    net.adeptropolis.nephila.LabeledTSVGraphSource g = new net.adeptropolis.nephila.LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Essentials/Workbench/fb_names.5M.tsv"));
//    net.adeptropolis.nephila.LabeledTSVGraphSource g = new net.adeptropolis.nephila.LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.tsv"));
//    net.adeptropolis.nephila.LabeledTSVGraphSource g = new net.adeptropolis.nephila.LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.small.tsv"));
//    net.adeptropolis.nephila.LabeledTSVGraphSource g = new net.adeptropolis.nephila.LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.large.tsv"));
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv"));


//    ClusteringSettings settings = new ClusteringSettings(30, 0.2, 0.95, 25, 0.95,true, 10000);    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    ClusteringSettings settings = new ClusteringSettings(50, 0.1, 0.4, 25, 0.95, 10000);
    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    CompressedSparseGraph graph = builder.build();
    ConsistencyMetric metric = new RelativeWeightConsistencyMetric();
    Cluster root = Clustering.run(graph, metric, settings);

    AtomicInteger lost = new AtomicInteger();
    root.traverse(c -> {
      if (!c.getChildren().isEmpty()) {
        lost.addAndGet(c.getRemainder().size());
      }
    });
    LOG.debug("Lost: {} / {}", lost.get(), graph.order());


    Labeling labeling = new TopWeightsRemainderLabeling(25, graph);
    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters3.txt"), labeling, g.inverseLabels());
//    Sink textSink = new LeafTextSink(Paths.get("/home/florian/tmp/clusters.txt"), labeling, g.inverseLabels());
    textSink.consume(root);

  }

}
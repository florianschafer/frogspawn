/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila;

import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ConsistencyMetric;
import net.adeptropolis.nephila.clustering.RelativeWeightConsistencyMetric;
import net.adeptropolis.nephila.clustering.labeling.Labeling;
import net.adeptropolis.nephila.clustering.labeling.TopWeightsAggregateLabeling;
import net.adeptropolis.nephila.clustering.sinks.LeafTextSink;
import net.adeptropolis.nephila.clustering.sinks.Sink;
import net.adeptropolis.nephila.clustering.sinks.TextSink;
import net.adeptropolis.nephila.clustering.structuring.MetaCluster;
import net.adeptropolis.nephila.clustering.structuring.MetaClustering;
import net.adeptropolis.nephila.clustering.structuring.MetaGraphBuilder;
import net.adeptropolis.nephila.clustering.structuring.MetaNode;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Ignore
public class FooingOuterEdgeSourceTest {

  // Found bug: freq must not be float

  Logger LOG = LoggerFactory.getLogger(FooingOuterEdgeSourceTest.class);

  @Test
  public void aSillyExperiment() throws FileNotFoundException {
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.verysmall.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.tsv"));
    ClusteringSettings settings = new ClusteringSettings(50, 0.4, 0.95, 25, 0.95,true, 10000);    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    Graph graph = builder.build();
    ConsistencyMetric metric = new RelativeWeightConsistencyMetric();
    Cluster root = Clustering.run(graph, metric, settings);


    Labeling labeling = new TopWeightsAggregateLabeling(10, graph);
    Sink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters1.txt"), labeling, g.inverseLabels());
    textSink.consume(root);

//    IntArrayList nonLeafMembers = new IntArrayList();
//    root.traverse(cluster -> {
//      if (!cluster.getChildren().isEmpty()) {
//        nonLeafMembers.addAll(cluster.getRemainder());
//      }
//    });
//
//    Graph graph1 = graph.inducedSubgraph(nonLeafMembers.iterator());
//    Cluster root1 = Clustering.run(graph1, metric, settings);
//    Labeling labeling = new TopWeightsAggregateLabeling(10, graph1);
//    Sink textSink = new LeafTextSink(Paths.get("/home/florian/tmp/clusters1.txt"), labeling, g.inverseLabels());
//    textSink.consume(root1);
//
//    IntArrayList nonLeafMembers1 = new IntArrayList();
//    root1.traverse(cluster -> {
//      if (!cluster.getChildren().isEmpty()) {
//        nonLeafMembers1.addAll(cluster.getRemainder());
//      }
//    });
//
//    Graph graph2 = graph.inducedSubgraph(nonLeafMembers1.iterator());
//    Cluster root2 = Clustering.run(graph2, metric, settings);
//    Labeling labeling2 = new TopWeightsAggregateLabeling(10, graph2);
//    Sink textSink2 = new LeafTextSink(Paths.get("/home/florian/tmp/clusters2.txt"), labeling2, g.inverseLabels());
//    textSink2.consume(root2);

  }


  @Test
  public void metaShit() throws FileNotFoundException {

//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.small.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.large.tsv"));
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv"));
    ClusteringSettings settings = new ClusteringSettings(50, 0.1, 0.95, 25, 0.95,true, 10000);    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    String[] inverseLabels = g.inverseLabels();
    Graph graph = builder.build();
    ConsistencyMetric metric = new RelativeWeightConsistencyMetric();
    Cluster root = Clustering.run(graph, metric, settings);
    Labeling labeling = new TopWeightsAggregateLabeling(10, graph);
    Sink leafTextSink = new LeafTextSink(Paths.get("/home/florian/tmp/clusters.txt"), labeling, g.inverseLabels());
    leafTextSink.consume(root);
    Sink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters_r.txt"), labeling, g.inverseLabels());
    textSink.consume(root);

    System.out.println("==================");

    ClusteringSettings metaSettings = new ClusteringSettings(20, 0.1, 0.95, 25, 0.95,true, 10000);
    MetaClustering metaClustering = new MetaClustering(root, graph, metric, metaSettings);
    PrintWriter w = new PrintWriter("/home/florian/tmp/clusters_h.txt");
    metaClustering.run(m -> {
      String label = m.stringify(5, inverseLabels);
      w.println(label);
    });
    w.close();


  }


  @Test
  public void newShit() throws FileNotFoundException {

//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Essentials/Workbench/fb_names.5M.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.50.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.small.tsv"));
      LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.tsv"));


//    ClusteringSettings settings = new ClusteringSettings(30, 0.2, 0.95, 25, 0.95,true, 10000);    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    ClusteringSettings settings = new ClusteringSettings(50, 0.4, 0.95, 25, 0.95,true, 10000);    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    Graph graph = builder.build();
    ConsistencyMetric metric = new RelativeWeightConsistencyMetric();
    Cluster root = Clustering.run(graph, metric, settings);

    AtomicInteger lost = new AtomicInteger();
    root.traverse(c -> {
      if (!c.getChildren().isEmpty()) {
        lost.addAndGet(c.getRemainder().size());
      }
    });
    LOG.debug("Lost: {} / {}", lost.get(), graph.size());


    Labeling labeling = new TopWeightsAggregateLabeling(100, graph);
//    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters3.txt"), labeling, g.inverseLabels());
    Sink textSink = new LeafTextSink(Paths.get("/home/florian/tmp/clusters.txt"), labeling, g.inverseLabels());
    textSink.consume(root);
  }

  interface LabeledEdgeSource<T> {

    Stream<LabeledEdge<T>> edges();

  }

  class TSVLabeledEdgeSource implements LabeledEdgeSource<String> {

    private final Pattern TSVParsePattern = Pattern.compile("\\s*(?<weight>[0-9]+)\\s+(?<u>.+?)\\s+(?<v>.+?)\\s*");
    private final Path path;

    TSVLabeledEdgeSource(Path path) {
      this.path = path;
    }

    @Override
    public Stream<LabeledEdge<String>> edges() {
      ThreadLocal<LabeledEdge<String>> localEdges = ThreadLocal.withInitial(() -> new LabeledEdge<>("", "", 0));
      try {
        return Files.lines(path).parallel().map(line -> {
          Matcher matcher = TSVParsePattern.matcher(line);
          if (matcher.find()) {
            String weight = matcher.group("weight");
            String u = matcher.group("u");
            String v = matcher.group("v");
            if (weight != null && u != null && v != null) {
              double parsedWeight = Double.parseDouble(weight);
              if (parsedWeight > 0 && u.length() > 0 && v.length() > 0) {
                LabeledEdge<String> edge = localEdges.get();
                edge.u = u;
                edge.v = v;
                edge.weight = parsedWeight;
                return edge;
              } else {
                return null;
              }
            } else {
              return null;
            }
          } else {
            return null;
          }
        }).filter(Objects::nonNull);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

    }
  }

}
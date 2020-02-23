/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
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

  Logger LOG = LoggerFactory.getLogger(Playground.class);

  private static Cluster getCluster(int id, int parent, Int2ObjectOpenHashMap<Cluster> clusters) {
    if (clusters.containsKey(id)) {
      return clusters.get(id);
    } else {
      Cluster parentCluster = clusters.get(parent); // Due to the nature of the traversal during saving, this should exist already
      Cluster cluster = new Cluster(parentCluster);
      clusters.put(id, cluster);
      return cluster;
    }
  }

  @Test
  public void newShitWithRemainderClustering() throws FileNotFoundException {
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.50.tsv"));
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
  public void newShit() throws FileNotFoundException {

//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Essentials/Workbench/fb_names.5M.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.small.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.large.tsv"));
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
    LOG.debug("Lost: {} / {}", lost.get(), graph.size());


    Labeling labeling = new TopWeightsRemainderLabeling(25, graph);
    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters3.txt"), labeling, g.inverseLabels());
//    Sink textSink = new LeafTextSink(Paths.get("/home/florian/tmp/clusters.txt"), labeling, g.inverseLabels());
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
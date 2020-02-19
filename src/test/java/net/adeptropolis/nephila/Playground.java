/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ClusterExporter;
import net.adeptropolis.nephila.clustering.ConsistencyMetric;
import net.adeptropolis.nephila.clustering.RelativeWeightConsistencyMetric;
import net.adeptropolis.nephila.clustering.labeling.Labeling;
import net.adeptropolis.nephila.clustering.labeling.Labels;
import net.adeptropolis.nephila.clustering.labeling.TopWeightsRemainderLabeling;
import net.adeptropolis.nephila.clustering.sinks.TextSink;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphDatastore;
import net.adeptropolis.nephila.graphs.implementations.arrays.BigDoubles;
import net.adeptropolis.nephila.graphs.implementations.arrays.BigInts;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Ignore
public class Playground {

  Logger LOG = LoggerFactory.getLogger(Playground.class);

  @Test
  public void newShitWithRemainderClustering() throws FileNotFoundException {
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv"));
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.50.tsv"));
    ClusteringSettings settings = new ClusteringSettings(50, 0.1, 0.4, 25, 0.95,true, 10000);    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
    g.edges().sequential().forEach(e -> builder.add(e.u, e.v, e.weight));
    CompressedSparseGraph graph = builder.build();
    ConsistencyMetric metric = new RelativeWeightConsistencyMetric();
    Cluster root = Clustering.run(graph, metric, settings);
    Labeling labeling = new TopWeightsRemainderLabeling(50, graph);
    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters4.txt"), labeling, g.inverseLabels());
    textSink.consume(root);
    saveToFile(g, graph, root, "/home/florian/tmp/clustering4.snapshot");
  }



  @Test
  public void remainderStats() throws IOException {
    ClusterTree tree = loadClusters("/home/florian/tmp/clustering.snapshot");
    DescriptiveStatistics stats = new DescriptiveStatistics();
    tree.root.traverse(cluster -> {



      stats.addValue(cluster.getRemainder().size());
    });
    System.out.println(stats);

  }

  @Test
  public void playground() throws IOException {
    ClusterTree tree = loadClusters("/home/florian/tmp/clustering.snapshot");
    Labeling labeling = new TopWeightsRemainderLabeling(5, tree.graph);
//    TextSink textSink = new TextSink(Paths.get("/home/florian/tmp/clusters4.txt"), labeling, tree.inverseLabels);
//    textSink.consume(tree.root);

    String[] inverseLabels = tree.inverseLabels;
    Graph graph = tree.graph;
    Cluster root = tree.root;

    List<Cluster> clusters = Lists.newArrayList();
    root.traverse(clusters::add);


    Map<Integer, String> clusterLabelMap = IntStream.range(0, clusters.size()).boxed().collect(Collectors.toMap(
            i -> i,
            i -> {
              Labels labels = labeling.label(clusters.get(i));
              String label = IntStream.range(0, labels.size()).mapToObj(j -> inverseLabels[labels.getVertices()[j]]).collect(Collectors.joining("|"));
              return String.format("%d|%s", clusters.get(i).getRemainder().size(), label);
            }
    ));
    String[] clusterLabels = new String[clusterLabelMap.size()];
    for (int i = 0; i < clusterLabelMap.size(); i++) {
      clusterLabels[i] = clusterLabelMap.get(i);
    }

    PrintWriter writer = new PrintWriter("/home/florian/tmp/cluster.distances.p.tsv");
    writer.println(String.join("\t", clusterLabels));

    for (int i = 0; i < clusters.size(); i++) {
      System.out.println("Current: " + i);
      Graph iGraph = clusters.get(i).remainderGraph(graph);
      IntArrayList iRemainder = clusters.get(i).getRemainder();
      int finalI = i;
      IntStream.range(i + 1, clusters.size()).parallel().forEach(j -> {
        Graph jGraph = clusters.get(j).remainderGraph(graph);
        IntOpenHashSet union = new IntOpenHashSet();
        union.addAll(iRemainder);
        union.addAll(clusters.get(j).getRemainder());
        Graph unionGraph = graph.inducedSubgraph(union.iterator());
        AtomicDouble cut = new AtomicDouble();
        unionGraph.traverseParallel((u, v, w) -> {
          int uP = iGraph.localVertexId(unionGraph.globalVertexId(u));
          int vP = iGraph.localVertexId(unionGraph.globalVertexId(v));
          if (uP < 0 ^ vP < 0) {
            cut.addAndGet(w);
          }
        });
        double ncut = cut.get() / totalEmbeddingWeight(iGraph, unionGraph) + cut.get() / totalEmbeddingWeight(jGraph, unionGraph);
        if (ncut > 0) {
          writer.println(String.format("%d\t%d\t%f", finalI, j, ncut));
        }

      });
//      for (int j = i + 1; j < clusters.size(); j++) {
//      }
    }


    writer.close();

  }

  private double totalEmbeddingWeight(Graph graph, Graph supergraph) {
    VertexIterator it = graph.vertexIterator();
    double totalWeight = 0;
    while (it.hasNext()) {
      totalWeight += supergraph.weights()[supergraph.localVertexId(it.globalId())];
    }
    return totalWeight;
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
    ClusteringSettings settings = new ClusteringSettings(50, 0.1, 0.4, 25, 0.95,true, 10000);    CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
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

    saveToFile(g, graph, root, "/home/florian/tmp/clustering.snapshot");
  }

  private static void saveToFile(LabeledTSVGraphSource g, CompressedSparseGraph graph, Cluster root, String path) throws FileNotFoundException {
    PrintWriter w = new PrintWriter(path);
    exportMapping(g.inverseLabels(), w);
    graph.export(w);
    new ClusterExporter().export(root, w);
    w.close();
  }

  private static ClusterTree loadClusters(String path) throws IOException {
    String[] inverseLabels;
    int graphSize;
    int edgeCount;
    long[] pointers;
    BigInts edges;
    BigDoubles weights;
    Cluster root = new Cluster(null);
    Int2ObjectOpenHashMap<Cluster> clusters = new Int2ObjectOpenHashMap<>(); // TODO: Might be a list if null == non-existent
    clusters.put(0, root);
    try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
      inverseLabels = reader.readLine().split("\t");
      String line;
      line = reader.readLine();
      String[] comps = line.split("\t");
      Preconditions.checkState(comps.length == 2);
      graphSize = Integer.parseInt(comps[0]);
      edgeCount = Integer.parseInt(comps[1]);
      pointers = new long[graphSize + 1];
      edges = new BigInts(edgeCount);
      weights = new BigDoubles(edgeCount);
      for (int i = 0; i <= graphSize; i++) {
        comps = reader.readLine().split("\t");
        Preconditions.checkState(comps.length == 2);
        pointers[Integer.parseInt(comps[0])] = Integer.parseInt(comps[1]);
      }
      for (long i = 0; i < edgeCount; i++) {
        comps = reader.readLine().split("\t");
        Preconditions.checkState(comps.length == 3);
        int j = Integer.parseInt(comps[0]);
        edges.set(j, Integer.parseInt(comps[1]));
        weights.set(j, Double.parseDouble(comps[2]));
      }
      while ((line = reader.readLine()) != null) {
        comps = line.split("\t");
        Preconditions.checkState(comps.length == 3);
        int clusterId = Integer.parseInt(comps[0]);
        int parent = Integer.parseInt(comps[1]);
        Cluster cluster = getCluster(clusterId, parent, clusters);
        Arrays.stream(comps[2].split(","))
                .forEach(id -> cluster.addToRemainder(Integer.parseInt(id)));
      }
    }

    CompressedSparseGraphDatastore storage = new CompressedSparseGraphDatastore(graphSize, edgeCount, pointers, edges, weights);
    CompressedSparseGraph graph = new CompressedSparseGraph(storage);

    return new ClusterTree(inverseLabels, graph, root);
  }

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

  private static void exportMapping(String[] mapping, PrintWriter writer) {
    for (int i = 0; i < mapping.length; i++) {
      writer.print(mapping[i]);
      if (i != mapping.length - 1) {
        writer.print("\t");
      }
    }
    writer.println();
  }

  private static class ClusterTree {

    final String[] inverseLabels;
    final Graph graph;
    final Cluster root;

    private ClusterTree(String[] inverseLabels, Graph graph, Cluster root) {
      this.inverseLabels = inverseLabels;
      this.graph = graph;
      this.root = root;
    }
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
/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.playground;

import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.RecursiveClustering;
import net.adeptropolis.metis.digest.ClusterDigester;
import net.adeptropolis.metis.digest.LabeledDigestMapping;
import net.adeptropolis.metis.graphs.labeled.LabeledGraph;
import net.adeptropolis.metis.graphs.labeled.LabeledGraphSource;
import org.apache.commons.lang3.StringUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

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
    LabeledGraph<String> labeledGraph = LabeledGraphSource.fromTSV(Files.lines(LARGE_GRAPH));
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinVertexConsistency(0.05)
            .build();
    Cluster root = RecursiveClustering.run(labeledGraph.getGraph(), settings);
    ClusterDigester digester = new ClusterDigester(settings);
    LabeledDigestMapping<String, String> mapping = (label, weight, score) -> String.format("%s <%.1f %.2f>", label, weight, score);
    export("/home/florian/tmp/clusters16.txt", labeledGraph, root, digester, mapping);

  }

  private void export(String path, LabeledGraph<String> labeledGraph, Cluster root, ClusterDigester digester, LabeledDigestMapping<String, String> mapping) throws FileNotFoundException {
    PrintWriter w = new PrintWriter(path);
    root.traverse(cluster -> {
      String c = digester.digest(cluster)
              .map(mapping, labeledGraph.getLabels())
              .collect(Collectors.joining(", "));
      String d = StringUtils.repeat("==", cluster.depth());
      w.printf("%s> %s\n", d, c);
    });
    w.close();
  }

}

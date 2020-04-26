/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.playground;

import net.adeptropolis.metis.ClusteringSettings;
import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.clustering.RecursiveClustering;
import net.adeptropolis.metis.digest.ClusterDigester;
import net.adeptropolis.metis.digest.Digest;
import net.adeptropolis.metis.digest.LabeledDigestMapping;
import net.adeptropolis.metis.graphs.labeled.LabeledGraph;
import net.adeptropolis.metis.graphs.labeled.LabeledGraphSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static net.adeptropolis.metis.digest.DigestRankings.COMBINED_RANKING;

/**
 * !!!
 * Please ignore this class. It's only being used for manual testing and will go away soon
 * !!!
 */

public class Playground {

  private static final Logger LOG = LoggerFactory.getLogger(Playground.class.getSimpleName());

  private static final Path LARGE_GRAPH = Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.very_large.tsv");
  private static final Path MEDIUM_GRAPH = Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.10M.tsv");
  private static final Path SMALL_GRAPH = Paths.get("/home/florian/Datasets/Workbench/wiki_corenlp.filtered.graph.small.tsv");
  private static final Path ENTITY_GRAPH = Paths.get("/home/florian/tmp/wiki_ents.tsv");
  private static final Path ENTITY_GRAPH_TERMS = Paths.get("/home/florian/tmp/wiki_ents_terms_only.norm.tsv");
  private static final Path NAMES_2M = Paths.get("/home/florian/Datasets/Workbench/fb_names.2M.tsv");
  private static final Path NAMES_20M = Paths.get("/home/florian/Datasets/Workbench/fb_names.20M.tsv");

  public static void main(String[] args) throws IOException {
    new Playground().standardClustering();
  }

  private void standardClustering() throws IOException {
    LabeledGraph<String> labeledGraph = LabeledGraphSource.fromTSV(Files.lines(ENTITY_GRAPH_TERMS));
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinVertexAffiliation(0.1)
            .withMinAncestorSimilarity(0.4)
            .withMinClusterSize(100)
            .withDigestRanking(COMBINED_RANKING.apply(1.2))
            .build();
    Cluster root = RecursiveClustering.run(labeledGraph.getGraph(), settings);
    ClusterDigester digester = new ClusterDigester(settings);
    LabeledDigestMapping<String, String> mapping = (label, weight, score) -> String.format("%s <%.1f %.2f>", label, weight, score);
    export("/home/florian/tmp/clusters4.txt", labeledGraph, root, digester, mapping);

  }

  private void export(String path, LabeledGraph<String> labeledGraph, Cluster root, ClusterDigester digester, LabeledDigestMapping<String, String> mapping) throws FileNotFoundException {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    PrintWriter w = new PrintWriter(path);
    root.traverse(cluster -> {
      Digest digest = digester.digest(cluster);
      String vertices = digest.map(mapping, labeledGraph.getLabels())
              .collect(Collectors.joining(", "));
      String prefix = StringUtils.repeat("==", cluster.depth());
      double totalWeight = cluster.aggregateGraph().totalWeight();
      w.printf("%s> %d / %.3f: %s\n", prefix, digest.totalSize(), totalWeight, vertices);
    });
    w.close();
    stopWatch.stop();
    LOG.info("Output creation finished after {}", stopWatch);
  }

}

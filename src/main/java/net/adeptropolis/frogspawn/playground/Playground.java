/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.playground;

import net.adeptropolis.frogspawn.ClusteringSettings;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.clustering.RecursiveClustering;
import net.adeptropolis.frogspawn.clustering.postprocessing.Postprocessing;
import net.adeptropolis.frogspawn.clustering.postprocessing.PostprocessingSettings;
import net.adeptropolis.frogspawn.digest.ClusterDigester;
import net.adeptropolis.frogspawn.digest.Digest;
import net.adeptropolis.frogspawn.digest.DigesterSettings;
import net.adeptropolis.frogspawn.digest.LabeledDigestMapping;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.labeled.LabeledGraph;
import net.adeptropolis.frogspawn.graphs.labeled.LabeledGraphSource;
import net.adeptropolis.frogspawn.graphs.similarity.GraphSimilarityMetric;
import net.adeptropolis.frogspawn.graphs.similarity.NormalizedCutMetric;
import net.adeptropolis.frogspawn.persistence.Snapshot;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import static net.adeptropolis.frogspawn.digest.DigestRankings.WEIGHT_RANKING;

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
  private static final Path WIKI_LINKS = Paths.get("/home/florian/tmp/wiki-links.graph");

  public static void main(String[] args) throws IOException {
//    new Playground().standardClustering();
    new Playground().playWithSnapshot();
//    new Playground().altClustering();
  }

  private void playWithSnapshot() throws FileNotFoundException {
    Snapshot<String> snapshot = Snapshot.load(new File("/home/florian/tmp/entity-terms-snapshot-raw.bin"));
    Cluster root = snapshot.getRoot();

    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinVertexAffiliation(0.075)
            .withMinClusterSize(100)
            .build();

    PostprocessingSettings postprocessingSettings = PostprocessingSettings.builder(settings)
            .withMinParentSimilarity(0.1)
            .withMaxParentSimilarity(0.38)
            .build();

    Postprocessing.apply(root, postprocessingSettings);

    Snapshot.save(new File("/home/florian/tmp/entity-terms-0.1-0.38-postprocessed-test.bin"), root, snapshot.getGraph());

    DigesterSettings digesterSettings = DigesterSettings.builder(settings)
            .withDigestRanking(WEIGHT_RANKING)
            .build();
    ClusterDigester digester = new ClusterDigester(digesterSettings);


    LabeledDigestMapping<String, String> mapping = (label, weight, score) -> String.format("%s <%.1f %.2f>", label, weight, score);
    export("/home/florian/tmp/clusters1.txt", snapshot.getGraph(), root, digester, mapping, snapshot.getGraph().getGraph());

  }

  private void standardClustering() throws IOException {
    LabeledGraph<String> labeledGraph = LabeledGraphSource.fromTSV(Files.lines(ENTITY_GRAPH_TERMS));
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinVertexAffiliation(0.075)
            .withMinClusterSize(100)
            .build();
    Cluster root = RecursiveClustering.run(labeledGraph.getGraph(), settings);

    Snapshot.save(new File("/home/florian/tmp/entity-terms-snapshot-raw.bin"), root, labeledGraph);

    PostprocessingSettings postprocessingSettings = PostprocessingSettings.builder(settings)
            .withMinParentSimilarity(0.15)
            .withMaxParentSimilarity(0.35)
            .build();
    Postprocessing.apply(root, postprocessingSettings);

    Snapshot.save(new File("/home/florian/tmp/entity-terms-postprocessed.bin"), root, labeledGraph);

    DigesterSettings digesterSettings = DigesterSettings.builder(settings)
            .withDigestRanking(WEIGHT_RANKING)
            .build();
    ClusterDigester digester = new ClusterDigester(digesterSettings);

    LabeledDigestMapping<String, String> mapping = (label, weight, score) -> String.format("%s <%.1f %.2f>", label, weight, score);
    export("/home/florian/tmp/clusters7.txt", labeledGraph, root, digester, mapping, labeledGraph.getGraph());

  }

  private void altClustering() throws IOException {
    LabeledGraph<String> labeledGraph = LabeledGraphSource.fromTSV(Files.lines(WIKI_LINKS));
    ClusteringSettings settings = ClusteringSettings.builder()
            .withMinVertexAffiliation(0.3)
            .withMinClusterSize(10)
            .build();

    Cluster root = RecursiveClustering.run(labeledGraph.getGraph(), settings);

    Snapshot.save(new File("/home/florian/tmp/links-raw.bin"), root, labeledGraph);

    PostprocessingSettings postprocessingSettings = PostprocessingSettings.builder(settings)
            .withMinParentSimilarity(0.1)
            .withMaxParentSimilarity(0.4)
            .build();
    Postprocessing.apply(root, postprocessingSettings);

    Snapshot.save(new File("/home/florian/tmp/links-pp.bin"), root, labeledGraph);

    DigesterSettings digesterSettings = DigesterSettings.builder(settings)
            .withDigestRanking(WEIGHT_RANKING)
            .build();
    ClusterDigester digester = new ClusterDigester(digesterSettings);

    LabeledDigestMapping<String, String> mapping = (label, weight, score) -> String.format("%s <%.1f %.2f>", label, weight, score);
    export("/home/florian/tmp/links-pp.txt", labeledGraph, root, digester, mapping, labeledGraph.getGraph());

  }

  private void export(String path, LabeledGraph<String> labeledGraph, Cluster root, ClusterDigester digester, LabeledDigestMapping<String, String> mapping, Graph rootGraph) throws FileNotFoundException {
    GraphSimilarityMetric metric = new NormalizedCutMetric();
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    PrintWriter w = new PrintWriter(path);
    root.traverse(cluster -> {
      Digest digest = digester.digest(cluster);
      String vertices = digest.map(mapping, labeledGraph.getLabels())
              .collect(Collectors.joining(", "));
      String prefix = StringUtils.repeat("==", cluster.depth());
      double cut = (cluster.getParent() != null) ? metric.compute(cluster.getParent().aggregateGraph(), cluster.aggregateGraph()) : 0;
      w.printf("%s>: %.3f : %d : %s\n", prefix, cut, digest.totalSize(), vertices);
    });
    w.close();
    stopWatch.stop();
    LOG.info("Output creation finished after {}", stopWatch);
  }

}

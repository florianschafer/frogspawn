package net.adeptropolis.nephila;

import net.adeptropolis.nephila.clustering.*;
import net.adeptropolis.nephila.graph.LabeledEdge;
import net.adeptropolis.nephila.graph.backend.CSRStorage;
import net.adeptropolis.nephila.graph.backend.CSRStorageBuilder;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FooingOuterEdgeSourceTest {

  @Test
  public void clusteringStuff() throws FileNotFoundException {

//     TODO: Change partition back into something like partitionMetrics
//     calculate consistency AFTER low-scoring vertices have been removed

//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.getVertex("/home/florian/Datasets/Essentials/Workbench/fb_names.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.getVertex("/home/florian/Datasets/Essentials/Workbench/fb_names.5M.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.getVertex("/home/florian/Datasets/Essentials/Workbench/wiki_en.listjson.lemmas.pairs"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.getVertex("/home/florian/Datasets/Essentials/Workbench/wiki_en.listjson.lemmas.2M.pairs"));
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Essentials/Workbench/wiki_en.listjson.lemmas.250k.pairs"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.getVertex("/home/florian/Datasets/Essentials/Workbench/wiki_en.listjson.lemmas.500k.pairs"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.getVertex("/home/florian/Datasets/Essentials/Workbench/fb_names.30M.tsv"));


    CSRStorageBuilder b = new CSRStorageBuilder();
    g.edges().sequential().forEach(e -> b.addSymmetric(e.u, e.v, e.weight));
    CSRStorage storage = b.build();

    ClusteringTemplate template = new ClusteringTemplate(storage);
//    Cluster root = new RecursiveSpectralClustering(template, 0.3, 0.85,1E-6, 150, true).compute();
    Cluster root = new RecursiveSpectralClustering(template, 0.05, 0.0,1E-6, 100, false).compute();

//    Alternative approach ,in pre-recursion: Use a maxSimilarity and join in when appropriate
//    also todo: If we don't need consistency measure in score, don't compute it

    String[] inverseLabels = g.inverseLabels();
    new DotSink(Paths.get("/home/florian/tmp/clusters.dot"), 8)
            .consume(template, root, inverseLabels);

    List<String> topLeafs = new TopLeafSink(30).consume(template, root, inverseLabels);
    topLeafs.forEach(System.out::println);

    storage.free();


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
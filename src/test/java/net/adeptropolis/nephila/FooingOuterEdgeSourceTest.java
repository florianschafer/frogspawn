package net.adeptropolis.nephila;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ClusterMetrics;
import net.adeptropolis.nephila.clustering.ClusteringTemplate;
import net.adeptropolis.nephila.clustering.RecursiveSpectralClustering;
import net.adeptropolis.nephila.graph.LabeledEdge;
import net.adeptropolis.nephila.graph.implementations.BipartiteSSNLSolver;
import net.adeptropolis.nephila.graph.implementations.CSRStorage;
import net.adeptropolis.nephila.graph.implementations.CSRStorageBuilder;
import net.adeptropolis.nephila.graph.implementations.ConnectedComponents;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FooingOuterEdgeSourceTest {

  @Test
  public void wikiStuff() throws FileNotFoundException {

    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/tmp/_2.tsv"));
    Int2ObjectOpenHashMap<String> inverseLabels = g.inverseLabels();
    CSRStorageBuilder b = new CSRStorageBuilder();
    g.edges().sequential().forEach(e -> b.addSymmetric(e.u, e.v, e.weight));
    CSRStorage storage = b.build();

    ClusteringTemplate template = new ClusteringTemplate(storage);
    Cluster root = new RecursiveSpectralClustering(template, 0.6, 0.9,1E-6, 10, false)
//    Cluster root = new RecursiveSpectralClustering(template, 0.6, 0.95,1E-6, 50)
            .compute();

    HashMap<String, String> allClusters = new HashMap<>();
    root.traverseSubclusters(cluster -> {
      ClusterMetrics metrics = template.aggregateMetrics(cluster);

      String labels = IntStream.range(0, metrics.getSortedVertices().length)
              .limit(16)
              .mapToObj(i -> String.format("%s [%.3f]", inverseLabels.get(metrics.getSortedVertices()[i]), metrics.getScores()[i]))
              .collect(Collectors.joining("\\n"));
      String combinedLabel = String.format("%s\\n%s", metrics.getSortedVertices().length, labels);
      allClusters.put(cluster.id(), combinedLabel);
    });

    PrintWriter writer = new PrintWriter("/home/florian/tmp/clusters.dot");
    writer.println("graph g {");
    writer.println("\tnode[shape=box, fontname = helvetica]");
    writer.println("\trankdir=LR;");
    allClusters.forEach((key, value) -> writer.printf("\t%s [label=\"%s\"]\n", key, value));
    root.traverseGraphEdges((parent, child) -> writer.printf("\t%s -- %s\n", parent.id(), child.id()));
    writer.println("}");
    writer.close();

    storage.free();


  }


  @Test
  public void clusteringStuff() throws FileNotFoundException {

//     TODO: Change partition back into something like partitionMetrics
//     calculate consistency AFTER low-scoring vertices have been removed

//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.5M.tsv"));
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_en.listjson.lemmas.pairs"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/wiki_en.listjson.lemmas.2M.pairs"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.30M.tsv"));
    Int2ObjectOpenHashMap<String> inverseLabels = g.inverseLabels();
    CSRStorageBuilder b = new CSRStorageBuilder();
    g.edges().sequential().forEach(e -> b.addSymmetric(e.u, e.v, e.weight));
    CSRStorage storage = b.build();

    ClusteringTemplate template = new ClusteringTemplate(storage);
    Cluster root = new RecursiveSpectralClustering(template, 0.55, 0.9,1E-6, 10, false)
//    Cluster root = new RecursiveSpectralClustering(template, 0.6, 0.95,1E-6, 50)
            .compute();

    HashMap<String, String> allClusters = new HashMap<>();
    root.traverseSubclusters(cluster -> {
      ClusterMetrics metrics = template.aggregateMetrics(cluster);
      String labels = IntStream.range(0, Math.min(10, metrics.getSortedVertices().length))
              .mapToObj(i -> String.format("%s [%.3f]", inverseLabels.get(metrics.getSortedVertices()[i]), metrics.getScores()[i]))
              .collect(Collectors.joining("\\n"));
      String combinedLabel = String.format("%s\\n%s", metrics.getSortedVertices().length, labels);
      allClusters.put(cluster.id(), combinedLabel);
    });

    PrintWriter writer = new PrintWriter("/home/florian/tmp/clusters.dot");
    writer.println("graph g {");
    writer.println("\tnode[shape=box, fontname = helvetica]");
    writer.println("\trankdir=LR;");
    allClusters.forEach((key, value) -> writer.printf("\t%s [label=\"%s\"]\n", key, value));
    root.traverseGraphEdges((parent, child) -> writer.printf("\t%s -- %s\n", parent.id(), child.id()));
    writer.println("}");
    writer.close();

//    root.traverseSubclusters(cluster -> {
//      ClusterMetrics metrics = template.aggregateMetrics(cluster);
//      String label = IntStream.range(0, Math.min(3, metrics.getSortedVertices().length))
//              .mapToObj(i -> String.format("%s [%.3f]", inverseLabels.get(metrics.getSortedVertices()[i]), metrics.getScores()[i]))
//              .collect(Collectors.joining(", "));
//      System.out.println(metrics.getSortedVertices().length + label);
//    });


    storage.free();


  }


  @Test
  public void ccStuff() {

//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.5M.tsv"));
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.30M.tsv"));
    CSRStorageBuilder b = new CSRStorageBuilder();
    g.edges().sequential().forEach(e -> b.addSymmetric(e.u, e.v, e.weight));
    CSRStorage storage = b.build();

    long start = System.nanoTime();
    ConnectedComponents connectedComponents = new ConnectedComponents(storage.defaultView());
    AtomicInteger comps = new AtomicInteger();
    connectedComponents.find(component -> {
      comps.getAndIncrement();
      System.out.printf("Found component of size %d\n", component.size());
    });
    System.out.println(comps.get());
    long runTimeMs = (System.nanoTime() - start) / 1000000000L;
    System.out.println("Runtime: " + runTimeMs + "s");


    storage.free();


  }


  @Test
  public void eigenstuff() {

//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/tmp/deleteme.wiki.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.5M.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.30M.tsv"));
    CSRStorageBuilder b = new CSRStorageBuilder();
    g.edges().sequential().forEach(e -> b.addSymmetric(e.u, e.v, e.weight));
    CSRStorage storage = b.build();

    CSRStorage.View view = storage.defaultView();
    BipartiteSSNLSolver solver = new BipartiteSSNLSolver(view);
    long start = System.nanoTime();
//    double[] v2 = solver.approxV2(1E-6);
    double[] v2 = solver.approxV2Signatures(1E-6, 100);
    long runTimeMs = (System.nanoTime() - start) / 1000000000L;
    System.out.println("Runtime: " + runTimeMs + "s");

    storage.free();


  }

//  @Test
//  public void multiplication() {
//
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
////    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.5M.tsv"));
////    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.30M.tsv"));
//    CSRStorageBuilder b = new CSRStorageBuilder();
//    g.edges().sequential().forEach(e -> b.addSymmetric(e.u, e.v, e.weight));
//    CSRStorage storage = b.build();
//
//    int[] indices = new int[storage.getNumRows()];
//    double[] arg = new double[storage.getNumRows()];
//    double[] res = new double[storage.getNumRows()];
//
//    for (int i = 0; i < storage.getNumRows(); i++) {
//      indices[i] = i;
//      arg[i] = i;
//    }
//
//    NormalizedLaplacianCSRSubmatrix mat = new NormalizedLaplacianCSRSubmatrix(storage, indices);
//
//    System.out.println("Finished building matrix");
//    System.out.println("NumRows: " + storage.getNumRows());
//    long start = System.nanoTime();
//    for (int i = 0; i < 2500; i++) {
//      mat.multiplySpectrallyShiftedNormalizedLaplacian(arg, res);
//    }
//    long runTimeMs = (System.nanoTime() - start) / (2500L * 1000000L);
//    System.out.println("Avg runtime: " + runTimeMs + "ms");
//
//    storage.free();
//
//
//  }

////  @Test
////  public void scalarProductWithEntryOverhang() {
////
////    LabeledTSVGraphSource graphSource = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
//////    graphSource.edges().count();
////
////
////    Graph graph = new Graph(graphSource);
////    int[][] adj = graph.adjacencyView();
////    for (int u = 0; u < adj.length; u++) {
////      System.out.printf("%d - %d\n", u, adj[u].length);
////    }
////
////
////  }
//
//  @Test
//  public void bar() {
//
//    LabeledEdgeSource<String> edgeSource = new TSVLabeledEdgeSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.10.tsv"));
//
//    VertexIndex<String> index = new VertexIndex<>(edgeSource); // Iterates ONCE over all edges
//
//    // TODO: With a better suited data structure for degrees (concurrent + growing), the next step may be combined in the iteration above!
//    AtomicIntegerArray degrees = new AtomicIntegerArray(index.size());
//    edgeSource.edges().parallel().forEach(edge -> degrees.incrementAndGet(index.get(edge.u))); // Iterates ONCE MORE over all edges
//
//    System.out.println(degrees);
//
//    // Compute Offsets
//    int[] offsets = new int[degrees.length()];
//    offsets[0] = 0;
//    for (int i = 1; i < degrees.length(); i++) {
//      offsets[i] = offsets[i-1] + degrees.get(i-1);
//    }
//
//    AtomicIntegerArray degreePopulation = new AtomicIntegerArray(degrees.length());
//    MMapStorage storage = new MMapStorage("/home/florian/scalarProductWithEntryOverhang.bin", offsets[degrees.length() - 1] + degrees.get(degrees.length() - 1));
//    edgeSource.edges().parallel().forEach(edge -> {
//      System.out.printf("%d / %d / %f\n", index.get(edge.u), index.get(edge.v), edge.weight);
//      int u = index.get(edge.u);
//      storage.insertEdge(offsets[u] + degreePopulation.getAndIncrement(u), index.get(edge.v), edge.weight);
//    });
//
//
//    for (int i = 0; i < offsets.length-1; i++) {
//      for (int j = offsets[i]; j < offsets[i+1]; j++) {
//        System.out.printf("%d - %d - %f\n", i, storage.getNeighbour(j), storage.getEdgeWeight(j));
//      }
//    }
//
//
//
//    storage.close();
//
//
//
//  }

  interface LabeledEdgeSource<T> {

    Stream<LabeledEdge<T>> edges();
////    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.5M.tsv"));
////    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.30M.tsv"));
//    CSRStorageBuilder b = new CSRStorageBuilder();
//    g.edges().sequential().forEach(e -> b.addSymmetric(e.u, e.v, e.weight));
//    CSRStorage storage = b.build();
//
//    Ints indices = new ArrayInts(storage.getNumRows());
//    for (int i = 0; i < storage.getNumRows(); i++) indices.set(i, i);
//    CSRSubmatrix mat = new CSRSubmatrix(storage, indices);
//
//    TarjanSCC tarjanSCC = new TarjanSCC(mat);
//    tarjanSCC.compute(comp -> {
//      System.out.println(comp.size());
//    });
//
//
//    indices.free();
//    mat.free();
//    storage.free();

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
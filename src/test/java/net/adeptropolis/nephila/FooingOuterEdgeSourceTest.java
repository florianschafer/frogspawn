package net.adeptropolis.nephila;

import net.adeptropolis.nephila.graph.implementations.CSRStorage;
import net.adeptropolis.nephila.graph.implementations.CSRStorageBuilder;
import net.adeptropolis.nephila.graph.implementations.CSRSubMatrix;
import net.adeptropolis.nephila.graph.implementations.buffers.*;
import net.adeptropolis.nephila.graph.implementations.old.LabeledEdge;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FooingOuterEdgeSourceTest {

  public void multiplication() {

    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.5M.tsv"));
//    LabeledTSVGraphSource g = new LabeledTSVGraphSource(Paths.get("/home/florian/Datasets/Workbench/fb_names.30M.tsv"));
    CSRStorageBuilder b = new CSRStorageBuilder();
    g.edges().sequential().forEach(e -> b.addSymmetric(e.u, e.v, e.weight));
    CSRStorage storage = b.build();

    IntBuffer indices = new ArrayIntBuffer(storage.getNumRows());
    DoubleBuffer arg = new ArrayDoubleBuffer(storage.getNumRows());
    DoubleBuffer res = new ArrayDoubleBuffer(storage.getNumRows());

    for (int i = 0; i < storage.getNumRows(); i++) {
      indices.set(i, i);
      arg.set(i, i);
    }

    CSRSubMatrix mat = new CSRSubMatrix(storage, indices);

    System.out.println("Finished building matrix");
    System.out.println("NumRows: " + storage.getNumRows());
    long start = System.nanoTime();
    for (int i = 0 ; i < 2500; i++) {
      mat.multiply(arg);
    }
    long runTimeMs = (System.nanoTime() - start) / (2500L * 1000000L);
    System.out.println("Avg runtime: " + runTimeMs + "ms");



    res.free();
    arg.free();
    indices.free();
    mat.free();
    storage.free();


  }

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
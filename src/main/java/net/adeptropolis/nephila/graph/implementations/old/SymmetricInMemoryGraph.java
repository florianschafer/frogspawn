package net.adeptropolis.nephila.graph.implementations.old;

import net.adeptropolis.nephila.graph.DoubleVertexProperty;
import net.adeptropolis.nephila.graph.Edge;
import net.adeptropolis.nephila.graph.Graph;
import sun.misc.Unsafe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SymmetricInMemoryGraph implements Graph {

  private static final Pattern TSV_PARSER_PATTERN = Pattern.compile("\\s*(?<weight>[0-9]+)\\s+(?<u>.+?)\\s+(?<v>.+?)\\s*");
//
//  private final int[] vertices;
//  private final DoubleVertexProperty[] outEdges;

  public SymmetricInMemoryGraph(Path path) {




  }

  @Override
  public IntStream vertices() {
//    return Arrays.stream(vertices);
    return null;
  }

  @Override
  public Stream<Edge> edges() {
    return null;
  }

  @Override
  public Stream<DoubleVertexProperty> outEdges(int vertexId) {
    return null;
  }

  @Override
  public int degree(int vertexId) {
    return 0;
  }

//  private Stream<LabeledEdge<String>> readLabeledEdges(Path path) {
//    ThreadLocal<LabeledEdge<String>> localEdges = ThreadLocal.withInitial(() -> new LabeledEdge<>("", "", 0));
//    try {
//      return Files.lines(path).parallel().map(line -> {
//        Matcher matcher = TSV_PARSER_PATTERN.matcher(line);
//        if (matcher.find()) {
//          String weight = matcher.group("weight");
//          String u = matcher.group("u");
//          String v = matcher.group("v");
//          if (weight != null && u != null && v != null) {
//            double parsedWeight = Double.parseDouble(weight);
//            if (parsedWeight > 0 && u.length() > 0 && v.length() > 0) {
//              LabeledEdge<String> edge = localEdges.get();
//              edge.u = u;
//              edge.v = v;
//              edge.weight = parsedWeight;
//              return edge;
//            } else {
//              return null;
//            }
//          } else {
//            return null;
//          }
//        } else {
//          return null;
//        }
//      }).filter(Objects::nonNull);
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
//  }


}

package net.adeptropolis.nephila;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.adeptropolis.nephila.graph.Edge;
import net.adeptropolis.nephila.graph.LabeledEdge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// Todo: This class is a mess resource-wise!

public class LabeledTSVGraphSource implements GraphSource {

  private static final Pattern TSVParsePattern = Pattern.compile("\\s*(?<weight>[0-9]+)\t+(?<u>[^\t]+)\t+(?<v>.+)\\s*");
  private final Path path;
  private final ConcurrentMap<String, Integer> labelMap;

  public LabeledTSVGraphSource(Path path) {
    this.path = path;
    this.labelMap = computeLabelMap();
  }

  private ConcurrentMap<String, Integer> computeLabelMap() {
    ConcurrentMap<String, Integer> indices = new ConcurrentHashMap<>();
    AtomicInteger currentIdx = new AtomicInteger(0);
    parseLabeledEdges()
            .flatMap(edge -> Stream.of(edge.u, edge.v))
            .parallel()
            .forEach(label -> indices.computeIfAbsent(label, (x) -> currentIdx.getAndIncrement()));
    return indices;
  }

  public Int2ObjectOpenHashMap<String> inverseLabels() {
    Int2ObjectOpenHashMap<String> reverseMap = new Int2ObjectOpenHashMap<>();
    labelMap.entrySet().stream().forEach(entry -> reverseMap.put(entry.getValue(), entry.getKey()));
    return reverseMap;
  }

  private Stream<LabeledEdge<String>> parseLabeledEdges() {
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

  @Override
  public IntStream vertices() {
    return labelMap.entrySet()
            .parallelStream()
            .mapToInt(Map.Entry::getValue);
  }

  @Override
  public int vertexCount() {
    return labelMap.size();
  }

  @Override
  public Stream<Edge> edges() {
    ThreadLocal<Edge> localEdges = ThreadLocal.withInitial(() -> new Edge(0, 0, 0.0));
    return parseLabeledEdges()
            .parallel()
            .map(labeledEdge -> {
              Edge edge = localEdges.get();
              edge.u = labelMap.get(labeledEdge.u);
              edge.v = labelMap.get(labeledEdge.v);
              edge.weight = labeledEdge.weight;
              return edge;
            });
  }
}

package net.adeptropolis.nephila.clustering;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// TODO: Clean up, replace println and hashmaps

// TODO: Iek!
public class DotSink implements HierarchySink<Object> {

  private final Path path;
  private final int labelSize;

  public DotSink(Path path, int labelSize) {
    this.path = path;
    this.labelSize = labelSize;
  }

  @Override
  public Object consume(ClusteringTemplate template, Cluster root, String[] labelMap) {
    HashMap<String, String> allClusters = collectClusters(template, root, labelMap);
    writeDot(root, allClusters);
    return null;
  }

  private HashMap<String, String> collectClusters(ClusteringTemplate template, Cluster root, String[] labelMap) {
    HashMap<String, String> allClusters = new HashMap<>();
    root.traverseSubclusters(cluster -> {
      ClusterMetrics metrics = template.aggregateMetrics(cluster);
      String labels = IntStream.range(0, Math.min(labelSize, metrics.getSortedVertices().length))
              .mapToObj(i -> String.format("%s [%.2f]", labelMap[metrics.getSortedVertices()[i]], metrics.getIntraClusterWeights()[i]))
              .collect(Collectors.joining("\\n"));
      String combinedLabel = String.format("%s\\n%s", metrics.getSortedVertices().length, labels);
      allClusters.put(cluster.id(), combinedLabel);
    });
    return allClusters;
  }

  private void writeDot(Cluster root, HashMap<String, String> allClusters) {
    try {
      final PrintWriter writer = new PrintWriter(path.toFile());
      writer.println("graph g {");
      writer.println("\tnode[shape=box, fontname = helvetica]");
      writer.println("\trankdir=LR;");
      allClusters.forEach((key, value) -> writer.printf("\t%s [label=\"%s\"]\n", key, value));
      root.traverseGraphEdges((parent, child) -> writer.printf("\t%s -- %s\n", parent.id(), child.id()));
      writer.println("}");
      writer.close();
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

}

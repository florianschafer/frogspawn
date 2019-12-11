/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.sinks;

import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.labeling.Labeling;
import net.adeptropolis.nephila.clustering.labeling.Labels;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TextSink implements Sink {

  private final Labeling labeling;
  private final String[] vertexLabels;
  private final PrintWriter writer;

  public TextSink(Path path, Labeling labeling, String[] vertexLabels) throws FileNotFoundException {
    this.labeling = labeling;
    this.vertexLabels = vertexLabels;
    this.writer = new PrintWriter(path.toFile());;
  }

  @Override
  public void consume(Cluster root) {
    traverse(root, "");
    writer.close();
  }

  private void traverse(Cluster cluster, String prefix) {
    Labels labels = labeling.label(cluster);
    String labelStr = IntStream.range(0, labels.size()).mapToObj(i -> vertexLabels[labels.getVertices()[i]])
            .collect(Collectors.joining(", "));
    writer.println(String.format("%s %d: %s", prefix, labels.aggregateSize(), labelStr));
    for (Cluster child : cluster.getChildren()) {
      traverse(child, prefix + "---");
    }
  }

}

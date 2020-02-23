/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
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

public class LeafTextSink implements Sink {

  private final Labeling labeling;
  private final String[] vertexLabels;
  private final PrintWriter writer;

  public LeafTextSink(Path path, Labeling labeling, String[] vertexLabels) throws FileNotFoundException {
    this.labeling = labeling;
    this.vertexLabels = vertexLabels;
    this.writer = new PrintWriter(path.toFile());
  }

  @Override
  public void consume(Cluster root) {
    root.traverse(cluster -> {
      if (cluster.getChildren().isEmpty()) {
        Labels labels = labeling.label(cluster);
        String labelStr = IntStream.range(0, labels.size()).mapToObj(i -> vertexLabels[labels.getVertices()[i]])
                .collect(Collectors.joining(", "));
        writer.println(String.format("%d\t%s", labels.aggregateSize(), labelStr));
      }
    });
    writer.close();
  }

}

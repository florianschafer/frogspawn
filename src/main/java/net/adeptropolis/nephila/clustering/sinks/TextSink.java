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

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TextSink implements Sink {

  private final Labeling labeling;
  private final String[] vertexLabels;

  public TextSink(Labeling labeling, String[] vertexLabels) {
    this.labeling = labeling;
    this.vertexLabels = vertexLabels;
  }

  @Override
  public void consume(Cluster root) {
    traverse(root, "");
  }

  private void traverse(Cluster cluster, String prefix) {
    Labels labels = labeling.label(cluster);
    String labelStr = IntStream.range(0, labels.size()).mapToObj(i -> String.format("%s [%.2f]",
            vertexLabels[labels.getVertices()[i]],
            labels.getWeights()[i]))
            .collect(Collectors.joining(", "));
    System.out.println(String.format("%s %d: %s", prefix, labels.aggregateSize(), labelStr));
    for (Cluster child : cluster.getChildren()) {
      traverse(child, prefix + "-----");
    }
  }

}

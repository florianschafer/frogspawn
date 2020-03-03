/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.sinks;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.digest.ClusterDigester;
import net.adeptropolis.metis.digest.Digest;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * <p>Plaintext cluster sink that produces a text file containing the full cluster hierarchy. The tree structure is preserved
 * through indentation</p>
 * <p>NOTE: This class assumes the existence of some <code>String[]</code> that maps between vertex ids and string values</p>
 */

public class TextSink implements Sink {

  private final ClusterDigester digester;
  private final String[] vertexLabels;
  private final PrintWriter writer;

  /**
   * Constructor
   *
   * @param path         Path to the output file
   * @param digester     Digester to use
   * @param vertexLabels Array used to map between vertex ids and the String values represented by that vertex
   * @throws FileNotFoundException
   * @see ClusterDigester
   */

  public TextSink(Path path, ClusterDigester digester, String[] vertexLabels) throws FileNotFoundException {
    this.digester = digester;
    this.vertexLabels = vertexLabels;
    this.writer = new PrintWriter(path.toFile());
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public void consume(Cluster root) {
    traverse(root, "");
    writer.close();
  }

  /**
   * Traversal callback
   *
   * @param cluster Current cluster
   * @param prefix  Current prefix (indentation)
   */

  private void traverse(Cluster cluster, String prefix) {
    Digest digest = digester.create(cluster);
    String labelStr = IntStream.range(0, digest.size())
            .mapToObj(i -> vertexLabels[digest.getVertices()[i]])
            .collect(Collectors.joining(", "));
    writer.println(String.format("%s %d: %s", prefix, digest.totalSize(), labelStr));
    for (Cluster child : cluster.getChildren()) {
      traverse(child, prefix + "---");
    }
  }

}
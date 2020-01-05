/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.algorithms.power_iteration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.stream.Collectors;

public class VectorDumper {

  private final PrintWriter writer;

  public VectorDumper(int correlationId) {
    try {
      writer = new PrintWriter(new File("/home/florian/tmp/debug", String.format("%d.tsv", correlationId)));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void dump(double[] vector) {
    String row = Arrays.stream(vector).mapToObj(String::valueOf).collect(Collectors.joining("\t"));
    writer.println(row);
  }

  public void close() {
    writer.close();
  }
}

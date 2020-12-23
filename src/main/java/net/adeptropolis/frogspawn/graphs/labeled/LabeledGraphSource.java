/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Parser(s) for labeled graphs. Right now, only TSV is supported
 */

public class LabeledGraphSource {

  private static final Pattern TSVParsePattern = Pattern.compile("\\s*(?<weight>[0-9.]+)\t+(?<u>[^\t]+)\t+(?<v>.+)\\s*");

  /**
   * Default constructor
   */

  private LabeledGraphSource() {

  }

  /**
   * Read a labeled graph from a Tab-delimited file
   * <p>The expected format is <code>weight left right</code></p>
   *
   * @param lines Stream of lines
   * @return A new weighted graph
   */

  public static LabeledGraph<String> fromTSV(Stream<String> lines) {
    LabeledGraphBuilder<String> builder = new LabeledGraphBuilder<>(new DefaultLabelling<>(String.class));
    lines.forEach(line -> {
      Matcher matcher = TSVParsePattern.matcher(line);
      if (matcher.find() && matcher.groupCount() == 3) {
        String weight = matcher.group("weight");
        builder.add(matcher.group("u"), matcher.group("v"), Double.parseDouble(weight));
      }
    });
    return builder.build();
  }

}

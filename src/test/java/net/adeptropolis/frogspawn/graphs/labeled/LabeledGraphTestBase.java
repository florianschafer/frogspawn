/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LabeledGraphTestBase {

  static class EdgeFingerprinter implements LabeledEdgeConsumer<String> {

    private final Stream.Builder<String> builder = Stream.builder();

    @Override
    public void accept(String left, String right, double weight) {
      builder.add(String.format("%s#%s#%.0f", left, right, weight));
    }

    public String fingerprint() {
      return builder.build().collect(Collectors.joining("|"));
    }

  }
}

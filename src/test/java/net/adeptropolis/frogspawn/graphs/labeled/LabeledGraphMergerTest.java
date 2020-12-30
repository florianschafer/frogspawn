/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.graphs.labeled;

import net.adeptropolis.frogspawn.graphs.functions.AverageEdgeWeight;
import net.adeptropolis.frogspawn.graphs.labeled.labelings.DefaultLabeling;
import net.adeptropolis.frogspawn.graphs.labeled.labelings.StringLabeling;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

public class LabeledGraphMergerTest extends LabeledGraphTestBase {

  @Test
  public void mergeTwoGraphs() {
    LabeledGraphMerger<String> merger = new LabeledGraphMerger<>(new AverageEdgeWeight(), new StringLabeling());
    merger.add(new LabeledGraphBuilder<>(new DefaultLabeling<>(String.class))
            .add("0", "1", 1)
            .add("1", "2", 1)
            .add("2", "0", 1)
            .add("2", "3", 1)
            .build(), 1);
    merger.add(new LabeledGraphBuilder<>(new DefaultLabeling<>(String.class))
            .add("2", "3", 2)
            .add("3", "4", 2)
            .add("4", "5", 2)
            .add("5", "3", 2)
            .build(), 3);
    LabeledGraph<String> merged = merger.merge();
    assertThat(merged.getLabeling().labels().collect(Collectors.toList()), containsInAnyOrder("0", "1", "2", "3", "4", "5"));
    EdgeFingerprinter edgeFingerprinter = new EdgeFingerprinter();
    merged.traverse(edgeFingerprinter);
    assertThat(edgeFingerprinter.fingerprint(), is("1#0#1|1#2#1|0#1#1|0#2#1|2#1#1|2#0#1|2#3#4|3#2#4|3#4#3|3#5#3|4#3#3|4#5#3|5#3#3|5#4#3"));
  }

  @Test
  public void mergeThreeGraphs() {
    LabeledGraphMerger<String> merger = new LabeledGraphMerger<>(new AverageEdgeWeight(), new StringLabeling());
    merger.add(new LabeledGraphBuilder<>(new DefaultLabeling<>(String.class))
            .add("0", "1", 2)
            .add("1", "2", 2)
            .add("2", "0", 2)
            .build(), 1);
    merger.add(new LabeledGraphBuilder<>(new DefaultLabeling<>(String.class))
            .add("2", "3", 3)
            .add("3", "4", 3)
            .add("4", "5", 3)
            .add("5", "3", 3)
            .build(), 2);
    merger.add(new LabeledGraphBuilder<>(new DefaultLabeling<>(String.class))
            .add("5", "6", 4)
            .add("6", "7", 4)
            .add("7", "8", 4)
            .add("8", "6", 4)
            .build(), 3);
    LabeledGraph<String> merged = merger.merge();
    assertThat(merged.getLabeling().labels().collect(Collectors.toList()), containsInAnyOrder("0", "1", "2", "3", "4", "5", "6", "7", "8"));
    EdgeFingerprinter edgeFingerprinter = new EdgeFingerprinter();
    merged.traverse(edgeFingerprinter);
    assertThat(edgeFingerprinter.fingerprint(), is("1#0#1|1#2#1|0#1#1|0#2#1|2#1#1|2#0#1|2#3#2|3#2#2|3#4#2|3#5#2|4#3#2|4#5#2|5#3#2|5#4#2|5#6#3|6#5#3|6#7#3|6#8#3|7#6#3|7#8#3|8#6#3|8#7#3"));
  }


}
package net.adeptropolis.nephila.graphs.algorithms;

import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graphs.GraphTestBase;
import net.adeptropolis.nephila.graphs.algorithms.power_iteration.SignumConvergence;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class SpectralBisectorTest extends GraphTestBase {

  private static final SignumConvergence conv = new SignumConvergence(1E-6);

  @Test
  public void completeBipartiteGraphs() {
    SpectralBisector bisector = new SpectralBisector(completeBipartiteWithWeakLink(), conv);
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    IntIterator remaining = bisector.bisect(c, 100000);
    assertFalse(remaining.hasNext());
    List<List<Integer>> partitions = c.vertices();
    assertThat(partitions.get(0), containsInAnyOrder(0, 1, 2, 3, 4));
    assertThat(partitions.get(1), containsInAnyOrder(5, 6, 7, 8));
  }

  @Test
  public void paths() {
    SpectralBisector bisector = new SpectralBisector(pathWithWeakLink(), conv);
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    IntIterator remaining = bisector.bisect(c, 100000);
    assertFalse(remaining.hasNext());
    List<List<Integer>> partitions = c.vertices();
    assertThat(partitions.get(0), containsInAnyOrder(0, 1, 2, 3));
    assertThat(partitions.get(1), containsInAnyOrder(4, 5, 6, 7, 8, 9, 10, 11));
  }

  @Test
  public void iterationExcessReturnsNull() {
    SpectralBisector bisector = new SpectralBisector(largeCircle(), new SignumConvergence(0));
    SubgraphCollectingConsumer c = new SubgraphCollectingConsumer();
    IntIterator remaining = bisector.bisect(c, 10);
    assertNull(remaining);
  }

}
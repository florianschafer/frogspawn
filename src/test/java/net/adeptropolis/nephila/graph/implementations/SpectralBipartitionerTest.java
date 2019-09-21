package net.adeptropolis.nephila.graph.implementations;

import com.google.common.collect.Lists;
import net.adeptropolis.nephila.graph.backend.Backend;
import net.adeptropolis.nephila.graph.backend.GraphBuilder;
import net.adeptropolis.nephila.graph.backend.View;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SpectralBipartitionerTest {

  @Test
  public void standardCase() {
    withTwoWeaklyLinkedCompleteBipartiteGraphs(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}, new int[]{0, 1, 2, 3, 4}, new int[]{5, 6, 7, 8});
  }

  private void withTwoWeaklyLinkedCompleteBipartiteGraphs(int[] viewIndices, int[]... expected) {

    Backend graph = new GraphBuilder()
            .add(0, 1, 1)
            .add(0, 2, 1)
            .add(0, 3, 1)
            .add(4, 1, 1)
            .add(4, 2, 1)
            .add(4, 3, 1)
            .add(5, 3, 0.5)
            .add(5, 6, 1)
            .add(5, 8, 1)
            .add(7, 6, 1)
            .add(7, 8, 1)
            .build();
    List<View> partitions = Lists.newArrayList();
    new SpectralBipartitioner(graph.view(viewIndices), 1E-9).partition(partitions::add);
    assertThat("Number of partitions should agree", partitions.size(), is(expected.length));
    partitions.sort(Comparator.comparingInt(comp -> comp.getVertex(0)));
    for (int i = 0; i < partitions.size(); i++) {
      View component = partitions.get(i);
      assertThat("Partition size should agree", component.size(), is(expected[i].length));
      for (int j = 0; j < component.size(); j++) {
        assertThat("Partition has member", component.getVertex(j), is(expected[i][j]));
      }
    }
  }

  @Test
  public void partitionSubset() {
    withTwoWeaklyLinkedCompleteBipartiteGraphs(new int[]{0, 1, 2, 3, 4, 5, 6, 8}, new int[]{0, 1, 2, 3, 4}, new int[]{5, 6, 8});
  }


}
package net.adeptropolis.nephila.graph.implementations;

import com.google.common.collect.Lists;
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

  @Test
  public void partitionSubset() {
    withTwoWeaklyLinkedCompleteBipartiteGraphs(new int[]{0, 1, 2, 3, 4, 5, 6, 8}, new int[]{0, 1, 2, 3, 4}, new int[]{5, 6, 8});
  }

  private void withTwoWeaklyLinkedCompleteBipartiteGraphs(int[] viewIndices, int[]... expected) {

    CSRStorage graph = new CSRStorageBuilder()
            .addSymmetric(0, 1, 1)
            .addSymmetric(0, 2, 1)
            .addSymmetric(0, 3, 1)
            .addSymmetric(4, 1, 1)
            .addSymmetric(4, 2, 1)
            .addSymmetric(4, 3, 1)
            .addSymmetric(5, 3, 0.5)
            .addSymmetric(5, 6, 1)
            .addSymmetric(5, 8, 1)
            .addSymmetric(7, 6, 1)
            .addSymmetric(7, 8, 1)
            .build();
    List<CSRStorage.View> partitions = Lists.newArrayList();
    new SpectralBipartitioner(graph.view(viewIndices)).partition(partitions::add);
    assertThat("Number of partitions should agree", partitions.size(), is(expected.length));
    partitions.sort(Comparator.comparingInt(comp -> comp.get(0)));
    for (int i = 0; i < partitions.size(); i++) {
      CSRStorage.View component = partitions.get(i);
      assertThat("Partition size should agree", component.size(), is(expected[i].length));
      for (int j = 0; j < component.size(); j++) {
        assertThat("Partition has member", component.get(j), is(expected[i][j]));
      }
    }
  }


}
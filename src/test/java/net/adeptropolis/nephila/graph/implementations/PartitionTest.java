package net.adeptropolis.nephila.graph.implementations;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

public class PartitionTest {

  @Test
  public void emptyPartition() {
    verifyK3_3Subset(new int[]{}, new double[]{}, 0.0);
  }

  @Test
  public void singletonPartition() {
    verifyK3_3Subset(new int[]{5}, new double[]{0}, 0.0);
  }

  @Test
  public void fullGraph() {
    verifyK3_3Subset(new int[]{0, 1, 2, 3, 4, 5}, new double[]{1, 1, 1, 1, 1, 1}, 1.0);
  }

  @Test
  public void singleEdgePartition() {
    verifyK3_3Subset(new int[]{1, 5}, new double[]{ 17.0 / (2 + 7 + 17), 17.0 / (17 + 19 + 23)}, (17.0 + 17.0) / (2 + 7 + 17 + 17 + 19 + 23));
  }

  private void verifyK3_3Subset(int[] childIndices, double[] expectedScores, double expectedConsistency) {
    assertThat("Indices size equals expected size", childIndices.length, is(expectedScores.length));
    CSRStorage graph = new CSRStorageBuilder()
            .addSymmetric(0, 1, 2)
            .addSymmetric(0, 2, 3)
            .addSymmetric(0, 3, 5)
            .addSymmetric(4, 1, 7)
            .addSymmetric(4, 2, 11)
            .addSymmetric(4, 3, 13)
            .addSymmetric(5, 1, 17)
            .addSymmetric(5, 2, 19)
            .addSymmetric(5, 3, 23)
            .build();
    Partition p = Partition.of(graph.view(childIndices), graph.defaultView());
    assertThat("Child view has correct size", p.getView().size(), is(childIndices.length));
    assertThat("Score array has correct length", p.getScores().length, is(childIndices.length));
    for (int i = 0; i < expectedScores.length; i++)
      assertThat("Score matches", p.getScores()[i], closeTo(expectedScores[i], 1E-6));
    assertThat("Consistency matches", p.getConsistency(), closeTo(expectedConsistency, 1E-6));
  }

}
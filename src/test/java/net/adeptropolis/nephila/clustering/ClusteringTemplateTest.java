package net.adeptropolis.nephila.clustering;

import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphDatastore;
import net.adeptropolis.nephila.graphs.implementations.DeprecatedCompressedSparseGraphBuilder;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;

@Deprecated
@Ignore
public class ClusteringTemplateTest {

  @Test
  public void emptyPartitionScores() {
    verifyK3_3SubsetScores(new int[]{}, new double[]{});
  }

  @Test
  public void singletonPartitionScores() {
    verifyK3_3SubsetScores(new int[]{5}, new double[]{0});
  }

  @Test
  public void fullGraphScores() {
    verifyK3_3SubsetScores(new int[]{0, 1, 2, 3, 4, 5}, new double[]{1, 1, 1, 1, 1, 1});
  }

  @Test
  public void singleEdgePartitionScores() {
    verifyK3_3SubsetScores(new int[]{1, 5}, new double[]{17.0 / (2 + 7 + 17), 17.0 / (17 + 19 + 23)});
  }

  private void verifyK3_3SubsetScores(int[] childIndices, double[] expectedScores) {
    assertThat("Indices size equals expected size", childIndices.length, is(expectedScores.length));
    CompressedSparseGraphDatastore graph = new DeprecatedCompressedSparseGraphBuilder()
            .add(0, 1, 2)
            .add(0, 2, 3)
            .add(0, 3, 5)
            .add(4, 1, 7)
            .add(4, 2, 11)
            .add(4, 3, 13)
            .add(5, 1, 17)
            .add(5, 2, 19)
            .add(5, 3, 23)
            .build();
    ClusteringTemplate template = new ClusteringTemplate(graph);
    double[] scores = template.globalOverlap(graph.view(childIndices));
    assertThat("Score array has correct length", scores.length, is(childIndices.length));
    for (int i = 0; i < expectedScores.length; i++)
      assertThat("Score matches", scores[i], closeTo(expectedScores[i], 1E-6));
  }

}
package net.adeptropolis.nephila.clustering.shapers;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ClusteringSettings;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SingletonCollapsingShaperTest {

  @Test
  public void noCollapsing() {
    SingletonCollapsingShaper shaper = new SingletonCollapsingShaper(settings(false));
    Cluster rootCluster = new Cluster(null);
    Cluster childCluster = new Cluster(rootCluster);
    childCluster.addToRemainder(IntIterators.wrap(new int[]{1, 2, 3}));
    boolean modified = shaper.imposeStructure(childCluster);
    assertFalse(modified);
    assertThat(rootCluster.getChildren().size(), is(1));
    assertThat(rootCluster.getRemainder(), is(IntLists.EMPTY_LIST));
    assertThat(childCluster.getRemainder(), is(new IntArrayList(new int[]{1, 2, 3})));
  }

  @Test
  public void notApplicable() {
    SingletonCollapsingShaper shaper = new SingletonCollapsingShaper(settings(true));
    Cluster rootCluster = new Cluster(null);
    Cluster childCluster1 = new Cluster(rootCluster);
    childCluster1.addToRemainder(IntIterators.wrap(new int[]{1, 2, 3}));
    Cluster childCluster2 = new Cluster(rootCluster);
    childCluster2.addToRemainder(IntIterators.wrap(new int[]{4, 5, 6}));
    boolean modified = shaper.imposeStructure(childCluster1);
    assertFalse(modified);
    assertThat(rootCluster.getChildren().size(), is(2));
    assertThat(rootCluster.getRemainder(), is(IntLists.EMPTY_LIST));
    assertThat(childCluster1.getRemainder(), is(new IntArrayList(new int[]{1, 2, 3})));
  }

  @Test
  public void doCollapse() {
    SingletonCollapsingShaper shaper = new SingletonCollapsingShaper(settings(true));
    Cluster root = new Cluster(null);
    Cluster child = new Cluster(root);
    child.addToRemainder(IntIterators.wrap(new int[]{1, 2, 3}));
    Cluster grandchild1 = new Cluster(child);
    Cluster grandchild2 = new Cluster(child);
    boolean modified = shaper.imposeStructure(child);
    assertTrue(modified);
    assertThat(root.getChildren().size(), is(2));
    assertThat(root.getChildren(), containsInAnyOrder(grandchild1, grandchild2));
    assertThat(root.getRemainder(), is(new IntArrayList(new int[]{1, 2, 3})));
  }

  private ClusteringSettings settings(boolean collapse) {
    return new ClusteringSettings(0, 0, 0, 0, collapse, 0);
  }


}
package net.adeptropolis.nephila.clustering.shapers;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntLists;
import net.adeptropolis.nephila.clustering.Cluster;
import net.adeptropolis.nephila.clustering.ClusteringSettings;
import net.adeptropolis.nephila.clustering.Protocluster;
import org.junit.Test;

import static net.adeptropolis.nephila.clustering.Protocluster.GraphType.COMPONENT;
import static org.hamcrest.MatcherAssert.assertThat;
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
    Protocluster protocluster = new Protocluster(null, COMPONENT, childCluster);
    boolean modified = shaper.imposeStructure(protocluster);
    assertFalse(modified);
    assertThat(protocluster.getCluster(), is(childCluster));
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
    Protocluster protocluster = new Protocluster(null, COMPONENT, childCluster1);
    boolean modified = shaper.imposeStructure(protocluster);
    assertFalse(modified);
    assertThat(protocluster.getCluster(), is(childCluster1));
    assertThat(rootCluster.getRemainder(), is(IntLists.EMPTY_LIST));
    assertThat(childCluster1.getRemainder(), is(new IntArrayList(new int[]{1, 2, 3})));
  }

  @Test
  public void doCollapse() {
    SingletonCollapsingShaper shaper = new SingletonCollapsingShaper(settings(true));
    Cluster rootCluster = new Cluster(null);
    Cluster childCluster = new Cluster(rootCluster);
    childCluster.addToRemainder(IntIterators.wrap(new int[]{1, 2, 3}));
    Protocluster protocluster = new Protocluster(null, COMPONENT, childCluster);
    boolean modified = shaper.imposeStructure(protocluster);
    assertTrue(modified);
    assertThat(protocluster.getCluster(), is(rootCluster));
    assertThat(rootCluster.getRemainder(), is(new IntArrayList(new int[]{1, 2, 3})));
  }

  private ClusteringSettings settings(boolean collapse) {
    return new ClusteringSettings(0, 0, 0, collapse, 0);
  }


}
package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntLists;
import org.junit.Test;

import static net.adeptropolis.nephila.clustering.Protocluster.GraphType.COMPONENT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SingletonCollapsingShaperTest {

  @Test
  public void noCollapsing() {
    SingletonCollapsingShaper shaper = new SingletonCollapsingShaper(settings(false));
    Cluster rootCluster = new Cluster(null);
    Cluster childCluster = new Cluster(rootCluster);
    childCluster.addToRemainder(IntIterators.wrap(new int[]{1, 2, 3}));
    Protocluster protocluster = new Protocluster(null, COMPONENT, childCluster);
    Protocluster shaped = shaper.imposeStructure(protocluster);
    assertThat(shaped.getCluster(), is(childCluster));
    assertThat(rootCluster.getRemainder(), is(IntLists.EMPTY_LIST));
    assertThat(childCluster.getRemainder(), is(new IntArrayList(new int[]{1, 2, 3})));
  }

  @Test
  public void doCollapse() {
    SingletonCollapsingShaper shaper = new SingletonCollapsingShaper(settings(true));
    Cluster rootCluster = new Cluster(null);
    Cluster childCluster = new Cluster(rootCluster);
    childCluster.addToRemainder(IntIterators.wrap(new int[]{1, 2, 3}));
    Protocluster protocluster = new Protocluster(null, COMPONENT, childCluster);
    Protocluster shaped = shaper.imposeStructure(protocluster);
    assertThat(shaped.getCluster(), is(rootCluster));
    assertThat(rootCluster.getRemainder(), is(new IntArrayList(new int[]{1, 2, 3})));
  }

  private ClusteringSettings settings(boolean collapse) {
    return new ClusteringSettings(0, 0, 0, collapse, 0);
  }


}
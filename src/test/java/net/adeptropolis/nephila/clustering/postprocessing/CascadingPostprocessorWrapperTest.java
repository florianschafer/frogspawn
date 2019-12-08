/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import com.google.common.collect.ImmutableSet;
import net.adeptropolis.nephila.clustering.Cluster;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class CascadingPostprocessorWrapperTest {

  @Test
  public void noPostprocessingRequired() {
    boolean changed = new CascadingPostprocessorWrapper(cluster -> false).apply(null);
    assertFalse(changed);
  }

  @Test
  public void basicFunctionality() {
    Cluster root = new Cluster(null);
    Cluster c1 = new Cluster(root);
    Cluster c11 = new Cluster(c1);
    Cluster c12 = new Cluster(c1);
    Cluster c121 = new Cluster(c12);
    Cluster c2 = new Cluster(root);
    SingleVisitEnsuringPostprocessor postprocessor = new SingleVisitEnsuringPostprocessor();
    boolean changed = new CascadingPostprocessorWrapper(postprocessor).apply(root);
    assertTrue(changed);
    Set<Cluster> visited = postprocessor.getVisited();
    assertThat(visited, is(ImmutableSet.of(root, c1, c11, c12, c121, c2)));
  }

  private static class SingleVisitEnsuringPostprocessor implements Postprocessor {

    private Set<Cluster> visited;

    private SingleVisitEnsuringPostprocessor() {
      this.visited = new HashSet<>();
    }

    @Override
    public boolean apply(Cluster cluster) {
      boolean hasBeenVisitedAlready = visited.contains(cluster);
      visited.add(cluster);
      return !hasBeenVisitedAlready;
    }

    public Set<Cluster> getVisited() {
      return visited;
    }

  }

}
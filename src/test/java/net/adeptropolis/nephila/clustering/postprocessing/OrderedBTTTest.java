/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.postprocessing;

import net.adeptropolis.nephila.clustering.Cluster;
import org.junit.Test;

import java.util.PriorityQueue;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class OrderedBTTTest {

  @Test
  public void defaultBehaviour() {
    Cluster root = new Cluster(null);
    Cluster child1 = new Cluster(root);
    Cluster child11 = new Cluster(child1);
    Cluster child12 = new Cluster(child1);
    Cluster child2 = new Cluster(root);
    Cluster child21 = new Cluster(child2);
    Cluster child22 = new Cluster(child2);
    Cluster child221 = new Cluster(child22);
    PriorityQueue<Cluster> queue = OrderedBTT.queue(root);
    assertThat(queue.poll().depth(), is(3));
    assertThat(queue.poll().depth(), is(2));
    assertThat(queue.poll().depth(), is(2));
    assertThat(queue.poll().depth(), is(2));
    assertThat(queue.poll().depth(), is(2));
    assertThat(queue.poll().depth(), is(1));
    assertThat(queue.poll().depth(), is(1));
    assertThat(queue.poll().depth(), is(0));
    assertTrue(queue.isEmpty());
  }

}
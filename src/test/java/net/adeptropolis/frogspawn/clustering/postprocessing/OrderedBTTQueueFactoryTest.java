/*
 * Copyright Florian Schaefer 2019.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.clustering.postprocessing;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntIterators;
import net.adeptropolis.frogspawn.clustering.Cluster;
import net.adeptropolis.frogspawn.graphs.Graph;
import net.adeptropolis.frogspawn.graphs.GraphTestBase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.PriorityQueue;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

public class OrderedBTTQueueFactoryTest extends GraphTestBase {

  @Test
  public void checkOrder() {
    Graph graph = completeGraph(21);
    Cluster root = new Cluster(graph);
    root.addToRemainder(IntIterators.wrap(new int[]{0, 1, 2, 3, 4, 5}));
    Cluster c1 = new Cluster(root);
    c1.addToRemainder(IntIterators.wrap(new int[]{6, 7}));
    Cluster c4 = new Cluster(root);
    c4.addToRemainder(IntIterators.wrap(new int[]{15, 16, 17, 18, 19}));
    Cluster c2 = new Cluster(root);
    c2.addToRemainder(IntIterators.wrap(new int[]{8, 9, 10}));
    Cluster c3 = new Cluster(root);
    c3.addToRemainder(IntIterators.wrap(new int[]{11, 12, 13, 14}));
    Cluster c41 = new Cluster(c4);
    c41.addToRemainder(IntIterators.wrap(new int[]{20}));
    ArrayList<Object> collected = Lists.newArrayList();
    PriorityQueue<Cluster> queue = OrderedBTTQueueFactory.queue(root);
    while (!queue.isEmpty()) {
      collected.add(queue.poll());
    }
    assertThat(collected, contains(c41, c4, c3, c2, c1, root));
  }

  @Test
  public void defaultBehaviour() {
    Cluster root = new Cluster((Graph) null);
    Cluster child1 = new Cluster(root);
    Cluster child11 = new Cluster(child1);
    Cluster child12 = new Cluster(child1);
    Cluster child2 = new Cluster(root);
    Cluster child21 = new Cluster(child2);
    Cluster child22 = new Cluster(child2);
    Cluster child221 = new Cluster(child22);
    PriorityQueue<Cluster> queue = OrderedBTTQueueFactory.queue(root);
    assertThat(queue.poll().depth(), is(3));
    assertThat(queue.poll().depth(), is(2));
    assertThat(queue.poll().depth(), is(2));
    assertThat(queue.poll().depth(), is(2));
    assertThat(queue.poll().depth(), is(2));
    assertThat(queue.poll().depth(), is(1));
    assertThat(queue.poll().depth(), is(1));
    assertThat(queue.poll().depth(), is(0));
    assertThat(queue.isEmpty(), is(true));
  }

}
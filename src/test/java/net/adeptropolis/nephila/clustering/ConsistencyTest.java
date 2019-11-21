package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ConsistencyTest {

  @Test
  public void sizeBelowThreshold() {
    Cluster cluster = new Cluster(null);
    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
            .add(0, 1, 1)
            .add(1,2, 1)
            .add(2, 3, 1)
            .build();
    CompressedSparseGraph candidate = new CompressedSparseGraphBuilder()
            .add(0, 1, 1)
            .add(1,2, 1)
            .build();
    Consistency consistency = new Consistency(graph, 10, 0.0);
    Graph consistentSubgraph = consistency.ensure(cluster, candidate);
    assertNull(consistentSubgraph);
    assertThat(cluster.getRemainder(), is(IntArrayList.wrap(new int[]{0, 1, 2})));
  }

  @Test
  public void someVerticesSortedOut() {
todo: continue here
//    Cluster cluster = new Cluster(null);
//    CompressedSparseGraph graph = new CompressedSparseGraphBuilder()
//            .add(0, 1, 1)
//            .add(0,1, 1)
//            .add(0, 2, 1)
//            .add(1, 2, 1)
//            .add(1, 2, 1)
//            .build();
//    CompressedSparseGraph candidate = new CompressedSparseGraphBuilder()
//            .add(0, 1, 1)
//            .add(1,2, 1)
//            .build();
//    Consistency consistency = new Consistency(graph, 10, 0.0);
//    Graph consistentSubgraph = consistency.ensure(cluster, candidate);
//    assertNull(consistentSubgraph);
//    assertThat(cluster.getRemainder(), is(IntArrayList.wrap(new int[]{0, 1, 2})));


  }



}
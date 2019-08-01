package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.IntStream;

// TODO: Cluster scores
public class ClusterIndex {

  private final Int2ObjectOpenHashMap<Cluster> index;

  public ClusterIndex(Cluster root) {
    index = new Int2ObjectOpenHashMap<>();
    root.traverseSubclusters(cluster -> {
      for (int node : cluster.getRemainder()) {
        index.put(node, cluster);
      }});
  }

  public Cluster get(int node) {
    return index.get(node);
  }

  public Cluster getCommon(int... nodes) {
    // TODO: This might be horribly inefficient
    Cluster[] clusters = new Cluster[nodes.length];
    for (int v : nodes) clusters[v]  = get(v);
    while (!aligned(clusters)) {
      int deepest = IntStream.range(0, clusters.length)
              .boxed() // wtf, man?
              .max(Comparator.comparingInt(i -> clusters[i].depth()))
              .orElseThrow(() -> new RuntimeException("wtf?!"));
      clusters[deepest] = clusters[deepest].getParent();
    }
    return clusters[0];
  }

  private boolean aligned(Cluster... clusters) {
    Cluster any = clusters[0];
    return Arrays.stream(clusters).allMatch(cluster -> cluster.equals(any));
  }

}

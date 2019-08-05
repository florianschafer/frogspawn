package net.adeptropolis.nephila.clustering;

public interface HierarchySink<T> {

  T consume(ClusteringTemplate template, Cluster root, String[] labelMap);

}

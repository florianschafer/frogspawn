package net.adeptropolis.nephila.clustering;

public interface HierarchySink {

  void consume(ClusteringTemplate template, Cluster root, String[] labelMap);

}

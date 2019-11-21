package net.adeptropolis.nephila.clustering.sinks;

import net.adeptropolis.nephila.clustering.ClusteringTemplate;
import net.adeptropolis.nephila.clustering.DeprecatedCluster;

@Deprecated
public interface HierarchySink<T> {

  T consume(ClusteringTemplate template, DeprecatedCluster root, String[] labelMap);

}

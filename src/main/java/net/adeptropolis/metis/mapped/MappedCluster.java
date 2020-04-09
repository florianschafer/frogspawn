/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.metis.mapped;

import net.adeptropolis.metis.clustering.Cluster;
import net.adeptropolis.metis.digest.ClusterDigester;
import net.adeptropolis.metis.digest.Digest;

import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A mapped cluster
 * <p>
 * This class provides a wrapper structure around regular clusters that provides a more high-level access
 * to clusters created from labeled graphs. Creation of cluster digests and mapping between vertex ids
 * to effective cluster members is handled implicitly.
 * </p>
 *
 * @param <V> Vertex label type
 * @param <T> Effective cluster member type
 */

public class MappedCluster<V, T> {

  private final Cluster cluster;
  private final MappingTemplate<V, T> template;

  /**
   * Constructor
   *
   * @param cluster  Cluster instance to wrap
   * @param template Mapping template to be used
   */

  private MappedCluster(Cluster cluster, MappingTemplate<V, T> template) {
    this.cluster = cluster;
    this.template = template;
  }

  /**
   * Map a whole cluster tree
   *
   * @param root     Root cluster
   * @param labels   Cluster labels
   * @param digester Digester to use
   * @param mapper   Mapper between vertex ids and effective cluster members
   * @param <V>      Vertex label type
   * @param <T>      Effective cluster member type
   * @return New root node of a mapped cluster tree
   */

  public static <V, T> MappedCluster<V,T> mapTree(Cluster root, V[] labels, ClusterDigester digester, ClusterMemberMapper<V, T> mapper) {
    MappingTemplate<V, T> mappingTemplate = new MappingTemplate<>(labels, digester, mapper);
    return new MappedCluster<>(root, mappingTemplate);
  }

  /**
   * @return Effective cluster members
   */

  public Stream<T> getMembers() {
    return template.mapClusterMembers(cluster);
  }

  /**
   * @return Children
   */

  public Stream<MappedCluster<V, T>> getChildren() {
    return cluster.getChildren()
            .stream()
            .sorted(Comparator.comparingInt(Cluster::getId))
            .map(child -> new MappedCluster<>(child, template));
  }

  /**
   * @return Parent
   */

  public MappedCluster getParent() {
    if (cluster.getParent() == null) {
      return null;
    }
    return new MappedCluster<>(cluster.getParent(), template);
  }

  /**
   * Traverse a a (sub-)tree of mapped clusters
   *
   * @param consumer Mapped cluster consumer
   */

  public void traverse(Consumer<MappedCluster<V, T>> consumer) {
    cluster.traverse(c -> consumer.accept(new MappedCluster<>(c, template)));
  }

  /**
   * @return Id for this mapped cluster. It is identical to the id of the underlying cluster.
   */

  public int getId() {
    return cluster.getId();
  }

  /**
   * Equals
   *
   * @return True if this object's underlying cluster is the same as that of <code>obj</code>; false otherwise.
   */

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof MappedCluster)) {
      return false;
    }
    MappedCluster<?, ?> other = (MappedCluster<?, ?>) obj;
    return cluster.equals(other.cluster);
  }

  /**
   * Hash Code
   *
   * @return The hash code of the underlying cluster instance
   */

  @Override
  public int hashCode() {
    return cluster.hashCode();
  }

  /**
   * Helper class for brokering between clusters and mapped clusters
   *
   * @param <V> Vertex label type
   * @param <T> Effective cluster member type
   */

  private static class MappingTemplate<V, T> {

    private final V[] labels;
    private final ClusterDigester digester;
    private final ClusterMemberMapper<V, T> mapper;

    /**
     * Constructor
     *
     * @param labels   Vertex labels
     * @param digester Cluster digester instance
     * @param mapper   Mapping between vertex ids and effective cluster members
     */

    private MappingTemplate(V[] labels, ClusterDigester digester, ClusterMemberMapper<V, T> mapper) {
      this.labels = labels;
      this.digester = digester;
      this.mapper = mapper;
    }

    /**
     * Create a cluster digest and map it onto a stream of cluster members
     *
     * @param cluster Cluster whose vertices should be mapped
     * @return Stream of effective cluster members
     */

    private Stream<T> mapClusterMembers(Cluster cluster) {
      Digest digest = digester.digest(cluster);
      return IntStream.range(0, digest.size())
              .mapToObj(v -> mapClusterMember(digest, v));
    }

    /**
     * Map a particular vertex id onto a cluster member
     *
     * @param digest Digest to be used
     * @param v      Vertex id
     * @return New cluster member
     */

    private T mapClusterMember(Digest digest, int v) {
      return mapper.map(
              labels[digest.getVertices()[v]],
              digest.getWeights()[v],
              digest.getScores()[v]);
    }

  }

}

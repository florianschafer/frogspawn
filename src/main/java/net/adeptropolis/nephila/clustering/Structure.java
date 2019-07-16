package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.nephila.clustering.RecursiveSpectralClustering.Branch;
import net.adeptropolis.nephila.graph.implementations.CSRStorage;

import java.util.Arrays;

public class Structure {

  // TODO: Used here once again. Move all this logic into Cluster
  private final ClusteringTemplate template;
  private final double minParentOverlap;

  public Structure(ClusteringTemplate template, double minParentOverlap) {
    this.template = template;
    this.minParentOverlap = minParentOverlap;
  }

  // NOTE: Modifies branch in-place
  public Branch applyPreRecursion(Branch branch) {
    Cluster cluster = branch.getCluster();
    Cluster parent = cluster.getParent();
    if (parent != null && cluster.getParent().getChildren().size() == 1) {
      parent.addToRemainder(cluster.getRemainder());
      parent.getChildren().remove(cluster); // TODO: Make children a set
      branch.setCluster(parent);
    }
    return branch;
  }

  // NOTE: Modifies branch in-place
  public Branch applyPostRecursion(Branch branch) {

//    Cluster cluster = branch.getCluster();
//    Cluster parent = cluster.getParent();
//
//    if (parent != null) System.out.println("OVL: " + parentOverlap(branch, parent));
//
//    while (parent != null && parentOverlap(branch, parent) < minParentOverlap) {
//      if (parent.getParent() == null) break;
//      parent = parent.getParent();
//    }
//    if (parent != null && cluster.getParent() != null && cluster.getParent() != parent) {
//      cluster.getParent().getChildren().remove(cluster); // TODO: Make children a set
//      cluster.setParent(parent);
//      parent.getChildren().add(cluster);
//    }

    return branch;
  }

  private double parentOverlap(Branch branch, Cluster parent) {

    // TODO: This sucks
    IntArrayList vertices = branch.getCluster().aggregateVertices();
    for (int v : branch.getView().getIndices()) vertices.add(v);
    int[] clusterVertices = vertices.toIntArray();
    Arrays.parallelSort(clusterVertices);

    if (clusterVertices.length == 0 ) return 0;

    CSRStorage.View clusterView = template.getRootView().subview(clusterVertices);
    int[] parentVertices = parent.aggregateVertices().toIntArray();
    Arrays.parallelSort(parentVertices);

    if (parentVertices.length == 0) return 0;

    CSRStorage.View parentView = template.getRootView().subview(parentVertices);

    System.out.println("S: " + clusterView.size() +  " / " + parentView.size());

    return template.overlapScore(clusterView, parentView);
  }


}

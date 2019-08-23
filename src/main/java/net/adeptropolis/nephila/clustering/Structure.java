package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.nephila.clustering.RecursiveSpectralClustering.Branch;
import net.adeptropolis.nephila.graph.backend.View;

import java.util.Arrays;

public class Structure {

  // TODO: Used here once again. Move all this logic into Cluster
  private final ClusteringTemplate template;
  private final double minParentOverlap;
  private final boolean collapseSingletons;

  public Structure(ClusteringTemplate template, double minParentOverlap, boolean collapseSingletons) {
    this.template = template;
    this.minParentOverlap = minParentOverlap;
    this.collapseSingletons = collapseSingletons;
  }

  // NOTE: Modifies branch in-place
  public Branch applyPreRecursion(Branch branch) {
    if (collapseSingletons) {
      Cluster cluster = branch.getCluster();
      Cluster parent = cluster.getParent();
      if (parent != null && cluster.getParent().getChildren().size() == 1) {
        parent.addToRemainder(cluster.getRemainder());
        parent.getChildren().remove(cluster);
        branch.setCluster(parent);
      }
    }
    return branch;
  }

  // NOTE: Modifies branch in-place
  public Branch applyPostRecursion(Branch branch) {

    Cluster cluster = branch.getCluster();
    Cluster ancestor = cluster.getParent();
    if (ancestor == null) return branch;

    while (ancestorOverlap(branch, ancestor) < minParentOverlap) {
      if (ancestor.getParent() == null) break;
      ancestor = ancestor.getParent();
    }

    if (ancestor != cluster.getParent()) {
      cluster.getParent().getChildren().remove(cluster);
      cluster.setParent(ancestor);
      ancestor.getChildren().add(cluster);
    }
    return branch;
  }

  // NOTE: This REALLY only works for ancestors!
  private double ancestorOverlap(Branch branch, Cluster parent) {
    View view = finalizedView(branch.getCluster(), branch.getView());
    View parentView = finalizedView(branch.getCluster().getParent(), branch.getView());
    return template.overlapScore(view, parentView);
  }

  private View finalizedView(Cluster cluster, View clusterView) {
    // TODO: This is super-inefficient
    IntArrayList vertices = cluster.aggregateVertices();
    for (int v : clusterView.getVertices()) vertices.add(v);
    int[] combinedVertices = vertices.toIntArray();
    Arrays.parallelSort(combinedVertices);
    return clusterView.subview(combinedVertices);
  }


}

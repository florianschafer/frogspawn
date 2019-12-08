/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.nephila.graphs.algorithms.DeprecatedConnectedComponents;
import net.adeptropolis.nephila.graphs.algorithms.DeprecatedSpectralBipartitioner;
import net.adeptropolis.nephila.graphs.implementations.View;

import java.util.Arrays;
import java.util.PriorityQueue;

// TODO:
//  1) Find proper merging criteria
//  2) Intruduce a monte-carlo style multiplication on submatrices!!!
//  3) Current implementation still allows clusters below min size. Possibly need to add check after post-iteration processing
//  4) Check whether a binary overlap (or something else) would even make more sense!

@Deprecated
public class RecursiveSpectralClustering {

  private final int minPartitionSize;
  private final double minVertexConsistency;
  private final double maxAlternations;

  private final ClusteringTemplate template;
  private final PriorityQueue<Branch> queue;
  private final Structure structure;

  public RecursiveSpectralClustering(ClusteringTemplate template, double minVertexConsistency, double minParentOverlap, double maxAlternations, int minPartitionSize, boolean collapseSingletons) {
    this.template = template;
    this.minPartitionSize = minPartitionSize;
    this.minVertexConsistency = minVertexConsistency;
    this.maxAlternations = maxAlternations;
    this.queue = new PriorityQueue<>();
    this.structure = new Structure(template, minParentOverlap, collapseSingletons);
  }

  public DeprecatedCluster compute() {
    View rootView = template.getRootView();
    DeprecatedCluster rootCluster = new DeprecatedCluster(null);
    Branch rootBranch = new Branch(Branch.Type.ROOT, rootView, rootCluster);
    queue.add(rootBranch);
    while (!queue.isEmpty()) {
      Branch branch = structure.applyPreRecursion(queue.poll());
      if (branch.type == Branch.Type.COMPONENT) {
        spectralPartition(branch);
      } else {
        partitionComponents(branch);
      }
    }
    return rootCluster;
  }

  private void spectralPartition(Branch branch) {
    new DeprecatedSpectralBipartitioner(branch.view, maxAlternations).partition(partition -> {
      if (partition.size() < minPartitionSize || partition.size() == branch.view.size()) {
        // 1st Condition: trivial
        // 2nd: Could not cluster further. This should only happen in rare instances where we don't fully converge. Stop right here
        branch.cluster.addToRemainder(partition);
      } else {
        View consistentSubview = ensureConsistency(branch, partition);
        if (consistentSubview.size() < minPartitionSize) {
          branch.cluster.addToRemainder(consistentSubview);
        } else {
          enqueueBranch(Branch.Type.SPECTRAL, branch.cluster, consistentSubview);
        }
      }
    });
  }

  private void enqueueBranch(Branch.Type type, DeprecatedCluster parent, View consistentSubview) {
    DeprecatedCluster childCluster = new DeprecatedCluster(parent);
    Branch childBranch = new Branch(type, consistentSubview, childCluster);
    queue.add(structure.applyPostRecursion(childBranch));
  }

  private View ensureConsistency(Branch branch, View partition) {
    IntRBTreeSet remainingVertices = new IntRBTreeSet(partition.getVertices());

    while (true) {
      int prevSize = remainingVertices.size();
      int[] vertices = remainingVertices.toIntArray();
      Arrays.parallelSort(vertices);
      View subview = branch.view.subview(vertices);
      double[] vertexConsistencies = template.globalOverlap(subview);
      for (int i = 0; i < subview.size(); i++) {
        if (vertexConsistencies[i] < minVertexConsistency) {
          branch.cluster.addToRemainder(subview.getVertex(i));
          remainingVertices.remove(subview.getVertex(i));
        }
      }
      if (remainingVertices.size() == prevSize) return subview;
    }
  }

  private void partitionComponents(Branch branch) {
    new DeprecatedConnectedComponents(branch.view).find(component -> {
      if (component.size() == branch.view.size()) {
        // Special case: We only have a single CC => Label cluster as COMPONENT and re-add to queue
        branch.type = Branch.Type.COMPONENT;
        queue.add(branch);
      } else if (component.size() < minPartitionSize) {
        branch.cluster.addToRemainder(component);
      } else {
        enqueueBranch(Branch.Type.COMPONENT, branch.cluster, component);
      }
    });
  }

  static class Branch implements Comparable<Branch> {

    private final View view;
    private Type type;
    private DeprecatedCluster cluster;

    Branch(Type type, View view, DeprecatedCluster cluster) {
      this.type = type;
      this.view = view;
      this.cluster = cluster;
    }

    @Override
    public int compareTo(Branch other) {
      return Integer.compare(view.size(), other.view.size());
    }

    public View getView() {
      return view;
    }

    public DeprecatedCluster getCluster() {
      return cluster;
    }

    public void setCluster(DeprecatedCluster cluster) {
      this.cluster = cluster;
    }

    private enum Type {
      ROOT, COMPONENT, SPECTRAL
    }

  }


}

package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import net.adeptropolis.nephila.graph.implementations.CSRStorage;
import net.adeptropolis.nephila.graph.implementations.CSRStorage.View;
import net.adeptropolis.nephila.graph.implementations.ConnectedComponents;
import net.adeptropolis.nephila.graph.implementations.SpectralBipartitioner;

import java.util.Arrays;
import java.util.PriorityQueue;

public class RecursiveSpectralClustering {

  private final int minPartitionSize;
  private final double minVertexConsistency;
  private final double maxAlternations;

  private final ClusteringTemplate template;
  private final PriorityQueue<Branch> queue;

  public RecursiveSpectralClustering(ClusteringTemplate template, double minVertexConsistency, double maxAlternations, int minPartitionSize) {
    this.template = template;
    this.minPartitionSize = minPartitionSize;
    this.minVertexConsistency = minVertexConsistency;
    this.maxAlternations = maxAlternations;
    this.queue = new PriorityQueue<>();
  }

  public Cluster compute() {
    View rootView = template.getRootView();
    Cluster rootCluster = new Cluster(null);
    Branch rootBranch = new Branch(Branch.Type.ROOT, rootView, rootCluster);
    queue.add(rootBranch);
    while (!queue.isEmpty()) {
      Branch branch = queue.poll();
      if (branch.type == Branch.Type.COMPONENT) {
        spectralPartition(branch);
      } else {
        partitionComponents(branch);
      }
    }
    return rootCluster;
  }

  private void spectralPartition(Branch branch) {
    new SpectralBipartitioner(branch.view, maxAlternations).partition(partition -> {
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

  private void enqueueBranch(Branch.Type type, Cluster parent, View consistentSubview) {
    Cluster childCluster = new Cluster(parent);
    Branch childBranch = new Branch(type, consistentSubview, childCluster);
    queue.add(childBranch);
  }

  private View ensureConsistency(Branch branch, View partition) {
    IntRBTreeSet remainingVertices = new IntRBTreeSet(partition.getIndices());

    int rounds = 0;
    while (true) {
      int prevSize = remainingVertices.size();
      int[] vertices = remainingVertices.toIntArray();
      Arrays.parallelSort(vertices);
      View subview = branch.view.subview(vertices);
      double[] vertexConsistencies = template.computeVertexConsistencies(subview);
      for (int i = 0; i < subview.size(); i++) {
        if (vertexConsistencies[i] < minVertexConsistency) {
          branch.cluster.addToRemainder(subview.get(i));
          remainingVertices.remove(subview.get(i));
        }
      }
      rounds++;
      if (remainingVertices.size() == prevSize) {
        System.out.printf("Concistency step finished after %d iterations. Remaining: %d / %d\n", rounds, remainingVertices.size(), partition.size());
        return subview;
      };
    }
  }

  private void partitionComponents(Branch branch) {
    new ConnectedComponents(branch.view).find(component -> {
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

  private static class Branch implements Comparable<Branch> {

    private Type type;
    private final View view;
    private final Cluster cluster;

    Branch(Type type, View view, Cluster cluster) {
      this.type = type;
      this.view = view;
      this.cluster = cluster;
    }

    @Override
    public int compareTo(Branch other) {
      return Integer.compare(view.size(), other.view.size());
    }

    private enum Type {
      ROOT, COMPONENT, SPECTRAL
    }

  }



}

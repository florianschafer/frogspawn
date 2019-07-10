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

  public RecursiveSpectralClustering(CSRStorage graph, int minPartitionSize, double minVertexConsistency, double maxAlternations) {
    this.minPartitionSize = minPartitionSize;
    this.minVertexConsistency = minVertexConsistency;
    this.maxAlternations = maxAlternations;
    this.queue = new PriorityQueue<>();
    this.template = new ClusteringTemplate(graph);
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
      if (partition.size() == branch.view.size()) {
        // Could not cluster further. This should only happen in rare instances where we don't fully converge. Stop right here
        branch.addViewToRemainder();
      } else if (partition.size() < minPartitionSize) {
        branch.addViewToRemainder();
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
      if (remainingVertices.size() == prevSize) return subview;
    }
  }

  private void partitionComponents(Branch branch) {
    new ConnectedComponents(branch.view).find(component -> {
      if (component.size() == branch.view.size()) {
        // Special case: We only have a single CC => Relabel cluster to COMPONENT and re-add
        branch.type = Branch.Type.COMPONENT;
        queue.add(branch);
      } else if (component.size() < minPartitionSize) {
        branch.addViewToRemainder();
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

    void addViewToRemainder() {
      cluster.addToRemainder(view);
    }

    private enum Type {
      ROOT, COMPONENT, SPECTRAL
    }

  }



}

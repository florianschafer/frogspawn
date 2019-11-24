package net.adeptropolis.nephila.clustering;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntIterator;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.VertexIterator;

import java.util.ArrayList;
import java.util.List;

public class Cluster {

  private final Cluster parent;
  private final List<Cluster> children;

  private final IntArrayList remainder;

  public Cluster(Cluster parent) {
    this.parent = parent;
    this.children = new ArrayList<>();
    this.remainder = new IntArrayList();
    if (parent != null) parent.children.add(this);
  }

  public IntArrayList getRemainder() {
    return remainder;
  }

  public void addToRemainder(int globalId) {
    remainder.add(globalId);
  }

  public void addToRemainder(IntIterator it) {
    while (it.hasNext()) {
      addToRemainder(it.nextInt());
    }
  }

  public void addToRemainder(Graph graph) {
    remainder.ensureCapacity(remainder.size() + graph.size());
    VertexIterator vertexIterator = graph.vertexIterator();
    while (vertexIterator.hasNext()) {
      remainder.add(vertexIterator.globalId());
    }
  }

  /**
   * Equals
   *
   * <p>Please note that due to the fact that clusters may be wildly modified in the process and still two clusters should only
   * be regarded as equal if they refer to the same reference. The call below is just there as a reminder of this fact.</p>
   * @param obj The reference object with which to compare.
   * @return True if this object is the same as the obj argument; false otherwise.
   */

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /**
   * Hash Code
   *
   * <p>Please note that due to the fact that clusters may be wildly modified in the process and still two clusters should only
   * yield the same hash code if they refer to the same reference. The call below is just there as a reminder of this fact.</p>
   *
   * @return A hash code value for this object.
   */

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}

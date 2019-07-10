package net.adeptropolis.nephila.clustering;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.adeptropolis.nephila.graph.implementations.CSRStorage;

import java.util.List;

public class Cluster {

    private final IntArrayList remainder;
    private final Cluster parent;
    private final List<Cluster> children;

    public Cluster(Cluster parent) {
        this.parent = parent;
        this.remainder = new IntArrayList();
        this.children = Lists.newArrayList();
        if (parent != null) parent.children.add(this);
    }

    public void addToRemainder(CSRStorage.View view) {
        for (int v : view.getIndices()) addToRemainder(v);
    }

    public void addToRemainder(int v) {
        remainder.add(v);
    }
}

/*
 * Copyright (c) Florian Schaefer 2020.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.meta;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.List;

public class MetaCluster {

  private MetaCluster parent;

  private final List<MetaCluster> children;
  private final DoubleArrayList childrenScores;
  private final IntArrayList vertices;
  private final DoubleArrayList vertexScores;

  public MetaCluster() {
    children = Lists.newArrayList();
    childrenScores = new DoubleArrayList();
    vertices = new IntArrayList();
    vertexScores = new DoubleArrayList();
  }

  public void setParent(MetaCluster parent) {
    this.parent = parent;
  }

  public void addChild(MetaCluster child, double score) {
    children.add(child);
    child.setParent(this);
    childrenScores.add(score);
  }

  public void addVertex(int v, double score) {
    vertices.add(v);
    vertexScores.add(score);
  }
}


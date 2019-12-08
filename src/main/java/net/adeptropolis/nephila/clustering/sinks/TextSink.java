/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.clustering.sinks;

import net.adeptropolis.nephila.clustering.Cluster;

public class TextSink implements Sink {

  private final String[] labels;

  public TextSink(String[] labels) {
    this.labels = labels;
  }

  @Override
  public void consume(Cluster root) {


  }

}

/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.persistence;

import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigDoubles;
import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigInts;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import static net.adeptropolis.frogspawn.graphs.implementations.arrays.BigDoubles.BIN_BITS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ClusterIOTest {

  private ClusterIO clusterIO = new ClusterIO();
  private static final Random RAND = new Random(1337L);

  @Test
  public void bigInts() throws IOException {
    BigInts bigInts = new BigInts(10);
    for (int i = 0; i < 3 * (1 << BIN_BITS) + 10; i++) {
      bigInts.set(i, RAND.nextInt());
    }
    verifyIsCopy(bigInts, BigInts.class);
  }

  @Test
  public void bigDoubles() throws IOException {
    BigDoubles bigDoubles = new BigDoubles(10);
    for (int i = 0; i < 3 * (1 << BIN_BITS) + 10; i++) {
      bigDoubles.set(i, RAND.nextDouble());
    }
    verifyIsCopy(bigDoubles, BigDoubles.class);
  }

  private <T> void verifyIsCopy(Object object, Class<T> type) throws IOException {
    T deserialized = clusterIO.load(save(object), type);
    assertThat(deserialized, is(object));
  }

  private File save(Object object) throws IOException {
    File tmpFile = File.createTempFile(UUID.randomUUID().toString(), null);
    tmpFile.deleteOnExit();
    clusterIO.save(object, tmpFile);
    return tmpFile;
  }

}
/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.persistence;

import java.io.*;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Serialization {

  private Serialization() {

  }

  public static void save(Object object, File outputFile) {
    try (FileOutputStream fos = new FileOutputStream(outputFile)) {
      try (GZIPOutputStream gos = new GZIPOutputStream(fos) {{
        def.setLevel(Deflater.BEST_SPEED);
      }}) {
        try (ObjectOutputStream oos = new ObjectOutputStream(gos)) {
          oos.writeObject(object);
        }
      }
    } catch (IOException e) {
      throw new SnapshotException(e);
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T load(File file) {
    try (FileInputStream fis = new FileInputStream(file)) {
      try (GZIPInputStream gis = new GZIPInputStream(fis)) {
        try (ObjectInputStream ois = new ObjectInputStream(gis)) {
          return (T) ois.readObject();
        }
      } catch (ClassNotFoundException e) {
        throw new SnapshotException(e);
      }
    } catch (IOException e) {
      throw new SnapshotException(e);
    }
  }

}

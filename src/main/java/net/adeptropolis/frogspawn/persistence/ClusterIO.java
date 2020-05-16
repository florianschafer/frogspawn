/*
 * Copyright (c) Florian Schaefer 2020.
 * SPDX-License-Identifier: Apache-2.0
 */

package net.adeptropolis.frogspawn.persistence;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers;
import com.esotericsoftware.kryo.serializers.FieldSerializer;
import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigDoubles;
import net.adeptropolis.frogspawn.graphs.implementations.arrays.BigInts;

import java.io.*;

public class ClusterIO {

  private final Kryo kryo;

  public ClusterIO() {
    kryo = new Kryo();

    kryo.register(int[].class, new DefaultArraySerializers.IntArraySerializer());
    kryo.register(double[].class, new DefaultArraySerializers.DoubleArraySerializer());

    FieldSerializer<BigInts> bigIntsSerializer = new FieldSerializer<>(kryo, BigInts.class);
    kryo.register(BigInts.class, bigIntsSerializer);

    FieldSerializer<BigDoubles> bigDoublesSerializer = new FieldSerializer<>(kryo, BigDoubles.class);
    kryo.register(BigDoubles.class, bigDoublesSerializer);

  }

  public void save(Object object, File outputFile) throws IOException {
    try(FileOutputStream fos = new FileOutputStream(outputFile)) {
      try(Output output = new Output(fos)) {
        kryo.writeObject(output, object);
      }
    }
  }

  public  <T> T load(File file, Class<T> type) throws IOException {
    try(FileInputStream fis = new FileInputStream(file)) {
      try(Input input = new Input(fis)) {
        return kryo.readObject(input, type);
      }
    }
  }

}

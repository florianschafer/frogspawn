package net.adeptropolis.nephila;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

class MMapStorage {

  private final MMapBuffer neighbours;
  private final MMapBuffer edgeWeights;
  private final RandomAccessFile file;
  private final FileChannel fileChannel;

  public MMapStorage(String path, long edgesCount) {
    file = getRandomAccessFile(path);
    fileChannel = file.getChannel();
    neighbours = new MMapBuffer(0, edgesCount * Integer.BYTES);
    edgeWeights = new MMapBuffer(edgesCount * Integer.BYTES, edgesCount * Double.BYTES);
  }

  private RandomAccessFile getRandomAccessFile(String path) {
    try {
      return new RandomAccessFile(path, "rw");
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public void insertEdge(long ptr, int v, double weight) {
    neighbours.putInt(ptr << 2, v);
    edgeWeights.putDouble(ptr << 3, weight);
  }

  public int getNeighbour(long ptr) {
    return neighbours.getInt(ptr << 2);
  }

  public double getEdgeWeight(long ptr) {
    return edgeWeights.getDouble(ptr << 3);
  }

  public void close() {
    try {
      fileChannel.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    try {
      file.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private class MMapBuffer {

    private static final long MAX_PAGE_SIZE = 1L << 31;
    private final MappedByteBuffer[] buffers;

    public MMapBuffer(long offset, long size) {
      int buffersCount = Math.toIntExact((size >> 31) + 1);
      buffers = new MappedByteBuffer[buffersCount];
      for (int i = 0; i < buffersCount; i++) {
        long bufferSize = Math.min(MAX_PAGE_SIZE, size - MAX_PAGE_SIZE * (size >> 31));
        buffers[i] = createBuffer(i * MAX_PAGE_SIZE, bufferSize);
      }
    }

    private MappedByteBuffer createBuffer(long offset, long size) {
      try {
        return fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, size);
      } catch (IOException e) {
        throw new RuntimeException("Unable to create buffer", e);
      }
    }

    public void putInt(long ptr, int val) {
      buffers[(int) (ptr >> 31)].putInt((int) (ptr & 0x7FFFFFFF), val);
    }

    public int getInt(long ptr) {
      return buffers[(int) (ptr >> 31)].getInt((int) (ptr & 0x7FFFFFFF));
    }

    public void putDouble(long ptr, double val) {
      buffers[(int) (ptr >> 31)].putDouble((int) (ptr & 0x7FFFFFFF), val);
    }

    public double getDouble(long ptr) {
      return buffers[(int) (ptr >> 31)].getDouble((int) (ptr & 0x7FFFFFFF));
    }

  }

}

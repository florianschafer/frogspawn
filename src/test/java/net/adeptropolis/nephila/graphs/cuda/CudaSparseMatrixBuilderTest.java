/*
 * Copyright (c) Florian Schaefer 2019.
 *
 * This file is subject to the terms and conditions defined in the
 * file 'LICENSE.txt', which is part of this source code package.
 */

package net.adeptropolis.nephila.graphs.cuda;

import jcuda.jcusparse.cusparseHandle;
import net.adeptropolis.nephila.graphs.Graph;
import net.adeptropolis.nephila.graphs.GraphTestBase;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CUDAException;
import net.adeptropolis.nephila.graphs.cuda.exceptions.CUDAMallocException;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraph;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphBuilder;
import net.adeptropolis.nephila.graphs.implementations.CompressedSparseGraphDatastore;
import net.adeptropolis.nephila.helpers.Arr;
import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CudaSparseMatrixBuilderTest extends GraphTestBase {

  private static cusparseHandle sparseHandle = new cusparseHandle();

  @Test
  public void simpleTriangle() throws CUDAMallocException {
    verify(triangle());
  }

  @Test
  public void someBandedGraph() throws CUDAMallocException {
    verify(bandedGraph(10000, 5));
  }

  @Test
  public void exceedBufferSize() throws CUDAMallocException {
    verify(completeGraph(1000), 1 << 10);
  }

  private void verify(Graph graph, int bufSize) throws CUDAMallocException {
    CompressedSparseGraphDatastore refDatastore = refDatastore(graph);
    CUDASparseMatrix matrix = new CudaSparseMatrixBuilder(graph, sparseHandle).build();
    assertThat(Arrays.compare(getRowPtrs(matrix), refDatastore.pointers), is(0));
    assertThat(Arrays.compare(getColIndices(matrix), refDatastore.edges.toArray()), is(0));
    assertThat(Arrays.compare(getValues(matrix), Arr.asFloats(refDatastore.weights.toArray())), is(0));
    matrix.destroy();
  }

  private void verify(Graph graph) throws CUDAMallocException {
    verify(graph, CudaSparseMatrixBuilder.BUF_SIZE);
  }

  private static long[] getRowPtrs(CUDASparseMatrix mat) throws CUDAException {
    long[] host = new long[mat.getSize() + 1];
    CUDA.copyToHost(mat.getRowPtrs(), host, mat.getSize() + 1);
    return host;
  }

  private static int[] getColIndices(CUDASparseMatrix mat) throws CUDAException {
    int[] host = new int[(int) mat.getNumEdges()];
    CUDA.copyToHost(mat.getColIndices(), host, mat.getNumEdges());
    return host;
  }

  private static float[] getValues(CUDASparseMatrix mat) throws CUDAException {
    float[] host = new float[(int) mat.getNumEdges()];
    CUDA.copyToHost(mat.getValues(), host, mat.getNumEdges());
    return host;
  }

  private CompressedSparseGraphDatastore refDatastore(Graph graph) {
    CompressedSparseGraphBuilder builder = CompressedSparseGraph.builder();
    graph.traverseSequential(builder::addDirected);
    return builder.build().getDatastore();
  }

}
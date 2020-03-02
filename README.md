# Metis
## A Novel Bifurcation Sieve Approach to Spectral Graph Clustering

| DISCLAIMER: This is a work in progress! While the core implementation is mostly finished and very stable (read: "don't hesitate to use in production"), there are currently some features missing. Notably, Metis is library-only right now and will require you to build and weight the input graphs and vertex mappings yourself. If used as such, you may need to expect some considerable API changes in the near future. |
| --- |

### Usage

#### Building Graphs

All graphs are efficiently stored in a compressed sparse format. You may use the supplied
builder class for construction:
```java
CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
```
Adding edges to the builder is straightforward:
```java
builder.add(left, right, weight)
```
Note that during graph construction, edges may appear multiple times. In this situation,
their weights are simply added together. To finally build the graph, use  
```java
Graph graph = builder.build();
```

##### Remarks
 - Vertices are identified as `int`. It is strongly recommended (although not strictly required) to construct vertex ids in such a way that they are **consecutive** integers starting at 0.
 - All edges are interpreted as being **undirected**.
 - All vertex weights must be ≥ 1.
 - By design, vertices with only 1 neighbour do not contribute to the clustering process and should be filtered out prior to building the graph to avoid performance degradation.
 - To improve performance, it is **highly** recommended to not blindly feed all possible edges, but instead apply some variant of relevance filtering beforehand. 

In case of bipartite document-feature graphs, using the length-adjusted tfidf and normalizing through division by the minimal value has been proven to be very successful. That same measure may also be used for edge filtering.

#### Configuration

The desired clustering outcome may be configured using a variety of parameters.
The most important ones are described in the example below (using the defaults):

```java
ClusteringSettings settings = ClusteringSettings.builder()
  .withMinClusterSize(50)         // Minimum size of a cluster
  .withMinClusterLikelihood(0.1)  // Minimum consistency score that a vertex may yield with respect to its cluster
  .withMinAncestorOverlap(0.4)    // Minimum consistency score that a cluster may yield with respect to its parent
  .build();
```

For further details and additional options, please refer to the Javadocs.

#### Starting the Clustering

The following method will start the actual clustering and return the root node of the resulting cluster tree:
```java
Cluster root = new RecursiveClusterSieve(graph, settings).run();
``` 
# Metis
## A Bifurcation Sieve Approach to Spectral Graph Clustering

| DISCLAIMER: This is a work in progress! While the core implementation is mostly finished and very stable (read: "don't hesitate to use in production"), there are currently some features missing. Notably, Metis is library-only right now and will require you to build and weight the input graphs and vertex mappings yourself. Depending on your intended usage pattern, you may also need to create a suitable result representation yourself. |
| --- |

### Summary

At its core, Metis is a very fast sieve-augmented variant of recursive spectral graph clustering that embeds all
vertices of the graph in a tree of clusters such that similar vertices end up in the same cluster and similar clusters
form a parent-child relationship in the resulting cluster tree. For a more technically detailed description, please
refer to the bottom section.

### Usage

#### Building Graphs

All graphs are efficiently stored in a compressed sparse format. You may use the supplied
builder class for construction:
```
CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
```
Adding edges to the builder is straightforward:
```
builder.add(left, right, weight)
```
Note that during graph construction, edges may appear multiple times. In this situation,
their weights are simply added together. To finally build the graph, use  
```
Graph graph = builder.build();
```

##### Remarks
 - Vertices are identified as `int`. It is strictly required to construct vertex ids as **consecutive** integers starting at 0.
 - All edges are interpreted as being **undirected**.
 - All edge weights must be ≥ 1.
 - By design, leaf vertices (i.e. those of degree 1) do not contribute to the clustering process and should be filtered out prior to building the graph to avoid unnecessary performance degradation.
 - To improve performance, it is **highly** recommended to not blindly feed all possible edges, but instead apply some variant of relevance filtering beforehand. 
 - There is currently no built-in mapping between vertex ids and the objects they represent. Thus, this must be taken care of externally. 

In case of bipartite document-feature graphs, using the length-adjusted tfidf and normalizing through division by the minimal value has been proven to be very successful. That same measure may also be used for edge filtering.

#### Configuration

The desired clustering outcome may be configured using a variety of parameters.
The most important ones are described in the example below (using the defaults):

```
ClusteringSettings settings = ClusteringSettings.builder()
  .withMinClusterSize(50)        // Minimum size of a cluster
  .withVertexConsistency(0.1)    // Minimum consistency score that a vertex may yield with respect to its cluster
  .withMinParentOverlap(0.4)     // Minimum consistency score that a cluster may yield with respect to its parent
  .build();
```

For further details and additional options, please refer to the Javadocs.

#### Start the Clustering

The following method will start the actual clustering and return the root node of the resulting cluster tree:
```
Cluster root = new RecursiveClustering(graph, settings).run();
```
Please refer to the Javadocs about how to process the output. `Cluster#traverse` is a good start.
You may also look into `ClusterDigester` and `Sink`.

### Details

Ignoring some details, the canonical approach to recursive spectral clustering is to bisect the graph
along its *minimal normalized cut*, i.e. the set of edges whose removal splits the graph into two disjoint partitions in
such a way that the (normalized) cumulative weight of the removed edges is minimal over all possible ways to bisect
the graph. This process is then applied in a recursive manner until some termination condition is satisfied,
effectively creating a binary tree with the clusters at its leafs.

Here, this procedure is augmented in two important ways: Firstly, instead of indiscriminately applying recursion to the
partitions from former steps, a vertex consistency score is computed for every vertex with respect to every potentially
new cluster. All vertices that fall below this score are not considered for further clustering and are instead assigned
to the last cluster tree node where they satisfy the consistency criterion (hence the "sieve"). The same goes for all
vertices of partitions falling short of a certain minimal size. This approach not only yields and enormous benefit to
both cluster quality and overall speed, but also (especially in combination with a minimal cluster size)
introduces a straightforward and comprehensive parameter that puts a strict condition on the desired clustering outcome
that is inherent to the graph itself.

Secondly, after the recursive clustering has terminated, the resulting binary tree is postprocessed in such a way that
every cluster is required to satisfy a certain consistency/overlap criterion with regards to its parent. Clusters that do not
automatically fulfil that condition, are "pushed up" the cluster tree until it is satisfied. This not only
counteracts the "shaving" phenomenon, where the resulting tree becomes a mere artifact of the recursive bisection
process, but also imposes a more natural structure on the cluster hierarchy while also doing away with the strictly
binary nature of the process.

On the more technical side, Metis' spectral bisector stage is multiple orders of magnitude faster than any naive
implementation using standard eigensolvers. This is mostly achieved by a two-pronged approach: On the one hand, it
is possible to exploit known properties of some of the involved eigensystems and using that information to apply a
custom variant of spectral shifting in order to transform the eigenproblem at hand into one that can be solved much
more efficiently. On the other hand, focusing on the nature of this particular clustering problem allows for an
immense relaxation of the eigensolver's convergence criterion, which is something that generic eigensolvers simply
cannot provide. Last but not least, those pen-and-paper optimizations are complemented by a very fast and
memory-efficient sparse graph representation that allows for the creation of induced subgraphs at very little cost.

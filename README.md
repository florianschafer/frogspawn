# Metis
## A Bifurcation Sieve Approach to Spectral Graph Clustering

### Summary

At its core, Metis is a very fast sieve-augmented variant of recursive spectral graph clustering that embeds all
vertices of the graph in a tree of clusters such that similar vertices end up in the same cluster and similar clusters
form a parent-child relationship in the resulting cluster tree. For a more technically detailed description, please
refer to the bottom section.

### Details

Ignoring some details, the canonical approach to recursive spectral clustering is to bisect the graph
along its *minimal normalized cut*, i.e. the set of edges whose removal splits the graph into two disjoint partitions in
such a way that the (normalized) cumulative weight of the removed edges is minimal over all possible ways to bisect
the graph. This process is then applied in a recursive manner until some termination condition is satisfied,
effectively creating a binary tree with the clusters at its leafs.

Here, this procedure is augmented in two important ways: Firstly, instead of indiscriminately applying recursion to the
partitions from former steps, a vertex affiliation score is computed for every vertex with respect to every potentially
new cluster. All vertices that fall below this score are not considered for further clustering and are instead assigned
to the last cluster tree node where they satisfy the min-affiliation criterion (hence the "sieve"). The same goes for all
vertices of partitions falling short of a certain minimal size. This approach not only yields an enormous benefit to
both cluster quality and overall speed, but also introduces a straightforward and comprehensive parameter that puts a
strict condition on the desired clustering outcome that is inherent to the graph itself.

Secondly, after the recursive clustering has terminated, the resulting binary tree is postprocessed in such a way that
every cluster is required to yield a certain number of subclusters. In order to do so, large clusters from deeper
levels within the hierarchy are "pushed up" the cluster tree until that criterion is satisfied. This not only
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

### Usage

#### Building and feeding Graphs

##### Building Labeled Graphs

Internally, graphs are efficiently stored in a compressed sparse format with integers as vertex ids. Since
most of the time graphs are labeled, i.e. their vertices represent some business objects or just plain
strings, there are a couple of convenience classes that automatically provide a mapping between internal
vertex ids and those objects. In these cases, it is recommended to use the supplied `LabeledGraphBuilder`
for building graphs:

```java
LabeledGraphBuilder<String> builder = new LabeledGraphBuilder<>(String.class);
```

Adding edges to the builder is straightforward using two vertex labels and an edge weight:

```java
builder.add("left", "right", 42);
```

All edges are interpreted as being undirected, i.e. using `builder.add("right", "left", 42)` in the above
example would yield the same result. Furthermore, edges may be added multiple times, in which case their
weights are simply summed up later. To finally build the graph, use

```java
LabeledGraph<String> = builder.build();
```

Note that instances of `LabeledGraph` are merely a thin wrapper around the internal graph API (see below)
that store the graph itself together with a mapping between vertex ids and label objects. Most methods
relevant to clustering expect simple `Graph` objects, which can be retrieved via `LabeledGraph#getGraph`.

For a more practical example on how to use the builder, please refer to `LabeledGraphSource#fromTSV`. 

##### Building Graphs using the Low-Level API

Building graphs using the internal API is almost identical to using the labeled version above, but
requires the vertex ids to be **consecutive integers starting at 0**. Note that violating this
requirement will result in undefined behavior.

```java
CompressedSparseGraphBuilder builder = new CompressedSparseGraphBuilder();
builder.add(left, right, weight)
...
builder.add(anotherLeft, anotherRight, anotherWeight)
Graph graph = builder.build();
```

For more information on how to use the low-level Graph API, please refer to the javadocs.

##### General Remarks
 - Edge weights **must** be ≥ 1 when used for clustering.
 - By design, leaf vertices do not contribute to the clustering process and should be filtered out prior to building the graph to avoid unnecessary performance degradation.
 - To improve performance, it is highly recommended to not blindly feed all possible edges, but instead apply some variant of relevance filtering beforehand.

#### Configuration

The desired clustering outcome may be configured using a variety of parameters. The most important ones are described
in the example below (using the defaults):

```java
ClusteringSettings settings = ClusteringSettings.builder()
  .withMinClusterSize(50)          // Min cluster size
  .withMinVertexAffiliation(0.1)      // Min affiliation score that a vertex may yield with respect to its cluster
  .withMinChildren(10) // Minumum number of children for a cluster
  .build();
```

Using the default affiliation metric, the vertex score is computed as the fraction of the intra-cluster
weight of the vertex (also counting descendant clusters) compared to its global weight.

Note that the affiliation and similarity settings are always guaranteed to be satisfied: Any cluster will only
contain vertices whose intra-cluster score satisfies at least the min affiliation criterion.

For further details and additional options, please refer to the javadocs.

#### Start the Clustering

The following method will start the actual clustering and return the root node of the resulting cluster tree:

```
Cluster root = RecursiveClustering.run(graph, settings);
```

As mentioned before, when using labeled graphs, the input graph can be retrieved from the labeled
instance, i.e.

```java
Cluster root = RecursiveClustering.run(labeledGraph.getGraph(), settings);
```

#### Cluster Output Creation

There is currently no generic way to produce cluster outputs. However, there are a couple of classes that make it
very easy to create custom ones, namely `ClusterDigester`, `Digest`, `DigestMapping` and `LabeledDigestMapping`.

##### Digests and Mappings

In this context, a digest is an output-ready representation of a cluster. Digests store all vertices, weights
and affiliation scores, sorted by custom or predefined ranking function. Moreover, depending on the configuration,
it may not just encompass the cluster vertices but also aggregate those of all of its descendants.

Currently, there may be only one digest per clustering, which is configured as part of the general settings:

```java
ClusteringSettings settings = ClusteringSettings.builder()
  ...
  .withMaxDigestSize(0)                             # Return all vertices
  .withAggregateDigests(false)                      # Only return the vertices of the cluster itself
  .withDigestRanking(COMBINED_RANKING.apply(1.75))  # Predefined ranking function: weight^1.75 * affiliationScore
  .build();

ClusterDigester digester = new ClusterDigester(settings);
```

Please refer to the `DigestRanking` interface on how to create custom ranking functions. Currently, there are three
predefined options:

 - `WEIGHT_RANKING`: Sort by vertex weight (descending)
 - `SCORE_RANKING`: Sort by vertex affiliation score (descending)
 - `COMBINED_RANKING`: Sort by both vertex weight and score, balancing their impact using an exponent on the weighs 

Digests are not intended to be used directly for output. Instead, one should supply a digest mapping function
that maps the cluster members onto custom user-defined objects. For this purpose, there are two functional interfaces
that one may use to supply arbitrary mapping functions:
 
 - `DigestMapping<output type>`: Simple mapping using vertex ids
 - `LabeledDigestMapping<label type, output type>`: Mapping using vertex labels

As an example, the following mapping creates a simple String representation for every digest vertex:

```java
LabeledDigestMapping<String, String> mapping = (label, weight, score) -> String.format("%s [%.1f %.2f]", label, weight, score);
```

##### Traversing clusters for output

Digests only refer to single clusters. In order to create the output for the full cluster tree, one needs to traverse
it starting from the root cluster and create the digests and output hierarchy on the fly. There are many ways to
navigate through a cluster hierarchy, although in most cases, it is recommended to use `Cluster#traverse`, which takes a
`Consumer<Cluster>` and walks through the hierarchy in a classical depth-first-like fashion. For more information,
please see the javadocs.

```java
root.traverse(cluster -> {
 // Apply a digester to the cluster, evaluate the local hierarchy using cluster.getChildren() / cluster.getParent,... 
});
```

### Full working example

Below, you will find a fully working example of running clustering and exporting results using a labeled graph that has
been imported from a TSV file using default settings.

```java
LabeledGraph<String> labeledGraph = LabeledGraphSource.fromTSV(Files.lines("/path/to/edges/in/tsv/format.tsv"));

ClusteringSettings settings = ClusteringSettings.builder().build();

Cluster root = RecursiveClustering.run(labeledGraph.getGraph(), settings);

ClusterDigester digester = new ClusterDigester(settings);
LabeledDigestMapping<String, String> mapping = (label, weight, score) -> String.format("%s [%.1f]", label, weight);

PrintWriter w = new PrintWriter("/output/path.txt");
root.traverse(cluster -> {
  Digest digest = digester.digest(cluster);
  String vertices = digest.map(mapping, labeledGraph.getLabels()).collect(Collectors.joining(", "));
  String prefix = StringUtils.repeat("==", cluster.depth());
  w.printf("%s> %d: %s\n", prefix, digest.totalSize(), vertices);
});
w.close();
```

This will create a semi-structured output in the following fashion:

```
> 4250: root_member_a [123], root_member_b [42], ...
==> 15497: child_1_member_a [435], child_1_member_b [271], ...
====> 878: child_11_member_a [435], child_11_member_b [271], ...
======> 878: child_111_member_a [43], child_111_member_b [21], ...
======> 878: child_112_member_a [56], child_112_member_b [28], ...
====> 248: child_12_member_a [124], child_12_member_b [67], ...
====> 124: child_13_member_a [341], child_13_member_b [221], ...
==> 15497: child_2_member_a [345], child_2_member_b [45], ...
...
```

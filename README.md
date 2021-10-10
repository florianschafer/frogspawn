# Frogspawn
## A Fast Recursive Spectral Graph Partitioner

## Summary

At its core, Frogspawn is a very fast and memory efficient sieve-augmented variant of recursive spectral graph
clustering (i.e. min normalized cut) that embeds all vertices of the graph in a tree of clusters such that similar
vertices end up in the same cluster and similar clusters form a parent-child relationship in the resulting cluster tree.
Instead of storing all vertices only in the bottom leaves, they may end of anywhere within the hierarchy, depending on
their affiliation with the respective cluster.

## Usage

**Note:** These instructions refer to the clustering of labeled graphs using the high-level API and convenience
wrappers. For more information on bare integer-indexed graphs and using the low-level API, see below.

### Creating Graphs

There are multiple ways of creating input graphs. In either case, there are a few things to keep in mind:

 - All edges are interpreted as being undirected
 - Edges may be added multiple times, in which case their weights are simply added
 - Edge weights **must** be ≥ 0 when used for clustering.
 - The task of assigning sensible edge weights is completely up to the user.
 
#### Direct Import from a File or URL: 

The easiest way of creating labeled graphs is to import them from a stream of TSV records with columns for
edge weight, left vertex, right vertex:

```java
    Path path = Paths.get(".../graph.tsv");
    LabeledGraph<String> graph = LabeledGraphSource.fromTSV(Files.lines(path));
```

#### Building Labeled Graphs

Alternatively, graphs can be created on the fly using a builder:

```java
LabeledGraphBuilder<String> builder = new LabeledGraphBuilder<>(new DefaultLabeling<>(String.class));
```

Adding edges to the builder is straightforward using two vertex labels and an edge weight:

```java
builder.add("left", "right", 42);
```

To finally build the graph, use

```java
LabeledGraph<String> = builder.build();
```

### Configuration

The desired clustering outcome may be configured using a variety of parameters. The most important ones are described
in the example below (using the defaults):

```java
ClusteringSettings settings = ClusteringSettings.builder()
  .minClusterSize(50)
  .minAffiliation(0.2)
  .minParentSimilarity(0.09)
  .maxParentSimilarity(0.60)
  .singletonMode(ASSIMILATE)
  .aggregateDigests(false)
  .digestRanking(DEFAULT_COMBINED_RANKING)
  .flatten(false) //  
  .build();
```

Some of these settings may require a little bit more explanation:

- `minClusterSize`: Just what you think it means: The minimum number of vertices required to form a cluster
- `minAffiliation`: Ensures that all vertices inside a cluster obey the min affiliation criterion. By default,
  the affiliation score is computed as the fraction of the intra-cluster weight of the vertex
  (also counting descendant clusters) compared to its global weight.
- `minParentSimilarity, maxParentSimilarity`: Ensures that the normalized cuts (as a proxy for similarity) between
  clusters and their parents lie within a certain range. Too dissimilar clusters are assigned to an ancestor with higher
  similarity while clusters with very high similarity are merged together.
- `singletonMode`: Configure treatment of singleton clusters (i.e. those without any siblings). Possible options are:
  - `ASSIMILATE`: Merge singletons into their parents
  - `REDISTRIBUTE`: Make singletons a sibling of the original parent
  - `NONE`: Leave singletons where they are.
- `aggregateDigests`: Controls whether the cluster output stage should only consider a cluster's remainder (`false`) or
  also the aggregate remainders of all descendant clusters (`true`).
- `digestRanking`: Control the inner-cluster vertex sorting in the output stage. Possible values:
  - `WEIGHT_RANKING`: Sort by vertex weight (descending)
  - `SCORE_RANKING`: Sort by vertex affiliation score (descending)
  - `COMBINED_RANKING`: Sort by both vertex weight and score, balancing their impact using an exponent on the weighs
- `maxDigestSize`: Whether to output just a sample of a cluster's vertices or everything (`0`).
- `flatten`: Produce a flat list of clusters instead of a tree structure. Note that this will technically still
  create a tree of depth 2, where the root node contains all vertices that couldn't be assigned to any of the clusters.

All affiliation and similarity settings are always guaranteed to be satisfied. For further details and
additional options, please refer to the javadocs.

### Starting the clustering process

Doing the actual clustering is just as easy as creating graphs. In most cases, this will be:

```java
OutputCluster<String> tree = SimpleClustering.cluster(graph, settings);
```

This will create a tree of `OutputCluster<String>` objects, each with a list of child clusters and vertices.

In case of plain integer graphs, custom output structures or whenever the number of nodes or clusters is so large
that it should be streamed instead of collected all at once, please refer to the low level API section about cluster
digests.

## Low-Level API

Documentation on this topic is a work in progress. Until then, please refer to the javadocs and the implementations
of `SimpleClustering` and `RecursiveClustering`. Or just send me a message.

## Technical Background

Ignoring some details, the canonical approach to recursive spectral clustering is to bisect the graph
along its *minimal normalized cut*, i.e. the set of edges whose removal splits the graph into two disjoint partitions in
such a way that the (normalized) cumulative weight of the removed edges is minimal over all possible ways to bisect
the graph. This process is then applied in a recursive manner until some termination condition is satisfied,
effectively creating a binary tree with the clusters at its leaves.

Here, this procedure is augmented in two important ways: Instead of indiscriminately applying recursion to the
partitions from former steps, a vertex affiliation score is computed for every vertex with respect to every potentially
new cluster. All vertices that fall below this score are not considered for further clustering and are instead assigned
to the last cluster tree node where they satisfy the min-affiliation criterion (hence the "sieve"). The same goes for all
vertices of partitions falling short of a certain minimal size. This approach not only yields an enormous benefit to
both cluster quality and overall speed, but also introduces a straightforward and comprehensive parameter that puts a
strict condition on the desired clustering outcome that is inherent to the graph itself.

Secondly, after the recursive clustering has terminated, the resulting binary tree is postprocessed to meet additional
criteria such as clusters being required to obey certain min-cut ranges with respect to their parents where
non-compliant clusters are either attached to an ancestor with higher similarity or assimilated by their parents.
This not only counteracts the "shaving" phenomenon, where the resulting tree becomes a mere artifact of the recursive
bisection process, but also imposes a more natural structure on the cluster hierarchy while also doing away with the
strictly binary nature of the process.

On the more technical side, Frogspawn's spectral bisector stage is multiple orders of magnitude faster than any naive
implementation using standard eigensolvers. This is mostly achieved by a two-pronged approach: On the one hand, it
is possible to exploit known properties of some of the involved eigensystems and using that information to apply a
custom variant of spectral shifting in order to transform the eigenproblem at hand into one that can be solved much
more efficiently. On the other hand, focusing on the nature of this particular clustering problem allows for an
immense relaxation of the eigensolver's convergence criterion, which is something that generic eigensolvers simply
cannot provide. Last but not least, those pen-and-paper optimizations are complemented by a very fast and
memory-efficient sparse graph representation that allows for the creation of induced subgraphs at very little cost.
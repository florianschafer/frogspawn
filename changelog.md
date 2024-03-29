# Frogspawn Changelog

## Latest
    
    - Optionally flatten cluster trees
    - Provide a more simple overall interface to clustering of labeled graphs
    - Tune down logging to DEBUG for pretty much everything that's not an error or warning
    - Remove unnecessary passing of label classes to DefaultLabeling
    - Add Python bindings

## v1.3.2

    - General cleanup
    - Reunite config classes using Lombok. This breaks part of the api
    - Get rid of Guava and math3 dependency
    - Incorporate postprocessing directly into recursive clustering step

## v1.3.1

    - Add vertex filtering by max degree
    - Create subgraphs using vertex label predicates

## v1.3.0

    - LabeledGraphBuilder::add is now thread safe
    - Provide better defaults
    - Remove min weight checks in builder.
    - Fix an int overflow issue in the interpolation search on big arrays
    - Added option for subgraph creation from vertex id predicates
    - Added convenience methods for sequential graph traversal
    - Considerable speed boost when building large graphs
    - Faster graph serialization
    - Introduce better graph labeling. This breaks compatibility with previously serialized graphs
    - Labeled graphs can now be traversed directly
    - Add minimal graph filter pipeline, min degree filter
    - Ability to collapse labeled subgraphs
    - Add graph functions
    - Labeled graph merging
            
## v1.2.0

    - Improved parent similarity postfiltering
    - New postprocessing setting `parentSimilarityAcceptanceLimit` makes the acceptance threshold accseeible
    - Much faster ncut similarity metric

## v1.1.0

    - NCut graph similarity metric
    - Introduce postfiltering based on parent-child cluster similarity
    - Rework postprocessing pipeline: Better configurability, run consistency guards automatically,\\
      allow for idempotency criteria

## v1.0
First published version
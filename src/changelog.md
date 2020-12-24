# Frogspawn Changelog

## Current

## v1.2.1

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
    - Add graph functions
            
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